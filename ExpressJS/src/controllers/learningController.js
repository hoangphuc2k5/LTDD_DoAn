const Flashcard = require('../models/Flashcard');
const { applySm2, normalizeRating } = require('../utils/sm2');
const mongoose = require('mongoose');

const seedCards = [
  {
    term: 'clarify',
    pronunciation: '/ˈkler.ə.faɪ/',
    meaning: 'làm rõ, giải thích rõ hơn',
    example: 'Could you clarify this sentence for me?',
    topic: 'Giao tiếp',
  },
  {
    term: 'retain',
    pronunciation: '/rɪˈteɪn/',
    meaning: 'giữ lại, ghi nhớ',
    example: 'Spaced repetition helps learners retain vocabulary longer.',
    topic: 'Học tập',
  },
  {
    term: 'consistent',
    pronunciation: '/kənˈsɪs.tənt/',
    meaning: 'đều đặn, nhất quán',
    example: 'A consistent review habit improves fluency.',
    topic: 'Thói quen học',
  },
  {
    term: 'prioritize',
    pronunciation: '/praɪˈɔːr.ə.taɪz/',
    meaning: 'ưu tiên',
    example: 'Prioritize the words that are due today.',
    topic: 'Lập kế hoạch',
  },
  {
    term: 'recall',
    pronunciation: '/rɪˈkɔːl/',
    meaning: 'nhớ lại',
    example: 'Try to recall the meaning before flipping the card.',
    topic: 'Ghi nhớ',
  },
  {
    term: 'elaborate',
    pronunciation: '/ɪˈlæb.ə.reɪt/',
    meaning: 'giải thích chi tiết, trình bày thêm',
    example: 'Can you elaborate on your answer?',
    topic: 'Giao tiếp',
  },
  {
    term: 'infer',
    pronunciation: '/ɪnˈfɝː/',
    meaning: 'suy ra, kết luận từ thông tin có sẵn',
    example: 'We can infer the meaning from context.',
    topic: 'Đọc hiểu',
  },
  {
    term: 'revise',
    pronunciation: '/rɪˈvaɪz/',
    meaning: 'ôn lại, chỉnh sửa',
    example: 'Revise your vocabulary before the quiz.',
    topic: 'Học tập',
  },
  {
    term: 'accomplish',
    pronunciation: '/əˈkɑːm.plɪʃ/',
    meaning: 'hoàn thành, đạt được',
    example: 'She accomplished her daily study goal.',
    topic: 'Mục tiêu',
  },
  {
    term: 'encounter',
    pronunciation: '/ɪnˈkaʊn.t̬ɚ/',
    meaning: 'gặp phải, tình cờ gặp',
    example: 'You may encounter this word in academic texts.',
    topic: 'Đọc hiểu',
  },
];

function getUserId(req) {
  return String(req.body?.userId || req.query?.userId || '').trim();
}

function requireUserId(req, res) {
  const userId = getUserId(req);

  if (!userId) {
    res.status(400).json({
      success: false,
      message: 'userId is required',
    });
    return null;
  }

  return userId;
}

function endOfToday() {
  const date = new Date();
  date.setHours(23, 59, 59, 999);
  return date;
}

function serializeFlashcard(card) {
  return {
    id: card._id.toString(),
    userId: card.userId,
    term: card.term,
    pronunciation: card.pronunciation,
    meaning: card.meaning,
    example: card.example,
    topic: card.topic,
    repetitions: card.repetitions,
    intervalDays: card.intervalDays,
    easeFactor: card.easeFactor,
    dueAt: card.dueAt,
    lastReviewedAt: card.lastReviewedAt,
    createdAt: card.createdAt,
    updatedAt: card.updatedAt,
  };
}

function requireValidCardId(cardId, res) {
  if (!mongoose.isValidObjectId(cardId)) {
    res.status(400).json({
      success: false,
      message: 'cardId is invalid',
    });
    return false;
  }

  return true;
}

function buildDailyPlan(cards) {
  const now = new Date();
  const dueLimit = endOfToday();
  const dueCards = cards.filter((card) => card.dueAt <= dueLimit);
  const newCards = cards.filter((card) => !card.lastReviewedAt);
  const overdueCards = cards.filter((card) => card.dueAt < now && card.lastReviewedAt);
  const nextDueCard = cards
    .filter((card) => card.dueAt > dueLimit)
    .sort((a, b) => a.dueAt.getTime() - b.dueAt.getTime())[0];

  return {
    totalCards: cards.length,
    newCards: newCards.length,
    dueCards: dueCards.length,
    overdueCards: overdueCards.length,
    estimatedMinutes: cards.length === 0
      ? 0
      : Math.max(5, dueCards.length * 2 + newCards.length * 3),
    nextDueAt: nextDueCard ? nextDueCard.dueAt : null,
  };
}

async function listFlashcards(req, res) {
  const userId = requireUserId(req, res);
  if (!userId) return;

  const cards = await Flashcard.find({ userId }).sort({ dueAt: 1, createdAt: 1 });

  res.json({
    success: true,
    flashcards: cards.map(serializeFlashcard),
  });
}

async function createFlashcard(req, res) {
  const userId = requireUserId(req, res);
  if (!userId) return;

  const { term, pronunciation, meaning, example, topic } = req.body || {};

  if (!term || !meaning) {
    return res.status(400).json({
      success: false,
      message: 'term and meaning are required',
    });
  }

  const card = await Flashcard.create({
    userId,
    term: String(term).trim(),
    pronunciation: pronunciation ? String(pronunciation).trim() : '',
    meaning: String(meaning).trim(),
    example: example ? String(example).trim() : '',
    topic: topic ? String(topic).trim() : 'General',
  });

  return res.status(201).json({
    success: true,
    flashcard: serializeFlashcard(card),
  });
}

async function updateFlashcard(req, res) {
  const userId = requireUserId(req, res);
  if (!userId) return;
  if (!requireValidCardId(req.params.id, res)) return;

  const allowedFields = ['term', 'pronunciation', 'meaning', 'example', 'topic'];
  const updates = {};

  allowedFields.forEach((field) => {
    if (req.body?.[field] !== undefined) {
      updates[field] = String(req.body[field]).trim();
    }
  });

  if (updates.term === '' || updates.meaning === '') {
    return res.status(400).json({
      success: false,
      message: 'term and meaning cannot be empty',
    });
  }

  const card = await Flashcard.findOneAndUpdate(
    { _id: req.params.id, userId },
    updates,
    { new: true }
  );

  if (!card) {
    return res.status(404).json({
      success: false,
      message: 'Flashcard not found',
    });
  }

  return res.json({
    success: true,
    flashcard: serializeFlashcard(card),
  });
}

async function deleteFlashcard(req, res) {
  const userId = requireUserId(req, res);
  if (!userId) return;
  if (!requireValidCardId(req.params.id, res)) return;

  const card = await Flashcard.findOneAndDelete({ _id: req.params.id, userId });

  if (!card) {
    return res.status(404).json({
      success: false,
      message: 'Flashcard not found',
    });
  }

  return res.json({
    success: true,
    message: 'Flashcard deleted',
  });
}

async function listDueReview(req, res) {
  const userId = requireUserId(req, res);
  if (!userId) return;

  const cards = await Flashcard.find({
    userId,
    dueAt: { $lte: endOfToday() },
  }).sort({ dueAt: 1, createdAt: 1 });

  return res.json({
    success: true,
    flashcards: cards.map(serializeFlashcard),
  });
}

async function submitReview(req, res) {
  const userId = requireUserId(req, res);
  if (!userId) return;

  const { cardId, rating } = req.body || {};
  const normalizedRating = normalizeRating(rating);

  if (!cardId || !normalizedRating) {
    return res.status(400).json({
      success: false,
      message: 'cardId and rating Again/Hard/Good/Easy are required',
    });
  }
  if (!requireValidCardId(cardId, res)) return;

  const card = await Flashcard.findOne({ _id: cardId, userId });

  if (!card) {
    return res.status(404).json({
      success: false,
      message: 'Flashcard not found',
    });
  }

  const nextSchedule = applySm2(card, normalizedRating.label);
  Object.assign(card, nextSchedule);
  await card.save();

  return res.json({
    success: true,
    rating: normalizedRating.label,
    flashcard: serializeFlashcard(card),
    schedule: {
      repetitions: card.repetitions,
      intervalDays: card.intervalDays,
      easeFactor: card.easeFactor,
      dueAt: card.dueAt,
      lastReviewedAt: card.lastReviewedAt,
    },
  });
}

async function getDailyPlan(req, res) {
  const userId = requireUserId(req, res);
  if (!userId) return;

  const cards = await Flashcard.find({ userId }).sort({ dueAt: 1, createdAt: 1 });
  const dueCards = cards.filter((card) => card.dueAt <= endOfToday());
  const upcomingCards = cards
    .filter((card) => card.dueAt > endOfToday())
    .sort((a, b) => a.dueAt.getTime() - b.dueAt.getTime())
    .slice(0, 5);

  return res.json({
    success: true,
    plan: buildDailyPlan(cards),
    dueCards: dueCards.map(serializeFlashcard),
    upcomingCards: upcomingCards.map(serializeFlashcard),
  });
}

async function seedFlashcards(req, res) {
  const userId = requireUserId(req, res);
  if (!userId) return;

  const existingCount = await Flashcard.countDocuments({ userId });

  if (existingCount > 0 && !req.body?.force) {
    const cards = await Flashcard.find({ userId }).sort({ dueAt: 1, createdAt: 1 });
    return res.json({
      success: true,
      message: 'Flashcards already exist',
      flashcards: cards.map(serializeFlashcard),
    });
  }

  if (req.body?.force) {
    await Flashcard.deleteMany({ userId });
  }

  const cards = await Flashcard.insertMany(seedCards.map((card) => ({
    ...card,
    userId,
  })));

  return res.status(201).json({
    success: true,
    flashcards: cards.map(serializeFlashcard),
  });
}

module.exports = {
  listFlashcards,
  createFlashcard,
  updateFlashcard,
  deleteFlashcard,
  listDueReview,
  submitReview,
  getDailyPlan,
  seedFlashcards,
};
