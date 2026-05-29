require('dotenv').config();
const app = require('./app');
const { connectDb } = require('./config/db');

const port = process.env.PORT || 3001;

async function start() {
  await connectDb(process.env.MONGODB_URI);
  app.listen(port, () => {
    console.log(`Tegram API listening on port ${port}`);
  });
}

start().catch((error) => {
  console.error('Failed to start backend:', error);
  process.exit(1);
});