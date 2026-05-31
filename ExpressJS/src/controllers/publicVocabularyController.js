const PublicVocabulary = require('../models/PublicVocabulary');
const PublicWord = require('../models/PublicWord');
const Vocabulary = require('../models/Vocabulary');

// @desc    Get public vocabularies (with pagination, search, topic filter)
// @route   GET /api/public-vocabulary
// @access  Private
const getPublicVocabularies = async (req, res) => {
  try {
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 20;
    const skip = (page - 1) * limit;

    const query = {};
    if (req.query.q) {
      query.$text = { $search: req.query.q };
    }
    if (req.query.topic) {
      query.topic = req.query.topic;
    }

    let sort = { downloads: -1 }; // Default popular
    if (req.query.sort === 'newest') sort = { createdAt: -1 };

    const collections = await PublicVocabulary.find(query)
      .sort(sort)
      .skip(skip)
      .limit(limit);

    const total = await PublicVocabulary.countDocuments(query);

    res.json({
      data: collections,
      page,
      pages: Math.ceil(total / limit),
      total
    });
  } catch (error) {
    res.status(500).json({ message: 'Server Error' });
  }
};

// @desc    Get details of a public vocabulary collection
// @route   GET /api/public-vocabulary/:id
// @access  Private
const getPublicVocabularyDetails = async (req, res) => {
  try {
    const collection = await PublicVocabulary.findById(req.params.id);
    if (!collection) {
      return res.status(404).json({ message: 'Collection not found' });
    }

    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 50;
    const skip = (page - 1) * limit;

    const words = await PublicWord.find({ publicVocabularyId: req.params.id })
      .skip(skip)
      .limit(limit);

    const totalWords = await PublicWord.countDocuments({ publicVocabularyId: req.params.id });

    res.json({
      collection,
      words: {
        data: words,
        page,
        pages: Math.ceil(totalWords / limit),
        total: totalWords
      }
    });
  } catch (error) {
    res.status(500).json({ message: 'Server Error' });
  }
};

// @desc    Save public vocabulary words to personal vocabulary
// @route   POST /api/public-vocabulary/:id/save
// @access  Private
const saveToPersonal = async (req, res) => {
  try {
    const collection = await PublicVocabulary.findById(req.params.id);
    if (!collection) {
      return res.status(404).json({ message: 'Collection not found' });
    }

    const words = await PublicWord.find({ publicVocabularyId: req.params.id });
    
    let inserted = 0;
    let skipped = 0;

    for (let w of words) {
      const existing = await Vocabulary.findOne({ userId: req.user._id, word: w.word.toLowerCase() });
      if (!existing) {
        await Vocabulary.create({
          userId: req.user._id,
          word: w.word,
          meaning: w.meaning,
          pronunciation: w.pronunciation,
          example: w.example,
          topic: collection.topic,
          isPublic: false
        });
        inserted++;
      } else {
        skipped++;
      }
    }

    // Increment download count
    collection.downloads += 1;
    await collection.save();

    res.json({
      message: 'Saved to personal vocabulary successfully',
      inserted,
      skipped
    });
  } catch (error) {
    res.status(500).json({ message: 'Server Error' });
  }
};

module.exports = {
  getPublicVocabularies,
  getPublicVocabularyDetails,
  saveToPersonal
};
