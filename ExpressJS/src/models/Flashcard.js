const mongoose = require('mongoose');

const flashcardSchema = new mongoose.Schema(
  {
    userId: {
      type: String,
      required: true,
      trim: true,
      index: true,
    },
    term: {
      type: String,
      required: true,
      trim: true,
    },
    pronunciation: {
      type: String,
      default: '',
      trim: true,
    },
    meaning: {
      type: String,
      required: true,
      trim: true,
    },
    example: {
      type: String,
      default: '',
      trim: true,
    },
    topic: {
      type: String,
      default: 'General',
      trim: true,
    },
    repetitions: {
      type: Number,
      default: 0,
      min: 0,
    },
    intervalDays: {
      type: Number,
      default: 0,
      min: 0,
    },
    easeFactor: {
      type: Number,
      default: 2.5,
      min: 1.3,
    },
    dueAt: {
      type: Date,
      default: () => new Date(),
      index: true,
    },
    lastReviewedAt: {
      type: Date,
      default: null,
    },
  },
  {
    timestamps: true,
  }
);

flashcardSchema.index({ userId: 1, dueAt: 1 });

module.exports = mongoose.model('Flashcard', flashcardSchema);
