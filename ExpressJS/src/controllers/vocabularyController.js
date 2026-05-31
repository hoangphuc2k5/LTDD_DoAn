const Vocabulary = require('../models/Vocabulary');
const fs = require('fs');
const fastcsv = require('fast-csv');
const { Parser } = require('json2csv');

// @desc    Get user's personal vocabularies
// @route   GET /api/vocabulary
// @access  Private
const getVocabularies = async (req, res) => {
  try {
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 20;
    const skip = (page - 1) * limit;
    
    const query = { userId: req.user._id };
    if (req.query.q) {
      query.$or = [
        { word: { $regex: req.query.q, $options: 'i' } },
        { meaning: { $regex: req.query.q, $options: 'i' } },
        { topic: { $regex: req.query.q, $options: 'i' } }
      ];
    }

    const vocabularies = await Vocabulary.find(query)
      .sort({ createdAt: -1 })
      .skip(skip)
      .limit(limit);

    const total = await Vocabulary.countDocuments(query);

    res.json({
      data: vocabularies,
      page,
      pages: Math.ceil(total / limit),
      total
    });
  } catch (error) {
    res.status(500).json({ message: 'Server Error' });
  }
};

// @desc    Create a new vocabulary
// @route   POST /api/vocabulary
// @access  Private
const createVocabulary = async (req, res) => {
  try {
    const { word, meaning, pronunciation, example, topic, isPublic } = req.body;

    // Check if word already exists for this user
    const existing = await Vocabulary.findOne({ userId: req.user._id, word: word.toLowerCase() });
    if (existing) {
      return res.status(400).json({ message: 'Vocabulary already exists' });
    }

    const vocabulary = await Vocabulary.create({
      userId: req.user._id,
      word,
      meaning,
      pronunciation,
      example,
      topic,
      isPublic
    });

    res.status(201).json(vocabulary);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// @desc    Update vocabulary
// @route   PUT /api/vocabulary/:id
// @access  Private
const updateVocabulary = async (req, res) => {
  try {
    const vocabulary = await Vocabulary.findById(req.params.id);

    if (!vocabulary) {
      return res.status(404).json({ message: 'Vocabulary not found' });
    }

    if (vocabulary.userId.toString() !== req.user._id.toString()) {
      return res.status(401).json({ message: 'User not authorized' });
    }

    const updatedVocabulary = await Vocabulary.findByIdAndUpdate(
      req.params.id,
      req.body,
      { new: true }
    );

    res.json(updatedVocabulary);
  } catch (error) {
    res.status(500).json({ message: 'Server Error' });
  }
};

// @desc    Delete vocabulary
// @route   DELETE /api/vocabulary/:id
// @access  Private
const deleteVocabulary = async (req, res) => {
  try {
    const vocabulary = await Vocabulary.findById(req.params.id);

    if (!vocabulary) {
      return res.status(404).json({ message: 'Vocabulary not found' });
    }

    if (vocabulary.userId.toString() !== req.user._id.toString()) {
      return res.status(401).json({ message: 'User not authorized' });
    }

    await vocabulary.deleteOne();
    res.json({ message: 'Vocabulary removed' });
  } catch (error) {
    res.status(500).json({ message: 'Server Error' });
  }
};

// @desc    Import CSV
// @route   POST /api/vocabulary/import
// @access  Private
const importCSV = async (req, res) => {
  if (!req.file) {
    return res.status(400).json({ message: 'Please upload a CSV file' });
  }

  const vocabularies = [];
  const errors = [];
  let skipped = 0;

  try {
    const csvString = req.file.buffer.toString('utf8');
    fastcsv.parseString(csvString, { headers: true, ignoreEmpty: true })
      .on('data', (row) => {
        if (!row.word || !row.meaning) {
          errors.push(`Row missing word or meaning: ${JSON.stringify(row)}`);
          return;
        }
        vocabularies.push({
          userId: req.user._id,
          word: row.word.trim().toLowerCase(),
          meaning: row.meaning.trim(),
          pronunciation: row.pronunciation ? row.pronunciation.trim() : '',
          example: row.example ? row.example.trim() : '',
          topic: row.topic ? row.topic.trim() : 'Uncategorized'
        });
      })
      .on('end', async () => {
        let inserted = 0;
        for (let item of vocabularies) {
          try {
            const existing = await Vocabulary.findOne({ userId: item.userId, word: item.word });
            if (!existing) {
              await Vocabulary.create(item);
              inserted++;
            } else {
              skipped++;
            }
          } catch (err) {
            errors.push(`Failed to insert word: ${item.word}`);
          }
        }
        res.json({
          message: 'CSV Import Completed',
          inserted,
          skipped,
          errors
        });
      });
  } catch (error) {
    res.status(500).json({ message: 'Failed to process CSV' });
  }
};

// @desc    Export CSV
// @route   GET /api/vocabulary/export
// @access  Private
const exportCSV = async (req, res) => {
  try {
    const vocabularies = await Vocabulary.find({ userId: req.user._id }, '-_id word meaning pronunciation example topic');
    
    if (!vocabularies || vocabularies.length === 0) {
      return res.status(404).json({ message: 'No vocabularies found' });
    }

    // Convert Mongoose documents to plain objects
    const data = vocabularies.map(v => v.toObject());

    const fields = ['word', 'meaning', 'pronunciation', 'example', 'topic'];
    const json2csvParser = new Parser({ fields });
    const csv = json2csvParser.parse(data);

    res.header('Content-Type', 'text/csv');
    res.attachment('vocabularies.csv');
    return res.send(csv);
  } catch (error) {
    res.status(500).json({ message: 'Failed to export CSV' });
  }
};

module.exports = {
  getVocabularies,
  createVocabulary,
  updateVocabulary,
  deleteVocabulary,
  importCSV,
  exportCSV
};
