const express = require('express');
const bodyParser = require("body-parser");
require("dotenv").config();

const sequelize = require("./untils/connect-db"); // 👉 kết nối DB
const authRoutes = require("./routes/authRoute");
const userRoutes = require("./routes/userRoute");
const errorHandler = require("./untils/errorHandler");

const {initProducer} = require("./event/UserCreatedEvent")
const app = express();
const port = process.env.SERVER_PORT || 8001;

// Test route
app.get('/api/v1/auth/demo', (req, res) => {
    res.send('Hello from Auth service!');
});

// Middleware đọc JSON/body
app.use(express.json());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Routes
app.use("/api/v1/auth", authRoutes);
app.use("/api/v1/user", userRoutes);

// Error handler
app.use(errorHandler);

// Kết nối DB và sync model
(async () => {
    try {
        await  initProducer()
        await sequelize.authenticate();
        console.log("✅ Database connected");

        await sequelize.sync({ alter: true });
        console.log("✅ Models synced");

        // Start server sau khi DB sẵn sàng
        app.listen(port, () => {
            console.log(`🚀 Auth Service running at http://localhost:${port}`);
        });

        require('../eureka-client');

    } catch (error) {
        console.error("❌ Unable to connect to the database:", error.message);
    }
})();
