import React, { useState, useMemo } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import {
  Clock,
  AlertTriangle,
  CheckCircle2,
  Printer,
  Pencil,
  ChevronDown,
  RotateCcw,
  ArrowDown,
  ArrowUp,
  History,
  ClipboardList
} from "lucide-react";

/**
 * KOTDashboard
 * Kitchen Order Ticket board — Pending / Accepted / Preparing columns.
 *
 * Drop-in usage:
 *   <KOTDashboard orders={ordersFromApi} onStatusChange={(id, status) => ...} />
 *
 * If no `orders` prop is passed, falls back to MOCK_ORDERS below so this
 * renders standalone for design review.
 */

const STATUS = {
  PENDING: "pending",
  ACCEPTED: "accepted",
  PREPARING: "preparing",
  READY: "ready",
};

const MOCK_ORDERS = [
  {
    id: "kot-4582",
    kot: "4582",
    time: "22:55",
    table: "T-14",
    floor: "Rooftop",
    guests: 4,
    waiter: "Arun",
    status: STATUS.PENDING,
    vip: true,
    delayed: true,
    orderType: "dineIn",
    items: [
      { id: "i1", name: "Grilled Sea Bass", qty: 2, note: "No Lemon, extra virgin olive oil", tag: "Delayed" },
      { id: "i2", name: "Lobster Thermidor", qty: 1, note: null, tag: "Preparing" },
    ],
    chefNote: "Allergic to shell-fish (Guest 3). Prep sea bass in separate pan.",
  },
  {
    id: "kot-4590",
    kot: "4590",
    time: "04:22",
    table: "T-02",
    floor: "GF",
    guests: 2,
    waiter: "Priya",
    status: STATUS.PENDING,
    vip: false,
    delayed: false,
    orderType: "delivery",
    items: [{ id: "i3", name: "Margherita Pizza", qty: 1, note: "Add Extra Basil", tag: null }],
    chefNote: null,
  },
  {
    id: "kot-4588",
    kot: "4588",
    time: "08:40",
    table: "T-08",
    floor: "GF",
    guests: 3,
    waiter: "Sam",
    status: STATUS.ACCEPTED,
    vip: false,
    delayed: false,
    orderType: "pickup",
    items: [{ id: "i4", name: "Truffle Risotto", qty: 2, note: null, tag: null }],
    chefNote: null,
  },
  {
    id: "kot-4579",
    kot: "4579",
    time: "22:10",
    table: "T-05",
    floor: "GF",
    guests: 5,
    waiter: "Sam",
    status: STATUS.PREPARING,
    vip: false,
    delayed: false,
    orderType: "website",
    items: [
      { id: "i5", name: "Caesar Salad", qty: 1, note: null, tag: null, done: true },
      { id: "i6", name: "Wagyu Burger", qty: 2, note: null, tag: null, done: false },
    ],
    chefNote: null,
  },
];


const ORDER_TYPE_META = {
  delivery: { label: "Delivery", dot: "bg-green-500", bg: "bg-green-50", text: "text-green-700" },
  dineIn: { label: "Dine In", dot: "bg-amber-500", bg: "bg-amber-50", text: "text-amber-700" },
  pickup: { label: "Pick Up", dot: "bg-sky-500", bg: "bg-sky-50", text: "text-sky-700" },
};

const STATUS_META = {
  [STATUS.PENDING]: { label: "Pending", bg: "bg-blue-50", text: "text-blue-700" },
  [STATUS.ACCEPTED]: { label: "Accepted", bg: "bg-purple-50", text: "text-purple-700" },
  [STATUS.PREPARING]: { label: "Preparing", bg: "bg-amber-50", text: "text-amber-700" },
};

const COLUMN_META = {
  [STATUS.PENDING]: { label: "Pending", dot: "bg-blue-600" },
  [STATUS.ACCEPTED]: { label: "Accepted", dot: "bg-purple-600" },
  [STATUS.PREPARING]: { label: "Preparing", dot: "bg-amber-500" },
};

const FILTER_CHIPS = ["Ground Floor", "Priority: VIP", "Type: Dine-in", "Waiter: All", "Captain: Jack S."];

function ItemRow({ item, onToggle, showCheckbox }) {
  return (
    <div className="flex items-start justify-between gap-2 py-1">
      <div className="flex items-start gap-2 min-w-0">
        {showCheckbox && (
          <button
            onClick={onToggle}
            className={`mt-0.5 w-4 h-4 shrink-0 rounded border flex items-center justify-center transition-colors ${item.done ? "bg-green-600 border-green-600" : "border-slate-300 bg-white"
              }`}
            aria-label={item.done ? "Mark item not done" : "Mark item done"}
          >
            {item.done && <CheckCircle2 className="w-3 h-3 text-white" strokeWidth={3} />}
          </button>
        )}
        <div className="min-w-0">
          <p className={`text-sm font-medium text-black truncate ${item.done ? "line-through text-slate-400" : ""}`}>
            {item.name} <span className="text-slate-500 font-normal">x{item.qty}</span>
          </p>
          {item.note && <p className="text-xs text-slate-400 italic truncate">{item.note}</p>}
        </div>
      </div>
    </div>
  );
}

function OrderCard({ order, onAdvance, onToggleItem }) {
  const doneCount = order.items.filter((i) => i.done).length;
  const progressPct = order.items.length ? Math.round((doneCount / order.items.length) * 100) : 0;
  const typeMeta = ORDER_TYPE_META[order.orderType] || ORDER_TYPE_META.dineIn;
  const statusMeta = STATUS_META[order.status] || STATUS_META[STATUS.PENDING];

  return (
    <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
      {/* Accent bar — order type color */}
      <div className={typeMeta.dot + " h-1"} />

      <div className="p-4 flex flex-col gap-3">
        {/* Header: type pill + status pill */}
        <div className="flex items-center justify-between gap-2">


        </div>

        {/* Stat row: Table / KOT / Time */}
        <div className="flex items-center divide-x divide-slate-300 border border-slate-300 rounded-xl py-2">
          <div className="flex-1 text-center">
            <p className="text-base font-semibold text-black leading-none">{order.table}</p>
            <p className="text-[10px] text-slate-400 uppercase tracking-wide mt-1">Table</p>
          </div>
          <div className="flex-1 text-center">
            <p className="text-base font-semibold text-black leading-none">{order.kot}</p>
            <p className="text-[10px] text-slate-400 uppercase tracking-wide mt-1">KOT No.</p>
          </div>
          <div className="flex-1 text-center">
            <p className="text-base font-semibold text-black leading-none">{order.time}</p>
            <p className="text-[10px] text-slate-400 uppercase tracking-wide mt-1">Time</p>
          </div>
        </div>

        {/* Secondary meta */}
        <div className="flex flex-wrap gap-x-4 gap-y-0.5 text-xs text-slate-500">
          <span>
            Floor: <span className="font-medium text-slate-700">{order.floor}</span>
          </span>
          <span>
            Guests: <span className="font-medium text-slate-700">{order.guests}</span>
          </span>
        </div>

        {/* Items */}
        <div className="divide-y divide-slate-100">
          {order.items.map((item) => (
            <ItemRow
              key={item.id}
              item={item}

              onToggle={() => onToggleItem(order.id, item.id)}
            />
          ))}
        </div>

        {/* Preparing progress bar */}


        {/* Chef notes */}
        {order.chefNote && (
          <div className="bg-orange-50 border border-orange-100 rounded-lg px-3 py-2">
            <p className="text-[11px] font-semibold text-orange-600 uppercase tracking-wide mb-0.5">notes</p>
            <p className="text-xs text-orange-700 leading-snug">{order.chefNote}</p>
          </div>
        )}

        {/* Actions */}
        <div className="flex items-center gap-2 pt-1">
          {order.status === STATUS.PENDING && (
            <button
              onClick={() => onAdvance(order.id, STATUS.ACCEPTED)}
              className="flex-1 bg-primary  text-white text-sm font-semibold rounded-lg py-2 transition-colors"
            >
              Accept Order
            </button>
          )}
          {order.status === STATUS.ACCEPTED && (
            <button
              onClick={() => onAdvance(order.id, STATUS.PREPARING)}
              className="flex-1 bg-primary  text-white text-sm font-semibold rounded-lg py-2 transition-colors"
            >
              Start Preparing
            </button>
          )}
          {order.status === STATUS.PREPARING && (
            <button
              onClick={() => onAdvance(order.id, STATUS.READY)}
              className="flex-1 bg-primary  text-white text-sm font-semibold rounded-lg py-2 transition-colors"
            >
              Mark Ready
            </button>
          )}
          {order.status === STATUS.PENDING && (
            <button className="w-9 h-9 flex items-center justify-center rounded-lg border border-slate-200 text-slate-500 hover:bg-slate-50">
              <Printer className="w-4 h-4" />
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

export default function KOTDashboard({ orders: ordersProp, onStatusChange }) {
  const [orders, setOrders] = useState(ordersProp || MOCK_ORDERS);
  const [stationFilter, setStationFilter] = useState("All Stations");
  const [showScrollTop, setShowScrollTop] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const isHistory = location.pathname === "/kot-history";

  React.useEffect(() => {
    const handleScroll = () => setShowScrollTop(window.scrollY > 240);
    window.addEventListener("scroll", handleScroll, { passive: true });
    handleScroll();
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const advanceOrder = (id, nextStatus) => {
    setOrders((prev) => prev.map((o) => (o.id === id ? { ...o, status: nextStatus, delayed: false } : o)));
    onStatusChange?.(id, nextStatus);
  };

  const toggleItem = (orderId, itemId) => {
    setOrders((prev) =>
      prev.map((o) =>
        o.id === orderId
          ? { ...o, items: o.items.map((it) => (it.id === itemId ? { ...it, done: !it.done } : it)) }
          : o
      )
    );
  };

  const columns = [STATUS.PENDING, STATUS.ACCEPTED, STATUS.PREPARING];

  return (
    <div className="min-h-screen px-10">
      <div className="flex items-center gap-8 border-b border-slate-200 mb-6">
        {/* <button
  onClick={() => navigate("/kot-view")}
  className={`flex items-center gap-2 pb-3 text-lg font-semibold border-b-[3px] transition ${
    !isHistory
      ? "text-primary border-primary"
      : "text-slate-500 border-transparent hover:text-slate-700"
  }`}
>
  <ClipboardList className="w-6 h-6" />
  KOT View
</button> */}

        <button
          onClick={() => navigate("/kot-history")}
          className={`flex items-center gap-2 pb-3 text-base font-semibold border-b-[3px] transition ${isHistory
            ? "text-primary border-primary"
            : "text-slate-500 border-transparent hover:text-slate-700"
            }`}
        >
          <History className="w-5 h-5" />
          Kot History
        </button>
      </div>
      {/* Order-type legend */}
      <div className="flex justify-end mb-4">
        <div className="inline-flex flex-wrap items-center gap-4 w-fit border border-slate-200 rounded-full px-2.5 py-1">
          {Object.entries(ORDER_TYPE_META).map(([key, meta]) => (
            <span
              key={key}
              className="inline-flex items-center gap-1.5 text-xs font-medium text-slate-600"
            >
              <span className={`w-2.5 h-2.5 rounded-full ${meta.dot}`} />
              {meta.label}
            </span>
          ))}
        </div>
      </div>


      {/* Board */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {columns.map((statusKey) => {
          const columnOrders = orders.filter((o) => o.status === statusKey);
          const meta = COLUMN_META[statusKey];
          return (
            <div key={statusKey} className="flex flex-col gap-3">
              <div className="flex items-center gap-2 px-1">

                <h2 className="text-sm font-semibold text-slate-700">{meta.label}</h2>
                <span className="text-xs text-slate-400">
                  {String(columnOrders.length).padStart(2, "0")}
                </span>
              </div>
              <div className="flex flex-col gap-3">
                {columnOrders.length === 0 && (
                  <div className="text-xs text-slate-400 italic px-1 py-6 text-center border border-dashed border-slate-200 rounded-xl">
                    No orders here
                  </div>
                )}
                {columnOrders.map((order) => (
                  <OrderCard key={order.id} order={order} onAdvance={advanceOrder} onToggleItem={toggleItem} />
                ))}
              </div>
            </div>
          );
        })}
      </div>

      {/* Floating helper buttons */}
      <div className="fixed bottom-6 right-6 flex flex-col gap-2">
        <button
          onClick={() => setOrders(ordersProp || MOCK_ORDERS)}
          className="w-11 h-11 rounded-full bg-white border border-slate-200 shadow flex items-center justify-center text-slate-500 hover:bg-slate-50"
          aria-label="Reset board"
          title="Reset board"
        >
          <RotateCcw className="w-4 h-4" />
        </button>
        {showScrollTop && (
          <button
            onClick={scrollToTop}
            className="w-11 h-11 rounded-full bg-blue-600 shadow flex items-center justify-center text-white hover:bg-blue-700 transition-opacity"
            aria-label="Scroll to top"
            title="Scroll to top"
          >
            <ArrowUp className="w-4 h-4" />
          </button>
        )}
      </div>
    </div>
  );
}