const mongoose = require('mongoose');

const userSchema = new mongoose.Schema(
  {
    uid: {
      type: String,
      required: true,
      trim: true,
      index: true,
    },
    fullName: {
      type: String,
      required: true,
      trim: true,
    },
    email: {
      type: String,
      required: true,
      trim: true,
      lowercase: true,
      unique: true,
      index: true,
    },
    provider: {
      type: String,
      required: true,
      trim: true,
      default: 'email',
    },
    photoUrl: {
      type: String,
      default: null,
    },
    isGoogleUser: {
      type: Boolean,
      default: false,
    },
    passwordHash: {
      type: String,
      default: null,
    },
    passwordSalt: {
      type: String,
      default: null,
    },
    streak: {
      type: Number,
      default: 0,
    },
    level: {
      type: String,
      default: 'A1',
    },
    wordsLearned: {
      type: Number,
      default: 0,
    },
    totalReviews: {
      type: Number,
      default: 0,
    },
    correctReviews: {
      type: Number,
      default: 0,
    },
    syncedAt: {
      type: Number,
      default: () => Date.now(),
    },
  },
  {
    timestamps: true,
  }
);

module.exports = mongoose.model('User', userSchema);
