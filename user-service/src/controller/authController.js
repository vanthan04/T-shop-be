const axios = require("../config/axiosInstance");
const jwt = require("jsonwebtoken");
const adminService = require("../services/adminService");

const register = async (req, res, next) => {
    try {
        const { username, email, password, firstName, lastName } = req.body;

        if (!username || !email || !password) {
            return res.status(400).json({
                success: false,
                message: "Missing required fields!",
                dataResponse: null
            });
        }

        const adminToken = await adminService.getAminToken();
        const userInfo = {
            username,
            email,
            firstName,
            lastName,
            emailVerified: false,
            enabled: true
        };

        const response = await adminService.createNewUser(adminToken, userInfo, password);
        return res.status(response.status).json({
            success: response.success,
            message: response.message,
            dataResponse: response.dataResponse
        });

    } catch (err) {
        next(err);
    }
};

const login = async (req, res, next) => {
    try {
        const { username, password } = req.body;
        if (!username || !password) {
            return res.status(400).json({
                success: false,
                message: "Please enter username and password!",
                dataResponse: null
            });
        }

        const tokenResponse = await axios.post(
            `/realms/${process.env.KEYCLOAK_REALM}/protocol/openid-connect/token`,
            new URLSearchParams({
                grant_type: "password",
                client_id: process.env.KEYCLOAK_CLIENT_ID,
                client_secret: process.env.KEYCLOAK_CLIENT_SECRET,
                username,
                password
            }),
            { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }
        );

        const accessToken = tokenResponse.data.access_token;
        const userData = jwt.decode(accessToken);
        const userId = userData.sub;

        if (!userData.email_verified) {
            const adminToken = await adminService.getAminToken();
            await adminService.send_verify_email(adminToken, userId);
            return res.status(200).json({
                success: false,
                message: "Please check your email to verify your account!",
                dataResponse: null
            });
        }

        const { email, given_name, family_name, preferred_username, resource_access } = userData;
        const roles = resource_access?.["identity-rest-api"]?.roles || [];
        const role = roles.length > 0 ? roles[0] : null;

        return res.status(200).json({
            success: true,
            message: "Login successfully!",
            dataResponse: {
                token: tokenResponse.data,
                userData: {
                    email,
                    username: preferred_username,
                    firstName: given_name,
                    lastName: family_name,
                    userId,
                    role
                }
            }
        });

    } catch (err) {
        next(err);
    }
};

const loginGoogle = async (req, res, next) => {
    try {
        const { code } = req.body;
        if (!code) {
            return res.status(400).json({
                success: false,
                message: "Missing Google authorization code!",
                dataResponse: null
            });
        }

        const params = new URLSearchParams({
            grant_type: "authorization_code",
            code,
            client_id: process.env.KEYCLOAK_CLIENT_ID,
            client_secret: process.env.KEYCLOAK_CLIENT_SECRET,
            redirect_uri: "http://localhost:5173/callback"
        });

        const response = await axios.post(
            `/realms/${process.env.KEYCLOAK_REALM}/protocol/openid-connect/token`,
            params.toString(),
            { headers: { "Content-Type": "application/x-www-form-urlencoded" } }
        );

        const accessToken = response.data.access_token;
        const userData = jwt.decode(accessToken);

        const { email, given_name, family_name, preferred_username, sub, resource_access } = userData;
        const roles = resource_access?.["identity-rest-api"]?.roles || [];
        const role = roles.length > 0 ? roles[0] : null;

        return res.status(200).json({
            success: true,
            message: "Login with Google successfully!",
            dataResponse: {
                token: response.data,
                userData: {
                    email,
                    username: preferred_username,
                    firstName: given_name,
                    lastName: family_name,
                    userId: sub,
                    role
                }
            }
        });

    } catch (err) {
        next(err);
    }
};

module.exports = {
    register,
    login,
    loginGoogle
};
