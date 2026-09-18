import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Plus } from "lucide-react";
import NewReservationModal from "./Newreservationmodal";
import { SECTIONS } from "./tablesConfig";
import { useTables, TAX_RATE, formatRs } from "./tableStore";
import OrderTabs from "./OrderTabs";

const STATUS_STYLES = {
  available: {
    bg: "bg-white",
    border: "border-gray-400",
    text: "text-gray-800",
    sub: "text-gray-400",
  },
  running: {
    bg: "bg-blue-50",
    border: "border-blue-300",
    text: "text-blue-900",
    sub: "text-blue-500",
  },
  printed: {
    bg: "bg-green-50",
    border: "border-green-300",
    text: "text-green-900",
    sub: "text-green-600",
  },
  paid: {
    bg: "bg-amber-50",
    border: "border-amber-300",
    text: "text-amber-900",
    sub: "text-amber-600",
  },
  kot: {
    bg: "bg-red-50",
    border: "border-red-300",
    text: "text-red-900",
    sub: "text-red-500",
  },
  stool: {
    bg: "bg-white",
    border: "border-dashed border-gray-300",
    text: "text-gray-400",
    sub: "text-gray-300",
  },
};

const LEGEND = [
  { key: "available", label: "Available" },
  { key: "running", label: "Running Table" },
  { key: "kot", label: "Running KOT" },
  { key: "printed", label: "Printed Table" },
  { key: "paid", label: "Paid Table" },
];

const STATUS_ICON = { running: "print", printed: "print", kot: "kot", paid: "check" };

function tableMeta(table, runtime) {
  switch (runtime.status) {
    case "available":
      return `${table.seats} Seats`;
    case "stool":
      return "Stool";
    case "kot":
      return "Pending KOT";
    case "printed":
      return "Bill Printed";
    case "paid":
      return "Paid";
    case "running": {
      const subtotal = runtime.cart.reduce((sum, c) => sum + c.price * c.qty, 0);
      const total = subtotal * (1 + TAX_RATE);
      return formatRs(total);
    }
    default:
      return "";
  }
}

function StatusIcon({ icon, className }) {
  if (icon === "print") {
    return (
      <svg viewBox="0 0 24 24" className={className} fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M6 9V3h12v6" strokeLinecap="round" strokeLinejoin="round" />
        <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2" strokeLinecap="round" strokeLinejoin="round" />
        <rect x="6" y="14" width="12" height="7" strokeLinecap="round" strokeLinejoin="round" />
      </svg>
    );
  }
  if (icon === "check") {
    return (
      <svg viewBox="0 0 24 24" className={className} fill="none" stroke="currentColor" strokeWidth="2">
        <circle cx="12" cy="12" r="9" />
        <path d="M8.5 12.5l2.2 2.2L15.5 9.5" strokeLinecap="round" strokeLinejoin="round" />
      </svg>
    );
  }
  if (icon === "kot") {
    return (
      <svg viewBox="0 0 24 24" className={className} fill="none" stroke="currentColor" strokeWidth="2">
        <circle cx="12" cy="12" r="9" />
        <path d="M12 7v5l3 2" strokeLinecap="round" strokeLinejoin="round" />
      </svg>
    );
  }
  return null;
}

function TableCard({ table, runtime, onSelect }) {
  const s = STATUS_STYLES[runtime.status];
  const icon = STATUS_ICON[runtime.status];
  return (
    <button
      type="button"
      onClick={() => onSelect(table)}
      className={`relative flex h-24 w-full flex-col justify-between rounded-lg border-2 ${s.bg} ${s.border} p-3 text-left transition hover:shadow-sm`}
    >
      <span className={`text-sm font-semibold ${s.text}`}>{table.id}</span>
      <span className={`text-md ${s.sub}`}>{tableMeta(table, runtime)}</span>
      {icon && <StatusIcon icon={icon} className={`absolute bottom-2 right-2 h-4 w-4 ${s.sub}`} />}
    </button>
  );
}

function LegendDot({ statusKey }) {
  const s = STATUS_STYLES[statusKey];
  return <span className={`inline-block h-3 w-3 rounded-sm border ${s.bg} ${s.border}`} />;
}

export default function FloorPlanTableView() {
  const navigate = useNavigate();
  const [reservationOpen, setReservationOpen] = useState(false);
  const [selectedTable, setSelectedTable] = useState(null);
  const tables = useTables();

  const handleTableClick = (table) => {
    navigate("/ordertaking", { state: { tableId: table.id } });
  };
  return (
    <div className="min-h-screen w-full px-10">
      <div className="">
        {/* Header */}
        <OrderTabs />

        {/* Action Button */}
        <div className="mb-6">
          {/* <button
            onClick={() => navigate("/ordertaking")}
            className="flex items-center gap-2 rounded-xl bg-red-600 px-5 py-2.5 text-sm font-semibold text-white hover:bg-red-700 transition shadow-sm cursor-pointer"
          >
            <Plus className="h-4 w-4" />
            Book Order
          </button> */}
        </div>
        {/*
        <div className="flex justify-end mb-6">
          <div className="inline-flex flex-wrap items-center gap-4 w-fit border border-gray-200 rounded-xl px-3 py-1.5">
            {LEGEND.map((item) => (
              <span key={item.key} className="flex items-center text-md text-black gap-1.5">
                <LegendDot statusKey={item.key} />
                {item.label}
              </span>
            ))}
          </div>
        </div>
        */}

        {/* Sections */}
        {/*
        <div className="space-y-8">
          {SECTIONS.map((section) => (
            <div key={section.id}>
              <div className="mb-3 flex items-center gap-3">
                <h2 className="whitespace-nowrap text-lg font-semibold text-black">
                  {section.title}
                </h2>
                <div className="h-px flex-1 bg-gray-200" />
              </div>
              <div
                className="grid gap-3"
                style={{ gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))" }}
              >
                {section.tables.map((table) => (
                  <TableCard
                    key={table.id}
                    table={table}
                    runtime={tables[table.id]}
                    onSelect={handleTableClick}
                  />
                ))}
              </div>
            </div>
          ))}
        </div>
        */}
      </div>

      <NewReservationModal
        open={reservationOpen}
        onClose={() => setReservationOpen(false)}
        onSave={(data) => {
          console.log(data);
          setReservationOpen(false);
          // TODO: Call Reservation API, Refresh Floor Plan
        }}
        selectedTable={selectedTable}
      />
    </div>
  );
}