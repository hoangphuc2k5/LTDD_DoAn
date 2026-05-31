const express = require("express");
const { asyncHandler } = require("../utils/asyncHandler");
const { authenticateUser } = require("../middleware/authMiddleware");
const {
  syncUser,
  updateUserProfile,
} = require("../controllers/userController");

const router = express.Router();

router.post("/sync", asyncHandler(syncUser));
router.put("/:uid", authenticateUser, asyncHandler(updateUserProfile));

module.exports = router;
