const express = require('express');
const { asyncHandler } = require('../utils/asyncHandler');
const { login, register } = require('../controllers/authController');

const router = express.Router();

router.post('/login', asyncHandler(login));
router.post('/register', asyncHandler(register));

module.exports = router;