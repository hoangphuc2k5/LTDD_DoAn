const express = require('express');
const router = express.Router();
const multer = require('multer');
const { protect } = require('../middleware/authMiddleware');
const { validateVocabulary, checkValidation } = require('../middleware/validateVocabulary');
const {
  getVocabularies,
  createVocabulary,
  updateVocabulary,
  deleteVocabulary,
  importCSV,
  exportCSV
} = require('../controllers/vocabularyController');

// Multer config for in-memory upload
const storage = multer.memoryStorage();
const upload = multer({ 
  storage,
  fileFilter: (req, file, cb) => {
    if (file.mimetype === 'text/csv' || file.mimetype === 'application/vnd.ms-excel') {
      cb(null, true);
    } else {
      cb(new Error('Only CSV files are allowed'), false);
    }
  }
});

router.route('/')
  .get(protect, getVocabularies)
  .post(protect, validateVocabulary, checkValidation, createVocabulary);

router.post('/import', protect, upload.single('file'), importCSV);
router.get('/export', protect, exportCSV);

router.route('/:id')
  .put(protect, validateVocabulary, checkValidation, updateVocabulary)
  .delete(protect, deleteVocabulary);

module.exports = router;
