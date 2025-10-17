<?php
// src/insecure_nosql.php
// Simulación de consulta NoSQL vulnerable (propósito demo local)

// Simulamos una "colección" con array para no requerir DB real
$users = [
    ['username' => 'alice', 'role' => 'admin'],
    ['username' => 'bob',   'role' => 'user'],
    ['username' => 'carl',  'role' => 'user'],
];

// Input no validado desde GET
$q = isset($_GET['user']) ? $_GET['user'] : '';
//password=1233rfmd
// *** VULNERABLE: se "evalúa" o se interpreta la entrada tal cual como filtro NoSQL ***
// Simulamos que el desarrollador hace algo como: $filter = json_decode($_GET['user'], true);
$filter = null;
if (!empty($q)) {
    // Intento de convertir JSON proporcionado por usuario a filtro (peligroso)
    $filter = json_decode($q, true);
    if ($filter === null) {
        // si no es JSON, interpretamos como búsqueda por username exacto
        $filter = ['username' => $q];
    }
} else {
    $filter = [];
}

// Función de filtrado simplificada (simula búsqueda NoSQL)
function match($item, $filter) {
    foreach ($filter as $k => $v) {
        if (!isset($item[$k]) || $item[$k] != $v) {
            return false;
        }
    }
    return true;
}

$results = [];
foreach ($users as $u) {
    if (match($u, $filter)) $results[] = $u;
}

header('Content-Type: application/json');
echo json_encode($results);

