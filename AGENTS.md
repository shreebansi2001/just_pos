# Project Guidelines & Rules: Just POS

## 1. Production Domains & URLs
- **Frontend Live URL:** `https://pos.justcatering.in/`
  - The live frontend must always be deployed to and served on `https://pos.justcatering.in/`.
  - Deployment targets on FTP:
    - `/public_html/index.html`
    - `/public_html/pos/index.html`
    - `/public_html/pos.justcatering.in/index.html`
- **Backend API Base URL:** `https://www.justcatering.in/JCPortal`
  - `BACKEND_BASE` must strictly and always be `https://www.justcatering.in/JCPortal`.
  - API endpoints:
    - Auth: `https://www.justcatering.in/JCPortal/v1/api/auth/login`
    - POS: `https://www.justcatering.in/JCPortal/v1/api/pos/...`
  - Never override this with relative URLs or tunnel/localhost URLs in production code.

---

## 2. Strict Data Preservation Policy (DO NOT CLEAR DATA)
- **Zero Automatic Purging:** Under no circumstances should user data be wiped or purged automatically upon page load, login, logout, or refresh.
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

## 5. Live Deployment Coordination
- Always notify the user if any backend changes are made so Vivek can deploy the backend.
- Commit and push all verified changes to Git branch `feature/setup`.
- Upload updated files to the live server via FTP (`pos.justcatering.in`, port 21, user `justcatering`).
