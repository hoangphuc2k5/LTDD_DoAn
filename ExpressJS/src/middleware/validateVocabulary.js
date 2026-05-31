const { body, validationResult } = require('express-validator');

// Validation cho Thêm/Sửa Vocabulary cá nhân
const validateVocabulary = [
  body('word')
    .trim()
    .notEmpty()
    .withMessage('Word is required')
    .isLength({ max: 100 })
    .withMessage('Word cannot exceed 100 characters')
    .escape(),
  body('meaning')
    .trim()
    .notEmpty()
    .withMessage('Meaning is required')
    .isLength({ max: 200 })
    .withMessage('Meaning cannot exceed 200 characters')
    .escape(),
  body('pronunciation')
    .optional()
    .trim()
    .isLength({ max: 100 })
    .escape(),
  body('example')
    .optional()
    .trim()
    .isLength({ max: 500 })
    .escape(),
  body('topic')
    .optional()
    .trim()
    .isLength({ max: 50 })
    .escape()
];

const checkValidation = (req, res, next) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ errors: errors.array() });
  }
  next();
};

module.exports = {
  validateVocabulary,
  checkValidation
};
