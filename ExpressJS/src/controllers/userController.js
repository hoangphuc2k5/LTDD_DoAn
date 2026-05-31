const User = require('../models/User');

function normalizeEmail(email) {
  return String(email || '').trim().toLowerCase();
}

function serializeUser(user) {
  return {
    uid: user.uid || user.email,
    fullName: user.fullName,
    email: user.email,
    provider: user.provider,
    photoUrl: user.photoUrl,
    isGoogleUser: user.isGoogleUser,
    streak: user.streak || 0,
    level: user.level || 'A1',
    wordsLearned: user.wordsLearned || 0,
    totalReviews: user.totalReviews || 0,
    correctReviews: user.correctReviews || 0,
    syncedAt: user.syncedAt,
  };
}

async function syncUser(req, res) {
  const {
    uid,
    fullName,
    email,
    provider,
    photoUrl,
    isGoogleUser,
    streak,
    level,
    wordsLearned,
    totalReviews,
    correctReviews,
    syncedAt
  } = req.body || {};
  const normalizedEmail = normalizeEmail(email);

  if (!uid || !fullName || !normalizedEmail || !provider) {
    return res.status(400).json({
      success: false,
      message: 'uid, fullName, email and provider are required',
    });
  }

  const existing = await User.findOne({
    $or: [{ uid: String(uid).trim() }, { email: normalizedEmail }],
  });

  const user = existing || new User({
    uid: String(uid).trim(),
    fullName: String(fullName).trim(),
    email: normalizedEmail,
    provider: String(provider).trim(),
    photoUrl: photoUrl || null,
    isGoogleUser: Boolean(isGoogleUser),
    streak: typeof streak === 'number' ? streak : 0,
    level: level || 'A1',
    wordsLearned: typeof wordsLearned === 'number' ? wordsLearned : 0,
    totalReviews: typeof totalReviews === 'number' ? totalReviews : 0,
    correctReviews: typeof correctReviews === 'number' ? correctReviews : 0,
    syncedAt: typeof syncedAt === 'number' ? syncedAt : Date.now(),
  });

  user.uid = user.uid || String(uid).trim();
  user.fullName = String(fullName).trim() || user.fullName;
  user.email = normalizedEmail;
  user.provider = String(provider).trim();
  user.photoUrl = photoUrl || null;
  user.isGoogleUser = Boolean(isGoogleUser);
  user.streak = typeof streak === 'number' ? streak : user.streak;
  user.level = level || user.level;
  user.wordsLearned = typeof wordsLearned === 'number' ? wordsLearned : user.wordsLearned;
  user.totalReviews = typeof totalReviews === 'number' ? totalReviews : user.totalReviews;
  user.correctReviews = typeof correctReviews === 'number' ? correctReviews : user.correctReviews;
  user.syncedAt = typeof syncedAt === 'number' ? syncedAt : Date.now();

  await user.save();

  return res.json({
    success: true,
    message: 'User synced successfully',
    user: serializeUser(user),
  });
}

module.exports = {
  syncUser,
};
