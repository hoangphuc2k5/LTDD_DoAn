const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const dotenv = require('dotenv');
const path = require('path');

// Load env vars
dotenv.config({ path: path.join(__dirname, '../../.env') });

const User = require('../models/User');
const Vocabulary = require('../models/Vocabulary');
const PublicVocabulary = require('../models/PublicVocabulary');
const PublicWord = require('../models/PublicWord');

const connectDB = async () => {
  try {
    const conn = await mongoose.connect(process.env.MONGODB_URI);
    console.log(`MongoDB Connected: ${conn.connection.host}`);
  } catch (error) {
    console.error(`Error: ${error.message}`);
    process.exit(1);
  }
};

const seedData = async () => {
  try {
    await connectDB();

    // 1. Clear existing data
    await User.deleteMany();
    await Vocabulary.deleteMany();
    await PublicVocabulary.deleteMany();
    await PublicWord.deleteMany();

    console.log('Cleared existing data.');

    // 2. Create Test User
    const passwordHash = await bcrypt.hash('123456', 10);
    const testUser = await User.create({
      uid: 'testuser@gmail.com',
      fullName: 'Test User',
      email: 'testuser@gmail.com',
      passwordHash: passwordHash,
      provider: 'email'
    });

    console.log('Created test user: testuser@gmail.com (password: 123456)');

    // 3. Create Personal Vocabulary for Test User
    await Vocabulary.create([
      { userId: testUser._id, word: 'apple', meaning: 'Quả táo', pronunciation: 'ˈæp.əl', example: 'I eat an apple every day.', topic: 'Fruit' },
      { userId: testUser._id, word: 'hello', meaning: 'Xin chào', pronunciation: 'həˈloʊ', example: 'Hello, how are you?', topic: 'Greeting' },
      { userId: testUser._id, word: 'book', meaning: 'Quyển sách', pronunciation: 'bʊk', example: 'I am reading a book.', topic: 'Education' },
      { userId: testUser._id, word: 'computer', meaning: 'Máy tính', pronunciation: 'kəmˈpjuː.t̬ɚ', example: 'I work on my computer.', topic: 'Technology' },
      { userId: testUser._id, word: 'travel', meaning: 'Du lịch', pronunciation: 'ˈtræv.əl', example: 'I love to travel the world.', topic: 'Hobby' },
      { userId: testUser._id, word: 'success', meaning: 'Sự thành công', pronunciation: 'səkˈses', example: 'Hard work leads to success.', topic: 'Motivation' },
      { userId: testUser._id, word: 'beautiful', meaning: 'Xinh đẹp', pronunciation: 'ˈbjuː.t̬ə.fəl', example: 'She is a beautiful girl.', topic: 'Appearance' },
      { userId: testUser._id, word: 'friend', meaning: 'Bạn bè', pronunciation: 'frend', example: 'He is my best friend.', topic: 'Relationship' },
      { userId: testUser._id, word: 'money', meaning: 'Tiền bạc', pronunciation: 'ˈmʌn.i', example: 'Money cannot buy happiness.', topic: 'Finance' },
      { userId: testUser._id, word: 'health', meaning: 'Sức khỏe', pronunciation: 'helθ', example: 'Health is wealth.', topic: 'Life' }
    ]);

    console.log('Created personal vocabularies.');

    // 4. Create Public Vocabulary Collection
    const toeicCollection = await PublicVocabulary.create({
      title: 'TOEIC Essential Words',
      topic: 'TOEIC',
      level: 'Intermediate',
      totalWords: 3,
      downloads: 100
    });

    const ieltsCollection = await PublicVocabulary.create({
      title: 'IELTS Academic',
      topic: 'IELTS',
      level: 'Advanced',
      totalWords: 2,
      downloads: 50
    });

    // 5. Create Public Words
    await PublicWord.create([
      {
        publicVocabularyId: toeicCollection._id,
        word: 'negotiation',
        meaning: 'Sự đàm phán',
        pronunciation: 'nɪˌɡoʊ.ʃiˈeɪ.ʃən',
        example: 'The negotiation was successful.'
      },
      {
        publicVocabularyId: toeicCollection._id,
        word: 'contract',
        meaning: 'Hợp đồng',
        pronunciation: 'ˈkɑːn.trækt',
        example: 'Please sign the contract.'
      },
      {
        publicVocabularyId: toeicCollection._id,
        word: 'revenue',
        meaning: 'Doanh thu',
        pronunciation: 'ˈrev.ə.nuː',
        example: 'The company revenue increased.'
      },
      {
        publicVocabularyId: ieltsCollection._id,
        word: 'ubiquitous',
        meaning: 'Có mặt ở khắp nơi',
        pronunciation: 'juːˈbɪk.wə.t̬əs',
        example: 'Computers are increasingly ubiquitous.'
      },
      {
        publicVocabularyId: ieltsCollection._id,
        word: 'mitigate',
        meaning: 'Làm giảm nhẹ',
        pronunciation: 'ˈmɪt̬.ə.ɡeɪt',
        example: 'We need to mitigate the effects of climate change.'
      }
    ]);

    console.log('Created public vocabularies and words.');

    console.log('Data seeding completed successfully!');
    process.exit();

  } catch (error) {
    console.error(`Error with seeding: ${error}`);
    process.exit(1);
  }
};

seedData();
