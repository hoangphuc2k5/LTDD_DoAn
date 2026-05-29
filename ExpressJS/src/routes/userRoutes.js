const express = require('express');
const { asyncHandler } = require('../utils/asyncHandler');
const { syncUser } = require('../controllers/userController');

const router = express.Router();

router.post('/sync', asyncHandler(syncUser));

module.exports = router;