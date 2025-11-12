// server.ts
import express from 'express';
import { Pool } from 'pg';
import bodyParser from 'body-parser';

const app = express();
app.use(bodyParser.json());

const pool = new Pool({
  user: 'postgres',
  host: 'localhost',
  database: 'bd_colegios',
  password: 'tu_contraseña',
  port: 5432,
});

// =============== CRUD ESTUDIANTES ===============

// CREATE
app.post('/students', async (req, res) => {
  try {
    const { full_name, user_id, tenant_id } = req.body;
    const result = await pool.query(
      'INSERT INTO edu.students (full_name, user_id, tenant_id) VALUES ($1, $2, $3) RETURNING *',
      [full_name, user_id, tenant_id]
    );
    res.json(result.rows[0]);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// READ
app.get('/students', async (_, res) => {
  const result = await pool.query('SELECT * FROM edu.students ORDER BY id');
  res.json(result.rows);
});

// UPDATE
app.put('/students/:id', async (req, res) => {
  const { id } = req.params;
  const { full_name } = req.body;
  const result = await pool.query(
    'UPDATE edu.students SET full_name = $1, updated_at = NOW() WHERE id = $2 RETURNING *',
    [full_name, id]
  );
  res.json(result.rows[0]);
});

// DELETE
app.delete('/students/:id', async (req, res) => {
  const { id } = req.params;
  await pool.query('DELETE FROM edu.students WHERE id = $1', [id]);
  res.sendStatus(204);
});

// Export dataset view
app.get('/ml/export', async (_, res) => {
  await pool.query('REFRESH MATERIALIZED VIEW CONCURRENTLY ml.students_dataset_mv');
  const result = await pool.query('SELECT * FROM ml.students_dataset_mv');
  res.json(result.rows);
});

app.listen(4000, () => console.log('Server running on port 4000'));
