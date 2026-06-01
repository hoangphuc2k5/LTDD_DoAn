const RATINGS = {
  Again: 1,
  Hard: 3,
  Good: 4,
  Easy: 5,
};

const MIN_PASSING_QUALITY = 3;
const MIN_EASE_FACTOR = 1.3;
const MILLIS_PER_DAY = 24 * 60 * 60 * 1000;

function normalizeRating(rating) {
  const normalized = String(rating || '').trim();

  if (!Object.prototype.hasOwnProperty.call(RATINGS, normalized)) {
    return null;
  }

  return {
    label: normalized,
    quality: RATINGS[normalized],
  };
}

function calculateEaseFactor(currentEaseFactor, quality) {
  const qualityGap = 5 - quality;
  const nextEaseFactor = currentEaseFactor + (0.1 - qualityGap * (0.08 + qualityGap * 0.02));
  return Math.max(Number(nextEaseFactor.toFixed(2)), MIN_EASE_FACTOR);
}

function addDays(date, days) {
  return new Date(date.getTime() + days * MILLIS_PER_DAY);
}

function applySm2(card, ratingLabel, reviewedAt = new Date()) {
  const rating = normalizeRating(ratingLabel);

  if (!rating) {
    throw new Error('rating must be one of Again, Hard, Good or Easy');
  }

  const nextEaseFactor = calculateEaseFactor(card.easeFactor || 2.5, rating.quality);

  if (rating.quality < MIN_PASSING_QUALITY) {
    return {
      repetitions: 0,
      intervalDays: 0,
      easeFactor: nextEaseFactor,
      dueAt: reviewedAt,
      lastReviewedAt: reviewedAt,
    };
  }

  const nextRepetitions = (card.repetitions || 0) + 1;
  const nextIntervalDays = nextRepetitions === 1
    ? 1
    : nextRepetitions === 2
      ? 6
      : Math.max(1, Math.round((card.intervalDays || 1) * nextEaseFactor));

  return {
    repetitions: nextRepetitions,
    intervalDays: nextIntervalDays,
    easeFactor: nextEaseFactor,
    dueAt: addDays(reviewedAt, nextIntervalDays),
    lastReviewedAt: reviewedAt,
  };
}

module.exports = {
  RATINGS,
  applySm2,
  normalizeRating,
};
