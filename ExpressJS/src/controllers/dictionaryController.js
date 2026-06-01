const axios = require('axios');

// @desc    Lookup word from Free Dictionary API
// @route   GET /api/dictionary/lookup/:word
// @access  Private
const lookupWord = async (req, res) => {
  try {
    const response = await axios.get(`https://api.dictionaryapi.dev/api/v2/entries/en/${req.params.word}`);
    res.json(response.data);
  } catch (error) {
    if (error.response && error.response.status === 404) {
      return res.status(404).json({ message: 'Word not found' });
    }
    res.status(500).json({ message: 'Failed to fetch from dictionary' });
  }
};

// @desc    Get word suggestions from Datamuse API
// @route   GET /api/dictionary/suggest?q=...
// @access  Private
const suggestWord = async (req, res) => {
  try {
    const query = req.query.q;
    if (!query) {
      return res.status(400).json({ message: 'Query parameter q is required' });
    }
    const response = await axios.get(`https://api.datamuse.com/words?ml=${query}&max=10`);
    res.json(response.data);
  } catch (error) {
    res.status(500).json({ message: 'Failed to fetch suggestions' });
  }
};

module.exports = {
  lookupWord,
  suggestWord
};
