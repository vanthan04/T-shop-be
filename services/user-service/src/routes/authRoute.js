const router = require("express").Router();
const authController = require("../controller/authController");
const asyncHandler = require("express-async-handler");

router.post("/register", asyncHandler(authController.register));
router.post("/login", asyncHandler(authController.login));
router.post("/login_google", asyncHandler(authController.loginGoogle));

module.exports = router;
