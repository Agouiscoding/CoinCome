# Deploy Instructions

> If you have any deployment questions, or you need to upload your SSH keypair to the instance, please contact **[zg2921@nyu.edu](mailto:zg2921@nyu.edu)**.

---

## Table of Contents

* [Overview](#overview)
* [Part 2: Deployment on Server](#part-2-deployment-on-server)

  * [2a. Database Hosting (OCI MySQL)](#2a-database-hosting-oci-mysql)
  * [2b. Application Hosting (OCI Compute Instance)](#2b-application-hosting-oci-compute-instance)
  * [2c. Server Deployment Steps](#2c-server-deployment-steps)

    * [Step 1: Build Backend & Frontend](#step-1-build-backend--frontend)
    * [Step 2: Prepare Compute Instance](#step-2-prepare-compute-instance)
    * [Step 3: Directory Layout & File Storage](#step-3-directory-layout--file-storage)
    * [Step 4: Environment Variables & DB Config](#step-4-environment-variables--db-config)
    * [Step 5: Run Spring Boot Backend](#step-5-run-spring-boot-backend)
    * [Step 6: Deploy Vue 3 Frontend with Nginx](#step-6-deploy-vue-3-frontend-with-nginx)
    * [Step 7: Enable HTTPS (Let’s Encrypt)](#step-7-enable-https-lets-encrypt)
* [Part 3: Local Development](#part-3-local-development)

  * [3a. Access Remote MySQL via SSH Tunnel](#3a-access-remote-mysql-via-ssh-tunnel)
  * [3b. Run Spring Boot Locally](#3b-run-spring-boot-locally)
  * [3c. Run Python Scripts Locally](#3c-run-python-scripts-locally)
  * [3d. Run Vue 3 Locally](#3d-run-vue-3-locally)
  * [3e. Full End-to-End Local Test](#3e-full-end-to-end-local-test)

---

# Overview

This document explains how to deploy the full **CoinCome** platform:

* Backend (Spring Boot + MyBatis-Plus)
* Frontend (Vue 3 + Vite + Nginx)
* Database (OCI MySQL DB System)
* Python scripts (via Java subprocess)
* Local development workflow

---

# Part 2: Deployment on Server

## 2a. Database Hosting (OCI MySQL)

The relational database is hosted on **Oracle Cloud MySQL DB System**.

* **Subnet**: `10.0.1.0/24`
* **DB private IP**: `10.0.1.116`
* **DB type**: OCI MySQL 9.5
* **DB name**: `bitcome`
* **Port**: `3306` (only accessible inside subnet)

No public IP is assigned.
The Spring Boot backend connects through environment variables:

```
DB_HOST     DB_PORT     DB_NAME
DB_USERNAME DB_PASSWORD
```

JDBC URL example:

```
jdbc:mysql://10.0.1.116:3306/bitcome
```

To access remotely (e.g., DataGrip), use SSH tunnel.

---

## 2b. Application Hosting (OCI Compute Instance)

Both backend and frontend are hosted on the same OCI compute instance:

### Compute Instance

* Private IP: `10.0.1.97`
* Public IP: `150.136.242.115`
* OS: Ubuntu 64-bit

### Backend Stack

* Java 17 (OpenJDK)
* Spring Boot JAR (`coincome.jar`)
* MyBatis-Plus
* Python3 + pandas + requests
* Systemd service (`coincome.service`)

### Frontend Stack

* Vue 3 + Vite
* Built static files served by Nginx from `/var/www/coincome`
* Nginx reverse proxy → Spring Boot (localhost:8080)

---

## 2c. Server Deployment Steps

---

## Step 1: Build Backend & Frontend

### 1. Clone the repo

```bash
git clone https://github.com/Agouiscoding/CoinCome.git
```

### 2. Build backend

```bash
cd CoinCome/coincome
mvn clean package -DskipTests
```

### 3. Build frontend

Edit `.env`:

```env
VITE_API_URL=https://150-136-242-115.sslip.io/api
VITE_GOOGLE_CLIENT_ID=xxxx.apps.googleusercontent.com
VITE_GOOGLE_REDIRECT_URI=https://150-136-242-115.sslip.io/auth/google/callback
```

Build:

```bash
npm install
npm run build
```

---

## Step 2: Prepare Compute Instance

Install dependencies:

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y openjdk-17-jre-headless python3 python3-pip nginx-core
pip3 install --no-cache-dir pandas requests
```

Setup swap (recommended for 1 GB RAM):

```bash
sudo fallocate -l 4G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

---

## Step 3: Directory Layout & File Storage

Directory structure:

```
/opt/coincome/app/coincome.jar
/opt/coincome/uploads
/opt/coincome/python
/opt/coincome/tmp
/opt/coincome/logs
/var/www/coincome  ← frontend
```

Create them:

```bash
sudo mkdir -p /opt/coincome/{app,uploads,python,tmp,logs}
sudo mkdir -p /var/www/coincome
sudo chown -R ubuntu:ubuntu /opt/coincome /var/www/coincome
```

---

## Step 4: Environment Variables & DB Config

Create:

```
/etc/coincome/coincome.env
```

Content:

```bash
SPRING_PROFILES_ACTIVE=prod

DB_HOST=10.0.1.116
DB_PORT=3306
DB_NAME=bitcome
DB_USERNAME=agou
DB_PASSWORD=<db-password>

FILE_UPLOAD_BASE_DIR=/opt/coincome/uploads
PYTHON_EXEC=/usr/bin/python3
PYTHON_SCRIPT_DIR=/opt/coincome/python
PYTHON_TMP_DIR=/opt/coincome/tmp

GOOGLE_CLIENT_ID=xxxx.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=<google-client-secret>

APP_BASE_URL=https://150-136-242-115.sslip.io
OAUTH_REDIRECT_URI=https://150-136-242-115.sslip.io/api/auth/google/callback
```

Backend remote config (`application_remote.yml`) already supports `${VAR}`.

---

## Step 5: Run Spring Boot Backend

Upload JAR:

```bash
scp target/coincome-0.0.1-SNAPSHOT.jar \
    ubuntu@150.136.242.115:/opt/coincome/app/coincome.jar
```

Create systemd service:

```
/etc/systemd/system/coincome.service
```

```ini
[Unit]
Description=CoinCome Spring Boot Service
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/opt/coincome/app
EnvironmentFile=/etc/coincome/coincome.env
ExecStart=/usr/bin/java -jar /opt/coincome/app/coincome.jar
Restart=always
RestartSec=5
SuccessExitStatus=143
StandardOutput=append:/opt/coincome/logs/coincome.out.log
StandardError=append:/opt/coincome/logs/coincome.err.log

[Install]
WantedBy=multi-user.target
```

Start:

```bash
sudo systemctl daemon-reload
sudo systemctl enable coincome
sudo systemctl start coincome
sudo systemctl status coincome
```

---

## Step 6: Deploy Vue 3 Frontend with Nginx

Upload frontend:

```bash
scp -r dist/* ubuntu@150.136.242.115:/var/www/coincome/
```

Nginx config:

```
/etc/nginx/sites-available/coincome
```

```nginx
server {
    listen 80 default_server;
    server_name 150-136-242-115.sslip.io _;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name 150-136-242-115.sslip.io _;

    root /var/www/coincome;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://127.0.0.1:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

Enable config:

```bash
sudo ln -s /etc/nginx/sites-available/coincome /etc/nginx/sites-enabled/coincome
sudo nginx -t
sudo systemctl reload nginx
```

---

## Step 7: Enable HTTPS (Let’s Encrypt)

```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx \
  -d 150-136-242-115.sslip.io \
  --redirect --agree-tos -m you@example.com
```

Your site is now live at:

👉 [https://150-136-242-115.sslip.io](https://150-136-242-115.sslip.io)

---

# Part 3: Local Development

## 3a. Access Remote MySQL via SSH Tunnel

```bash
ssh -i <your-private-key> \
    -L 3307:10.0.1.116:3306 \
    ubuntu@150.136.242.115
```

Use `localhost:3307` in local backend.

---

## 3b. Run Spring Boot Locally

Check `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/bitcome?useSSL=false&allowLoadLocalInfile=true
    username: agou
    password: your-password
```

Run:

```bash
mvn build
mvn spring-boot:run
```

Environment variables needed:

```
GOOGLE_CLIENT_ID=xxx.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=xxx
```

---

## 3c. Run Python Scripts Locally

Ensure python3 is installed:

```bash
python3 --version
pip3 install pandas requests
```

---

## 3d. Run Vue 3 Locally

`.env`:

```env
VITE_API_URL=http://localhost:8080
VITE_GOOGLE_CLIENT_ID=xxx.apps.googleusercontent.com
VITE_GOOGLE_REDIRECT_URI=http://localhost:5173/auth/google/callback
```

Run:

```bash
npm install
npm run dev
```

Frontend: [http://localhost:5173](http://localhost:5173)

---

## 3e. Full End-to-End Local Test

1. Connect SSH tunnel (exposes DB at `localhost:3307`)
2. Start backend on port 8080
3. Start frontend on port 5173
4. Add a folder `upload/` under `CoinCome/coincome`

---
