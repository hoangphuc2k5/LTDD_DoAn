const express = require('express');
const router = express.Router();
const { protect } = require('../middleware/authMiddleware');
const { lookupWord, suggestWord } = require('../controllers/dictionaryController');

router.get('/lookup/:word', protect, lookupWord);
router.get('/suggest', protect, suggestWord);

module.exports = router;
