<div align="center">

# FinSight 💸

### Make every rupee count.

An AI-powered personal finance backend for tracking transactions, managing budgets, and understanding spending with a conversational assistant to help make sense of it all.

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&labelColor=9B5E00&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?style=for-the-badge&labelColor=3D7A40&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?style=for-the-badge&labelColor=294C7A&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-Cache%20%26%20Chat%20Memory-DC382D?style=for-the-badge&labelColor=8D2320&logo=redis&logoColor=white)](https://redis.io/)
[![Maven](https://img.shields.io/badge/Maven-Build%20Tool-C71A36?style=for-the-badge&labelColor=7F1830&logo=apachemaven&logoColor=white)](https://maven.apache.org/)

</div>

---

## ✨ What is FinSight?

FinSight is a personal finance management **REST API** built with Spring Boot. It brings transaction tracking, budgets, spending analytics, and an AI-powered finance assistant together in one backend.

> “I spent ₹50 on shawarma” — ask the assistant to record an expense, check your budget, or summarize your spending.

## 🚀 Features

- **Accounts and authentication** — Sign-up and login with JWT-based authentication, refresh tokens, and Google OAuth2.
- **Transactions** — Create, filter, paginate, and soft-delete income and expense transactions.
- **Categories** — Organize spending with personal categories.
- **Monthly budgets** — Set and update category budgets, with email notifications when spending reaches a threshold or exceeds a budget.
- **Spending analytics** — Monthly summaries, category breakdowns, six-month trends, and top expenses.
- **AI finance assistant** — Ask questions about your finances and use natural language to create expense transactions.
- **Conversational memory** — Redis-backed chat memory for the assistant.
- **Monthly insights** — Scheduled, AI-generated finance report emails with spending summaries and trends.
- **API documentation** — Explore endpoints through the generated Swagger UI.

## 🧰 Built with

| Area | Technologies |
| --- | --- |
| Language and framework | Java 21, Spring Boot |
| API and validation | Spring MVC, Spring Validation, SpringDoc OpenAPI |
| Authentication | Spring Security, JWT, Google OAuth2 |
| Persistence | Spring Data JPA, Hibernate, PostgreSQL |
| Caching and chat memory | Redis, Spring Cache, Spring AI |
| Email | Spring Mail |
| Build and tests | Maven, JUnit, H2, Testcontainers |

## ⚠️ Deployment Coming Soon