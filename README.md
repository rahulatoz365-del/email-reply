# 🧠 Email Reply AI Service (Backend)

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](#)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](#)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](#)
[![OpenRouter](https://img.shields.io/badge/AI_API-OpenRouter-black?style=for-the-badge)](#)

This is the backend REST API service that powers the **[Email Reply Chrome Extension](https://github.com/rahulatoz365-del/email-reply-frontend)**. Built with Java 21 and Spring Boot, it acts as the secure middleware between the browser extension and the OpenRouter AI API.

---

## 📖 Overview

To keep the Chrome extension lightweight and secure, all heavy lifting and AI integration is delegated to this Spring Boot backend. 

When a user requests an email reply via the frontend, this service receives the context, formats a strict prompt, and asynchronously calls the OpenRouter API using Spring WebClient. It then processes the AI's response and returns a clean, ready-to-send draft back to the client.

## ✨ Key Features

* **AI Integration:** Seamlessly connects to various LLMs via the OpenRouter API.
* **Non-Blocking I/O:** Utilizes Spring WebClient for efficient, reactive HTTP calls to external APIs.
* **Context Formatting:** Handles prompt engineering dynamically based on the requested "tone" (e.g., professional, casual, direct).
* **Containerized:** Includes a Dockerfile and `compose.yaml` for rapid, consistent deployment across environments.

---

## 🛠️ Technology Stack

* **Language:** Java 21
* **Framework:** Spring Boot
* **HTTP Client:** Spring WebClient
* **Build Tool:** Maven
* **DevOps:** Docker

---

## 🚀 Getting Started

Follow these steps to run the backend service locally.

### 1. Prerequisites
* Java 21 (JDK) installed.
* An active [OpenRouter API Key](https://openrouter.ai/).
* Docker (Optional, but recommended).

### 2. Environment Setup
You must provide your OpenRouter API key to the application. Set it as an environment variable in your terminal:
```bash
# On Linux/macOS
export OPEN_API_KEY=your_openrouter_api_key

# On Windows (Command Prompt)
set OPEN_API_KEY=your_openrouter_api_key

# On Windows (PowerShell)
$env:OPEN_API_KEY="your_openrouter_api_key"

```

### 3. Clone & Run (Using Maven)

Clone the repository and use the included Maven wrapper to start the server:

```bash
git clone [https://github.com/rahulatoz365-del/email-reply.git](https://github.com/rahulatoz365-del/email-reply.git)
cd email-reply

# Run the application
./mvnw spring-boot:run

```

The service will boot up and listen for requests on `http://localhost:8080`.

### 🐳 Alternative: Run via Docker

If you prefer not to install Java/Maven locally, you can spin up the service using Docker:

```bash
# Build the image
docker build -t email-reply-service .

# Run the container (Make sure to pass your API key!)
docker run -p 8080:8080 -e OPEN_API_KEY=your_openrouter_api_key email-reply-service

```

---

## 📡 API Endpoints

The service exposes the following main REST endpoints:

* `GET /api/email/health` – Returns basic health and status information to ensure the service is running.
* `GET /api/email/tones` – Fetches the list of available AI writing tones (e.g., Professional, Casual).
* `POST /api/email/generate` – Accepts a JSON payload containing the original email context and desired tone, returning the AI-generated reply.

*(Note: Ensure your frontend extension is configured to point to `http://localhost:8080` while developing locally.)*
