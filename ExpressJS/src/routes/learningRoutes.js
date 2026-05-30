const express = require('express');
const { asyncHandler } = require('../utils/asyncHandler');
const {
  createFlashcard,
  deleteFlashcard,
  getDailyPlan,
  listDueReview,
  listFlashcards,
  seedFlashcards,
  submitReview,
  updateFlashcard,
} = require('../controllers/learningController');

const router = express.Router();

router.get('/flashcards', asyncHandler(listFlashcards));
router.post('/flashcards', asyncHandler(createFlashcard));
router.patch('/flashcards/:id', asyncHandler(updateFlashcard));
router.delete('/flashcards/:id', asyncHandler(deleteFlashcard));

router.get('/review', asyncHandler(listDueReview));
router.post('/review', asyncHandler(submitReview));

router.get('/daily-plan', asyncHandler(getDailyPlan));
router.post('/seed', asyncHandler(seedFlashcards));

module.exports = router;
