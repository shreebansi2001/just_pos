import React, { useState } from "react";
import OrderTypeMultiSelect from "../../components/OrderTypeMultiSelect";
import FilterSelect from "../../components/FilterSelect";
import DatePickerInput from "../../components/DatePickerInput";
import {
  Sparkles,
  Settings,
  Bell,
  Link2,
  Compass,
  Tv2,
  ChevronDown,
  Calendar,
  Search,
  Eye,
  FileText,
  SquarePen,
  Printer,
  CornerUpLeft,
  Database,
  Globe,
  Scissors,
  TrendingUp,
  ArrowDown,
  X,
  Trash2,
  Pencil,
} from "lucide-react";

// Order items for the right-side Order Details drawer matching reference screenshot
const sampleOrderItems = [
  { name: "Paneer Cheese Chilly Dosa ( Special Dosa)", note: "--", qty: 1, unitPrice: "190.00", totalPrice: "190.00" },
  { name: "Matka Gravy Dosa", note: "--", qty: 1, unitPrice: "280.00", totalPrice: "280.00" },
  { name: "Jini Dosa", note: "--", qty: 1, unitPrice: "150.00", totalPrice: "150.00" },
  { name: "Pahadi Dosa", note: "--", qty: 1, unitPrice: "190.00", totalPrice: "190.00" },
  { name: "Butter Sada Dosa", note: "--", qty: 1, unitPrice: "60.00", totalPrice: "60.00" },
  { name: "Butter Sada Dosa", note: "--", qty: 1, unitPrice: "60.00", totalPrice: "60.00" },
  { name: "Butter Sada Dosa", note: "--", qty: 1, unitPrice: "60.00", totalPrice: "60.00" },
  { name: "Butter Sada Dosa", note: "--", qty: 1, unitPrice: "60.00", totalPrice: "60.00" },
  { name: "Sprite Small", note: "--", qty: 1, unitPrice: "25.00", totalPrice: "25.00" },
  { name: "Butter Sada Dosa", note: "--", qty: 2, unitPrice: "60.00", totalPrice: "120.00" },
  { name: "Maaza Small", note: "--", qty: 1, unitPrice: "25.00", totalPrice: "25.00" },
  { name: "Mineral Water Bottle 1l", note: "--", qty: 2, unitPrice: "25.00", totalPrice: "50.00" },
  { name: "Thumps Up Small", note: "--", qty: 1, unitPrice: "25.00", totalPrice: "25.00" },
  { name: "Banta Soda", note: "--", qty: 1, unitPrice: "45.00", totalPrice: "45.00" },
];

// KOT details list matching reference screenshot exact entries
const sampleKotList = [
  { id: "37", qty: 1, items: "Banta Soda", biller: "biller", created: "9 Sep 2026 22:17:21", info: "--" },
  { id: "36", qty: 3, items: "Maaza Small, Mineral Water Bottle 1l, Thumps Up Small", biller: "biller", created: "9 Sep 2026 22:14:27", info: "--" },
  { id: "33", qty: 1, items: "Butter Sada Dosa", biller: "biller", created: "9 Sep 2026 22:10:37", info: "--" },
  { id: "31", qty: 1, items: "Sprite Small", biller: "biller", created: "9 Sep 2026 22:00:37", info: "--" },
  { id: "25", qty: 1, items: "Butter Sada Dosa", biller: "biller", created: "9 Sep 2026 21:50:46", info: "--" },
  { id: "24", qty: 1, items: "Butter Sada Dosa", biller: "biller", created: "9 Sep 2026 21:47:44", info: "--" },
  { id: "22", qty: 1, items: "Butter Sada Dosa", biller: "biller", created: "9 Sep 2026 21:44:15", info: "--" },
  { id: "17", qty: 1, items: "Butter Sada Dosa", biller: "biller", created: "9 Sep 2026 21:33:32", info: "--" },
  { id: "16", qty: 2, items: "Jini Dosa, Pahadi Dosa", biller: "biller", created: "9 Sep 2026 21:33:08", info: "--" },
  { id: "15", qty: 2, items: "Paneer Cheese Chilly Dosa ( Special Dosa), Matka Gravy Dosa", biller: "biller", created: "9 Sep 2026 21:31:53", info: "--" },
];

// Edit Order items matching reference screenshots exact layout
const initialEditItems = [
  { id: 1, name: "Paneer Cheese Chilly Dosa ( Special Dosa)", note: "--", qty: 1, unitPrice: 190 },
  { id: 2, name: "Matka Gravy Dosa", note: "--", qty: 1, unitPrice: 280 },
  { id: 3, name: "Jini Dosa", note: "--", qty: 1, unitPrice: 150 },
  { id: 4, name: "Pahadi Dosa", note: "--", qty: 1, unitPrice: 190 },
  { id: 5, name: "Butter Sada Dosa", note: "--", qty: 1, unitPrice: 60 },
  { id: 6, name: "Butter Sada Dosa", note: "--", qty: 1, unitPrice: 60 },
  { id: 7, name: "Butter Sada Dosa", note: "--", qty: 1, unitPrice: 60 },
  { id: 8, name: "Thumps Up Small", note: "--", qty: 1, unitPrice: 25 },
  { id: 9, name: "Banta Soda", note: "--", qty: 1, unitPrice: 45 },
];

// Chart data matching reference screenshot exact values
const chartData = [
  { label: "27th Aug", value: 14431 },
  { label: "28th Aug", value: 31166 },
  { label: "29th Aug", value: 18051 }, // Active hovered point in screenshot
  { label: "30th Aug", value: 21676 },
  { label: "31st Aug", value: 5879 },
  { label: "1st Sep", value: 11390 },
  { label: "2nd Sep", value: 10554 },
  { label: "3rd Sep", value: 10772 },
  { label: "4th Sep", value: 15040 },
  { label: "5th Sep", value: 36815 },
  { label: "6th Sep", value: 38453 },
  { label: "7th Sep", value: 11171 },
  { label: "8th Sep", value: 6115 },
  { label: "9th Sep", value: 11470 },
  { label: "10th Sep", value: 1000 },
];

const initialOrders = [
  {
    id: "69221",
    type: "Dine In (3)",
    subType: "(Dine in)",
    customer: "-",
    assignTo: "-",
    items: "Cheese Spring Onion Garlic Paratha, Laccha Paratha, Spring Onion Paratha, Thumps Up",
    myAmount: "1,075.00",
    tax: "51.76",
    discount: "(0.00)",
    grandTotal: "1,127.00",
    roundOff: "[0.24]",
    payment: "Other",
    paymentDetail: "[UPI]",
    status: "Printed",
    createdDate: "10 Sep 2026",
    createdTime: "01:12:58",
  },
  {
    id: "69220",
    type: "Dine In (2)",
    subType: "(Dine in)",
    customer: "-",
    assignTo: "-",
    items: "Chutney Dosa, Mineral Water Bottle 1l",
    myAmount: "95.00",
    tax: "3.50",
    discount: "(0.00)",
    grandTotal: "99.00",
    roundOff: "[0.50]",
    payment: "Cash",
    paymentDetail: "",
    status: "Printed",
    createdDate: "10 Sep 2026",
    createdTime: "00:44:35",
  },
  {
    id: "69219",
    type: "Takeaway",
    subType: "(Takeaway)",
    customer: "Rahul Sharma",
    assignTo: "Captain Roy",
    items: "Paneer Butter Masala, Butter Naan x2, Cold Coffee",
    myAmount: "470.00",
    tax: "23.50",
    discount: "(0.00)",
    grandTotal: "493.50",
    roundOff: "[0.50]",
    payment: "UPI",
    paymentDetail: "[GPay]",
    status: "Printed",
    createdDate: "10 Sep 2026",
    createdTime: "00:15:10",
  },
  {
    id: "69218",
    type: "Delivery",
    subType: "(Swiggy)",
    customer: "Ananya Patel",
    assignTo: "-",
    items: "Veg Biryani, Masala Chaas, Gulab Jamun (2 pc)",
    myAmount: "400.00",
    tax: "20.00",
    discount: "(40.00)",
    grandTotal: "380.00",
    roundOff: "[0.00]",
    payment: "Online",
    paymentDetail: "[Prepaid]",
    status: "Printed",
    createdDate: "09 Sep 2026",
    createdTime: "23:50:02",
  },
  {
    id: "69217",
    type: "Dine In (4)",
    subType: "(Dine in)",
    customer: "-",
    assignTo: "Waiter Vikas",
    items: "Chicken 65, Mutton Rogan Josh, Jeera Rice x2, Garlic Naan x4",
    myAmount: "1,290.00",
    tax: "64.50",
    discount: "(100.00)",
    grandTotal: "1,254.50",
    roundOff: "[0.50]",
    payment: "Card",
    paymentDetail: "[POS]",
    status: "Printed",
    createdDate: "09 Sep 2026",
    createdTime: "22:30:15",
  },
];

// Natural smooth curve spline generator without wild overshoots
function getSmoothSplinePath(pts) {
  if (pts.length < 2) return "";
  let d = `M ${pts[0].x},${pts[0].y}`;
  const smoothing = 0.18;

  for (let i = 0; i < pts.length - 1; i++) {
    const p0 = pts[i === 0 ? i : i - 1];
    const p1 = pts[i];
    const p2 = pts[i + 1];
    const p3 = pts[i + 2] || p2;

    const cp1x = p1.x + (p2.x - p0.x) * smoothing;
    const cp1y = p1.y + (p2.y - p0.y) * smoothing;
    const cp2x = p2.x - (p3.x - p1.x) * smoothing;
    const cp2y = p2.y - (p3.y - p1.y) * smoothing;

    d += ` C ${cp1x},${cp1y} ${cp2x},${cp2y} ${p2.x},${p2.y}`;
  }
  return d;
}

function InteractiveLineChart() {
  const [hoveredIdx, setHoveredIdx] = useState(2); // 29th Aug active by default
  const width = 1200;
  const height = 240;
  const paddingX = 50;
  const paddingTop = 45;
  const paddingBottom = 45;

  const minVal = 0;
  const maxVal = 42000;

  const points = chartData.map((item, index) => {
    const x = paddingX + (index * (width - paddingX * 2)) / (chartData.length - 1);
    const y = height - paddingBottom - ((item.value - minVal) / (maxVal - minVal)) * (height - paddingTop - paddingBottom);
    return { x, y, ...item, index };
  });

  const smoothPath = getSmoothSplinePath(points);
  const baselineY = height - paddingBottom;
  const areaPath = `${smoothPath} L ${points[points.length - 1].x},${baselineY} L ${points[0].x},${baselineY} Z`;
  const hoveredPoint = points[hoveredIdx];

  return (
    <div className="relative rounded-2xl border border-slate-200 bg-white p-3 sm:p-4 shadow-xs overflow-hidden">
      <div className="overflow-x-auto scrollbar-thin scrollbar-thumb-slate-200 w-full">
        <div className="relative h-[210px] sm:h-[240px] min-w-[650px] lg:min-w-full text-red-600">
          <svg viewBox={`0 0 ${width} ${height}`} className="h-full w-full overflow-visible">
            <defs>
              <linearGradient id="redChartGradient" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor="#dc2626" stopOpacity="0.15" />
                <stop offset="100%" stopColor="#dc2626" stopOpacity="0.01" />
              </linearGradient>
            </defs>

            {/* Horizontal Dotted Gridlines */}
            {[0, 1, 2, 3].map((line) => {
              const y = paddingTop + (line * (height - paddingTop - paddingBottom)) / 3;
              return (
                <line
                  key={line}
                  x1={paddingX}
                  x2={width - paddingX}
                  y1={y}
                  y2={y}
                  stroke="#f1f5f9"
                  strokeDasharray="4 4"
                  strokeWidth="1"
                />
              );
            })}

            {/* Filled Area Under Curve */}
            <path d={areaPath} fill="url(#redChartGradient)" />

            {/* Main Spline Curve */}
            <path
              d={smoothPath}
              fill="none"
              stroke="#dc2626"
              strokeWidth="3.5"
              strokeLinecap="round"
              strokeLinejoin="round"
            />

            {/* Node Circles & Top Numbers */}
            {points.map((pt) => {
              const isHovered = pt.index === hoveredIdx;
              return (
                <g
                  key={pt.label}
                  className="cursor-pointer"
                  onClick={() => setHoveredIdx(pt.index)}
                  onMouseEnter={() => setHoveredIdx(pt.index)}
                >
                  {/* Outer halo on hover */}
                  {isHovered && (
                    <circle cx={pt.x} cy={pt.y} r="8" fill="#fca5a5" fillOpacity="0.4" />
                  )}

                  {/* Point circle */}
                  <circle
                    cx={pt.x}
                    cy={pt.y}
                    r={isHovered ? "5" : "3.5"}
                    fill="#dc2626"
                    stroke="#ffffff"
                    strokeWidth={isHovered ? "2" : "0"}
                  />

                  {/* Value Label Above Point */}
                  {!isHovered && (
                    <text
                      x={pt.x}
                      y={pt.y - 10}
                      textAnchor="middle"
                      fontSize="11"
                      fontWeight="600"
                      fill="#334155"
                    >
                      {pt.value.toLocaleString()}
                    </text>
                  )}

                  {/* X-Axis Date Label directly aligned under point */}
                  <text
                    x={pt.x}
                    y={height - 12}
                    textAnchor="middle"
                    fontSize="12"
                    fontWeight={isHovered ? "700" : "600"}
                    fill={isHovered ? "#dc2626" : "#475569"}
                  >
                    {pt.label}
                  </text>
                </g>
              );
            })}
          </svg>

          {/* Hover Tooltip Popover matching screenshot */}
          {hoveredPoint && (
            <div
              className="absolute z-20 pointer-events-none transition-all duration-150 ease-out"
              style={{
                left: `${(hoveredPoint.x / width) * 100}%`,
                top: `${(hoveredPoint.y / height) * 100}%`,
                transform: "translate(-50%, -120%)",
              }}
            >
              <div className="rounded-lg border border-slate-200 bg-white px-3 py-1.5 shadow-md text-xs">
                <div className="font-semibold text-slate-800 text-center">{hoveredPoint.label}</div>
                <div className="flex items-center justify-center gap-1 text-slate-600 font-medium mt-0.5">
                  <span className="h-2 w-2 rounded-full bg-red-600 inline-block"></span>
                  <span>: {hoveredPoint.value.toLocaleString()}</span>
                </div>
              </div>
              <div className="mx-auto -mt-1 h-2 w-2 rotate-45 border-b border-r border-slate-200 bg-white"></div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default function AllOrdersPage() {
  const [activeTab, setActiveTab] = useState("Order");
  const [showChart, setShowChart] = useState(true);
  const [showMoreFilters, setShowMoreFilters] = useState(false);
  const [selectAll, setSelectAll] = useState(false);
  const [selectedRows, setSelectedRows] = useState({});
  const [selectedOrderDetails, setSelectedOrderDetails] = useState(null);
  const [selectedKotOrder, setSelectedKotOrder] = useState(null);
  const [selectedEditOrder, setSelectedEditOrder] = useState(null);
  const [selectedCancelOrder, setSelectedCancelOrder] = useState(null);
  const [cancelReason, setCancelReason] = useState("");
  const [selectedPaymentOrder, setSelectedPaymentOrder] = useState(null);
  const [newPaymentType, setNewPaymentType] = useState("");
  const [paymentReason, setPaymentReason] = useState("");
  const [editItems, setEditItems] = useState(initialEditItems);
  const [orderTypeFilter, setOrderTypeFilter] = useState([]);
  const [startDate, setStartDate] = useState(new Date("2026-09-04T06:00:00"));
  const [endDate, setEndDate] = useState(new Date("2026-09-11T06:00:00"));
  const [subOrderTypeFilter, setSubOrderTypeFilter] = useState("");
  const [paymentTypeFilter, setPaymentTypeFilter] = useState("");
  const [orderStatusFilter, setOrderStatusFilter] = useState("");
  const [otherStatusFilter, setOtherStatusFilter] = useState("");
  const [assignToFilter, setAssignToFilter] = useState("");
  const [gstinFilter, setGstinFilter] = useState("");

  const handleEditQtyChange = (index, newQty) => {
    setEditItems((prev) =>
      prev.map((item, i) => (i === index ? { ...item, qty: Math.max(1, newQty) } : item))
    );
  };

  const handleDeleteEditItem = (index) => {
    setEditItems((prev) => prev.filter((_, i) => i !== index));
  };

  const calculateEditGrandTotal = () => {
    const itemsTotal = editItems.reduce((acc, item) => acc + item.qty * item.unitPrice, 0);
    const tax = 58.5; // SGST 29.25 + CGST 29.25
    const roundOff = 0.5;
    const total = itemsTotal + tax + roundOff;
    return total.toLocaleString("en-IN", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  };

  const toggleSelectAll = () => {
    const nextState = !selectAll;
    setSelectAll(nextState);
    const newSelected = {};
    if (nextState) {
      initialOrders.forEach((o) => (newSelected[o.id] = true));
    }
    setSelectedRows(newSelected);
  };

  const toggleRow = (id) => {
    setSelectedRows((prev) => ({ ...prev, [id]: !prev[id] }));
  };

  return (
    <div className="w-full max-w-full space-y-4 px-2 sm:px-4 py-1 text-slate-800 overflow-x-hidden">
      
      {/* Top Header Row 1: Location & Utility Actions */}
      <div className="flex flex-wrap items-center justify-between gap-2.5 bg-white py-1">
        <div className="flex items-center gap-3 w-full sm:w-auto">
          <button className="flex items-center justify-between gap-2 rounded-xl border border-slate-200 bg-white px-3.5 py-2 text-xs sm:text-sm font-semibold text-slate-700 hover:bg-slate-50 transition shadow-2xs w-full sm:w-auto">
            <span>Food Food (Kandivali)</span>
            <ChevronDown className="h-4 w-4 text-slate-400" />
          </button>
        </div>

        <div className="flex flex-wrap items-center gap-1.5 sm:gap-2 w-full sm:w-auto">
          <button className="flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3 py-1.5 sm:px-3.5 sm:py-2 text-xs sm:text-sm font-medium text-slate-700 hover:bg-slate-50 transition">
            <Sparkles className="h-4 w-4 text-red-600" />
            <span>AI Agent</span>
          </button>
          <button className="flex h-8 w-8 sm:h-9 sm:w-9 items-center justify-center rounded-xl border border-slate-200 bg-white text-slate-600 hover:bg-slate-50 transition" title="Maximize">
            <Tv2 className="h-4 w-4" />
          </button>
          <button className="flex h-8 w-8 sm:h-9 sm:w-9 items-center justify-center rounded-xl border border-slate-200 bg-white text-slate-600 hover:bg-slate-50 transition" title="Notifications">
            <Bell className="h-4 w-4" />
          </button>
          <button className="flex h-8 w-8 sm:h-9 sm:w-9 items-center justify-center rounded-xl border border-slate-200 bg-white text-slate-600 hover:bg-slate-50 transition" title="Link">
            <Link2 className="h-4 w-4" />
          </button>
          <button className="flex h-8 w-8 sm:h-9 sm:w-9 items-center justify-center rounded-xl border border-slate-200 bg-white text-slate-600 hover:bg-slate-50 transition" title="Settings">
            <Settings className="h-4 w-4" />
          </button>
          <button className="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-1.5 sm:px-3.5 sm:py-2 text-xs sm:text-sm font-medium text-slate-700 hover:bg-slate-50 transition">
            <Compass className="h-4 w-4 text-slate-500" />
            <span>Explore Products</span>
          </button>
        </div>
      </div>

      {/* Top Header Row 2: Title, Segmented Tabs & Summary Actions (Fully Responsive) */}
      <div className="flex flex-col lg:flex-row flex-wrap lg:items-center justify-between gap-3 bg-white py-1">
        <div className="flex flex-wrap items-center gap-3">
          <h1 className="text-xl font-bold text-slate-900 tracking-tight">All Orders</h1>

          <div className="flex items-center border border-slate-200 rounded-xl bg-slate-100/80 p-1 overflow-x-auto">
            {["Order", "Advance Order"].map((tab) => {
              const isActive = activeTab === tab;
              return (
                <button
                  key={tab}
                  onClick={() => setActiveTab(tab)}
                  className={`rounded-lg px-4 sm:px-5 py-1.5 text-xs sm:text-sm font-semibold transition whitespace-nowrap cursor-pointer ${
                    isActive
                      ? "bg-red-600 text-white shadow-xs"
                      : "text-slate-600 hover:text-slate-900"
                  }`}
                >
                  {tab}
                </button>
              );
            })}
          </div>
        </div>

        <div className="flex flex-wrap items-center gap-2.5">
          <div className="flex items-center gap-1.5 text-xs sm:text-sm font-medium text-slate-600 mr-1">
            <span>Grand Total :</span>
            <span className="text-sm sm:text-base text-slate-900 font-bold">₹ 119,319.00</span>
          </div>

          <button
            onClick={() => setShowChart((prev) => !prev)}
            className="flex items-center gap-2 rounded-xl border border-red-200 bg-red-50/60 px-3 py-1.5 sm:px-3.5 sm:py-2 text-xs sm:text-sm font-semibold text-red-600 hover:bg-red-100/60 transition cursor-pointer shadow-xs"
            title={showChart ? "Collapse Chart" : "Expand Chart"}
          >
            <TrendingUp className="h-4 w-4 text-red-600" />
            <span className="whitespace-nowrap">Last 15 Days Orders</span>
            <ChevronDown className={`h-4 w-4 text-red-600 transition-transform duration-300 ${showChart ? "rotate-180" : ""}`} />
          </button>

          <button className="rounded-xl border border-slate-200 bg-white px-3 py-1.5 sm:px-3.5 sm:py-2 text-xs sm:text-sm font-medium text-slate-700 hover:bg-slate-50 transition cursor-pointer">
            Generate Invoice
          </button>

          <button className="flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3 py-1.5 sm:px-3.5 sm:py-2 text-xs sm:text-sm font-medium text-slate-700 hover:bg-slate-50 transition cursor-pointer">
            <span>Action</span>
            <ChevronDown className="h-4 w-4 text-slate-400" />
          </button>

          <button className="flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3 py-1.5 sm:px-3.5 sm:py-2 text-xs sm:text-sm font-medium text-slate-700 hover:bg-slate-50 transition cursor-pointer">
            <span>Export Excel</span>
            <ChevronDown className="h-4 w-4 text-slate-400" />
          </button>
        </div>
      </div>

      {/* Line Chart Section - Animated Collapse/Expand */}
      <div
        className={`transition-all duration-500 ease-in-out overflow-hidden transform origin-bottom ${
          showChart
            ? "max-h-[350px] opacity-100 translate-y-0 scale-y-100"
            : "max-h-0 opacity-0 -translate-y-4 scale-y-95 pointer-events-none"
        }`}
      >
        <InteractiveLineChart />
      </div>

      {/* Filters Controls Bar - Collapsed View (Fully Responsive Flex Layout) */}
      {!showMoreFilters && (
        <div className="rounded-2xl border border-slate-200 bg-white p-3.5 sm:p-4 shadow-xs">
          <div className="flex flex-wrap items-end gap-3">
            <div className="flex-1 min-w-[170px] max-w-full">
              <label className="mb-1 block text-xs font-semibold text-slate-700">Start Date</label>
              <DatePickerInput
                value={startDate}
                onChange={setStartDate}
                placeholder="Start Date"
                accentColor="red"
              />
            </div>

            <div className="flex-1 min-w-[170px] max-w-full">
              <label className="mb-1 block text-xs font-semibold text-slate-700">End Date</label>
              <DatePickerInput
                value={endDate}
                onChange={setEndDate}
                placeholder="End Date"
                accentColor="red"
              />
            </div>

            <div className="flex-1 min-w-[150px] max-w-full">
              <label className="mb-1 block text-xs font-semibold text-slate-700">All Order Type</label>
              <OrderTypeMultiSelect
                selected={orderTypeFilter}
                onChange={setOrderTypeFilter}
                focusColor="red"
              />
            </div>

            <div className="flex-1 min-w-[130px] max-w-full">
              <label className="mb-1 block text-xs font-semibold text-slate-700">Order ID</label>
              <input
                type="text"
                placeholder=""
                className="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs sm:text-sm text-slate-700 placeholder-slate-400 focus:border-red-500 focus:ring-1 focus:ring-red-500 focus:outline-none"
              />
            </div>

            <div className="flex items-center gap-2 shrink-0 w-full sm:w-auto pt-1 sm:pt-0 justify-between sm:justify-start">
              <button
                onClick={() => setShowMoreFilters(true)}
                className="flex-1 sm:flex-initial whitespace-nowrap rounded-xl border border-slate-200 bg-white px-3.5 sm:px-4 py-2 text-xs sm:text-sm font-medium text-slate-700 hover:bg-slate-50 transition shadow-2xs cursor-pointer text-center"
              >
                More Filters
              </button>
              <button className="flex-1 sm:flex-initial whitespace-nowrap rounded-xl border border-red-500 bg-white px-4 sm:px-5 py-2 text-xs sm:text-sm font-medium text-red-600 hover:bg-red-50 transition shadow-2xs cursor-pointer text-center">
                Search
              </button>
              <button className="flex-1 sm:flex-initial whitespace-nowrap rounded-xl border border-slate-200 bg-white px-3.5 sm:px-4 py-2 text-xs sm:text-sm font-medium text-slate-700 hover:bg-slate-50 transition shadow-2xs cursor-pointer text-center">
                Show All
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Filters Controls Bar - Expanded View (Matching Screenshot Exact Layout) */}
      {showMoreFilters && (
        <div className="rounded-2xl border border-slate-200 bg-white p-3.5 sm:p-4 shadow-xs space-y-4">
          {/* Row 1 */}
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-6 gap-3">
            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">Start Date</label>
              <DatePickerInput
                value={startDate}
                onChange={setStartDate}
                placeholder="Start Date"
                accentColor="red"
              />
            </div>

            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">End Date</label>
              <DatePickerInput
                value={endDate}
                onChange={setEndDate}
                placeholder="End Date"
                accentColor="red"
              />
            </div>

            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">All Order Type</label>
              <OrderTypeMultiSelect
                selected={orderTypeFilter}
                onChange={setOrderTypeFilter}
                focusColor="red"
              />
            </div>

            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">Order ID</label>
              <input
                type="text"
                placeholder=""
                className="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs text-slate-700 focus:border-red-500 focus:ring-1 focus:ring-red-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">Customer Name</label>
              <input
                type="text"
                placeholder=""
                className="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs text-slate-700 focus:border-red-500 focus:ring-1 focus:ring-red-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">Customer Phone</label>
              <input
                type="text"
                placeholder=""
                className="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs text-slate-700 focus:border-red-500 focus:ring-1 focus:ring-red-500 focus:outline-none"
              />
            </div>
          </div>

          {/* Row 2 */}
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-5 gap-3">
            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">Sub Order Type</label>
              <FilterSelect
                value={subOrderTypeFilter}
                onChange={(e) => setSubOrderTypeFilter(e.target.value)}
                placeholder="All Sub Types"
                focusColor="red"
                options={[
                  "Swiggy",
                  "Zomato",
                  "Website",
                  "Walk-in",
                  "Phone Order",
                  "Third Party",
                ]}
              />
            </div>

            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">All Payment Type</label>
              <FilterSelect
                value={paymentTypeFilter}
                onChange={(e) => setPaymentTypeFilter(e.target.value)}
                placeholder="All"
                focusColor="red"
                options={[
                  "Cash",
                  "Card",
                  "UPI",
                  "Net Banking",
                  "Wallet",
                  "Credit",
                  "Complimentary",
                ]}
              />
            </div>

            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">Order Status</label>
              <FilterSelect
                value={orderStatusFilter}
                onChange={(e) => setOrderStatusFilter(e.target.value)}
                placeholder="All"
                focusColor="red"
                options={[
                  "Pending",
                  "Accepted",
                  "Preparing",
                  "Ready",
                  "Served",
                  "Billed",
                  "Paid",
                  "Cancelled",
                ]}
              />
            </div>

            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">Other Status</label>
              <FilterSelect
                value={otherStatusFilter}
                onChange={(e) => setOtherStatusFilter(e.target.value)}
                placeholder="All"
                focusColor="red"
                options={[
                  "KOT Printed",
                  "Bill Printed",
                  "Refunded",
                  "On Hold",
                  "Void",
                ]}
              />
            </div>

            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">Assign To</label>
              <FilterSelect
                value={assignToFilter}
                onChange={(e) => setAssignToFilter(e.target.value)}
                placeholder="All Staff"
                focusColor="red"
                options={[
                  "Captain Jack S.",
                  "Waiter Sam",
                  "Waiter Ravi",
                  "Waiter Priya",
                  "Manager",
                ]}
              />
            </div>
          </div>

          {/* Row 3 */}
          <div className="flex flex-col sm:flex-row flex-wrap items-stretch sm:items-end gap-3 pt-1">
            <div className="w-full sm:w-48">
              <label className="mb-1 block text-xs font-semibold text-slate-700">Grand Total</label>
              <input
                type="text"
                placeholder=""
                className="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs text-slate-700 focus:border-red-500 focus:ring-1 focus:ring-red-500 focus:outline-none"
              />
            </div>

            <div className="w-full sm:w-48">
              <label className="mb-1 block text-xs font-semibold text-slate-700">GSTIN</label>
              <FilterSelect
                value={gstinFilter}
                onChange={(e) => setGstinFilter(e.target.value)}
                placeholder="All"
                focusColor="red"
                options={[
                  { value: "with_gst", label: "With GSTIN" },
                  { value: "without_gst", label: "Without GSTIN" },
                ]}
              />
            </div>

            <div className="grid grid-cols-3 gap-2 w-full sm:w-auto pt-1 sm:pt-0">
              <button
                onClick={() => setShowMoreFilters(false)}
                className="whitespace-nowrap rounded-xl border border-slate-200 bg-white px-2 sm:px-4 py-2 text-xs sm:text-sm font-medium text-slate-700 hover:bg-slate-50 transition shadow-2xs cursor-pointer text-center"
              >
                Less Filters
              </button>
              <button className="whitespace-nowrap rounded-xl border border-red-500 bg-white px-2 sm:px-5 py-2 text-xs sm:text-sm font-medium text-red-600 hover:bg-red-50 transition shadow-2xs cursor-pointer text-center">
                Search
              </button>
              <button className="whitespace-nowrap rounded-xl border border-slate-200 bg-white px-2 sm:px-4 py-2 text-xs sm:text-sm font-medium text-slate-700 hover:bg-slate-50 transition shadow-2xs cursor-pointer text-center">
                Show All
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Data Table Section (Responsive Horizontally Scrollable Container) */}
      <div className="rounded-2xl border border-slate-200 bg-white shadow-xs overflow-hidden">
        <div className="overflow-x-auto scrollbar-thin scrollbar-thumb-slate-200">
          <table className="w-full min-w-[1180px] border-separate border-spacing-0 text-left">
            <thead>
              <tr className="bg-red-50/50">
                <th className="border-y border-slate-200 px-3 py-3 w-10">
                  <input
                    type="checkbox"
                    checked={selectAll}
                    onChange={toggleSelectAll}
                    className="h-5 w-5 rounded border-slate-300 text-red-600 focus:ring-red-500 cursor-pointer"
                  />
                </th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">Order No.</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">Order Type</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">Customer Name</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">Assign To</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800 max-w-[280px]">Items</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">
                  <div>My Amount</div>
                  <div className="font-normal text-slate-600">(₹)</div>
                </th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">
                  <div>Tax</div>
                  <div className="font-normal text-slate-600">(₹)</div>
                </th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">
                  <div>Discount</div>
                  <div className="font-normal text-slate-600">(₹)</div>
                </th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">
                  <div>Grand Total</div>
                  <div>[Round Off]</div>
                  <div className="font-normal text-slate-600">(₹)</div>
                </th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">Payment</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">Status</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">
                  <span className="flex items-center gap-1 cursor-pointer hover:text-slate-900">
                    Created <ArrowDown className="h-3 w-3 text-slate-600" />
                  </span>
                </th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800 text-center min-w-[210px]">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {/* Apply Order Type filter */}
              {initialOrders
                .filter((row) => {
                  if (orderTypeFilter.length === 0) return true;
                  return orderTypeFilter.some((type) => row.type.includes(type));
                })
                .map((row) => {
                const isChecked = !!selectedRows[row.id];
                return (
                  <tr key={row.id} className={`align-top hover:bg-slate-50/80 transition ${isChecked ? "bg-slate-50" : ""}`}>
                    <td className="border-b border-slate-200 px-3 py-4">
                      <input
                        type="checkbox"
                        checked={isChecked}
                        onChange={() => toggleRow(row.id)}
                        className="h-5 w-5 rounded border-slate-400 text-slate-800 focus:ring-slate-500 cursor-pointer"
                      />
                    </td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs font-bold text-slate-800">
                      {row.id}
                    </td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs">
                      <div className="font-semibold text-slate-800">{row.type}</div>
                      <div className="font-bold italic text-slate-900">{row.subType}</div>
                    </td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs text-slate-700">{row.customer}</td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs text-slate-700">{row.assignTo}</td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs text-slate-800 font-medium leading-normal max-w-[280px]">
                      {row.items}
                    </td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs font-semibold text-slate-800">{row.myAmount}</td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs text-slate-700">{row.tax}</td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs text-slate-700">{row.discount}</td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs">
                      <div className="font-semibold text-slate-800">{row.grandTotal}</div>
                      <div className="text-slate-600 font-medium">{row.roundOff}</div>
                    </td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs">
                      <div className="text-slate-800 font-semibold">{row.payment}</div>
                      {row.paymentDetail && <div className="text-slate-600 font-medium">{row.paymentDetail}</div>}
                    </td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs">
                      <span className="inline-flex rounded-full bg-[#e8f8f0] px-3 py-1 text-xs font-bold text-[#10b981]">
                        {row.status}
                      </span>
                    </td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs font-semibold text-slate-800 leading-tight">
                      <div>{row.createdDate.split(' ').slice(0, 2).join(' ')}</div>
                      <div>{row.createdDate.split(' ')[2]}</div>
                      <div className="text-slate-600 font-medium mt-0.5">{row.createdTime}</div>
                    </td>
                    <td className="border-b border-slate-200 px-3 py-4 text-center min-w-[210px]">
                      <div className="flex items-center justify-center gap-1.5">
                        {/* 1. Eye View Icon */}
                        <div className="relative group">
                          <button
                            onClick={() => setSelectedOrderDetails(row)}
                            className="h-8 w-8 rounded-lg border border-slate-200 bg-[#f8fafc] text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition flex items-center justify-center shadow-2xs cursor-pointer"
                            title="View"
                          >
                            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round">
                              <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
                              <circle cx="12" cy="12" r="3" />
                            </svg>
                          </button>
                          <div className="absolute bottom-full left-1/2 -translate-x-1/2 mb-1.5 hidden group-hover:flex flex-col items-center pointer-events-none z-30">
                            <span className="whitespace-nowrap rounded-md bg-slate-900 px-2 py-1 text-[11px] font-medium text-white shadow-md">
                              View
                            </span>
                            <div className="w-0 h-0 border-x-4 border-x-transparent border-t-4 border-t-slate-900 -mt-px" />
                          </div>
                        </div>

                        {/* 2. Receipt Ticket Icon - View KOT */}
                        <div className="relative group">
                          <button
                            onClick={() => setSelectedKotOrder(row)}
                            className="h-8 w-8 rounded-lg border border-slate-200 bg-[#f8fafc] text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition flex items-center justify-center shadow-2xs cursor-pointer"
                            title="View KOT"
                          >
                            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round">
                              <path d="M4 2v20l2-1 2 1 2-1 2 1 2-1 2 1 2-1 2 1V2l-2 1-2-1-2 1-2-1-2 1-2-1-2 1Z" />
                              <path d="M8 8h8" />
                              <path d="M8 12h8" />
                              <path d="M8 16h5" />
                            </svg>
                          </button>
                          <div className="absolute bottom-full left-1/2 -translate-x-1/2 mb-1.5 hidden group-hover:flex flex-col items-center pointer-events-none z-30">
                            <span className="whitespace-nowrap rounded-md bg-slate-900 px-2 py-1 text-[11px] font-medium text-white shadow-md">
                              View KOT
                            </span>
                            <div className="w-0 h-0 border-x-4 border-x-transparent border-t-4 border-t-slate-900 -mt-px" />
                          </div>
                        </div>

                        {/* 3. Pencil Line Edit Icon - Edit */}
                        <div className="relative group">
                          <button
                            onClick={() => {
                              setSelectedEditOrder(row);
                              setEditItems([...initialEditItems]);
                            }}
                            className="h-8 w-8 rounded-lg border border-slate-200 bg-[#f8fafc] text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition flex items-center justify-center shadow-2xs cursor-pointer"
                            title="Edit"
                          >
                            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round">
                              <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z" />
                              <path d="M15 5l4 4" />
                              <path d="M9 22h10" />
                            </svg>
                          </button>
                          <div className="absolute bottom-full left-1/2 -translate-x-1/2 mb-1.5 hidden group-hover:flex flex-col items-center pointer-events-none z-30">
                            <span className="whitespace-nowrap rounded-md bg-slate-900 px-2 py-1 text-[11px] font-medium text-white shadow-md">
                              Edit
                            </span>
                            <div className="w-0 h-0 border-x-4 border-x-transparent border-t-4 border-t-slate-900 -mt-px" />
                          </div>
                        </div>

                        {/* 4. Cancel / Refund Document Icon - Cancel */}
                        <div className="relative group">
                          <button
                            onClick={() => {
                              setSelectedCancelOrder(row);
                              setCancelReason("");
                            }}
                            className="h-8 w-8 rounded-lg border border-slate-200 bg-[#f8fafc] text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition flex items-center justify-center shadow-2xs cursor-pointer"
                            title="Cancel"
                          >
                            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round">
                              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h6" />
                              <path d="M14 2v6h6" />
                              <path d="M8 10h6" />
                              <path d="M8 14h4" />
                              <circle cx="18" cy="18" r="4" />
                              <path d="M15.5 15.5l5 5" />
                            </svg>
                          </button>
                          <div className="absolute bottom-full left-1/2 -translate-x-1/2 mb-1.5 hidden group-hover:flex flex-col items-center pointer-events-none z-30">
                            <span className="whitespace-nowrap rounded-md bg-slate-900 px-2 py-1 text-[11px] font-medium text-white shadow-md">
                              Cancel
                            </span>
                            <div className="w-0 h-0 border-x-4 border-x-transparent border-t-4 border-t-slate-900 -mt-px" />
                          </div>
                        </div>

                        {/* 5. Card Edit Icon - Change Payment Type (Right aligned to prevent cut off) */}
                        <div className="relative group">
                          <button
                            onClick={() => {
                              setSelectedPaymentOrder(row);
                              setNewPaymentType("");
                              setPaymentReason("");
                            }}
                            className="h-8 w-8 rounded-lg border border-slate-200 bg-[#f8fafc] text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition flex items-center justify-center shadow-2xs cursor-pointer"
                            title="Change Payment Type"
                          >
                            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round">
                              <rect width="18" height="13" x="3" y="5.5" rx="2.5" />
                              <line x1="3" x2="21" y1="9.5" y2="9.5" />
                              <path d="M14 17.5l2.5-2.5 1.5 1.5-2.5 2.5h-1.5v-1.5z" />
                            </svg>
                          </button>
                          <div className="absolute bottom-full right-0 mb-1.5 hidden group-hover:flex flex-col items-end pointer-events-none z-30">
                            <span className="whitespace-nowrap rounded-md bg-slate-900 px-2.5 py-1 text-[11px] font-medium text-white shadow-md">
                              Change Payment Type
                            </span>
                            <div className="w-0 h-0 border-x-4 border-x-transparent border-t-4 border-t-slate-900 -mt-px mr-3" />
                          </div>
                        </div>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>

        {/* Footer Pagination & Legend Action Bar (Fully Responsive Across Screens) */}
        <div className="flex flex-col lg:flex-row items-center justify-between gap-3.5 border-t border-slate-200 bg-white px-4 sm:px-5 py-3.5 text-xs sm:text-sm">
          <div className="text-slate-600 font-medium text-center lg:text-left">Showing 1 to 10 of 244 records</div>

          {/* Pagination Controls */}
          <div className="flex items-center justify-center gap-1.5">
            <button className="h-8 w-8 rounded-lg bg-red-600 text-white font-bold text-xs flex items-center justify-center shadow-xs">
              1
            </button>
            <button className="h-8 w-8 rounded-lg border border-slate-200 bg-white text-slate-600 hover:bg-slate-50 text-xs flex items-center justify-center font-medium">
              2
            </button>
            <button className="h-8 w-8 rounded-lg border border-slate-200 bg-white text-slate-600 hover:bg-slate-50 text-xs flex items-center justify-center font-medium">
              3
            </button>
            <button className="rounded-lg border border-slate-200 bg-white px-3 py-1 text-xs text-slate-600 hover:bg-slate-50 font-medium">
              Next
            </button>
            <button className="rounded-lg border border-slate-200 bg-white px-3 py-1 text-xs text-slate-600 hover:bg-slate-50 font-medium">
              Last
            </button>
          </div>

          {/* Bottom Quick Legend Action Buttons */}
          <div className="flex flex-wrap items-center justify-center lg:justify-end gap-2 text-xs">
            <button className="flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-slate-700 hover:bg-slate-50 font-medium transition">
              <Database className="h-3.5 w-3.5 text-slate-500" />
              <span>Settlement Amount</span>
            </button>
            <button className="flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-slate-700 hover:bg-slate-50 font-medium transition">
              <Printer className="h-3.5 w-3.5 text-slate-500" />
              <span>Updated After Save &amp; Print</span>
            </button>
            <button className="flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-slate-700 hover:bg-slate-50 font-medium transition">
              <Globe className="h-3.5 w-3.5 text-slate-500" />
              <span>Online Order</span>
            </button>
            <button className="flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-slate-700 hover:bg-slate-50 font-medium transition">
              <span className="h-3.5 w-3.5 rounded bg-amber-100 text-amber-700 font-bold text-[10px] flex items-center justify-center">A</span>
              <span>Advance Order</span>
            </button>
            <button className="flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-slate-700 hover:bg-slate-50 font-medium transition">
              <Scissors className="h-3.5 w-3.5 text-slate-500" />
              <span>Split Bill</span>
            </button>
          </div>
        </div>
      </div>

      {/* Right Side Slide-Over Drawer Backdrop Overlay */}
      {selectedOrderDetails && (
        <div
          className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs transition-opacity duration-300"
          onClick={() => setSelectedOrderDetails(null)}
        />
      )}

      {/* Right Side Slide-Over Order Details Drawer Panel with Responsive Margins & Rounded Corners */}
      <div
        className={`fixed inset-y-0 right-0 sm:top-3 sm:bottom-3 sm:right-3 z-50 w-full sm:max-w-2xl md:max-w-3xl bg-white shadow-2xl border-0 sm:border border-slate-200 sm:rounded-2xl overflow-hidden transition-transform duration-300 ease-in-out flex flex-col ${
          selectedOrderDetails ? "translate-x-0" : "translate-x-full"
        }`}
      >
        {selectedOrderDetails && (
          <>
            {/* Header */}
            <div className="flex items-center justify-between border-b border-slate-200 px-6 py-4 bg-white sticky top-0 z-10">
              <h2 className="text-lg font-bold text-slate-900">Order Details</h2>
              <button
                onClick={() => setSelectedOrderDetails(null)}
                className="rounded-lg p-1 text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition cursor-pointer"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            {/* Scrollable Content */}
            <div className="flex-1 overflow-y-auto p-6 space-y-6">
              {/* Order Info Key-Value Table Grid */}
              <div className="overflow-x-auto rounded-lg border border-slate-200">
                <table className="w-full min-w-[580px] border-collapse text-xs">
                  <tbody>
                    <tr className="divide-x divide-slate-200 border-b border-slate-200">
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Order No.:</span> <span className="text-slate-700">{selectedOrderDetails.id || "69211"}</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Billing User:</span> <span className="text-slate-700">biller</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Customer Name:</span> <span className="text-slate-700">{selectedOrderDetails.customer || "-"}</span></td>
                    </tr>
                    <tr className="divide-x divide-slate-200 border-b border-slate-200">
                      <td className="p-2.5"><span className="font-bold text-slate-800">Customer Phone:</span> <span className="text-slate-700">-</span></td>
                      <td className="p-2.5"><span className="font-bold text-slate-800">Customer Address:</span> <span className="text-slate-700">-</span></td>
                      <td className="p-2.5"><span className="font-bold text-slate-800">Customer Locality:</span> <span className="text-slate-700">-</span></td>
                    </tr>
                    <tr className="divide-x divide-slate-200 border-b border-slate-200">
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">No. of Persons:</span> <span className="text-slate-700">-</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Order Type:</span> <span className="text-slate-700">{selectedOrderDetails.type || "Dine In"} (Table No: 1)</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Assign to:</span> <span className="text-slate-700">{selectedOrderDetails.assignTo || "-"}</span></td>
                    </tr>
                    <tr className="divide-x divide-slate-200 border-b border-slate-200">
                      <td className="p-2.5"><span className="font-bold text-slate-800">Total Tax:</span> <span className="text-slate-700">₹ {selectedOrderDetails.tax || "58.50"}</span></td>
                      <td className="p-2.5"><span className="font-bold text-slate-800">Total Discount:</span> <span className="text-slate-700">₹ {selectedOrderDetails.discount || "(0.00)"}</span></td>
                      <td className="p-2.5"><span className="font-bold text-slate-800">Grand Total:</span> <span className="text-slate-700">₹ {selectedOrderDetails.grandTotal || "1,399.00"}</span></td>
                    </tr>
                    <tr className="divide-x divide-slate-200 border-b border-slate-200">
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Settlement Amount:</span> <span className="text-slate-700">₹ 0.00</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Order Status:</span> <span className="text-slate-700">{selectedOrderDetails.status || "Printed"}</span></td>
                      <td className="p-2.5 bg-slate-50/50">
                        <span className="font-bold text-slate-800">Printed:</span> <span className="text-slate-700">Yes (1 time(s))</span>
                        <div className="text-slate-600 text-[11px]">({selectedOrderDetails.createdDate || "9 Sep 2026"} {selectedOrderDetails.createdTime || "22:23:31"})</div>
                      </td>
                    </tr>
                    <tr className="divide-x divide-slate-200 border-b border-slate-200">
                      <td className="p-2.5"><span className="font-bold text-slate-800">Paid:</span> <span className="text-slate-700">Yes</span></td>
                      <td className="p-2.5"><span className="font-bold text-slate-800">Payment Type:</span> <span className="text-slate-700">{selectedOrderDetails.payment || "Cash"}</span></td>
                      <td className="p-2.5"><span className="font-bold text-slate-800">Coupon Code:</span> <span className="text-slate-700"></span></td>
                    </tr>
                    <tr className="divide-x divide-slate-200 border-b border-slate-200">
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Tip:</span> <span className="text-slate-700">-</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Sub Order Type:</span> <span className="text-slate-700">{selectedOrderDetails.subType?.replace(/[()]/g, '') || "Dine in"}</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Sequence Name:</span> <span className="text-slate-700">-</span></td>
                    </tr>
                    <tr className="divide-x divide-slate-200">
                      <td className="p-2.5"><span className="font-bold text-slate-800">Settlement Counter:</span> <span className="text-slate-700">Billing Station</span></td>
                      <td className="p-2.5" colSpan={2}><span className="font-bold text-slate-800">Settled By:</span> <span className="text-slate-700">biller (biller)</span></td>
                    </tr>
                  </tbody>
                </table>
              </div>

              {/* Order Items Table Section */}
              <div>
                <h3 className="text-sm font-bold text-slate-900 mb-3">Order Items</h3>
                <div className="overflow-x-auto rounded-lg border border-slate-200">
                  <table className="w-full min-w-[500px] text-left text-xs border-collapse">
                    <thead>
                      <tr className="bg-red-50/60 border-b border-slate-200">
                        <th className="p-2.5 font-bold text-slate-800">Item Name</th>
                        <th className="p-2.5 font-bold text-slate-800">Special Note</th>
                        <th className="p-2.5 font-bold text-slate-800 text-center">Quantity</th>
                        <th className="p-2.5 font-bold text-slate-800 text-right">Unit Price (₹)</th>
                        <th className="p-2.5 font-bold text-slate-800 text-right">Total Price (₹)</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-200 text-slate-700">
                      {sampleOrderItems.map((item, idx) => (
                        <tr key={idx} className="hover:bg-slate-50">
                          <td className="p-2.5 font-medium text-slate-800">{item.name}</td>
                          <td className="p-2.5 text-slate-500">{item.note}</td>
                          <td className="p-2.5 text-center font-semibold text-slate-800">{item.qty}</td>
                          <td className="p-2.5 text-right">{item.unitPrice}</td>
                          <td className="p-2.5 text-right font-medium text-slate-800">{item.totalPrice}</td>
                        </tr>
                      ))}
                    </tbody>
                    <tfoot>
                      <tr className="border-t border-slate-200 text-xs">
                        <td colSpan={3}></td>
                        <td className="p-2 text-right font-medium text-slate-700">SGST 2.5%</td>
                        <td className="p-2 text-right font-medium text-slate-800">29.25</td>
                      </tr>
                      <tr className="text-xs">
                        <td colSpan={3}></td>
                        <td className="p-2 text-right font-medium text-slate-700">CGST 2.5%</td>
                        <td className="p-2 text-right font-medium text-slate-800">29.25</td>
                      </tr>
                      <tr className="text-xs">
                        <td colSpan={3}></td>
                        <td className="p-2 text-right font-medium text-slate-700">Delivery Charge</td>
                        <td className="p-2 text-right font-medium text-slate-800">0.00</td>
                      </tr>
                      <tr className="text-xs">
                        <td colSpan={3}></td>
                        <td className="p-2 text-right font-medium text-slate-700">Container Charge</td>
                        <td className="p-2 text-right font-medium text-slate-800">0.00</td>
                      </tr>
                      <tr className="text-xs">
                        <td colSpan={3}></td>
                        <td className="p-2 text-right font-medium text-slate-700">Service Charge</td>
                        <td className="p-2 text-right font-medium text-slate-800">0</td>
                      </tr>
                      <tr className="text-xs">
                        <td colSpan={3}></td>
                        <td className="p-2 text-right font-medium text-slate-700">Round Off</td>
                        <td className="p-2 text-right font-medium text-slate-800">0.50</td>
                      </tr>
                      <tr className="border-t border-slate-200 text-xs bg-slate-50">
                        <td colSpan={3}></td>
                        <td className="p-2.5 text-right font-bold text-slate-900">Grand Total</td>
                        <td className="p-2.5 text-right font-bold text-slate-900">{selectedOrderDetails.grandTotal || "1,399.00"}</td>
                      </tr>
                    </tfoot>
                  </table>
                </div>
              </div>
            </div>
          </>
        )}
      </div>

      {/* KOT Details Modal Backdrop & Popup */}
      {selectedKotOrder && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4">
          {/* Backdrop Overlay */}
          <div
            className="fixed inset-0 bg-slate-900/50 backdrop-blur-xs transition-opacity"
            onClick={() => setSelectedKotOrder(null)}
          />

          {/* Modal Content Box */}
          <div className="relative z-10 w-full max-w-3xl max-h-[90vh] flex flex-col rounded-2xl bg-white p-4 sm:p-6 shadow-2xl space-y-4 border border-slate-200 animate-in fade-in zoom-in-95 duration-200 overflow-hidden">
            {/* Modal Header */}
            <div className="flex items-center justify-between pb-3 border-b border-slate-200">
              <h2 className="text-sm sm:text-base font-bold text-slate-900 truncate">
                KOT Details [Order No :- {selectedKotOrder.id || "69211"}]
              </h2>
              <button
                onClick={() => setSelectedKotOrder(null)}
                className="rounded-lg p-1 text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition cursor-pointer"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            {/* KOT Data Table */}
            <div className="overflow-x-auto rounded-lg border border-slate-200 max-h-[420px] overflow-y-auto">
              <table className="w-full min-w-[520px] text-left text-xs border-collapse">
                <thead className="sticky top-0 bg-red-50/60 z-10 border-b border-slate-200">
                  <tr>
                    <th className="p-2.5 font-bold text-slate-800">KOT ID</th>
                    <th className="p-2.5 font-bold text-slate-800">No. Of Items</th>
                    <th className="p-2.5 font-bold text-slate-800 max-w-[320px]">Items</th>
                    <th className="p-2.5 font-bold text-slate-800">Biller</th>
                    <th className="p-2.5 font-bold text-slate-800">Created</th>
                    <th className="p-2.5 font-bold text-slate-800">Information</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-200 text-slate-700">
                  {sampleKotList.map((kot) => (
                    <tr key={kot.id} className="hover:bg-slate-50">
                      <td className="p-2.5 font-semibold text-slate-800">{kot.id}</td>
                      <td className="p-2.5 text-slate-800 font-medium">{kot.qty}</td>
                      <td className="p-2.5 font-medium text-slate-800 max-w-[320px] leading-relaxed">{kot.items}</td>
                      <td className="p-2.5 text-slate-600">{kot.biller}</td>
                      <td className="p-2.5 text-slate-700 font-medium whitespace-nowrap">{kot.created}</td>
                      <td className="p-2.5 text-slate-500">{kot.info}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Modal Bottom Note */}
            <div className="flex items-center gap-2 pt-1">
              <span className="flex items-center gap-1.5 rounded-lg bg-red-50 px-3 py-1.5 text-xs font-semibold text-red-600 border border-red-100">
                <svg className="h-4 w-4 text-red-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
                  <circle cx="9" cy="7" r="4" />
                  <path d="M22 21v-2a4 4 0 0 3-3.87" />
                  <path d="M16 3.13a4 4 0 0 1 0 7.75" />
                </svg>
                <span>Modified KOT</span>
              </span>
            </div>
          </div>
        </div>
      )}

      {/* Edit Order Modal Backdrop & Popup */}
      {selectedEditOrder && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4">
          {/* Backdrop Overlay */}
          <div
            className="fixed inset-0 bg-slate-900/50 backdrop-blur-xs transition-opacity"
            onClick={() => setSelectedEditOrder(null)}
          />

          {/* Modal Content Box */}
          <div className="relative z-10 w-full max-w-4xl max-h-[92vh] flex flex-col rounded-2xl bg-white p-4 sm:p-6 shadow-2xl space-y-4 border border-slate-200 animate-in fade-in zoom-in-95 duration-200 overflow-hidden">
            {/* Modal Header */}
            <div className="flex items-center justify-between pb-3 border-b border-slate-200">
              <h2 className="text-base sm:text-lg font-bold text-slate-900">Edit Order</h2>
              <button
                onClick={() => setSelectedEditOrder(null)}
                className="rounded-lg p-1 text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition cursor-pointer"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            {/* Modal Body - Scrollable */}
            <div className="flex-1 overflow-y-auto space-y-4 pr-1 scrollbar-thin">
              {/* Order Info Key-Value Table Grid */}
              <div className="overflow-x-auto rounded-lg border border-slate-200">
                <table className="w-full min-w-[600px] border-collapse text-xs">
                  <tbody>
                    <tr className="divide-x divide-slate-200 border-b border-slate-200">
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Order No.:</span> <span className="text-slate-700">{selectedEditOrder.id || "69211"}</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Billing User:</span> <span className="text-slate-700">biller</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Customer Name:</span> <span className="text-slate-700">{selectedEditOrder.customer || "-"}</span></td>
                    </tr>
                    <tr className="divide-x divide-slate-200 border-b border-slate-200">
                      <td className="p-2.5"><span className="font-bold text-slate-800">Customer Phone:</span> <span className="text-slate-700">-</span></td>
                      <td className="p-2.5"><span className="font-bold text-slate-800">Customer Address:</span> <span className="text-slate-700">-</span></td>
                      <td className="p-2.5"><span className="font-bold text-slate-800">Customer Locality:</span> <span className="text-slate-700">-</span></td>
                    </tr>
                    <tr className="divide-x divide-slate-200 border-b border-slate-200">
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">No. of Persons:</span> <span className="text-slate-700">-</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Order Type:</span> <span className="text-slate-700">{selectedEditOrder.type || "Dine In"} (Table No: 1)</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Settlement Amount:</span> <span className="text-slate-700">0.00</span></td>
                    </tr>
                    <tr className="divide-x divide-slate-200">
                      <td className="p-2.5"><span className="font-bold text-slate-800">Payment Type:</span> <span className="text-slate-700">{selectedEditOrder.payment || "Cash"}</span></td>
                      <td className="p-2.5"><span className="font-bold text-slate-800">Order Status:</span> <span className="text-slate-700">{selectedEditOrder.status || "Printed"}</span></td>
                      <td className="p-2.5"><span className="font-bold text-slate-800">Printed:</span> <span className="text-slate-700">Yes (1 time(s))</span></td>
                    </tr>
                  </tbody>
                </table>
              </div>

              {/* Editable Items Table */}
              <div className="overflow-x-auto rounded-lg border border-slate-200">
                <table className="w-full min-w-[650px] text-left text-xs border-collapse">
                  <thead>
                    <tr className="bg-slate-50 border-b border-slate-200">
                      <th className="p-2.5 w-10"></th>
                      <th className="p-2.5 w-10"></th>
                      <th className="p-2.5 font-bold text-slate-800">Item Name</th>
                      <th className="p-2.5 font-bold text-slate-800">Special Note</th>
                      <th className="p-2.5 font-bold text-slate-800 text-center w-24">Quantity</th>
                      <th className="p-2.5 font-bold text-slate-800 text-right">Unit Price (₹)</th>
                      <th className="p-2.5 font-bold text-slate-800 text-right">Total Price (₹)</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-200 text-slate-700">
                    {editItems.map((item, idx) => (
                      <tr key={idx} className="hover:bg-slate-50/80">
                        <td className="p-2 text-center">
                          <button
                            onClick={() => handleDeleteEditItem(idx)}
                            className="h-8 w-8 rounded-lg border border-slate-200 bg-white text-slate-400 hover:text-red-600 hover:border-red-200 transition flex items-center justify-center cursor-pointer shadow-2xs"
                            title="Remove Item"
                          >
                            <Trash2 className="h-4 w-4" />
                          </button>
                        </td>
                        <td className="p-2 text-center">
                          <button
                            className="h-8 w-8 rounded-lg border border-slate-200 bg-white text-slate-400 hover:text-slate-700 transition flex items-center justify-center cursor-pointer shadow-2xs"
                            title="Edit Item Note"
                          >
                            <Pencil className="h-4 w-4" />
                          </button>
                        </td>
                        <td className="p-2.5 font-semibold text-slate-800">{item.name}</td>
                        <td className="p-2.5 text-slate-500">{item.note}</td>
                        <td className="p-2.5 text-center">
                          <input
                            type="number"
                            min="1"
                            value={item.qty}
                            onChange={(e) => handleEditQtyChange(idx, parseInt(e.target.value) || 0)}
                            className="w-16 rounded-lg border border-slate-300 bg-white px-2 py-1 text-center font-semibold text-slate-800 focus:border-red-500 focus:ring-1 focus:ring-red-500 focus:outline-none"
                          />
                        </td>
                        <td className="p-2.5 text-right font-medium text-slate-700">{item.unitPrice}</td>
                        <td className="p-2.5 text-right font-semibold text-slate-900">
                          {(item.qty * item.unitPrice).toFixed(0)}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                  <tfoot>
                    <tr className="border-t border-slate-200 text-xs">
                      <td colSpan={5}></td>
                      <td className="p-2 text-right font-medium text-slate-700">SGST 2.5%</td>
                      <td className="p-2 text-right font-medium text-slate-800">29.25</td>
                    </tr>
                    <tr className="text-xs">
                      <td colSpan={5}></td>
                      <td className="p-2 text-right font-medium text-slate-700">CGST 2.5%</td>
                      <td className="p-2 text-right font-medium text-slate-800">29.25</td>
                    </tr>
                    <tr className="text-xs">
                      <td colSpan={5}></td>
                      <td className="p-2 text-right font-medium text-slate-700">Delivery Charge</td>
                      <td className="p-2 text-right font-medium text-slate-800">0.00</td>
                    </tr>
                    <tr className="text-xs">
                      <td colSpan={5}></td>
                      <td className="p-2 text-right font-medium text-slate-700">Container Charge</td>
                      <td className="p-2 text-right font-medium text-slate-800">0.00</td>
                    </tr>
                    <tr className="text-xs">
                      <td colSpan={5}></td>
                      <td className="p-2 text-right font-medium text-slate-700">Service Charge</td>
                      <td className="p-2 text-right font-medium text-slate-800">0.00</td>
                    </tr>
                    <tr className="text-xs">
                      <td colSpan={5}></td>
                      <td className="p-2 text-right font-medium text-slate-700">Round Off</td>
                      <td className="p-2 text-right font-medium text-slate-800">+0.50</td>
                    </tr>
                    <tr className="border-t border-slate-200 text-xs bg-slate-50">
                      <td colSpan={5}></td>
                      <td className="p-2.5 text-right font-bold text-slate-900">Grand Total</td>
                      <td className="p-2.5 text-right font-bold text-slate-900">
                        {calculateEditGrandTotal()}
                      </td>
                    </tr>
                  </tfoot>
                </table>
              </div>
            </div>

            {/* Modal Action Buttons */}
            <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-200">
              <button
                onClick={() => setSelectedEditOrder(null)}
                className="rounded-xl border border-slate-300 bg-white px-5 py-2 text-xs sm:text-sm font-semibold text-slate-700 hover:bg-slate-50 transition cursor-pointer"
              >
                Don't Save
              </button>
              <button
                onClick={() => setSelectedEditOrder(null)}
                className="rounded-xl bg-red-600 px-6 py-2 text-xs sm:text-sm font-semibold text-white hover:bg-red-700 transition cursor-pointer shadow-xs"
              >
                Save Changes
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Cancel Order Modal Backdrop & Popup */}
      {selectedCancelOrder && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4">
          {/* Backdrop Overlay */}
          <div
            className="fixed inset-0 bg-slate-900/50 backdrop-blur-xs transition-opacity"
            onClick={() => setSelectedCancelOrder(null)}
          />

          {/* Modal Content Box */}
          <div className="relative z-10 w-full max-w-lg flex flex-col rounded-2xl bg-white p-4 sm:p-6 shadow-2xl space-y-4 border border-slate-200 animate-in fade-in zoom-in-95 duration-200 overflow-hidden">
            {/* Modal Header */}
            <div className="flex items-center justify-between pb-3 border-b border-slate-200">
              <h2 className="text-base sm:text-lg font-bold text-slate-900">Cancel Order</h2>
              <button
                onClick={() => setSelectedCancelOrder(null)}
                className="rounded-lg p-1 text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition cursor-pointer"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            {/* Order Summary Info Box */}
            <div className="rounded-xl border border-slate-200 bg-white p-3.5 flex flex-wrap items-center justify-between gap-3 text-xs sm:text-sm">
              <div>
                <span className="font-bold text-slate-900">Order No.:</span>{" "}
                <span className="text-slate-700 font-medium">{selectedCancelOrder.id || "69210"}</span>
              </div>
              <div>
                <span className="font-bold text-slate-900">Order Type:</span>{" "}
                <span className="text-slate-700 font-medium">{selectedCancelOrder.type || "Dine In (6)"}</span>
              </div>
              <div>
                <span className="font-bold text-slate-900">Total:</span>{" "}
                <span className="text-slate-700 font-medium">{selectedCancelOrder.grandTotal || "620.00"}</span>
              </div>
            </div>

            {/* Cancel Reason Section */}
            <div>
              <label className="mb-2 block text-xs sm:text-sm font-bold text-slate-900">
                Please enter Cancel Reason
              </label>
              <textarea
                rows={4}
                value={cancelReason}
                onChange={(e) => setCancelReason(e.target.value)}
                placeholder=""
                className="w-full rounded-xl border border-slate-300 bg-white p-3 text-xs sm:text-sm text-slate-800 focus:border-red-500 focus:ring-1 focus:ring-red-500 focus:outline-none resize-none"
              />
            </div>

            {/* Modal Footer Action Buttons */}
            <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-200">
              <button
                onClick={() => setSelectedCancelOrder(null)}
                className="rounded-xl border border-slate-300 bg-white px-5 py-2 text-xs sm:text-sm font-semibold text-slate-700 hover:bg-slate-50 transition cursor-pointer"
              >
                Cancel
              </button>
              <button
                onClick={() => setSelectedCancelOrder(null)}
                className="rounded-xl bg-red-600 px-6 py-2 text-xs sm:text-sm font-semibold text-white hover:bg-red-700 transition cursor-pointer shadow-xs"
              >
                Save
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Change Order Payment Type Modal Backdrop & Popup */}
      {selectedPaymentOrder && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4">
          {/* Backdrop Overlay */}
          <div
            className="fixed inset-0 bg-slate-900/50 backdrop-blur-xs transition-opacity"
            onClick={() => setSelectedPaymentOrder(null)}
          />

          {/* Modal Content Box */}
          <div className="relative z-10 w-full max-w-lg flex flex-col rounded-2xl bg-white p-4 sm:p-6 shadow-2xl space-y-5 border border-slate-200 animate-in fade-in zoom-in-95 duration-200 overflow-hidden">
            {/* Modal Header */}
            <div className="flex items-center justify-between pb-3 border-b border-slate-200">
              <h2 className="text-base sm:text-lg font-bold text-slate-900">Change Order Payment Type</h2>
              <button
                onClick={() => setSelectedPaymentOrder(null)}
                className="rounded-lg p-1 text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition cursor-pointer"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            {/* Form Section */}
            <div className="space-y-4">
              {/* Field 1: Change Payment Type */}
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-2 sm:gap-4 items-center">
                <label className="text-xs sm:text-sm font-bold text-slate-900">
                  Change Payment Type <span className="text-red-500">*</span>
                </label>
                <div className="sm:col-span-2 relative">
                  <select
                    value={newPaymentType}
                    onChange={(e) => setNewPaymentType(e.target.value)}
                    className="w-full appearance-none rounded-xl border border-slate-300 bg-white px-3.5 py-2.5 pr-10 text-xs sm:text-sm text-slate-800 font-medium focus:border-red-500 focus:ring-1 focus:ring-red-500 focus:outline-none cursor-pointer"
                  >
                    <option value="">Select Payment Type</option>
                    <option value="Cash">Cash</option>
                    <option value="UPI">UPI</option>
                    <option value="Card">Card</option>
                    <option value="Prepaid">Prepaid</option>
                    <option value="Net Banking">Net Banking</option>
                    <option value="Other">Other</option>
                  </select>
                  <ChevronDown className="h-4 w-4 text-slate-400 absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none" />
                </div>
              </div>

              {/* Field 2: Reason */}
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-2 sm:gap-4 items-start pt-1">
                <label className="text-xs sm:text-sm font-bold text-slate-900 pt-1">
                  Reason <span className="text-red-500">*</span>
                </label>
                <div className="sm:col-span-2">
                  <textarea
                    rows={4}
                    value={paymentReason}
                    onChange={(e) => setPaymentReason(e.target.value)}
                    placeholder=""
                    className="w-full rounded-xl border border-slate-300 bg-white p-3 text-xs sm:text-sm text-slate-800 focus:border-red-500 focus:ring-1 focus:ring-red-500 focus:outline-none resize-none"
                  />
                </div>
              </div>
            </div>

            {/* Modal Footer Action Buttons */}
            <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-200">
              <button
                onClick={() => setSelectedPaymentOrder(null)}
                className="rounded-xl border border-slate-300 bg-white px-5 py-2 text-xs sm:text-sm font-semibold text-slate-700 hover:bg-slate-50 transition cursor-pointer"
              >
                Don't Save
              </button>
              <button
                onClick={() => setSelectedPaymentOrder(null)}
                className="rounded-xl bg-red-600 px-6 py-2 text-xs sm:text-sm font-semibold text-white hover:bg-red-700 transition cursor-pointer shadow-xs"
              >
                Save Changes
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}
