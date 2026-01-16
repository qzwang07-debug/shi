# Repository Guidelines

## Project Structure & Module Organization
- `src/`: Vue 3 + Vite frontend (`views/`, `api/`, `router/`, `store/`, `components/`, `utils/`).
- `public/`: static assets copied as-is.
- `vite/`: Vite plugin wiring and build helpers.
- `zNova/`: Spring Boot 2.5 multi-module backend (`zNova-admin/` is the runnable app; other `zNova-*` modules provide shared functionality).

## Build, Test, and Development Commands
Frontend (Node.js >= 18):
- `pnpm install` (preferred; lockfile is `pnpm-lock.yaml`)
- `pnpm dev` start dev server (default `http://localhost:80/`; proxy `/dev-api` → `http://localhost:8080`)
- `pnpm build:prod` build to `dist/`
- `pnpm preview` preview the production build locally

Backend (JDK 8, Maven 3.6+):
- `cd zNova && mvn -DskipTests clean package`
- `java -jar zNova-admin/target/zNova-admin.jar`
- IDE run: `zNova/zNova-admin/src/main/java/com/zNova/zNovaApplication.java`

## Coding Style & Naming Conventions
- Follow existing file style: 2-space indentation, no semicolons, and keep quote style consistent within a file.
- Vue: component files `PascalCase.vue`; routes/URLs use `kebab-case`; API wrappers live under `src/api/` by domain.
- Backend: keep module boundaries (`zNova-*/`) clean; avoid cross-module dependencies unless necessary.

## Testing Guidelines
- No automated test suite is checked in. Before opening a PR, run `pnpm build:prod` and smoke-test affected pages/endpoints locally.

## Commit & Pull Request Guidelines
- Git history uses simple subjects (for example, `update README.md.`). Keep commits small and scoped; add a prefix when helpful: `web: ...`, `server: ...`.
- PRs include: summary, how to test, screenshots for UI changes, and any config/DB changes (with migration steps).

## Security & Configuration Tips
- Frontend env files: `.env.development`, `.env.production` (see `.env.oss.example` for OSS storage examples).
- Backend config: `zNova/zNova-admin/src/main/resources/application.yml` and `application-druid.yml`.
- Never commit credentials, tokens, or production URLs.
