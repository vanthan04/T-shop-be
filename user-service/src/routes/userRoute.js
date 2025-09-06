const router = require("express").Router();
const userController = require("../controller/userController");
const asyncHandler = require("express-async-handler");

// router.post("/", asyncHandler(userController.addUser));
router.put("/:userId", asyncHandler(userController.updateUserById));
router.get("/:userId", asyncHandler(userController.getUserById));
router.post("/:userId/address", asyncHandler(userController.addAddress));
router.put("/:userId/address", asyncHandler(userController.updateAddress));
router.delete("/:userId/address", asyncHandler(userController.deleteAddress));

module.exports = router;
