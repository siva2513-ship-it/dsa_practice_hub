# dsa_practice_hub
# DSA Practice Hub

A full-stack web application that serves as a personal DSA (Data Structures & Algorithms) problem tracker. It exposes a REST API for managing coding problems and integrates with the Codeforces public API to bulk-load real contest problems. The frontend is a static HTML/CSS dashboard that organizes topics by category.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 (Spring MVC) |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| HTTP Client | Spring `RestTemplate` |
| JSON Parsing | Jackson (`ObjectMapper`) |
| Frontend | Static HTML + CSS (served by Spring Boot) |
| Containerization | Docker (multi-stage build) |
| Build Tool | Maven |

---

## Project Structure

```
dsa_practice_hub/
├── src/
│   └── main/
│       ├── java/com/sivakarthik/WebProject1/
│       │   ├── WebProject1Application.java     # Entry point, RestTemplate bean
│       │   ├── controller/
│       │   │   └── ProblemController.java       # REST endpoints
│       │   ├── model/
│       │   │   └── Problem.java                 # Problem data model
│       │   └── service/
│       │       └── ProblemService.java          # Business logic + Codeforces integration
│       └── resources/
│           ├── application.properties
│           └── static/
│               ├── index.html                   # Landing page with topic cards
│               ├── css/style.css
│               └── [topic pages]                # Placeholder pages (not yet implemented)
├── Dockerfile
└── pom.xml
```

---

## Data Model

**Problem**

| Field | Type | Description |
|---|---|---|
| `contestId` | `Integer` | Codeforces contest ID (acts as part of composite key) |
| `index` | `String` | Problem index within the contest (e.g., `"A"`, `"B1"`) |
| `name` | `String` | Problem title |
| `rating` | `Integer` | Difficulty rating (nullable) |
| `tags` | `List<String>` | Topic tags (e.g., `["dp", "graphs"]`) |

> **Note:** Data is stored in-memory (`ArrayList`). All data is lost on server restart. There is no database.

---

## API Endpoints

Base path: `/api/problems`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/problems` | Returns all problems currently in memory |
| `GET` | `/api/problems/{contestId}/{index}` | Returns a single problem by composite key |
| `GET` | `/api/problems/cf` | Fetches raw JSON response from Codeforces API (proxy) |
| `GET` | `/api/problems/cf/tags/{tag}` | Filters problems in memory by a specific tag |
| `GET` | `/api/problems/cf/load` | Calls Codeforces API and loads all problems into memory |
| `POST` | `/api/problems` | Creates a new problem (request body: Problem JSON) |
| `PUT` | `/api/problems/{contestId}/{index}` | Replaces an existing problem by composite key |
| `DELETE` | `/api/problems/{contestId}/{index}` | Deletes a problem by composite key |

### Example: Create a Problem

```http
POST /api/problems
Content-Type: application/json

{
  "contestId": 1234,
  "index": "A",
  "name": "Watermelon",
  "rating": 800,
  "tags": ["math", "brute force"]
}
```

### Swagger UI

Available at: `http://localhost:8080/swagger-ui/index.html`

---

## Setup and Running

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker (optional)

### Run Locally

```bash
# Clone the repository
git clone https://github.com/siva2513-ship-it/dsa_practice_hub.git
cd dsa_practice_hub

# Build and run
./mvnw spring-boot:run
```

App starts at: `http://localhost:8080`

### Run with Docker

```bash
# Build the image
docker build -t dsa-practice-hub .

# Run the container
docker run -p 8080:8080 dsa-practice-hub
```

The Dockerfile uses a two-stage build:
- **Stage 1:** Maven + JDK 21 (Alpine) — compiles and packages the JAR
- **Stage 2:** JRE 21 (Alpine) — runs the JAR as a non-root user

A health check is configured at `/actuator/health` with 30-second intervals.

---

## Frontend

The landing page (`index.html`) is served as a static file and includes:

- A navigation header with links to **Topics** and **Problems**
- A hero section with placeholder metrics
- A grid of 12 topic category cards (Dynamic Programming, Graphs, Trees, Binary Search, Two Pointers, Strings, Sorting, Math, Number Theory, Bit Manipulation, Data Structures, Greedy)

**Important:** Only `index.html` has content. All 12 individual topic pages and `problems.html` are currently empty files. The topic cards link to these pages but they render blank.

---

## Known Limitations

- **No persistence.** In-memory storage means all data resets on restart.
- **No duplicate check.** `POST /api/problems` will add a duplicate if the same `contestId + index` is posted twice.
- **No authentication.** All endpoints are publicly accessible.
- **Topic pages not implemented.** 11 of 12 topic HTML pages and the problems page are empty.
- **No pagination.** `GET /api/problems` returns the full list, which can be very large after loading from Codeforces (10,000+ problems).
- **`/api/problems/cf` proxies raw JSON.** It returns the full Codeforces response without filtering or transformation.

---

## Future Improvements

- **Add a database** (PostgreSQL or H2 for dev) with Spring Data JPA to make problem storage persistent.
- **Add duplicate validation** in `createProblem` before inserting.
- **Implement topic pages** — wire the frontend to query `/api/problems/cf/tags/{tag}` and render results.
- **Add pagination** to list endpoints to handle large datasets from Codeforces.
- **Add user progress tracking** — mark problems as solved, track completion per topic.
- **Add authentication** using Spring Security (JWT or session-based).
- **Write meaningful tests** — current test suite only verifies that the Spring context loads.
- **Add difficulty filtering** — filter problems by rating range via query parameters.
- **Implement search** — search problems by name from the frontend.
- **Cache Codeforces data** — avoid repeated external API calls on every `/cf/load` request.

---