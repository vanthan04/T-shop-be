// src/config/axiosInstance.js
const axios = require('axios');

const instance = axios.create({
    baseURL: process.env.KEYCLOAK_BASE_URL || 'http://localhost:8080',
    timeout: 5000,
});

module.exports = instance;
