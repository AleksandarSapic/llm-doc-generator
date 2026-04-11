# LLM Documentation Generator

A full-stack web application that automatically generates comprehensive project documentation from any Git repository using Large Language Models. Submit a repository URL, choose your preferred AI provider, and get a structured Markdown document explaining the codebase — in minutes.

> Inspired by [ReadMeReady](https://github.com/souradipp76/ReadMeReady).

---

## Features

- **Multi-LLM support** — choose between OpenAI, Anthropic (Claude), or a locally running Ollama model per job
- **Real-time progress streaming** — live status updates via Server-Sent Events (SSE) while the job runs
- **Custom prompt templates** — override the default file-explanation and project-summary prompts per request
- **Smart deduplication** — re-submitting the same repository and commit SHA reuses the cached result without re-calling the LLM
- **Broad language support** — analyzes `.java`, `.kt`, `.ts`, `.js`, `.py`, `.go`, `.rs`, `.cs`, `.cpp`, `.rb`, `.php`, `.swift`, `.scala`, `.sql`, `.sh`, and more
- **Job history** — browse all past and in-progress jobs with status badges
- **Rendered Markdown output** — documentation is displayed in the UI and available as raw Markdown or JSON
- **Automatic cleanup** — jobs older than 24 hours are removed automatically

---

## Tech Stack

### Frontend


| Technology             | Version  | Purpose                               |
| ---------------------- | -------- | ------------------------------------- |
| Angular                | 21       | SPA framework (standalone components) |
| Tailwind CSS           | 4        | Utility-first styling                 |
| Angular Material + CDK | 21       | UI components                         |
| RxJS                   | 7.8      | Reactive streams                      |
| marked + DOMPurify     | 18 / 3.3 | Safe Markdown rendering               |
| Vitest                 | 4        | Unit testing                          |


### Backend


| Technology                  | Version | Purpose                           |
| --------------------------- | ------- | --------------------------------- |
| Java                        | 21      | Runtime (virtual threads enabled) |
| Spring Boot                 | 4.0.3   | Application framework             |
| Spring AI                   | 2.0-M2  | Multi-provider LLM integration    |
| JGit                        | 7.1     | Git repository cloning            |
| Spring Data JPA + Hibernate | —       | Database access                   |
| Flyway                      | —       | Database schema migrations        |
| Lombok                      | —       | Boilerplate reduction             |
| Maven                       | 3.9+    | Build tool                        |


### Database


| Technology | Version |
| ---------- | ------- |
| PostgreSQL | 17      |


### DevOps


| Technology     | Purpose                                     |
| -------------- | ------------------------------------------- |
| Docker         | Containerization                            |
| Docker Compose | Multi-service orchestration                 |
| Nginx          | Frontend static serving + API reverse proxy |


---

## Project Structure

```
llm-doc-generator/
├── backend/                  # Spring Boot REST API
│   ├── src/
│   │   └── main/
│   │       ├── java/         # Application source code
│   │       └── resources/
│   │           ├── application.yaml
│   │           └── db/migration/   # Flyway SQL migrations
│   ├── Dockerfile
│   ├── pom.xml
│   ├── .env.example
│   └── .env                  # Local secrets (gitignored)
│
├── frontend/                 # Angular SPA
│   ├── src/
│   │   └── app/
│   │       ├── core/         # Services, interceptors, models
│   │       ├── features/     # Routed page components
│   │       └── shared/       # Reusable UI components
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
├── docker-compose.yml        # Orchestrates db + backend + frontend
├── .env.example              # Root environment variable template
└── .env                      # Root secrets (gitignored)
```

---

## Requirements

### Option A — Docker Compose (recommended)

- [Docker](https://docs.docker.com/get-docker/) 24+
- [Docker Compose](https://docs.docker.com/compose/) v2+

No other tools required — everything runs inside containers.

### Option B — Local Development

- [Node.js](https://nodejs.org/) 22 LTS + npm 11+
- [Java](https://adoptium.net/) 21 (LTS)
- [Apache Maven](https://maven.apache.org/) 3.9+
- [PostgreSQL](https://www.postgresql.org/download/) 17
- (Optional) [Ollama](https://ollama.com/) — only if you want to use local models

---

## Setup Instructions

### Option A — Run with Docker Compose

**1. Clone the repository**

```bash
git clone <your-repo-url>
cd llm-doc-generator
```

**2. Configure environment files**

Copy the root template and fill in database credentials:

```bash
cp .env.example .env
```

```bash
# .env
DATABASE_NAME=llm_doc_generator
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_secure_password
OLLAMA_BASE_URL=http://host.docker.internal:11434
```

Copy the backend template and fill in your LLM API keys:

```bash
cp backend/.env.example backend/.env
```

```bash
# backend/.env
OPENAI_API_KEY=sk-...
ANTHROPIC_API_KEY=sk-ant-...
OLLAMA_BASE_URL=http://host.docker.internal:11434
```

> You only need the API key for the provider(s) you intend to use.
> Ollama runs on your host machine — `host.docker.internal` lets the container reach it.

**3. Build and start all services**

```bash
docker-compose up --build
```

This starts three services:

- `db` — PostgreSQL 17 (internal only)
- `backend` — Spring Boot API on port `8080`
- `frontend` — Angular app served by Nginx on port `4200`

**4. Open the application**


| Service      | URL                                                                            |
| ------------ | ------------------------------------------------------------------------------ |
| Frontend     | [http://localhost:4200](http://localhost:4200)                                 |
| Backend API  | [http://localhost:8080/api/v1](http://localhost:8080/api/v1)                   |
| Health check | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) |


**5. Stop the application**

```bash
docker-compose down
```

To also remove the database volume (all data):

```bash
docker-compose down -v
```

---

### Option B — Run Locally

**1. Clone the repository**

```bash
git clone <your-repo-url>
cd llm-doc-generator
```

**2. Set up PostgreSQL**

Start a local PostgreSQL 17 instance and create the database:

```sql
CREATE DATABASE llm_doc_generator;
```

**3. Configure backend environment**

```bash
cp backend/.env.example backend/.env
```

Edit `backend/.env`:

```bash
OPENAI_API_KEY=sk-...
ANTHROPIC_API_KEY=sk-ant-...
OLLAMA_BASE_URL=http://localhost:11434

DATABASE_URL=jdbc:postgresql://localhost:5432/llm_doc_generator
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_password
```

**4. Start the backend**

```bash
cd backend
mvn spring-boot:run
```

Flyway will automatically create the required database tables on first startup.
The API will be available at `http://localhost:8080`.

**5. Start the frontend**

In a new terminal:

```bash
cd frontend
npm install
npm start
```

The Angular dev server starts at `http://localhost:4200` and proxies `/api` requests to the backend automatically.

---

## Environment Variables

The project uses two `.env` files. Never commit real secrets — `.env` files are in `.gitignore`.

### Root `.env` (Docker only)

Placed at the project root. Used by Docker Compose to configure the database container.


| Variable            | Description                           | Example                             |
| ------------------- | ------------------------------------- | ----------------------------------- |
| `DATABASE_NAME`     | Name of the PostgreSQL database       | `llm_doc_generator`                 |
| `DATABASE_USERNAME` | Database user                         | `postgres`                          |
| `DATABASE_PASSWORD` | Database password                     | `changeme`                          |
| `OLLAMA_BASE_URL`   | Ollama base URL reachable from Docker | `http://host.docker.internal:11434` |


### `backend/.env` (Docker + Local)

Placed inside the `backend/` folder. Used by the Spring Boot application.


| Variable            | Description                    | Example                                              |
| ------------------- | ------------------------------ | ---------------------------------------------------- |
| `OPENAI_API_KEY`    | OpenAI API key                 | `sk-...`                                             |
| `ANTHROPIC_API_KEY` | Anthropic API key              | `sk-ant-...`                                         |
| `OLLAMA_BASE_URL`   | Ollama base URL                | `http://localhost:11434`                             |
| `DATABASE_URL`      | Full JDBC URL (local dev only) | `jdbc:postgresql://localhost:5432/llm_doc_generator` |
| `DATABASE_USERNAME` | Database user                  | `postgres`                                           |
| `DATABASE_PASSWORD` | Database password              | `postgres`                                           |


> In Docker mode, `DATABASE_URL` is constructed automatically by Docker Compose and does not need to be set in `backend/.env`.

---

## Additional Notes

- **Ollama**: If you want to use a local Ollama model, make sure Ollama is running on your machine (`ollama serve`) before starting the app. The default model is `gemma3`.
- **No authentication**: This is a school/demo project. All API endpoints are publicly accessible — do not expose it to the internet without adding authentication first.
- **Schema management**: You do not need to create database tables manually. Flyway runs migrations automatically when the backend starts.
- **Build tool**: The project does not include the Maven wrapper (`mvnw`). Use a system-installed `mvn` 3.9+ directly.

---

## Future Improvements

- **RAG (Retrieval-Augmented Generation)** — store code embeddings in a vector database so the LLM can retrieve relevant context rather than processing the entire codebase sequentially; significantly improves quality on large repositories
- **Fine-tuning** — train a smaller model specifically on high-quality documentation examples to reduce API costs and improve output consistency
- **Testing** — expand unit and integration test coverage for services and API endpoints; use Testcontainers for database integration tests
- **CI/CD** — add a GitHub Actions pipeline for automated build, test, and Docker image publishing
- **Authentication** — protect the API with JWT or OAuth2 (e.g. Spring Security + Keycloak)
- **Performance** — run LLM calls for individual files in parallel instead of sequentially to reduce overall job duration
- **UI/UX** — add dark mode, pagination for job history, PDF export of generated documentation
- **Streaming output** — stream LLM-generated text token-by-token to the UI for a more responsive feel

---

## Inspiration

This project was inspired by [ReadMeReady](https://github.com/souradipp76/ReadMeReady), an open-source tool for automated README generation using LLMs.

---

## License

This project is licensed under the [MIT License](LICENSE).