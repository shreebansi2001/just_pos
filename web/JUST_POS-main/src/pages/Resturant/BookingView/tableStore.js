import { useSyncExternalStore } from "react";
import { SECTIONS } from "./tablesConfig";

// ---------------------------------------------------------------------------
// Runtime table store
// ---------------------------------------------------------------------------
// FloorPlanTableView and OrderTakingPage are on different routes, so they
// can't share state via props. This is a minimal external store (same pattern
// React's own useSyncExternalStore is designed for): one module-level object,
// a set of subscribers, and a handful of mutator functions. Any component
// that calls useTable()/useTables() re-renders the instant the cart or
// status for a table changes — including switching routes and back.
//
// If your app already has Redux/Zustand/Context set up, port this logic in
// there instead; the shape (tables keyed by id, each with status + cart) is
// the important part, not the storage mechanism.
// ---------------------------------------------------------------------------

export const TAX_RATE = 0.05; // swap for your jurisdiction's actual rate(s)
export const formatRs = (n) => `₹${n.toFixed(2)}`;

function buildInitialTables() {
  const tables = {};
  SECTIONS.forEach((section) => {
    section.tables.forEach((t) => {
      tables[t.id] = {
        seats: t.seats,
        isStool: !!t.stool,
        status: t.stool ? "stool" : "available",
        cart: [],
      };
    });
  });
  return tables;
}

let state = { tables: buildInitialTables() };
const listeners = new Set();

function emit() {
  listeners.forEach((listener) => listener());
}

function subscribe(listener) {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

function getSnapshot() {
  return state;
}

// A table's status is *derived* from its cart, except for "printed"/"paid"
// which are explicit checkout-stage flags set outside the cart itself:
//   empty cart               -> "stool" (bar seat) or "available"
//   has an unsent ("new") item -> "kot"      (needs a KOT sent)
//   all items sent            -> "running"  (order active, nothing pending)
function deriveStatus(cart, isStool) {
  if (!cart || cart.length === 0) return isStool ? "stool" : "available";
  if (cart.some((c) => c.status === "new")) return "kot";
  return "running";
}

export function getTable(tableId) {
  return state.tables[tableId];
}

export function setCart(tableId, updater) {
  const current = state.tables[tableId];
  if (!current) return;
  const nextCart = typeof updater === "function" ? updater(current.cart) : updater;
  state = {
    tables: {
      ...state.tables,
      [tableId]: { ...current, cart: nextCart, status: deriveStatus(nextCart, current.isStool) },
    },
  };
  emit();
}

// All "new" items become "sent" — table moves from "kot" to "running".
export function sendToKitchen(tableId) {
  setCart(tableId, (cart) => cart.map((c) => (c.status === "new" ? { ...c, status: "sent" } : c)));
}

// Explicit checkout-stage flag — doesn't touch the cart, so items stay
// intact if the captain needs to reopen the order.
export function markBillPrinted(tableId) {
  const current = state.tables[tableId];
  if (!current) return;
  state = { tables: { ...state.tables, [tableId]: { ...current, status: "printed" } } };
  emit();
}

// Payment recorded — clear the cart, which naturally derives back to
// "available" (or "stool") and frees the table for the next guest.
export function completeTable(tableId) {
  setCart(tableId, []);
}

export function useTables() {
  return useSyncExternalStore(subscribe, getSnapshot).tables;
}

export function useTable(tableId) {
  const tables = useTables();
  return tables[tableId];
}