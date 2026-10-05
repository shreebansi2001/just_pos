# Live Deployment & Architecture Rules: Just POS

1. **Frontend Host:** Always `https://pos.justcatering.in/`.
2. **Backend Base:** Always `https://www.justcatering.in/JCPortal`.
3. **Data Preservation:** Never clear or purge `localStorage` data on page load, login, or refresh. Never wipe local state when API sync returns `[]`.
4. **Tab Title:** Always `Just POS`. Never customer company names.
5. **KOT & Billing Flow:** Send to KOT must show in Kitchen KOT. Advance billing must send to KOT with "Paid in Advance" badge and show in Billing.
6. **Backend Coordination:** Always inform user if backend code changes were made so Vivek can deploy backend.
