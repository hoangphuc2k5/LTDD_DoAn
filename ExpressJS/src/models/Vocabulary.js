const mongoose = require('mongoose');

const vocabularySchema = new mongoose.Schema({
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  word: {
    type: String,
    required: true,
    trim: true,
    lowercase: true
  },
  meaning: {
    type: String,
    required: true,
    trim: true
  },
  pronunciation: {
    type: String,
    trim: true
  },
  example: {
    type: String,
    trim: true
  },
  topic: {
    type: String,
    trim: true,
    default: 'Uncategorized'
  },
  isPublic: {
    type: Boolean,
    default: false
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
}, { timestamps: true });

// Ensure unique vocabulary per user (case-insensitive due to lowercase)
vocabularySchema.index({ userId: 1, word: 1 }, { unique: true });

module.exports = mongoose.model('Vocabulary', vocabularySchema);
