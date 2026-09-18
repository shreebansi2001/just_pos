import React, { useState } from "react";
import {
  Utensils,
  ShoppingBag,
  Truck,
  ChefHat,
  PackageCheck,
  RotateCcw,
  Plus,
} from "lucide-react";
import OrderTabs from "../BookingView/OrderTabs";
import { useNavigate } from "react-router-dom";

const RUNNING_ORDER_ROWS = [
  { key: "dineIn", label: "Dine in", icon: Utensils, count: 0, amount: 0 },
  { key: "pickup", label: "Pick up", icon: ShoppingBag, count: 0, amount: 0 },
  { key: "delivery", label: "Delivery", icon: Truck, count: 0, amount: 0 },
];

const PENDING_ORDER_ROWS = [
  { key: "preparation", label: "In Preparation", icon: ChefHat, count: 0, amount: 0 },
  { key: "waitingPickup", label: "Waiting For Pickup", icon: PackageCheck, count: 0, amount: 0 },
  { key: "outForDelivery", label: "Out For Delivery", icon: Truck, count: 0, amount: 0 },
];

function SummaryCard({ title, totalOrders, totalAmount, rows }) {
  return (
    <div className="bg-white border border-slate-200 rounded-2xl p-5 flex-1 min-w-[320px]">
      <div className="flex items-center gap-2 mb-4">

        <h3 className="text-base font-semibold text-slate-800">{title}</h3>
      </div>

      <div className="grid grid-cols-2 gap-3 mb-4">
        <div className="bg-slate-50 rounded-xl px-4 py-3 text-center">
          <p className="text-md text-black mb-1">
            {title === "Running Orders" ? "Total Order" : "Total Orders"}
          </p>
          <p className="text-2xl font-bold text-slate-900">{totalOrders}</p>
        </div>
        <div className="bg-slate-50 rounded-xl px-4 py-3 text-center">
          <p className="text-xs text-black mb-1">Total Amount</p>
          <p className="text-2xl font-bold text-slate-900">₹{totalAmount.toFixed(2)}</p>
        </div>
      </div>

      <div className="divide-y divide-slate-100">
        {rows.map(({ key, label, icon: Icon, count, amount }) => (
          <div key={key} className="flex items-center justify-between py-3">
            <div className="flex items-center gap-3">
              <span className="w-9 h-9 flex items-center justify-center rounded-lg bg-slate-100 text-slate-600">
                <Icon className="w-4 h-4" />
              </span>
              <div>
                <p className="text-sm font-semibold text-slate-800">{label}</p>
                <p className="text-xs text-black">{count} orders</p>
              </div>
            </div>
            <p className="text-sm font-semibold text-slate-800">₹{amount.toFixed(2)}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default function LiveOrdersSummary({ onRefresh }) {
  const [cardView, setCardView] = useState("orders");
  const navigate = useNavigate();

  const runningTotals = {
    totalOrders: RUNNING_ORDER_ROWS.reduce((sum, r) => sum + r.count, 0),
    totalAmount: RUNNING_ORDER_ROWS.reduce((sum, r) => sum + r.amount, 0),
  };
  const pendingTotals = {
    totalOrders: PENDING_ORDER_ROWS.reduce((sum, r) => sum + r.count, 0),
    totalAmount: PENDING_ORDER_ROWS.reduce((sum, r) => sum + r.amount, 0),
  };

  return (
    <div className="px-10">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-3 mb-5  border-slate-200">
        <OrderTabs />

        <button
          className="flex items-center gap-2 px-4 py-2.5 rounded-lg text-white text-sm font-medium transition-colors"
          style={{ backgroundColor: "#9F1239" }}
          onClick={() => navigate("/ordertaking")}
        >
          <Plus size={16} />
          Book Order
        </button>
      </div>

      {/* Cards */}
      <div className="flex flex-wrap gap-4">
        <SummaryCard
          title="Running Orders"
          totalOrders={runningTotals.totalOrders}
          totalAmount={runningTotals.totalAmount}
          rows={RUNNING_ORDER_ROWS}
        />
        <SummaryCard
          title="Pending Orders"
          totalOrders={pendingTotals.totalOrders}
          totalAmount={pendingTotals.totalAmount}
          rows={PENDING_ORDER_ROWS}
        />
      </div>
    </div>
  );
}