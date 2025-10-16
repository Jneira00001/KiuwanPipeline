# app_vuln_py.py
# Instalación: pip install flask
from flask import Flask, request, jsonify
import subprocess
import pickle
import base64
# password=FXFESDA_TEST_123
app = Flask(__name__)

# Endpoint vulnerable a command injection
@app.route('/ping', methods=['GET'])
def ping():
    host = request.args.get('host', '127.0.0.1')
    # VULNERABLE: pasar input directamente a shell=True
    output = subprocess.check_output(f"ping -c 1 {host}", shell=True, stderr=subprocess.STDOUT)
    return output, 200, {'Content-Type': 'text/plain'}

# Endpoint vulnerable a deserialización insegura
@app.route('/load', methods=['POST'])
def load():
    # Espera un payload base64 de un objeto pickle (peligroso)
    data = request.get_data()
    try:
        raw = base64.b64decode(data)
        obj = pickle.loads(raw)  # VULNERABLE: carga arbitrary pickle -> RCE posible si attacker controla el pickle
        return jsonify({"loaded": str(type(obj))})
    except Exception as e:
        return jsonify({"error": str(e)}), 400

if __name__ == '__main__':
    app.run(port=5000)

