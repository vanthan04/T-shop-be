const axios = require("../config/axiosInstance");
require('dotenv').config();
const jwt = require("jsonwebtoken")
const adminService = require("../services/adminService")

const register = async (req, res) => {
    console.log("test register")
    const {username, email, password, firstName, lastName} = req.body;
    try{
        const adminToken = await adminService.getAminToken();
        const userInfo = {
            username,
            email,
            firstName,
            lastName,
            emailVerified: false,
            enabled: true
        }

        const response = await adminService.createNewUser(adminToken, userInfo, password);
        return res.status(response.status).json(response);
    }catch (e){
        console.log(e.message)
        return res.status(500).json({
            success: false,
            message: "false",
            error: "Internal Server Error!"
        })
    }

}

const login = async (req, res) => {
    const {username, password} = req.body;
    if(!username || !password){
        return res.status(400).json({
            success: false,
            error: "Please enter the username or password!"
        })
    }

    try{
        const tokenResponse = await axios.post(
            `/realms/${process.env.KEYCLOAK_REALM}/protocol/openid-connect/token`,
            {
                grant_type: "password",
                client_id: process.env.KEYCLOAK_CLIENT_ID,
                client_secret: process.env.KEYCLOAK_CLIENT_SECRET,
                username,
                password
            },
            {
                headers:{
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            })

        const accessToken = tokenResponse.data.access_token;
        const userData = jwt.decode(accessToken);

        const {email, given_name, family_name, preferred_username, sub, resource_access, ...details} = userData;
        const roles = resource_access?.["identity-rest-api"]?.roles
        const role = Array.isArray(roles) && roles.length > 0 ? roles[0] : null;

        if (userData.email_verified) {
            return res.status(200).json({
                success: true,
                message: "Login successfully!",
                data: {
                    token: tokenResponse.data,
                    userData: {
                        email,
                        username: preferred_username,
                        firstName: given_name,
                        lastName: family_name,
                        userId: sub,
                        role: role
                    }
                }
            });
        } else {
            const adminToken = await adminService.getAminToken();
            await adminService.send_verify_email(adminToken, userId);
            return res.status(200).json({ message: "Check email to verify!" });
        }
    } catch (error){
        const errMsg = error.response?.data?.error_description || error.message;
        const statusCode = error.response?.status || 500;
        return res.status(statusCode).json({ message: errMsg });
    }

}

const loginGoogle = async (req, res) => {
    const { code } = req.body;
    if (!code) {
        return res.status(400).json({ error: "Missing code" });
    }
    console.log("test")
    try {
        const params = new URLSearchParams();
        params.append("grant_type", "authorization_code");
        params.append("code", code);
        params.append("client_id", process.env.KEYCLOAK_CLIENT_ID);
        params.append("client_secret", process.env.KEYCLOAK_CLIENT_SECRET); // nếu cần
        params.append("redirect_uri", "http://localhost:5173/callback");

        const response = await axios.post(
            `/realms/${process.env.KEYCLOAK_REALM}/protocol/openid-connect/token`,
            params.toString(),
            {
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
            }
        );
        const accessToken = response.data.access_token;
        const userData = jwt.decode(accessToken);
        const {email, given_name, family_name, preferred_username, sub, resource_access, ...details} = userData;

        const roles = resource_access?.["identity-rest-api"]?.roles
        const role = Array.isArray(roles) && roles.length > 0 ? roles[0] : null;

        return res.status(200).json({
            success: true,
            message: "Login Google successfully!",
            data: {
                token: response.data,
                userData: {
                    email,
                    username: preferred_username,
                    firstName: given_name,
                    lastName: family_name,
                    userId: sub,
                    role: role
                }
            }
        });
    } catch (err) {
        console.error("Login Google Error:", err.response?.data || err.message);
        return res.status(500).json({
            success: false,
            message: "Failed to login with Google"
        });
    }
};

module.exports = {
    register,
    login,
    loginGoogle
}