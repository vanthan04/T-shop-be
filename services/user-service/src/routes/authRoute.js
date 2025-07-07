const router = require("express").Router();
const authController = require("../controller/authController")

router.post("/register", authController.register);
router.post("/login", authController.login);
router.post("/login_google", authController.loginGoogle);

module.exports = router;