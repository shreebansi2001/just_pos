import React, { useState, useMemo, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import {
  Search,
  Plus,
  Minus,
  X,
  ArrowLeft,
  ChefHat,
  Receipt,
  Printer,
  CheckCircle2,
} from "lucide-react";
import {
  useTable,
  setCart as storeSetCart,
  sendToKitchen,
  markBillPrinted,
  completeTable,
  TAX_RATE,
  formatRs,
} from "./BookingView/tableStore";

/**
 * OrderTakingPage
 * ----------------
 * Reads which table it's for from router state (set by FloorPlanTableView's
 * navigate("/ordertaking", { state: { tableId } })), then reads/writes that
 * table's cart through the shared tableStore. Every action here — adding an
 * item, sending a KOT, printing a bill, marking paid — updates the store, so
 * the floor plan reflects it the instant you navigate back.
 *
 * Swap MENU below for a real API call (categories + items with shortCode).
 */

const CATEGORIES = [
  { id: "starters", name: "Starters" },
  { id: "mains", name: "Main Course" },
  { id: "breads", name: "Breads" },
  { id: "rice", name: "Rice & Biryani" },
  { id: "beverages", name: "Beverages" },
  { id: "desserts", name: "Desserts" },
];

const MENU_ITEMS = [
  { id: "i1", categoryId: "starters", name: "Paneer Tikka", price: 220, code: "PT1", veg: true },
  { id: "i2", categoryId: "starters", name: "Chilli Garlic Mushroom", price: 190, code: "CGM", veg: true },
  { id: "i3", categoryId: "starters", name: "Chicken 65", price: 260, code: "C65", veg: false },
  { id: "i4", categoryId: "starters", name: "Veg Seekh Kebab", price: 210, code: "VSK", veg: true },
  { id: "i5", categoryId: "starters", name: "Tandoori Chicken (Half)", price: 320, code: "TCH", veg: false },

  { id: "i6", categoryId: "mains", name: "Paneer Butter Masala", price: 260, code: "PBM", veg: true },
  { id: "i7", categoryId: "mains", name: "Dal Makhani", price: 200, code: "DAL", veg: true },
  { id: "i8", categoryId: "mains", name: "Butter Chicken", price: 320, code: "BCK", veg: false },
  { id: "i9", categoryId: "mains", name: "Kadai Vegetable", price: 220, code: "KDV", veg: true },
  { id: "i10", categoryId: "mains", name: "Mutton Rogan Josh", price: 380, code: "MRJ", veg: false },

  { id: "i11", categoryId: "breads", name: "Tandoori Roti", price: 30, code: "TRT", veg: true },
  { id: "i12", categoryId: "breads", name: "Butter Naan", price: 45, code: "BNN", veg: true },
  { id: "i13", categoryId: "breads", name: "Garlic Naan", price: 55, code: "GNN", veg: true },
  { id: "i14", categoryId: "breads", name: "Laccha Paratha", price: 50, code: "LPR", veg: true },

  { id: "i15", categoryId: "rice", name: "Veg Biryani", price: 240, code: "VBR", veg: true },
  { id: "i16", categoryId: "rice", name: "Chicken Biryani", price: 290, code: "CBR", veg: false },
  { id: "i17", categoryId: "rice", name: "Jeera Rice", price: 150, code: "JRC", veg: true },
  { id: "i18", categoryId: "rice", name: "Curd Rice", price: 160, code: "CRD", veg: true },

  { id: "i19", categoryId: "beverages", name: "Masala Chaas", price: 70, code: "MCH", veg: true },
  { id: "i20", categoryId: "beverages", name: "Fresh Lime Soda", price: 80, code: "FLS", veg: true },
  { id: "i21", categoryId: "beverages", name: "Cold Coffee", price: 120, code: "CCF", veg: true },
  { id: "i22", categoryId: "beverages", name: "Masala Chai", price: 60, code: "CHY", veg: true },

  { id: "i23", categoryId: "desserts", name: "Gulab Jamun (2 pc)", price: 90, code: "GLJ", veg: true },
  { id: "i24", categoryId: "desserts", name: "Rasmalai (2 pc)", price: 110, code: "RSM", veg: true },
  { id: "i25", categoryId: "desserts", name: "Chocolate Brownie", price: 140, code: "BRW", veg: true },
];

export default function OrderTakingPage() {
  const location = useLocation();
  const navigate = useNavigate();

  // Fallback lets this file still render in isolation (e.g. a component
  // preview) without a router state; in the app it always comes from
  // FloorPlanTableView's navigate() call.
  const tableId = location.state?.tableId ?? "T3";
  const runtime = useTable(tableId);
  const cart = runtime?.cart ?? [];

  const [activeCategory, setActiveCategory] = useState(CATEGORIES[0].id);
  const [categoryQuery, setCategoryQuery] = useState("");
  const [itemQuery, setItemQuery] = useState("");
  const [shortCode, setShortCode] = useState("");
  const [shortCodeState, setShortCodeState] = useState("idle");
  const [billOpen, setBillOpen] = useState(false);
  const [toast, setToast] = useState(null);
  const shortCodeRef = useRef(null);

  const filteredCategories = useMemo(
    () => CATEGORIES.filter((c) => c.name.toLowerCase().includes(categoryQuery.trim().toLowerCase())),
    [categoryQuery]
  );

  const filteredItems = useMemo(() => {
    const q = itemQuery.trim().toLowerCase();
    return MENU_ITEMS.filter((it) => it.categoryId === activeCategory).filter(
      (it) => !q || it.name.toLowerCase().includes(q) || it.code.toLowerCase().includes(q)
    );
  }, [activeCategory, itemQuery]);

  const showToast = (msg) => {
    setToast(msg);
    window.clearTimeout(showToast._t);
    showToast._t = window.setTimeout(() => setToast(null), 2200);
  };

  const addItem = (item) => {
    storeSetCart(tableId, (prev) => {
      const existing = prev.find((c) => c.id === item.id);
      if (existing) {
        return prev.map((c) => (c.id === item.id ? { ...c, qty: c.qty + 1 } : c));
      }
      return [...prev, { id: item.id, name: item.name, price: item.price, qty: 1, status: "new" }];
    });
  };

  const changeQty = (id, delta) => {
    storeSetCart(tableId, (prev) =>
      prev.map((c) => (c.id === id ? { ...c, qty: c.qty + delta } : c)).filter((c) => c.qty > 0)
    );
  };

  const removeItem = (id) => storeSetCart(tableId, (prev) => prev.filter((c) => c.id !== id));

  const handleShortCodeSubmit = (e) => {
    e.preventDefault();
    const code = shortCode.trim().toLowerCase();
    if (!code) return;
    const match = MENU_ITEMS.find((it) => it.code.toLowerCase() === code);
    if (match) {
      addItem(match);
      setShortCodeState("ok");
      showToast(`Added ${match.name}`);
    } else {
      setShortCodeState("error");
      showToast(`No item with code "${shortCode.trim()}"`);
    }
    setShortCode("");
    window.setTimeout(() => setShortCodeState("idle"), 700);
    shortCodeRef.current?.focus();
  };

  const subtotal = cart.reduce((sum, c) => sum + c.price * c.qty, 0);
  const tax = subtotal * TAX_RATE;
  const total = subtotal + tax;
  const newCount = cart.filter((c) => c.status === "new").reduce((s, c) => s + c.qty, 0);

  const sendToKOT = () => {
    const pendingCount = newCount;
    if (pendingCount === 0) {
      showToast("No new items to send");
      return;
    }
    sendToKitchen(tableId); // -> table status becomes "running" on the floor plan
    showToast(`Sent ${pendingCount} item(s) to KOT`);
  };

  const generateBill = () => {
    if (cart.length === 0) {
      showToast("Add items before generating a bill");
      return;
    }
    setBillOpen(true);
  };

  const handlePrint = () => {
    markBillPrinted(tableId); // -> table status becomes "printed" on the floor plan
    showToast("Bill sent to printer");
  };

  const handleMarkPaid = () => {
    completeTable(tableId); // clears cart -> table becomes "available" again
    showToast("Payment recorded — table cleared");
    setBillOpen(false);
    navigate("/order-booking"); // back to order booking
  };

  return (
    <div className="flex h-screen w-full flex-col bg-zinc-50 font-sans text-zinc-900">
      {/* Top bar */}
      <div className="flex items-center justify-between border-b border-zinc-200 bg-white px-5 py-3">
        <div className="flex items-center gap-3">
          <button
            onClick={() => navigate("/order/live-order")}
            className="flex items-center gap-1.5 rounded-md border border-zinc-300 px-3 py-1.5 text-sm font-medium text-zinc-700 hover:bg-zinc-100 transition cursor-pointer shadow-2xs"
          >
            <ArrowLeft className="h-4 w-4" />
            Back
          </button>
          <div className="h-6 w-px bg-zinc-200" />
          <div>
            <div className="flex items-center gap-2">
              <span className="text-lg font-semibold text-rose-800">{tableId}</span>
              <span className="rounded-full bg-rose-50 px-2 py-0.5 text-xs font-medium text-rose-700">
                Dine-in
              </span>
            </div>
            <p className="text-xs text-zinc-400">{runtime?.seats ? `${runtime.seats} Seats` : ""}</p>
          </div>
        </div>

        <div className="flex items-center gap-2 font-mono text-xs text-primary">
          {new Date().toLocaleDateString(undefined, { weekday: "short", month: "short", day: "numeric" })}
        </div>
      </div>

      {/* Body: 3 columns */}
      <div className="flex flex-1 overflow-hidden">
        {/* LEFT — categories */}
        <div className="flex w-56 shrink-0 flex-col border-r border-rose-100 ">
          <div className="p-3">
            <div className="relative">
              <Search className="pointer-events-none absolute left-2.5 top-2.5 h-3.5 w-3.5 text-zinc-400" />
              <input
                value={categoryQuery}
                onChange={(e) => setCategoryQuery(e.target.value)}
                placeholder="Search category"
                className="w-full rounded-md border border-rose-200 bg-white py-2 pl-8 pr-2 text-sm text-zinc-800 placeholder-zinc-400 outline-none focus:border-rose-600"
              />
            </div>
          </div>
          <div className="flex-1 space-y-1 overflow-y-auto px-2 pb-3">
            <p className="px-2 pb-1 pt-1 text-[11px] font-semibold uppercase tracking-wider text-primary">
              Categories
            </p>
            {filteredCategories.map((c) => {
              const count = MENU_ITEMS.filter((it) => it.categoryId === c.id).length;
              const active = c.id === activeCategory;
              return (
                <button
                  key={c.id}
                  onClick={() => {
                    setActiveCategory(c.id);
                    setItemQuery("");
                  }}
                  className={`flex w-full items-center justify-between rounded-md px-3 py-2.5 text-left text-sm transition ${active ? "bg-primary text-white shadow-sm" : "text-zinc-600 hover:bg-rose-100"
                    }`}
                >
                  <span className="font-medium">{c.name}</span>
                  <span className={`font-mono text-xs ${active ? "text-rose-200" : "text-zinc-400"}`}>
                    {count}
                  </span>
                </button>
              );
            })}
            {filteredCategories.length === 0 && (
              <p className="px-3 py-4 text-center text-xs text-zinc-400">No categories match.</p>
            )}
          </div>
        </div>

        {/* MIDDLE — items */}
        <div className="flex flex-1 flex-col overflow-hidden">
          <div className="flex items-center gap-3 border-b border-zinc-200 bg-white px-4 py-3">
            <div className="relative flex-1">
              <Search className="pointer-events-none absolute left-2.5 top-2.5 h-3.5 w-3.5 text-zinc-400" />
              <input
                value={itemQuery}
                onChange={(e) => setItemQuery(e.target.value)}
                placeholder={`Search items in ${CATEGORIES.find((c) => c.id === activeCategory)?.name ?? ""}`}
                className="w-full rounded-md border border-zinc-300 py-2 pl-8 pr-2 text-sm outline-none focus:border-rose-600"
              />
            </div>
            <form onSubmit={handleShortCodeSubmit} className="flex items-center gap-2">
              <input
                ref={shortCodeRef}
                value={shortCode}
                onChange={(e) => {
                  setShortCode(e.target.value.toUpperCase());
                  setShortCodeState("idle");
                }}
                placeholder="Short code + Enter"
                className={`w-44 rounded-md border py-2 pl-2 pr-2 font-mono text-sm uppercase tracking-wider outline-none transition ${shortCodeState === "error"
                  ? "border-red-400 bg-red-50 text-red-700"
                  : shortCodeState === "ok"
                    ? "border-emerald-400 bg-emerald-50 text-emerald-700"
                    : "border-zinc-300 focus:border-primary"
                  }`}
              />
              <button
                type="submit"
                className="rounded-md bg-primary px-3 py-2 text-sm font-medium text-white hover:bg-primary-active"
              >
                Add
              </button>
            </form>
          </div>

          <div className="flex-1 overflow-y-auto p-4">
            <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 xl:grid-cols-4">
              {filteredItems.map((item) => (
                <button
                  key={item.id}
                  onClick={() => addItem(item)}
                  className="group relative flex h-28 flex-col justify-between rounded-lg border border-zinc-200 bg-white p-3 text-left shadow-sm transition hover:-translate-y-0.5 hover:border-rose-300 hover:shadow-md"
                >
                  <div className="flex items-start justify-between">
                    <span
                      className={`mt-0.5 h-3 w-3 shrink-0 rounded-sm border-2 ${item.veg ? "border-emerald-600" : "border-red-600"
                        } flex items-center justify-center`}
                    >
                      <span className={`h-1.5 w-1.5 rounded-full ${item.veg ? "bg-emerald-600" : "bg-red-600"}`} />
                    </span>
                    <span className="rounded bg-zinc-100 px-1.5 py-0.5 font-mono text-[10px] tracking-wider text-zinc-400 group-hover:bg-amber-50 group-hover:text-amber-600">
                      {item.code}
                    </span>
                  </div>
                  <div>
                    <p className="text-sm font-medium leading-snug text-zinc-800">{item.name}</p>
                    <p className="font-mono text-sm text-rose-800">{formatRs(item.price)}</p>
                  </div>
                  <Plus className="absolute bottom-2 right-2 h-4 w-4 text-zinc-300 group-hover:text-rose-700" />
                </button>
              ))}
            </div>
            {filteredItems.length === 0 && (
              <p className="mt-10 text-center text-sm text-zinc-400">
                No items match "{itemQuery}" in this category.
              </p>
            )}
          </div>
        </div>

        {/* RIGHT — order summary */}
        <div className="flex w-80 shrink-0 flex-col border-l border-zinc-200 bg-white">
          <div className="flex items-center justify-between border-b border-zinc-200 px-4 py-3">
            <p className="text-[11px] font-semibold uppercase tracking-wider text-zinc-500">Order Summary</p>
            {newCount > 0 && (
              <span className="rounded-full bg-amber-100 px-2 py-0.5 text-xs font-medium text-amber-700">
                {newCount} new
              </span>
            )}
          </div>

          <div className="flex-1 overflow-y-auto px-4 py-3">
            {cart.length === 0 ? (
              <div className="flex h-full flex-col items-center justify-center text-center text-zinc-400">
                <Receipt className="mb-2 h-8 w-8 text-zinc-200" />
                <p className="text-sm">No items yet.</p>
                <p className="text-xs">Tap a menu item or use a short code.</p>
              </div>
            ) : (
              <ul className="space-y-3">
                {cart.map((c) => (
                  <li key={c.id} className="border-b border-dashed border-zinc-200 pb-3">
                    <div className="flex items-start justify-between gap-2">
                      <div className="min-w-0">
                        <p className="truncate text-sm font-medium text-zinc-800">{c.name}</p>
                        <div className="mt-0.5 flex items-center gap-1.5">
                          <span
                            className={`rounded px-1.5 py-0.5 text-[10px] font-medium uppercase tracking-wide ${c.status === "sent" ? "bg-emerald-50 text-emerald-600" : "bg-amber-50 text-amber-600"
                              }`}
                          >
                            {c.status === "sent" ? "Sent to KOT" : "New"}
                          </span>
                        </div>
                      </div>
                      <button onClick={() => removeItem(c.id)} className="shrink-0 text-zinc-300 hover:text-red-500">
                        <X className="h-4 w-4" />
                      </button>
                    </div>
                    <div className="mt-2 flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <button
                          onClick={() => changeQty(c.id, -1)}
                          className="flex h-6 w-6 items-center justify-center rounded border border-zinc-300 text-zinc-500 hover:bg-zinc-100"
                        >
                          <Minus className="h-3 w-3" />
                        </button>
                        <span className="w-5 text-center font-mono text-sm">{c.qty}</span>
                        <button
                          onClick={() => changeQty(c.id, 1)}
                          className="flex h-6 w-6 items-center justify-center rounded border border-zinc-300 text-zinc-500 hover:bg-zinc-100"
                        >
                          <Plus className="h-3 w-3" />
                        </button>
                      </div>
                      <span className="font-mono text-sm text-zinc-700">{formatRs(c.price * c.qty)}</span>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>

          {/* Totals + actions */}
          <div className="border-t border-zinc-200 p-4">
            <div className="space-y-1 font-mono text-sm text-zinc-600">
              <div className="flex justify-between">
                <span>Subtotal</span>
                <span>{formatRs(subtotal)}</span>
              </div>
              <div className="flex justify-between">
                <span>Tax ({(TAX_RATE * 100).toFixed(0)}%)</span>
                <span>{formatRs(tax)}</span>
              </div>
              <div className="mt-1 flex justify-between border-t border-zinc-200 pt-2 text-base font-semibold text-zinc-900">
                <span>Total</span>
                <span>{formatRs(total)}</span>
              </div>
            </div>

            <div className="mt-3 flex flex-col gap-2">
              <button
                onClick={sendToKOT}
                className="flex items-center justify-center gap-2 rounded-md border-2 border-rose-800 py-2.5 text-sm font-semibold text-rose-800 transition hover:bg-rose-50 disabled:cursor-not-allowed disabled:opacity-40"
                disabled={cart.length === 0}
              >
                <ChefHat className="h-4 w-4" />
                Send to KOT
              </button>
              <button
                onClick={generateBill}
                className="flex items-center justify-center gap-2 rounded-md bg-rose-800 py-2.5 text-sm font-semibold text-white transition hover:bg-rose-900 disabled:cursor-not-allowed disabled:opacity-40"
                disabled={cart.length === 0}
              >
                <Receipt className="h-4 w-4" />
                Generate Bill
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Toast */}
      {toast && (
        <div className="pointer-events-none fixed bottom-5 left-1/2 z-50 -translate-x-1/2 rounded-md bg-zinc-900 px-4 py-2 text-sm text-white shadow-lg">
          {toast}
        </div>
      )}

      {/* Bill modal */}
      {billOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4" onClick={() => setBillOpen(false)}>
          <div className="w-full max-w-sm rounded-lg bg-white shadow-xl" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between border-b border-dashed border-zinc-200 px-5 py-4">
              <div>
                <p className="text-sm font-semibold text-zinc-900">Bill · {tableId}</p>
                <p className="text-xs text-zinc-400">
                  {new Date().toLocaleString(undefined, { dateStyle: "medium", timeStyle: "short" })}
                </p>
              </div>
              <button onClick={() => setBillOpen(false)} className="text-zinc-300 hover:text-zinc-600">
                <X className="h-4 w-4" />
              </button>
            </div>

            <div className="max-h-64 overflow-y-auto px-5 py-3">
              <ul className="space-y-2 font-mono text-sm">
                {cart.map((c) => (
                  <li key={c.id} className="flex justify-between text-zinc-700">
                    <span className="truncate pr-2">
                      {c.qty} × {c.name}
                    </span>
                    <span>{formatRs(c.price * c.qty)}</span>
                  </li>
                ))}
              </ul>
            </div>

            <div className="space-y-1 border-t border-dashed border-zinc-200 px-5 py-3 font-mono text-sm text-zinc-600">
              <div className="flex justify-between">
                <span>Subtotal</span>
                <span>{formatRs(subtotal)}</span>
              </div>
              <div className="flex justify-between">
                <span>Tax ({(TAX_RATE * 100).toFixed(0)}%)</span>
                <span>{formatRs(tax)}</span>
              </div>
              <div className="flex justify-between pt-1 text-base font-semibold text-zinc-900">
                <span>Grand Total</span>
                <span>{formatRs(total)}</span>
              </div>
            </div>

            <div className="flex gap-2 px-5 pb-5 pt-2">
              <button
                onClick={handlePrint}
                className="flex flex-1 items-center justify-center gap-2 rounded-md border border-zinc-300 py-2.5 text-sm font-medium text-zinc-700 hover:bg-zinc-50"
              >
                <Printer className="h-4 w-4" />
                Print
              </button>
              <button
                onClick={handleMarkPaid}
                className="flex flex-1 items-center justify-center gap-2 rounded-md bg-emerald-600 py-2.5 text-sm font-medium text-white hover:bg-emerald-700"
              >
                <CheckCircle2 className="h-4 w-4" />
                Mark Paid
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}