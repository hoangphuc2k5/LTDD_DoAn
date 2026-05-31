const mongoose = require('mongoose');

const publicVocabularySchema = new mongoose.Schema({
  title: {
    type: String,
    required: true,
    trim: true
  },
  topic: {
    type: String,
    required: true,
    trim: true
  },
  level: {
    type: String,
    enum: ['Beginner', 'Intermediate', 'Advanced', 'All'],
    default: 'All'
  },
  totalWords: {
    type: Number,
    default: 0
  },
  downloads: {
    type: Number,
    default: 0
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
}, { timestamps: true });

// Index for searching
publicVocabularySchema.index({ title: 'text', topic: 'text' });

module.exports = mongoose.model('PublicVocabulary', publicVocabularySchema);
