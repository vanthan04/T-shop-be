const express = require('express');
const app = express();
const bodyParser = require("body-parser");
const port = process.env.SERVER_PORT ||8001;
const authRoutes = require("./routes/authRoute")

app.get('/api/v1/auth/demo', (req, res) => {
    res.send('Hello from Auth service!');
});

app.use(express.json());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.use("/api/v1/auth", authRoutes);

app.listen(port, () => {
    console.log(`Node.js service listening at http://localhost:${port}`);
});

// Import and start Eureka client
require('../eureka-client');