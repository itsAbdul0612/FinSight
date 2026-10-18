# FinSight 💰
> A backend-heavy personal finance tracker with a conversational AI assistant.
> "I spent 50rs on shawarma" → AI logs it, updates your budget, tells you where you stand.

## Status
🚧 Active development — core features complete, AI and Redis in progress.

## Tech Stack
- Spring Boot, Spring Security, JWT + Google OAuth2
- PostgreSQL, Spring Data JPA, Hibernate Envers
- Redis (Upstash) — coming
- Spring AI + Groq (Llama 3) — coming
- Railway (deployment) — coming

## What's Built
- Auth — JWT + Google OAuth2, refresh token rotation, session management
- Budget Slots — CRUD with preset categories, breach detection, email alerts
- Transactions — CRD (immutable by design), dynamic filtering, soft delete
- Notifications — async email alerts (welcome, 80% warning, breach)
- Admin — user management, deactivation

## In Progress
- Analytics endpoints
- Redis caching (3 patterns)
- Spring AI tool calling

## Design Decisions
- Transactions are immutable — no PUT endpoint. Finance standard.
- spentAmount maintained incrementally, not recalculated on every read.
- 3 distinct Redis patterns (TTL, manual eviction, scheduled refresh)
