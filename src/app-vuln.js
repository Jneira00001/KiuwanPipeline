// app-vuln.js
// Instalación: npm init -y && npm i express sqlite3
const express = require('express');
const sqlite3 = require('sqlite3').verbose();
const app = express();
app.use(express.urlencoded({ extended: true }));
//
// DB de ejemplo
const db = new sqlite3.Database(':memory:');
db.serialize(() => {
  db.run("CREATE TABLE users(id INTEGER PRIMARY KEY, name TEXT, email TEXT)");
  db.run("INSERT INTO users(name,email) VALUES('Alice','alice@example.com')");
});

// Endpoint vulnerable: construye query con concatenación -> SQLi
app.post('/search', (req, res) => {
  const q = req.body.q || '';
  // VULNERABLE: concatenación directa en la query
  const sql = "SELECT id, name, email FROM users WHERE name LIKE '%" + q + "%'";
  db.all(sql, [], (err, rows) => {
    if (err) return res.status(500).send('DB error');
    // VULNERABLE: inserta directamente input en HTML -> reflected XSS
    let html = `<h1>Resultados para ${q}</h1><ul>`;
    rows.forEach(r => html += `<li>${r.name} - ${r.email}</li>`);
    html += '</ul>';
    res.send(html);
  });
});

app.listen(3000, () => console.log('Vulnerable app running on :3000'));

