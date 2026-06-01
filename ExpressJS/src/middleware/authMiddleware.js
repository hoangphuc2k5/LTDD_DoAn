const jwt = require("jsonwebtoken");

function authenticateUser(req, res, next) {
  try {
    const authHeader = req.headers.authorization;

    if (!authHeader || !authHeader.startsWith("Bearer ")) {
      return res.status(401).json({
        success: false,
        message: "No token provided or invalid format",
      });
    }

    const token = authHeader.substring(7); // Remove "Bearer " prefix
    const decoded = jwt.verify(
      token,
      process.env.JWT_SECRET || "your_jwt_secret_key",
    );

    req.user = decoded; // Attach user info to request
    next();
  } catch (err) {
    if (err.name === "TokenExpiredError") {
      return res.status(401).json({
        success: false,
        message: "Token expired",
      });
    }
    if (err.name === "JsonWebTokenError") {
      return res.status(401).json({
        success: false,
        message: "Invalid token",
      });
    }
    res.status(401).json({
      success: false,
      message: "Authentication failed",
    });
  }
}

module.exports = { authenticateUser };
