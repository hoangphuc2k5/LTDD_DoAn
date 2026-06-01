const User = require("../models/User");

function normalizeEmail(email) {
  return String(email || "")
    .trim()
    .toLowerCase();
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
      message: "uid, fullName, email and provider are required",
    });
  }

  const existing = await User.findOne({
    $or: [{ uid: String(uid).trim() }, { email: normalizedEmail }],
  });

  const user =
    existing ||
    new User({
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
      syncedAt: typeof syncedAt === "number" ? syncedAt : Date.now(),
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
  user.syncedAt = typeof syncedAt === "number" ? syncedAt : Date.now();

  await user.save();

  return res.json({
    success: true,
    message: "User synced successfully",
    user: serializeUser(user),
  });
}

async function updateUserProfile(req, res) {
  const { uid } = req.params;
  const { fullName, photoUrl } = req.body || {};

  // Validate: uid param is required
  if (!uid) {
    return res.status(400).json({
      success: false,
      message: "User ID (uid) is required",
    });
  }

  // Validate: at least one field to update
  if (!fullName && !photoUrl) {
    return res.status(400).json({
      success: false,
      message: "At least one field (fullName or photoUrl) must be provided",
    });
  }

  // Validate: fullName must not be empty if provided
  if (fullName !== undefined && fullName !== null) {
    const trimmedName = String(fullName).trim();
    if (trimmedName.length === 0) {
      return res.status(400).json({
        success: false,
        message: "fullName cannot be empty",
      });
    }
    if (trimmedName.length < 2 || trimmedName.length > 100) {
      return res.status(400).json({
        success: false,
        message: "fullName must be between 2 and 100 characters",
      });
    }
  }

  // Validate: photoUrl must be valid URL if provided
  if (photoUrl !== undefined && photoUrl !== null && photoUrl !== "") {
    const urlRegex = /^https?:\/\/.+/i;
    if (!urlRegex.test(photoUrl)) {
      return res.status(400).json({
        success: false,
        message: "photoUrl must be a valid URL or null",
      });
    }
  }

  try {
    // Find user by uid
    const user = await User.findOne({ uid: String(uid).trim() });

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "User not found",
      });
    }

    // Update fields
    if (fullName !== undefined && fullName !== null) {
      user.fullName = String(fullName).trim();
    }
    if (photoUrl !== undefined) {
      user.photoUrl = photoUrl === "" || photoUrl === null ? null : photoUrl;
    }

    // Save to database
    await user.save();

    return res.json({
      success: true,
      message: "Profile updated successfully",
      user: serializeUser(user),
    });
  } catch (err) {
    // Handle duplicate email error (should not happen in this update, but just in case)
    if (err.code === 11000 && err.keyPattern.email) {
      return res.status(409).json({
        success: false,
        message: "Email already exists",
      });
    }
    throw err;
  }
}

module.exports = {
  syncUser,
  updateUserProfile,
};
