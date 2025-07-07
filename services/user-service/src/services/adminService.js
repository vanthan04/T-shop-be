const axios = require("../config/axiosInstance")
require("dotenv").config()

const getAminToken = async () => {
    try {
        const response = await axios.post(
            `/realms/${process.env.KEYCLOAK_REALM}/protocol/openid-connect/token`,
            {
                grant_type: "password",
                client_id: process.env.KEYCLOAK_CLIENT_ID,
                client_secret: process.env.KEYCLOAK_CLIENT_SECRET,
                username: process.env.KEYCLOAK_ADMIN_USER,
                password: process.env.KEYCLOAK_ADMIN_PASSWORD
            },
            {
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                }
            }
        )
        return response.data.access_token;
    } catch (e){
        console.log(e.message);
        return null;
    }
}

const createNewUser = async (adminToken, userInfo, password) => {
    try{
        const res = await axios.post(
            `/admin/realms/${process.env.KEYCLOAK_REALM}/users`,
            userInfo,
            {
                headers: {
                    Authorization: `Bearer ${adminToken}`,
                    "Content-Type": "application/json"
                },
            }
        )
        if (res.status === 201) {
            const location = res.headers?.location;
            if (!location) {
                return {
                    status: 500,
                    message: "Internal Server Error"
                };
            }
            const userId = location.split('/').pop();


            //Neu khong lay duoc userId thi tra ve loi
            if (!userId){
                return {
                    success: false,
                    status: 500,
                    message: "Internal Server Error"
                }
            }

            // Set role user
            const resultSetRole = await setRoleUser(adminToken, userId);

            if (resultSetRole.status !== 200) {
                return {
                    success: false,
                    status: resultSetRole.status,
                    message: resultSetRole.message || "Failed to set user role"
                };
            }
            //Set Password neu set role thanh cong
            const resSetPassword = await setPassword(adminToken, userId, password);
            if (resSetPassword.status !== 204) {
                return {
                    success: false,
                    status: resultSetRole.status,
                    message: resultSetRole.message || "Failed to set user password"
                };
            }

            // Nếu set password va role thành công thì gửi mail verify
            const resultSendEmail = await send_verify_email(adminToken, userId);
            if (resultSendEmail.status !== 204){
                return {
                    success: false,
                    status: resultSendEmail.status,
                    message: resultSendEmail.message
                }
            }
            return {
                success: true,
                status: 200,
                message: "Please check email to verify!"
            };



        } else {
            console.error('❌ Failed to create user:', res.status, res.data);
            return {
                success: false,
                status: res.status,
                message: res.data.errorMessage || "Internal Server Error"
            }
        }
    } catch (err) {
        return {
            success: false,
            status: err.response?.status || 500,
            message: err.response?.data?.errorMessage || err.message || "Internal Server Error"
        }

    }
}

const send_verify_email = async (adminToken, userId) =>{
    try{
        const response = await axios.put(
            `/admin/realms/${process.env.KEYCLOAK_REALM}/users/${userId}/send-verify-email`,
            {},
            {
                headers: {
                    Authorization: `Bearer ${adminToken}`,
                    "Content-Type": 'application/json',
                },
                timeout: 10000
            })
        return {
            status: response.status,
            message: response.status === 204 ? "Please check email to verify!" :  response.data?.errorMessage
        }

    }catch (error){
        console.log(error)
        console.error("Failed to send verification email:", error.response?.data || error.message);
        return {
            success: false,
            status: 500,
            message: "Internal Server Error"
        }
    }
}

const setRoleUser = async (adminToken, userId) => {
    const roleName = "user";
    try {
        // Lấy thông tin role realm
        const resultGetRole = await axios.get(
            `/admin/realms/${process.env.KEYCLOAK_REALM}/roles/${roleName}`,
            {
                headers: {
                    Authorization: `Bearer ${adminToken}`
                }
            }
        );

        if (resultGetRole.status === 200) {
            // Gán role realm cho user
            const resultSetRole = await axios.post(
                `/admin/realms/${process.env.KEYCLOAK_REALM}/users/${userId}/role-mappings/realm`,
                [resultGetRole.data],
                {
                    headers: {
                        Authorization: `Bearer ${adminToken}`,
                        "Content-Type": "application/json"
                    }
                }
            );

            if (resultSetRole.status === 204) {
                return {
                    status: 200,
                };
            } else {
                return {
                    status: resultSetRole.status,
                    message: resultSetRole.data?.errorMessage || "Failed to set role"
                };
            }
        } else {
            return {
                status: resultGetRole.status,
                message: resultGetRole.data?.errorMessage || "Failed to get role"
            };
        }
    } catch (error) {
        console.error("Error in setRoleUser:", error.response?.data || error.message);
        return {
            success: false,
            status: 500,
            message: "Internal Server Error"
        };
    }
};

const setPassword = async (adminToken, userId, password) => {
    try{
        const payload = {
            type: "password",
            value: password,
            temporary: false // Nếu là true thì user phải đổi mật khẩu khi đăng nhập lần đầu
        };
        const resSetPassword = await axios.put(
            `/admin/realms/${process.env.KEYCLOAK_REALM}/users/${userId}/reset-password`,
            payload,
            {
                headers: {
                    Authorization: `Bearer ${adminToken}`
                }
            }
        )
        return {
            status: resSetPassword.status,
            message: "Set password successfully"
        };
    } catch (error){
        console.error("❌ Failed to set password:", error.response?.data || error.message);
        return {
            success: false,
            status: error.response?.status || 500,
            message: error.response?.data?.errorMessage || "Internal Server Error"
        };

    }

}

module.exports = {
    getAminToken,
    createNewUser,
    send_verify_email
}