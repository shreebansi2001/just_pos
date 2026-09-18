import { useState } from "react";
import {
  Search,
  Plus,
  Users,
  Clock,
  Phone,
  Globe,
  Cake,
  Heart,
  Briefcase,
  GripVertical,
  ArrowRight,
  LayoutGrid,
  KanbanSquare,
  Armchair,
} from "lucide-react";
import NewReservationModal from "./NewReservationModal";
import { KanbanBoard, KanbanStats } from "../../../components/kanban";

// Tokens pulled directly from tailwind.config.js -> colors.primary (light theme)
const PRIMARY = {
  DEFAULT: "#8E0000",
  active: "#991B1E",
  light: "#E53935",
  lighter: "#EF9A9A",
  clarity: "rgba(198, 40, 40, 0.20)",
  inverse: "#F5F5F5",
};

// Neutral scale from tailwind.config.js -> base.colors.gray.light
const GRAY = {
  100: "#F9F9F9",
  200: "#F1F1F4",
  300: "#DBDFE9",
  400: "#C4CADA",
  500: "#99A1B7",
  600: "#78829D",
  700: "#4B5675",
  800: "#252F4A",
  900: "#071437",
};

const OCCASION_ICON = { Birthday: Cake, Anniversary: Heart, Business: Briefcase };

// Each stage gets a shade from the primary ramp — intensity rises as the
// reservation approaches, then resolves to neutral gray once closed out.
const COLUMNS = [
  { key: "inquiry", label: "Inquiry", note: "Awaiting confirmation", accent: PRIMARY.lighter, accentText: PRIMARY.active, statColor: PRIMARY.active },
  { key: "reserved", label: "Reserved", note: "Confirmed, not yet arrived", accent: PRIMARY.light, accentText: "#FFFFFF", statColor: PRIMARY.light },
  { key: "running", label: "Running", note: "Currently dining", accent: PRIMARY.DEFAULT, accentText: "#FFFFFF", statColor: PRIMARY.DEFAULT },
  { key: "completed", label: "Completed", note: "Closed out", accent: GRAY[400], accentText: GRAY[800], statColor: GRAY[600] },
];

const INITIAL_CARDS = [
  { id: "c1", name: "Julianne Moore", phone: "+1 415 555 0142", guests: 4, service: "Dinner", time: "7:30 PM", source: "phone", occasion: "Birthday", status: "inquiry" },
  { id: "c2", name: "David Foster", phone: "+1 415 555 0198", guests: 2, service: "Dinner", time: "7:45 PM", source: "online", occasion: null, status: "inquiry" },
  { id: "c3", name: "Marcus Thorne", phone: "+1 415 555 0177", guests: 6, service: "Dinner", time: "8:00 PM", source: "phone", occasion: "Business", table: null, status: "reserved" },
  { id: "c4", name: "Sarah Adeyemi", phone: "+1 415 555 0113", guests: 3, service: "Lunch", time: "12:30 PM", source: "online", occasion: null, table: "T4", status: "reserved" },
  { id: "c5", name: "Elena Rodriguez", phone: "+1 415 555 0166", guests: 2, service: "Dinner", time: "6:15 PM", source: "phone", occasion: "Anniversary", table: "T7", course: "Main course", elapsedMin: 42, typicalMin: 75, status: "running" },
  { id: "c6", name: "Priya Nair", phone: "+1 415 555 0189", guests: 5, service: "Lunch", time: "1:00 PM", source: "online", occasion: null, table: "T2", total: "$186.40", status: "completed" },
];

const FLOOR_TABLES = {
  Ground: ["T1", "T2", "T3", "T4"],
  Mezzanine: ["T5", "T6"],
  Rooftop: ["T7", "T8"],
};

const SourceIcon = ({ source }) =>
  source === "phone" ? <Phone size={12} style={{ color: GRAY[400] }} /> : <Globe size={12} style={{ color: GRAY[400] }} />;

const OccasionBadge = ({ occasion }) => {
  const Icon = OCCASION_ICON[occasion];
  if (!Icon) return null;
  return (
    <span
      className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[11px] font-medium flex-shrink-0"
      style={{ backgroundColor: PRIMARY.clarity, color: PRIMARY.active }}
    >
      <Icon size={10} />
      {occasion}
    </span>
  );
};

const NEXT_STATUS = { inquiry: "reserved", reserved: "running", running: "completed" };
const NEXT_LABEL = { inquiry: "Confirm", reserved: "Seat now", running: "Close out" };

function ReservationCard({ card, onAdvance, onDragStart }) {
  return (
    <div
      draggable
      onDragStart={(e) => onDragStart(e, card.id)}
      className="bg-white rounded-lg p-3 cursor-grab active:cursor-grabbing transition-shadow"
      style={{ border: `1px solid ${GRAY[200]}` }}
      onMouseEnter={(e) => (e.currentTarget.style.borderColor = GRAY[300])}
      onMouseLeave={(e) => (e.currentTarget.style.borderColor = GRAY[200])}
    >
      <div className="flex items-start justify-between gap-2 mb-2">
        <div className="flex items-start gap-1.5 min-w-0">
          <GripVertical size={13} style={{ color: GRAY[300] }} className="mt-0.5 flex-shrink-0" />
          <div className="min-w-0">
            <p className="text-sm font-medium truncate" style={{ color: GRAY[900] }}>{card.name}</p>
            <p className="text-xs flex items-center gap-1 mt-0.5" style={{ color: GRAY[500] }}>
              <SourceIcon source={card.source} />
              {card.phone}
            </p>
          </div>
        </div>
        {card.occasion && <OccasionBadge occasion={card.occasion} />}
      </div>

      <div className="flex items-center gap-3 text-xs mb-2 ml-[19px]" style={{ color: GRAY[600] }}>
        <span className="flex items-center gap-1">
          <Users size={12} style={{ color: GRAY[400] }} />
          {card.guests}
        </span>
        <span className="flex items-center gap-1">
          <Clock size={12} style={{ color: GRAY[400] }} />
          {card.time}
        </span>
        <span className="px-1.5 py-0.5 rounded" style={{ backgroundColor: GRAY[100], color: GRAY[600] }}>
          {card.service}
        </span>
      </div>

      {card.status === "reserved" && (
        <p className="text-xs ml-[19px] mb-2" style={{ color: card.table ? PRIMARY.active : GRAY[400] }}>
          {card.table ? `Table ${card.table}` : "Table not yet assigned"}
        </p>
      )}

      {card.status === "running" && (
        <div className="ml-[19px] mb-2">
          <div className="flex items-center justify-between text-xs mb-1" style={{ color: GRAY[500] }}>
            <span>{card.table ? `Table ${card.table}` : ""}{card.course ? ` · ${card.course}` : ""}</span>
            <span>{card.elapsedMin}m elapsed</span>
          </div>
          <div className="h-1.5 w-full rounded-full overflow-hidden" style={{ backgroundColor: GRAY[200] }}>
            <div
              className="h-full rounded-full"
              style={{
                width: `${Math.min(100, (card.elapsedMin / card.typicalMin) * 100)}%`,
                backgroundColor: PRIMARY.DEFAULT,
              }}
            />
          </div>
        </div>
      )}

      {card.status === "completed" && (
        <p className="text-xs ml-[19px] mb-2" style={{ color: GRAY[600] }}>
          Total <span className="font-medium" style={{ color: GRAY[800] }}>{card.total}</span>
        </p>
      )}

      {NEXT_STATUS[card.status] && (
        <button
          onClick={() => onAdvance(card.id, NEXT_STATUS[card.status])}
          className="ml-[19px] flex items-center gap-1 text-xs font-medium mt-1"
          style={{ color: PRIMARY.DEFAULT }}
        >
          {NEXT_LABEL[card.status]}
          <ArrowRight size={12} />
        </button>
      )}
    </div>
  );
}

function FloorPlanView({ cards }) {
  const getTableCard = (tableId) => cards.find((c) => c.table === tableId && c.status !== "completed" && c.status !== "inquiry");

  return (
    <div className="space-y-5">
      {Object.entries(FLOOR_TABLES).map(([floor, tableIds]) => (
        <div key={floor} className="bg-white rounded-xl p-4" style={{ border: `1px solid ${GRAY[200]}` }}>
          <h3 className="text-sm font-semibold mb-3" style={{ color: GRAY[800] }}>{floor} floor</h3>
          <div className="grid grid-cols-4 sm:grid-cols-6 lg:grid-cols-8 gap-3">
            {tableIds.map((tableId) => {
              const occupant = getTableCard(tableId);
              const state = occupant ? occupant.status : "available";
              const style =
                state === "running"
                  ? { backgroundColor: PRIMARY.DEFAULT, borderColor: PRIMARY.DEFAULT, color: "#fff" }
                  : state === "reserved"
                  ? { backgroundColor: PRIMARY.clarity, borderColor: PRIMARY.light, color: PRIMARY.active }
                  : { backgroundColor: "#fff", borderColor: GRAY[300], color: GRAY[500] };
              return (
                <div
                  key={tableId}
                  className="rounded-lg p-2.5 flex flex-col items-center justify-center gap-1 text-center"
                  style={{ border: `1px solid ${style.borderColor}`, backgroundColor: style.backgroundColor, minHeight: "72px" }}
                >
                  <Armchair size={16} style={{ color: state === "running" ? "#fff" : style.color }} />
                  <span className="text-xs font-semibold" style={{ color: state === "running" ? "#fff" : GRAY[800] }}>
                    {tableId}
                  </span>
                  {occupant ? (
                    <span className="text-[10px] leading-tight" style={{ color: style.color }}>
                      {occupant.name.split(" ")[0]} · {occupant.guests}
                    </span>
                  ) : (
                    <span className="text-[10px]" style={{ color: GRAY[400] }}>Available</span>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      ))}
    </div>
  );
}

const VIEWS = [
  { key: "floorplan", label: "Floor plan", icon: LayoutGrid },
  { key: "board", label: "Board", icon: KanbanSquare },
];

function formatTime12h(time24) {
  if (!time24) return "";
  const [h, m] = time24.split(":").map(Number);
  const period = h >= 12 ? "PM" : "AM";
  const hour12 = h % 12 === 0 ? 12 : h % 12;
  return `${hour12}:${String(m).padStart(2, "0")} ${period}`;
}

function formToCard(form) {
  const [hour] = (form.time || "18:00").split(":").map(Number);
  const service = hour < 16 ? "Lunch" : "Dinner";
  const occasion = form.occasion && form.occasion !== "Standard Dining" ? form.occasion : null;
  return {
    id: `c${Date.now()}`,
    name: form.customerName?.trim() || "Unnamed guest",
    phone: form.mobile || "",
    guests: form.guests || 1,
    service,
    time: formatTime12h(form.time),
    source: form.source === "Online Portal" ? "online" : "phone",
    occasion,
    table: form.table || null,
    status: "inquiry",
  };
}

export default function ReservationKanbanBoard() {
  const [cards, setCards] = useState(INITIAL_CARDS);
  const [query, setQuery] = useState("");
  const [view, setView] = useState("board");
  const [isNewInquiryOpen, setIsNewInquiryOpen] = useState(false);

  const moveCard = (id, status) => {
    setCards((prev) => prev.map((c) => (c.id === id ? { ...c, status } : c)));
  };

  const handleSaveInquiry = (form) => {
    setCards((prev) => [...prev, formToCard(form)]);
    setIsNewInquiryOpen(false);
  };

  const filtered = cards.filter((c) => c.name.toLowerCase().includes(query.toLowerCase()));

  return (
    <div className="min-h-screen p-6" style={{ backgroundColor: GRAY[100] }}>
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="flex items-start justify-between mb-5 gap-4">
          <div>
            <h1 className="text-lg font-semibold" style={{ color: GRAY[900] }}>Reservation board</h1>
            <p className="text-sm mt-1">
              <span style={{ color: GRAY[600] }}>Friday, Oct 27</span>
              <span className="mx-1.5" style={{ color: PRIMARY.DEFAULT }}>•</span>
              <span className="font-medium" style={{ color: PRIMARY.DEFAULT }}>Evening service</span>
            </p>
          </div>

          <div className="flex items-center gap-2 flex-shrink-0">
            <div className="flex items-center gap-1 p-1 rounded-lg bg-white" style={{ border: `1px solid ${GRAY[200]}` }}>
              {VIEWS.map((v) => {
                const active = v.key === view;
                const Icon = v.icon;
                return (
                  <button
                    key={v.key}
                    onClick={() => setView(v.key)}
                    className="flex items-center gap-1.5 px-3 py-1.5 rounded-md text-sm font-medium transition-colors"
                    style={active ? { backgroundColor: PRIMARY.DEFAULT, color: "#fff" } : { color: GRAY[600] }}
                  >
                    <Icon size={14} />
                    {v.label}
                  </button>
                );
              })}
            </div>
            <button
              onClick={() => setIsNewInquiryOpen(true)}
              className="flex items-center gap-2 px-4 py-2.5 rounded-lg text-white text-sm font-medium transition-colors"
              style={{ backgroundColor: PRIMARY.DEFAULT }}
              onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = PRIMARY.active)}
              onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = PRIMARY.DEFAULT)}
            >
              <Plus size={16} />
              New inquiry
            </button>
          </div>
        </div>

        {/* Search + stats in a single row */}
        <div className="sticky top-3 z-10 flex items-center justify-between gap-4 mb-5">
          <div className="relative max-w-sm w-full">
            <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2" style={{ color: GRAY[400] }} />
            <input
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Search guests..."
              className="w-full pl-9 pr-3 py-2 rounded-lg text-sm bg-white focus:outline-none"
              style={{ border: `1px solid ${GRAY[300]}` }}
            />
          </div>

          <div className="flex-shrink-0">
            <KanbanStats columns={COLUMNS} items={filtered} gray={GRAY} />
          </div>
        </div>

        {/* Main view content */}
        {view === "board" && (
          <KanbanBoard
            columns={COLUMNS}
            items={filtered}
            onStatusChange={moveCard}
            emptyLabel="No guests here"
            gray={GRAY}
            renderCard={(card, { onDragStart }) => (
              <ReservationCard card={card} onAdvance={moveCard} onDragStart={onDragStart} />
            )}
          />
        )}

        {view === "floorplan" && <FloorPlanView cards={filtered} />}
      </div>

      <NewReservationModal
        open={isNewInquiryOpen}
        onClose={() => setIsNewInquiryOpen(false)}
        onSave={handleSaveInquiry}
      />
    </div>
  );
}