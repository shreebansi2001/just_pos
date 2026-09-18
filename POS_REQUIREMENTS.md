# POS (Point of Sale) & Catering Order Desk - System Requirements Specification

## 1. Overview & Objectives
The POS system is a high-efficiency order entry, table management, kitchen display, billing, and reservation system tailored for dining establishments, banquets, and catering operations. It integrates seamlessly into the JC Portal ecosystem while maintaining its own dedicated, high-performance module structure.

---

## 2. Visual Design & Theme Integration
- **Color Palette**: Aligned with the CRM's primary theme:
  - **Primary Brand Color**: `#017A9C` (Deep Cyan / Teal)
  - **Hover / Focus**: `#016582`
  - **Surface / Background**: Light mode (`#F8FAFC`, `#FFFFFF`), Dark mode (`#090D14`, `#111827`)
  - **Status Accents**:
    - Available / Veg: `#16A34A` (Green)
    - Occupied / Non-Veg / Alert: `#EA580C` / `#DC2626`
    - Billed / In-Service: `#2563EB` (Blue)
    - Cleaning: `#D97706` (Amber)
- **Typography**: Inter / Manrope clean sans-serif typography.
- **Theme Modes**: Dynamic Light / Dark mode toggle preserving state.

---

## 3. Functional Modules

### 3.1 Authentication & POS Login
- **Screen**: Dedicated POS Login interface matching CRM split-screen layout.
- **Credentials**:
  - `userCode`: Tenant / Company identifier.
  - `email` or `username`: Operator login.
  - `password`: Secure password input with show/hide toggle.
- **Session & Storage**: JWT Bearer token stored in `localStorage` (`userToken`, `userData`), role validation for POS operator/cashier/manager.

### 3.2 Tables & Floor Management
- **Floor Navigation**: Dynamic tabs for all active floors (e.g. *Ground Floor*, *Rooftop*, *Private Hall*, or *All*).
- **Table Lifecycle States**:
  1. `AVAILABLE`: Ready for walk-ins; displays upcoming reservation badge if booked today with instant "Seat Now" action.
  2. `OCCUPIED`: Order active; displays ordered item count, unsent KOT indicators, and table free override.
  3. `BILLED`: Invoice generated, payment pending; displays total amount due.
  4. `CLEANING`: Post-settlement sanitization state; single-tap marks table ready and available.
- **Quick Order Launchers**:
  - `Takeaway`: Walk-in takeout without table assignment.
  - `Delivery`: Delivery order capturing customer name, phone, and delivery address.
  - `Bulk Catering`: Event catering order (minimum 20 pax) capturing guest details and event requirements.
- **Active Non-Dine-In Orders**: Real-time grid of all active takeaway, delivery, and catering orders.

### 3.3 POS Order Desk
- **Context Bar**: Displays order ID, table code or order type, start time, customer fields, "Move Table", and "Cancel Order" controls.
- **Menu & Search**:
  - Live real-time search across all active dishes.
  - Category navigation tabs (*Starters, Main Course, Rice & Biryani, Breads, Live Counter, Desserts, Beverages*).
  - Dietary dots (🟢 Veg / 🔴 Non-Veg).
  - Variant selection (e.g. *Half* vs *Full* portion modal with separate pricing).
- **Cart & Calculations**:
  - Item lines with `-` and `+` steppers and line totals.
  - **Sent vs. Pending KOT tags**: Clear indication of items fired to kitchen vs. new items pending.
  - **Kitchen Void Safeguard**: Confirmation prompt before reducing quantities of already-fired items.
  - **Discounts**: Configurable by Percentage (`%`) or Flat amount (`₹`).
  - **Taxes**: Automated CGST (2.5%) + SGST (2.5%) computation on net taxable amount.
  - **Grand Total**: Real-time total calculation.
- **Order Triggers**:
  - `Send to kitchen (KOT)`: Generates kitchen ticket for all pending/unsent items.
  - `Generate invoice`: Locks cart and transitions to billing.

### 3.4 Kitchen Order Ticket (KOT) Live Kanban
- **4-Stage Workflow**:
  1. `NEW`: Newly fired ticket from POS desk.
  2. `PREPARING`: Chef has begun preparation.
  3. `READY`: Dishes ready for food runner / pickup.
  4. `SERVED`: Dishes delivered to the table.
- **Interactive Transitions**: Drag-and-drop between columns or sequential one-click forward/backward buttons.
- **Ticket Details**: Ticket ID (`KOT-001`), Table/Order reference, elapsed time counter (*"just now"*, *"X min ago"*), and ordered item quantities.
- **Active Badge**: Badge count in the navigation bar showing pending unserved tickets.

### 3.5 Billing & Invoices
- **Invoice Register**: Chronological list of today's invoices with settlement status (`Paid`, `Unpaid`, `Voided`).
- **Invoice Modal**:
  - Formatted printable invoice receipt.
  - Header: Restaurant name, invoice ID, table/order reference, date & time.
  - Itemized table: Item name, quantity, rate, amount.
  - Summary: Subtotal, discount, CGST (2.5%), SGST (2.5%), grand total.
  - Settlement modes: **Cash**, **Card**, **UPI**.
- **Settlement Lifecycle**:
  - Settling an invoice marks the bill `PAID`.
  - Sets the dining table to `CLEANING`.
  - Marks linked reservation to `COMPLETED`.
  - Clears the active order session.

### 3.6 Reservations Management
- **Filters**: Date picker filter + Status tabs (`All`, `Upcoming`, `Seated`, `Completed`, `Cancelled`, `No-show`).
- **Reservation Form**: Guest name, phone, party size (pax), duration, date, time, floor preference, assigned table (or auto-assign), special notes.
- **Seating Integration**: "Seat Now" validates table capacity, assigns the guest, marks reservation `SEATED`, and opens the order desk directly.

### 3.7 Masters Configuration
- **Categories Master**: CRUD for menu categories, sort orders, and active toggles.
- **Items Master**: CRUD for menu items, categories, kitchen stations (*Kitchen, Bar, Live Counter, Dessert Counter*), Veg/Non-veg, single price or dynamic size variants.
- **Floors Master**: CRUD for dining floors, shortcodes (*GF, RT, PH*), and active status.
- **Tables Master**: CRUD for tables, capacity, floor mapping, and active toggles with safety checks.

---

## 4. API Endpoints Contract (REST)
All POS endpoints are grouped under `/v1/api/pos/`:
- `GET /v1/api/pos/stats` - Floor stats, active KOT counts, today's sales.
- `GET /v1/api/pos/categories` | `POST /v1/api/pos/categories` | `PUT /v1/api/pos/categories/{id}`
- `GET /v1/api/pos/items` | `POST /v1/api/pos/items` | `PUT /v1/api/pos/items/{id}`
- `GET /v1/api/pos/floors` | `POST /v1/api/pos/floors` | `PUT /v1/api/pos/floors/{id}`
- `GET /v1/api/pos/tables` | `POST /v1/api/pos/tables` | `PUT /v1/api/pos/tables/{id}`
- `POST /v1/api/pos/orders` - Create new order (dine-in, takeaway, delivery, catering).
- `GET /v1/api/pos/orders/{id}` - Fetch order state.
- `POST /v1/api/pos/orders/{id}/items` - Update order items.
- `POST /v1/api/pos/orders/{id}/move` - Transfer order to another table.
- `POST /v1/api/pos/orders/{id}/cancel` - Cancel order.
- `POST /v1/api/pos/kots` - Fire pending items to KOT.
- `GET /v1/api/pos/kots` - Fetch active KOT tickets for Kanban board.
- `PUT /v1/api/pos/kots/{id}/status` - Move KOT between statuses.
- `POST /v1/api/pos/invoices` - Generate invoice.
- `POST /v1/api/pos/invoices/{id}/pay` - Settle invoice with payment mode.
- `GET /v1/api/pos/reservations` | `POST /v1/api/pos/reservations` | `PUT /v1/api/pos/reservations/{id}`
- `POST /v1/api/pos/reservations/{id}/seat` - Seat reservation at a table.
