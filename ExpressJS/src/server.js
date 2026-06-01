require('dotenv').config();
const app = require('./app');
const { connectDb } = require('./config/db');

const port = process.env.PORT || 3001;
const host = process.env.HOST || '0.0.0.0';

async function start() {
  await connectDb(process.env.MONGODB_URI);
  app.listen(port, host, () => {
    console.log(`Tegram API listening on http://${host}:${port}`);
  });
}

start().catch((error) => {
  console.error('Failed to start backend:', error);
  process.exit(1);
});
