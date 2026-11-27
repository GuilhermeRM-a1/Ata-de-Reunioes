#!/usr/bin/env bash
# Deploy do Ata de Reunioes numa EC2 Ubuntu. Rode de novo para atualizar.
# Uso: sudo ADMIN_EMAIL=admin@x.com ADMIN_SENHA=Senha1234 bash deploy.sh
set -euo pipefail
cd /tmp

REPO="https://github.com/GuilhermeRM-a1/Ata-de-Reunioes.git"
BRANCH="${BRANCH:-develop}"
HOST="${HOST:-$(curl -s https://checkip.amazonaws.com | tr -d '[:space:]')}"
SRC=/opt/reunioes-src
JAR=/opt/reunioes.jar
WEB=/var/www/reunioes
ENV_FILE=/etc/reunioes.env

# 1. Swap (instancia pequena nao aguenta o build sem isso)
if ! swapon --show | grep -q .; then
  fallocate -l 2G /swapfile && chmod 600 /swapfile && mkswap /swapfile && swapon /swapfile
fi

# 2. Pacotes
export DEBIAN_FRONTEND=noninteractive
apt-get update -y
apt-get install -y git curl nginx postgresql openjdk-17-jdk-headless
command -v node >/dev/null || { curl -fsSL https://deb.nodesource.com/setup_20.x | bash -; apt-get install -y nodejs; }

# 3. Segredos e banco (gerados so na primeira vez; nada fica no Git)
if [ ! -f "$ENV_FILE" ]; then
  DB_PASS=$(openssl rand -hex 16)
  sudo -u postgres psql -c "CREATE USER reunioes PASSWORD '$DB_PASS'" -c "CREATE DATABASE reunioes OWNER reunioes"
  touch "$ENV_FILE"; chmod 600 "$ENV_FILE"
  cat > "$ENV_FILE" <<EOF
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://localhost:5432/reunioes
DB_USER=reunioes
DB_PASSWORD=$DB_PASS
JWT_SECRET=$(openssl rand -base64 48 | tr -d '\n')
CORS_ALLOWED_ORIGINS=http://$HOST
EOF
fi
sed -i "s|^CORS_ALLOWED_ORIGINS=.*|CORS_ALLOWED_ORIGINS=http://$HOST|" "$ENV_FILE"

# 4. Codigo
if [ -d "$SRC/.git" ]; then git -C "$SRC" pull; else git clone -b "$BRANCH" "$REPO" "$SRC"; fi

# 5. Back-end: compila e registra como servico
cd "$SRC/back-reunioes"
chmod +x mvnw && ./mvnw -q -DskipTests package
cp target/*.jar "$JAR"
id reunioes &>/dev/null || useradd -r -s /usr/sbin/nologin reunioes
cat > /etc/systemd/system/reunioes.service <<EOF
[Unit]
After=network.target postgresql.service
[Service]
User=reunioes
EnvironmentFile=$ENV_FILE
ExecStart=/usr/bin/java -jar $JAR
Restart=on-failure
[Install]
WantedBy=multi-user.target
EOF
systemctl daemon-reload
systemctl enable reunioes
systemctl restart reunioes

# 6. Front-end: compila e copia para o Nginx
cd "$SRC/reunioes-front"
npm ci
npx ng build
mkdir -p "$WEB" && rm -rf "${WEB:?}"/*
cp -r dist/reunioes-front/browser/. "$WEB"/

# 7. Nginx: serve o Angular (fallback SPA) e repassa /api e /auth ao back
cat > /etc/nginx/sites-available/reunioes <<'EOF'
server {
    listen 80 default_server;
    root /var/www/reunioes;
    index index.html;
    location /api/  { proxy_pass http://127.0.0.1:8080; proxy_set_header Host $host; }
    location /auth/ { proxy_pass http://127.0.0.1:8080; proxy_set_header Host $host; }
    location /      { try_files $uri /index.html; }
}
EOF
rm -f /etc/nginx/sites-enabled/default
ln -sf /etc/nginx/sites-available/reunioes /etc/nginx/sites-enabled/reunioes
nginx -t && systemctl reload nginx

# 8. Admin inicial (opcional): cadastra pela API e promove no banco
if [ -n "${ADMIN_EMAIL:-}" ]; then
  for _ in $(seq 60); do curl -s -o /dev/null http://127.0.0.1:8080/ && break; sleep 2; done
  curl -s -o /dev/null -X POST http://127.0.0.1:8080/auth/register -H 'Content-Type: application/json' \
    -d "{\"nome\":\"Administrador\",\"email\":\"$ADMIN_EMAIL\",\"senha\":\"$ADMIN_SENHA\"}" || true
  sudo -u postgres psql -d reunioes -c "update colaborador set papel='ADMIN' where email='$ADMIN_EMAIL'"
fi

echo "Pronto: http://$HOST/"