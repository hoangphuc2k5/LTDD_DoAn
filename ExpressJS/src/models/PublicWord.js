const mongoose = require('mongoose');

const publicWordSchema = new mongoose.Schema({
  publicVocabularyId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'PublicVocabulary',
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
  }
}, { timestamps: true });

publicWordSchema.index({ publicVocabularyId: 1 });

module.exports = mongoose.model('PublicWord', publicWordSchema);
