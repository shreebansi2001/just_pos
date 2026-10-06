# 🚨 STRICT LIVE DEPLOYMENT SAFEGUARDS & PROTOCOL

> **CRITICAL RULE FOR ALL FUTURE SESSIONS & DEPLOYMENTS:**  
> **READ AND VERIFY THIS PROTOCOL BEFORE RUNNING ANY DEPLOYMENT SCRIPT OR UPLOADING ANY FILES.**

---

## 🚫 ABSOLUTE PROHIBITIONS (NEVER DO THIS)

1. **NEVER TOUCH THE MAIN DOMAIN:**
   - **File:** `/public_html/index.html` is the company marketing website (`https://justcatering.in/`).
   - **Assets:** `/public_html/images/*` are website assets.
   - **STRICT PROHIBITION:** POS files must **NEVER** be deployed to `/public_html/index.html` or `/public_html/`.

2. **NEVER TOUCH OTHER SUBDOMAINS OR PROJECTS:**
   - **App Subdomain:** `/public_html/app.justcatering.in/` (JCX CRM Application) must **NEVER** be touched, edited, or overwritten.
   - **Other Projects:** Do NOT touch or modify directories of Sizzlo, CRM-main, or any other unrelated projects.

3. **NEVER TOUCH OTHER DATABASES OR TABLES:**
   - POS strictly operates on its own dedicated tables (`pos_*`).
   - **STRICT PROHIBITION:** Do **NOT** drop, alter, truncate, or run queries against non-POS database tables (e.g. user authentication tables of other systems, customer enquiry CRM tables, agency tables, etc.).

---

## ✅ APPROVED DEPLOYMENT TARGETS FOR POS

The POS application is strictly a **SUBDOMAIN ONLY** application:

| Component | Allowed Live Target Path | Live URL |
| :--- | :--- | :--- |
| **POS Root** | `/public_html/pos.justcatering.in/index.html` | `https://pos.justcatering.in/` |
| **POS Sub-path** | `/public_html/pos.justcatering.in/pos/index.html` | `https://pos.justcatering.in/pos` |
| **POS SSO** | `/public_html/pos.justcatering.in/sso/index.html` | `https://pos.justcatering.in/sso` |
| **POS Fallback** | `/public_html/pos/index.html` | `https://pos.justcatering.in/` |

---

## 📋 PRE-DEPLOYMENT CHECKLIST (VERIFY BEFORE UPLOADING)

Before running any script or FTP/cPanel upload:

- [ ] **Target Directory Check:** Does the target directory explicitly contain `pos.justcatering.in`?  
      *(If the script points to `/public_html/index.html`, STOP IMMEDIATELY).*
- [ ] **Database Check:** Are queries/migrations restricted exclusively to `pos_*` tables?
- [ ] **Git Sync Check:** Are all code changes committed and pushed to `feature/setup`?
- [ ] **Local Verification:** Was `web/pos-preview/index.html` tested locally and verified error-free?

---

## 🔍 POST-DEPLOYMENT VERIFICATION (RUN IMMEDIATELY AFTER UPLOAD)

Immediately after any deployment script finishes, verify the health of all three live systems:

```bash
# 1. Verify Main Website is 100% Intact (Should return 200 OK & ~150KB)
curl -sI https://justcatering.in/

# 2. Verify CRM App is 100% Intact (Should return 200 OK)
curl -sI https://app.justcatering.in/

# 3. Verify POS is Live on Subdomain (Should return 200 OK & ~1MB)
curl -sI https://pos.justcatering.in/
```

If `https://justcatering.in/` ever displays POS or is altered, revert `/public_html/index.html` immediately from `scratch/restored_justcatering_index.html`.

---

## 👥 STAKEHOLDER COORDINATION
- **Frontend Changes:** Push to Git branch `feature/setup` and deploy exclusively to `pos.justcatering.in`.
- **Backend Changes:** Package changes into `Just_POS_Backend.zip` and coordinate with Vivek for backend deployment on `https://www.justcatering.in/JCPortal`.
