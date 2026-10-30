---

---

## ✅ Auth

- [x]  POST /register — create user, auto-issue JWT pair
- [x]  POST /login — validate credentials, return AT + RT
- [x]  POST /refresh — rotate refresh token, return new AT + RT
- [x]  POST /logout — invalidate session (isValid = false), clear cookie
- [x]  Google OAuth2 — find-or-create user, issue JWT pair
- [x]  JWT filter — validate token on every request, populate SecurityContext
- [x]  Global exception handler — structured error responses
- [x]  Logging — SLF4J via @Slf4j on services

---

## 🪙 Category (Budget Slots)

- [x]  POST /categories — create category + budget together (one request)
- [x]  GET /categories — return all system defaults + user's custom slots
- [x]  PATCH /categories/{id} — edit slot name, icon, allocated budget
- [x]  DELETE /categories/{id} — delete custom slot (protect system defaults)
- [x]  Seed system default categories on startup (Savings, Guilt-free, Subscriptions, EMI, House Expenses)

---

## 💸 Transactions

- [x]  POST /transactions — create transaction, update budget spentAmount, update user totalBalance
- [x]  GET /transactions — get all transactions for current user (filter by type, category, date range)
- [x]  GET /transactions/{id} — get single transaction
- [x]  DELETE /transactions/{id} — soft delete, reverse budget spentAmount, reverse totalBalance

---

## 🎯 Budget

- [x]  GET /budgets — get all budgets for current user (default: current month)
- [x]  GET /budgets?month=yyyy-MM — get budgets for specific month
- [x]  PUT /budgets/{id} — update allocated amount
- [x]  Budget breach detection — flip isBreached when spentAmount > allocatedAmount
- [x]  Monthly budget auto-creation scheduler — on 1st of every month, create new Budget rows for all active categories

---

## 👤 User / Profile

- [x]  GET /user/profile — get current user's profile
- [x]  PATCH /user/profile — update name, base currency
- [x]  PATCH /user/balance — update total bank balance manually
- [x]  DELETE /user/account — soft delete (isActive = false)

---

## 📧 Notifications (Email)

- [x]  Configure JavaMailSender with Gmail SMTP
- [x]  Welcome email on registration (@Async)
- [x]  Budget 80% warning email — triggered in TransactionService when spentAmount crosses 80% (@Async)
- [x]  Budget breach alert email — triggered when isBreached flips (@Async)
- [x]  Monthly financial report email — scheduler on 1st of month at 00:05 (@Scheduled)

---

## 📊 Analytics

- [x]  GET /analytics/summary?month= — total income, total expense, net savings
- [x]  GET /analytics/spending-by-category?month= — breakdown per slot
- [x]  GET /analytics/trend?months=6 — month-over-month comparison
- [x]  GET /analytics/top-expenses?month=&limit=5 — top spending transactions

---

## ⚡ Redis Caching

- [x]  Add Upstash Redis to project
- [x]  Pattern 2 — Manual eviction: monthly_summary:{userId}:{month} — evict on new transaction
---

## 🤖 AI Features

- [ ]  Configure Spring AI + Groq (Llama 3)
- [ ]  Context builder service — bundles last 5 transactions, budget status, monthly summary per request
- [ ]  POST /ai/chat — natural language input, AI may call tools to log transactions or fetch data
- [ ]  AI tool: addTransaction()
- [ ]  AI tool: getCurrentBudgets()
- [ ]  AI tool: getMonthlySummary()
- [ ]  AI tool: getRecentTransactions()
- [ ]  AI tool: getSpendingByCategory()
- [ ]  GET /ai/history — paginated conversation history
- [ ]  POST /ai/finance-plan — AI recommends slot allocations based on income + EMIs
- [ ]  POST /ai/monthly-insight — AI generates insight paragraph for a given month
- [ ]  Wire monthly AI insight into monthly report email

---

## 🛠 Admin

- [x]  GET /admin/users — all users (ADMIN role only)
- [x]  PATCH /admin/users/{id}/deactivate — soft deactivate a user
- [ ]  GET /admin/stats — platform stats (total users, total transactions, etc.)

---

## 🚀 Deploy

- [ ]  application-dev.yml + application-prod.yml (Spring Profiles)
- [ ]  Replace ddl-auto: update with Flyway versioned migrations
- [ ]  Push to Railway — connect Neon PostgreSQL + Upstash Redis via env variables
- [ ]  GitHub auto-deploy enabled
- [ ]  Live URL in README and resume