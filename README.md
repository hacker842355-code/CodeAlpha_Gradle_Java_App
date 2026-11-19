# CodeAlpha_Gradle_Java_App — Task 3: Java Application using Gradle

A small **Task Tracker REST API** in Java 17, built, tested and packaged with **Gradle (Kotlin DSL)**, with a GitHub Actions CI/CD pipeline.

## Task requirements → where they are met
| Requirement | Implementation |
|---|---|
| Automate Java builds with Gradle | `build.gradle.kts`: `gradle build` compiles (`-Xlint:all`), tests, produces jar + coverage |
| Manage dependencies efficiently | `gradle/libs.versions.toml` version catalog + JUnit BOM; zero runtime dependencies (JDK `HttpServer`) |
| Integrate CI/CD for continuous delivery | `.github/workflows/ci.yml`: build → test → JaCoCo report → `distZip` artifact → GitHub Release on `v*` tags |
| Streamline build and deployment | `./gradlew run`, `./gradlew installDist`, `./gradlew distZip` |
| Core DevOps principles | Automated tests, reproducible builds, versioned artifacts, pipeline-as-code |

## Prerequisites
- JDK 17+ (`java -version`)
- Gradle 8.10+ **only once**, to generate the wrapper (see below). After that use `./gradlew`.

## Setup
No wrapper is needed: the CI workflow installs Gradle 8.10.2 itself and runs `gradle build`.
Locally, install Gradle 8.10+ and use `gradle` instead of `./gradlew` in the commands below.
Optional: `gradle wrapper --gradle-version 8.10.2` creates `gradlew`; commit those files if you want everyone to use `./gradlew`.

## Commands (run from this folder)
```bash
./gradlew build          # compile + test + coverage + jar   (Windows: gradlew.bat build)
./gradlew test           # tests only; HTML report: build/reports/tests/test/index.html
./gradlew run            # start API on http://localhost:8080  (PORT env var changes it)
./gradlew distZip        # build/distributions/task-tracker-1.0.0.zip (bin/ scripts + lib/)
./gradlew dependencies --configuration testRuntimeClasspath   # inspect dependency tree
```

## API
| Method | Path | Body | Success |
|---|---|---|---|
| GET | `/health` | – | 200 `{"status":"UP"}` |
| GET | `/api/tasks` | – | 200 list |
| POST | `/api/tasks` | `{"title":"..."}` | 201 task (400 if blank / >200 chars) |
| GET | `/api/tasks/{id}` | – | 200 / 404 |
| PUT | `/api/tasks/{id}` | `{"title":"..","done":true}` (either field optional) | 200 / 404 |
| DELETE | `/api/tasks/{id}` | – | 204 / 404 |

```bash
curl -X POST localhost:8080/api/tasks -d '{"title":"learn gradle"}'
curl localhost:8080/api/tasks
```
Data is held **in memory** and is lost on restart (known limitation).

## CI/CD pipeline
Push to GitHub in a repo named `CodeAlpha_Gradle_Java_App`. Workflow runs on pushes to `main`, PRs and `v*` tags. Tag `v1.0.0` to publish the zip as a GitHub Release.

## Common errors
- `./gradlew: No such file` → use `gradle` instead, or run `gradle wrapper` once.
- `Unsupported class file major version` → use JDK 17+.
- Port in use → `PORT=9090 ./gradlew run`.

## Known limitations / future work
In-memory storage; hand-written minimal JSON parser (flat payloads only). Next: Gson/Jackson, a database, Docker image, deployment stage.
