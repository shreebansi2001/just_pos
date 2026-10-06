# Project Guidelines & Rules: Just POS

> [!CRITICAL]
> **MANDATORY PRE-DEPLOYMENT CHECKLIST (MUST READ BEFORE ANY LIVE ACTION):**
> 1. **Target Directory Verification:** POS frontend MUST ONLY be uploaded to `/public_html/pos.justcatering.in/`.
> 2. **Absolute Main Domain Prohibition:** NEVER TOUCH OR OVERWRITE `/public_html/index.html` under ANY circumstances.
> 3. **Zero Impact on Other Projects:** NEVER modify, touch, or query other subdomains (`app.justcatering.in`), other project folders, or non-POS database tables.
> 4. **Live Verification:** Always test `https://justcatering.in/`, `https://app.justcatering.in/`, and `https://pos.justcatering.in/` to confirm complete system health.

---

## 1. Production Domains & URLs
- **Frontend Live URL:** `https://pos.justcatering.in/`
  - The live POS frontend must ALWAYS be deployed to and served on `https://pos.justcatering.in/`.
  - Deployment targets on FTP / cPanel (SUBDOMAIN ONLY):
    - `/public_html/pos.justcatering.in/index.html`
    - `/public_html/pos.justcatering.in/pos/index.html`
    - `/public_html/pos.justcatering.in/sso/index.html`
  - **STRICT PROHIBITION:** 
    - **NEVER** touch, overwrite, or edit `/public_html/index.html`. That is the main company website `justcatering.in`.
    - **NEVER** touch `/public_html/app.justcatering.in/` (that is the JCX CRM application).
    - **NEVER** touch `/public_html/images/` of the main website.
- **Backend API Base URL:** `https://www.justcatering.in/JCPortal`
  - `BACKEND_BASE` must strictly and always be `https://www.justcatering.in/JCPortal`.
  - POS API endpoints: `https://www.justcatering.in/JCPortal/v1/api/pos/...`
  - Never override this with relative URLs or tunnel/localhost URLs in production code.

---

## 2. Strict Project & Database Isolation Policy
- **Zero Impact on Other Projects:**
  - The server hosts multiple systems (Main Marketing Website, JCX CRM App, POS, Sizzlo, etc.).
  - POS code, deployments, and scripts must remain **100% strictly isolated** to POS.
  - **NEVER** edit, drop, alter, truncate, or touch tables or databases of other projects.
  - Only interact with POS-specific tables (`pos_*` tables).
- **Zero Automatic Purging (Data Preservation):**
  - Under no circumstances should user data be wiped or purged automatically upon page load, login, logout, or refresh.
  - **Protected Keys in `localStorage`:**
    - `pos_custom_company_series_config` (Outlet & Series Master)
    - `pos_custom_categories` (Category Master)
    - `pos_custom_items` (Item Master)
    - `pos_custom_tables` (Table Master)
    - `pos_custom_floors` (Floor Master)
    - `pos_custom_kots` (Active Kitchen Tickets)
    - `pos_custom_invoices` (Billing & Invoices)
    - `pos_custom_table_orders` & `pos_custom_takeaway_orders` (All Orders)
    - `pos_staff_users` (Staff Master Users)
  - **API Sync Resilience:**
    - If the server returns empty arrays `[]` or null for categories, items, tables, floors, orders, or KOTs, the frontend MUST NOT wipe local data.
    - Server responses must always be **merged** into local data, preserving what the user has entered.

---

## 3. Brand & Browser Display
- **Browser Tab Title:**
  - Must always be **`Just POS`** (`<title>Just POS</title>` and `document.title = "Just POS"`).
  - Never override the browser title with individual client or company names (e.g., never display "Amoncar" in the browser tab).

---

## 4. Workflow Expectations
- **Order to KOT Dispatch:**
  - Orders placed in POS Desk or Tables must immediately appear in the **Kitchen KOT** Kanban view (`#view-kot`).
- **Advance Payment / Direct Billing:**
  - When payment is collected in advance / direct settlement, a KOT ticket must automatically be sent to the kitchen marked with `💳 Paid in Advance`, and the settled bill must appear in **Billing** (`#view-billing`).
- **Staff Master Roles & Login:**
  - Staff created in Staff Master must be able to log in with their Employee Code / Email + PIN and have their role permissions enforced.

---

## 5. Deployment Protocol & Health Verification
Every time before and after deploying:
1. **Target Path Audit:** Confirm deployment script only uploads to `/public_html/pos.justcatering.in/`.
2. **Git Commit & Push:** Commit and push verified code to Git branch `feature/setup`.
3. **Backend Coordination:** Always notify the user if backend code changes are made so Vivek can deploy the backend.
4. **Post-Deploy Smoke Test:**
   - `curl -sI https://justcatering.in/` -> Must return 200 OK (Main Website)
   - `curl -sI https://app.justcatering.in/` -> Must return 200 OK (CRM App)
   - `curl -sI https://pos.justcatering.in/` -> Must return 200 OK (Just POS)

