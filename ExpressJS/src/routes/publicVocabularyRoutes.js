const express = require('express');
const router = express.Router();
const { protect } = require('../middleware/authMiddleware');
const {
  getPublicVocabularies,
  getPublicVocabularyDetails,
  saveToPersonal
} = require('../controllers/publicVocabularyController');

router.get('/', protect, getPublicVocabularies);
router.get('/:id', protect, getPublicVocabularyDetails);
router.post('/:id/save', protect, saveToPersonal);

module.exports = router;
