const mongoose = require('mongoose');

async function connectDb(mongoUri) {
  const uri = mongoUri || process.env.MONGODB_URI || 'mongodb://127.0.0.1:27017/tegram';
  if (!mongoUri && !process.env.MONGODB_URI) {
    console.warn('MONGODB_URI not provided; falling back to', uri);
  }

  mongoose.set('strictQuery', true);
  await mongoose.connect(uri);
}

module.exports = { connectDb };
