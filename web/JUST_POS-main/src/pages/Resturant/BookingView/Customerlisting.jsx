import { useState } from "react";
import {
  Plus,
  SlidersHorizontal,
  Download,
  Users,
  Phone,
  Globe,
  Clock,
  ChevronLeft,
  ChevronRight,
  Eye,
  Pencil,
  Trash2,
  MapPin,
} from "lucide-react";
import NewReservationModal from "./Newreservationmodal";

// Brand tokens — matches the `primary` color used across the app
// (bg-[#F7E5EA] light fill, primary text/bg, primary-active hover)
const COLORS = {
  primary: "#9F1239",
  primaryActive: "#7F0F2E",
  primaryLight: "#F7E5EA",
  primaryLightHover: "#F0D3DC",
};

const TABS = ["Today", "Upcoming", "Completed", "Cancelled", "No-Show"];

const STATUS_STYLES = {
  Seated: { bg: "#EAF3DE", text: "#3B6D11" },
  Confirmed: { bg: "#E6F1FB", text: "#0C447C" },
  Arrived: { bg: COLORS.primaryLight, text: COLORS.primary },
  Pending: { bg: "#FAEEDA", text: "#854F0B" },
};

const RESERVATIONS = [
  {
    id: 1,
    name: "Eleanor Rigby",
    initials: "ER",
    phone: "+43 7793 30699",
    time: "19:30",
    duration: "1h 30m",
    guests: 4,
    table: "T-12",
    source: "online",
    status: "Seated",
  },
  {
    id: 2,
    name: "Marcus Knight",
    initials: "MK",
    phone: "+43 7793 30510",
    time: "20:00",
    duration: "2h",
    guests: 2,
    table: "T-06",
    source: "phone",
    status: "Confirmed",
  },
  {
    id: 3,
    name: "Sarah Holloway",
    initials: "SH",
    phone: "+43 7793 30022",
    time: "18:45",
    duration: "1h 45m",
    guests: 6,
    table: "B-02",
    source: "walk-in",
    status: "Arrived",
  },
  {
    id: 4,
    name: "Jermae Lennox",
    initials: "JL",
    phone: "",
    time: "21:15",
    duration: "",
    guests: 3,
    table: "unassigned",
    source: "online",
    status: "Pending",
  },
  {
    id: 5,
    name: "Amara Patel",
    initials: "AP",
    phone: "+43 7793 30063",
    time: "19:00",
    duration: "1h 30m",
    guests: 5,
    table: "T-08",
    source: "phone",
    status: "Seated",
  },
];

const SourceIcon = ({ source }) => {
  if (source === "phone") return <Phone size={14} className="text-gray-400" />;
  if (source === "walk-in") return <MapPin size={14} className="text-gray-400" />;
  return <Globe size={14} className="text-gray-400" />;
};

const StatusBadge = ({ status }) => {
  const style = STATUS_STYLES[status] || { bg: "#F1EFE8", text: "#444441" };
  return (
    <span
      className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium"
      style={{ backgroundColor: style.bg, color: style.text }}
    >
      {status}
    </span>
  );
};

export default function ReservationManagement() {
  const [activeTab, setActiveTab] = useState("Today");
  const [openModal, setOpenModal] = useState(false);

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <div className="flex items-start justify-between mb-6">
          <div>
            <h1 className="text-2xl font-semibold text-gray-900">
              Reservation management
            </h1>
            <p className="text-sm text-gray-500 mt-1">
              Track today's bookings and manage floor occupancy at a glance.
            </p>
          </div>
            <button
              className="flex items-center gap-2 px-4 py-2.5 rounded-lg text-white text-sm font-medium transition-colors"
              style={{ backgroundColor: COLORS.primary }}
              onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = COLORS.primaryActive)}
              onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = COLORS.primary)}
              onClick={() => setOpenModal(true)}
            >
              <Plus size={16} />
              New reservation
            </button>
          <NewReservationModal
            open={openModal}
            onClose={() => setOpenModal(false)}
            onSave={(form) => {
              console.log("New reservation saved:", form);
              setOpenModal(false);
            }}
          />
        </div>

        {/* Card */}
        <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
          {/* Tabs + actions */}
          <div className="flex items-center justify-between px-5 pt-4 pb-3 border-b border-gray-100">
            <div className="flex items-center gap-1">
              {TABS.map((tab) => {
                const active = tab === activeTab;
                return (
                  <button
                    key={tab}
                    onClick={() => setActiveTab(tab)}
                    className="px-3.5 py-1.5 rounded-lg text-sm font-medium transition-colors"
                    style={
                      active
                        ? { backgroundColor: COLORS.primaryLight, color: COLORS.primary }
                        : { color: "#6B7280" }
                    }
                    onMouseEnter={(e) => {
                      if (!active) e.currentTarget.style.backgroundColor = "#F3F4F6";
                    }}
                    onMouseLeave={(e) => {
                      if (!active) e.currentTarget.style.backgroundColor = "transparent";
                    }}
                  >
                    {tab}
                  </button>
                );
              })}
            </div>
            <div className="flex items-center gap-2">
              <button className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-gray-200 text-sm text-gray-600 hover:bg-gray-50 transition-colors">
                <SlidersHorizontal size={14} />
                Filters
              </button>
              <button className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-gray-200 text-sm text-gray-600 hover:bg-gray-50 transition-colors">
                <Download size={14} />
                Export
              </button>
            </div>
          </div>

          {/* Table */}
          <table className="w-full text-sm">
            <thead>
              <tr className="text-left text-xs font-medium text-gray-400 uppercase tracking-wide">
                <th className="px-5 py-3 font-medium">Customer</th>
                <th className="px-3 py-3 font-medium">Date &amp; time</th>
                <th className="px-3 py-3 font-medium">Guests</th>
                <th className="px-3 py-3 font-medium">Table</th>
                <th className="px-3 py-3 font-medium">Source</th>
                <th className="px-3 py-3 font-medium">Status</th>
                <th className="px-5 py-3 font-medium text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {RESERVATIONS.map((r) => (
                <tr
                  key={r.id}
                  className="border-t border-gray-100 hover:bg-gray-50/60 transition-colors"
                >
                  <td className="px-5 py-3.5">
                    <div className="flex items-center gap-3">
                      <div
                        className="w-9 h-9 rounded-full flex items-center justify-center text-xs font-semibold flex-shrink-0"
                        style={{ backgroundColor: COLORS.primaryLight, color: COLORS.primary }}
                      >
                        {r.initials}
                      </div>
                      <div>
                        <p className="font-medium text-gray-900 leading-tight">{r.name}</p>
                        <p className="text-xs text-gray-400 leading-tight mt-0.5">
                          {r.phone || "No phone on file"}
                        </p>
                      </div>
                    </div>
                  </td>
                  <td className="px-3 py-3.5">
                    <p className="text-gray-900 font-medium">Tonight, {r.time}</p>
                    {r.duration && (
                      <p className="text-xs text-gray-400 flex items-center gap-1 mt-0.5">
                        <Clock size={11} />
                        {r.duration}
                      </p>
                    )}
                  </td>
                  <td className="px-3 py-3.5">
                    <span className="flex items-center gap-1.5 text-gray-700">
                      <Users size={14} className="text-gray-400" />
                      {r.guests}
                    </span>
                  </td>
                  <td className="px-3 py-3.5">
                    {r.table === "unassigned" ? (
                      <span className="text-gray-400 italic">Unassigned</span>
                    ) : (
                      <span
                        className="font-medium"
                        style={{ color: COLORS.primary }}
                      >
                        {r.table}
                      </span>
                    )}
                  </td>
                  <td className="px-3 py-3.5">
                    <span className="flex items-center gap-1.5 text-gray-600 capitalize">
                      <SourceIcon source={r.source} />
                      {r.source}
                    </span>
                  </td>
                  <td className="px-3 py-3.5">
                    <StatusBadge status={r.status} />
                  </td>
                  <td className="px-5 py-3.5">
                    <div className="flex items-center justify-end gap-1">
                      <button
                        title="View reservation"
                        aria-label="View reservation"
                        className="p-1.5 rounded-lg text-gray-400 transition-colors"
                        style={{ backgroundColor: "transparent" }}
                        onMouseEnter={(e) => {
                          e.currentTarget.style.backgroundColor = "#E6F1FB";
                          e.currentTarget.style.color = "#0C447C";
                        }}
                        onMouseLeave={(e) => {
                          e.currentTarget.style.backgroundColor = "transparent";
                          e.currentTarget.style.color = "#9CA3AF";
                        }}
                      >
                        <Eye size={15} />
                      </button>
                      <button
                        title="Edit reservation"
                        aria-label="Edit reservation"
                        className="p-1.5 rounded-lg text-gray-400 transition-colors"
                        onMouseEnter={(e) => {
                          e.currentTarget.style.backgroundColor = COLORS.primaryLight;
                          e.currentTarget.style.color = COLORS.primary;
                        }}
                        onMouseLeave={(e) => {
                          e.currentTarget.style.backgroundColor = "transparent";
                          e.currentTarget.style.color = "#9CA3AF";
                        }}
                      >
                        <Pencil size={15} />
                      </button>
                      <button
                        title="Delete reservation"
                        aria-label="Delete reservation"
                        className="p-1.5 rounded-lg text-gray-400 transition-colors"
                        onMouseEnter={(e) => {
                          e.currentTarget.style.backgroundColor = "#FCEBEB";
                          e.currentTarget.style.color = "#A32D2D";
                        }}
                        onMouseLeave={(e) => {
                          e.currentTarget.style.backgroundColor = "transparent";
                          e.currentTarget.style.color = "#9CA3AF";
                        }}
                      >
                        <Trash2 size={15} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {/* Footer / pagination */}
          <div className="flex items-center justify-between px-5 py-3.5 border-t border-gray-100">
            <p className="text-xs text-gray-400">
              Showing 5 of 32 reservations for today
            </p>
            <div className="flex items-center gap-1">
              <button className="p-1.5 rounded-lg border border-gray-200 text-gray-400 hover:bg-gray-50 disabled:opacity-40" disabled>
                <ChevronLeft size={14} />
              </button>
              <button
                className="w-7 h-7 rounded-lg text-xs font-medium text-white"
                style={{ backgroundColor: COLORS.primary }}
              >
                1
              </button>
              <button className="w-7 h-7 rounded-lg text-xs font-medium text-gray-500 hover:bg-gray-50">
                2
              </button>
              <button className="w-7 h-7 rounded-lg text-xs font-medium text-gray-500 hover:bg-gray-50">
                3
              </button>
              <button className="p-1.5 rounded-lg border border-gray-200 text-gray-500 hover:bg-gray-50">
                <ChevronRight size={14} />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}