const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
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
    syncedAt: user.syncedAt,
  };
}

function buildToken(user, jwtSecret) {
  if (!jwtSecret) {
    return null;
  }

  return jwt.sign(
    {
      id: user._id,
      uid: user.uid || user.email,
      email: user.email,
    },
    jwtSecret,
    { expiresIn: '30d' }
  );
}

async function register(req, res) {
  const { fullName, email, password } = req.body || {};
  const normalizedEmail = normalizeEmail(email);

  if (!fullName || !normalizedEmail || !password) {
    return res.status(400).json({
      success: false,
      message: 'fullName, email and password are required',
    });
  }

  const passwordHash = await bcrypt.hash(String(password).trim(), 10);
  const existing = await User.findOne({ email: normalizedEmail });
  const user = existing || new User({
    uid: normalizedEmail,
    fullName: String(fullName).trim(),
    email: normalizedEmail,
    provider: 'email',
    isGoogleUser: false,
  });

  user.uid = user.uid || normalizedEmail;
  user.fullName = String(fullName).trim() || user.fullName || normalizedEmail.split('@')[0];
  user.email = normalizedEmail;
  user.provider = 'email';
  user.isGoogleUser = false;
  user.passwordHash = passwordHash;
  user.passwordSalt = null;
  user.syncedAt = Date.now();

  await user.save();

  return res.status(existing ? 200 : 201).json({
    success: true,
    message: existing ? 'Tài khoản đã được cập nhật' : 'Đăng ký thành công',
    token: buildToken(user, process.env.JWT_SECRET),
    user: serializeUser(user),
  });
}

async function login(req, res) {
  const { email, password } = req.body || {};
  const normalizedEmail = normalizeEmail(email);

  if (!normalizedEmail || !password) {
    return res.status(400).json({
      success: false,
      message: 'email and password are required',
    });
  }

  const user = await User.findOne({ email: normalizedEmail });
  if (!user) {
    return res.status(404).json({
      success: false,
      message: 'Không tìm thấy tài khoản',
    });
  }

  if (!user.passwordHash) {
    return res.status(400).json({
      success: false,
      message: 'Tài khoản này chưa được đặt mật khẩu',
    });
  }

  const passwordMatches = await bcrypt.compare(String(password).trim(), user.passwordHash);
  if (!passwordMatches) {
    return res.status(401).json({
      success: false,
      message: 'Sai email hoặc mật khẩu',
    });
  }

  user.syncedAt = Date.now();
  await user.save();

  return res.json({
    success: true,
    message: 'Đăng nhập thành công',
    token: buildToken(user, process.env.JWT_SECRET),
    user: serializeUser(user),
  });
}

module.exports = {
  register,
  login,
};
