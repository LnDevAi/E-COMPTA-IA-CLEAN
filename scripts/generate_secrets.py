#!/usr/bin/env python3
"""
Script de génération automatique de secrets forts pour E-COMPTA-IA
- Génère des mots de passe et clés (DB, Redis, JWT, Admin, Backup, etc.)
- Sauvegarde les secrets dans le dossier ./secrets/ pour usage Docker
"""
import secrets
import string
import os

SECRETS = {
    'db_password.txt': 32,
    'redis_password.txt': 32,
    'jwt_secret.txt': 64,
    'admin_password.txt': 24,
    'backup_encryption_key.txt': 32,
    'ssl_keystore_password.txt': 32,
    'ssl_key_password.txt': 32,
}

os.makedirs('secrets', exist_ok=True)

charset = string.ascii_letters + string.digits + string.punctuation

def generate_secret(length):
    return ''.join(secrets.choice(charset) for _ in range(length))

for filename, length in SECRETS.items():
    secret = generate_secret(length)
    with open(os.path.join('secrets', filename), 'w') as f:
        f.write(secret)
    print(f"Secret généré : secrets/{filename}")

print("\nTous les secrets ont été générés dans ./secrets/ (à utiliser avec Docker secrets ou Vault)")
