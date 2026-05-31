const express = require('express');
const cors = require('cors');
const morgan = require('morgan');
const authRoutes = require('./routes/authRoutes');
const userRoutes = require('./routes/userRoutes');
const vocabularyRoutes = require('./routes/vocabularyRoutes');
const publicVocabularyRoutes = require('./routes/publicVocabularyRoutes');
const dictionaryRoutes = require('./routes/dictionaryRoutes');
const { notFound, errorHandler } = require('./middleware/errorHandler');

const app = express();

app.use(cors({
  origin: process.env.CLIENT_ORIGIN === '*' ? true : process.env.CLIENT_ORIGIN,
}));
app.use(express.json({ limit: '2mb' }));
app.use(express.urlencoded({ extended: true }));
app.use(morgan('dev'));

app.get('/health', (req, res) => {
  res.json({ success: true, message: 'Tegram API is running' });
});

app.use('/auth', authRoutes);
app.use('/users', userRoutes);
app.use('/api/vocabulary', vocabularyRoutes);
app.use('/api/public-vocabulary', publicVocabularyRoutes);
app.use('/api/dictionary', dictionaryRoutes);

app.use(notFound);
app.use(errorHandler);

module.exports = app;