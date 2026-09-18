import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import OrderTypeMultiSelect from "../../../components/OrderTypeMultiSelect";
import FilterSelect from "../../../components/FilterSelect";
import DatePickerInput from "../../../components/DatePickerInput";
import {
  History,
  ClipboardList,
  Calendar,
  ChevronDown,
  FileText,
  Search,
  Clock,
  Database,
  Printer,
  Globe,
  Scissors,
  ArrowDown,
} from "lucide-react";
import KotDetailModal from "./KotDetailModal";

const kotData = [
  {
    id: "8821",
    table: "T-14",
    chef: "Chef Marcus",
    waiter: "David L.",
    captain: "Sarah J.",
    orderTime: "19:42:10",
    completed: "19:54:15",
    prep: "12m 05s",
    late: false,
    orderType: "Dine In",
    subType: "(Dine in)",
    customerName: "John D.",
    customerPhone: "+91 98765 43210",
    items: "Wagyu Beef Burger, Truffle Fries",
    itemCount: 2,
    status: "Completed",
    billPrintDate: "29/07/2026",
    completeDuration: "34m 47s",
    createdAtDate: "29 Jul 2026",
    createdAtTime: "19:20:15",
  },
  {
    id: "8820",
    table: "T-02",
    chef: "Chef Elena",
    waiter: "Robert K.",
    captain: "Sarah J.",
    orderTime: "19:35:00",
    completed: "19:38:12",
    prep: "03m 12s",
    late: true,
    orderType: "Takeaway",
    subType: "(Takeaway)",
    customerName: "Priya S.",
    customerPhone: "+91 91234 56789",
    items: "Margherita Pizza, Cold Coffee",
    itemCount: 2,
    status: "Completed",
    billPrintDate: "29/07/2026",
    completeDuration: "05m 30s",
    createdAtDate: "29 Jul 2026",
    createdAtTime: "19:15:00",
  },
  {
    id: "8819",
    table: "T-VIP-1",
    chef: "Chef Julian",
    waiter: "Maria G.",
    captain: "Chris M.",
    orderTime: "19:20:15",
    completed: "19:48:40",
    prep: "28m 25s",
    late: false,
    orderType: "Delivery",
    subType: "(Swiggy)",
    customerName: "Arjun M.",
    customerPhone: "+91 99887 76655",
    items: "Truffle Risotto, Caesar Salad",
    itemCount: 2,
    status: "Completed",
    billPrintDate: "29/07/2026",
    completeDuration: "32m 10s",
    createdAtDate: "29 Jul 2026",
    createdAtTime: "19:10:05",
  },
  {
    id: "8818",
    table: "T-05",
    chef: "Chef Marcus",
    waiter: "Waiter Vikas",
    captain: "Sarah J.",
    orderTime: "18:50:10",
    completed: "19:15:22",
    prep: "25m 12s",
    late: false,
    orderType: "Dine In",
    subType: "(Dine in)",
    customerName: "Rahul Sharma",
    customerPhone: "+91 98112 23344",
    items: "Paneer Butter Masala, Butter Naan x2",
    itemCount: 3,
    status: "Completed",
    billPrintDate: "29/07/2026",
    completeDuration: "25m 12s",
    createdAtDate: "29 Jul 2026",
    createdAtTime: "18:50:10",
  },
];

export default function KotHistory() {
  const navigate = useNavigate();
  const location = useLocation();
  const isHistory = location.pathname === "/kot-history";

  const [selectedOrder, setSelectedOrder] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [showMoreFilters, setShowMoreFilters] = useState(false);
  const [selectAll, setSelectAll] = useState(false);
  const [selectedRows, setSelectedRows] = useState({});
  // New state for Order Type filter
  const [orderTypeFilter, setOrderTypeFilter] = useState([]);
  const [startDate, setStartDate] = useState(new Date("2026-09-10T06:00:00"));
  const [endDate, setEndDate] = useState(new Date("2026-09-11T06:00:00"));
  const [kotStatusFilter, setKotStatusFilter] = useState("");
  const [kotFilterBy, setKotFilterBy] = useState("");

  const toggleSelectAll = () => {
    const nextState = !selectAll;
    setSelectAll(nextState);
    const newSelected = {};
    if (nextState) {
      kotData.forEach((o) => (newSelected[o.id] = true));
    }
    setSelectedRows(newSelected);
  };

  const toggleRow = (id) => {
    setSelectedRows((prev) => ({ ...prev, [id]: !prev[id] }));
  };

  const handleView = (row) => {
    setSelectedOrder({
      ticketId: row.id,
      billingUser: "biller",
      customerName: row.customerName || "Rajesh Kumar",
      phoneNumber: row.customerPhone || "+91 98765 43210",
      address: "124 Green Park, Sector 5 • South Extension",
      createdAt: `${row.createdAtDate} ${row.createdAtTime}`,
      orderType: row.orderType || "Dine In",
      tableNo: row.table,
      guests: 4,
      captain: row.captain || "-",
      items: [
        { id: "i1", name: "Mineral Water Bottle 1l", sku: "BEV-001", note: "Cold, No Lemon", qty: 2, status: "prepared" },
        { id: "i2", name: "Thumps Up Small", sku: "BEV-042", note: null, qty: 1, status: "cooking" },
        { id: "i3", name: "Thumps Up Large", sku: "BEV-043", note: "Extra Ice", qty: 1, status: "pending" },
      ],
    });
    setModalOpen(true);
  };

  return (
    <div className="w-full max-w-full space-y-4 px-3 sm:px-6 py-2 text-slate-800 overflow-x-hidden">
      {/* Navigation Tabs Header */}
      <div className="flex flex-wrap items-center justify-between border-b border-slate-200 pb-3 gap-3">
        <div className="flex items-center gap-6">
          {/* <button
            onClick={() => navigate("/kotview")}
            className={`flex items-center gap-2 pb-1 text-sm sm:text-base font-semibold border-b-2 transition cursor-pointer ${
              !isHistory
                ? "text-red-600 border-red-600"
                : "text-slate-600 border-transparent hover:text-slate-900"
            }`}
          >
            <ClipboardList className="w-4 h-4" />
            KOT View
          </button> */}

          <button
            onClick={() => navigate("/kot-history")}
            className={`flex items-center gap-2 pb-1 text-sm sm:text-base font-semibold border-b-2 transition cursor-pointer ${isHistory
              ? "text-red-600 border-red-600"
              : "text-slate-600 border-transparent hover:text-slate-900"
              }`}
          >
            <History className="w-4 h-4" />
            Kot History
          </button>
        </div>
      </div>

      {/* Title & Export Button Row matching reference screenshot */}
      <div className="flex items-center justify-between gap-4 pt-1">
        <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">KOT</h1>
        <button className="flex items-center gap-1.5 rounded-xl border border-slate-200 bg-white px-3.5 py-2 text-xs sm:text-sm font-medium text-slate-700 hover:bg-slate-50 transition shadow-2xs cursor-pointer">
          <FileText className="h-4 w-4 text-slate-500" />
          <span>Export Excel</span>
          <ChevronDown className="h-4 w-4 text-slate-400" />
        </button>
      </div>

      {/* Filters Controls Bar - Collapsed View */}
      {!showMoreFilters && (
        <div className="rounded-2xl border border-slate-200 bg-white p-3.5 sm:p-4 shadow-xs">
          <div className="flex flex-wrap items-end gap-3">
            <div className="flex-1 min-w-[170px] max-w-full">
              <label className="mb-1 block text-xs font-semibold text-slate-700">Start Date</label>
              <DatePickerInput
                value={startDate}
                onChange={setStartDate}
                placeholder="Start Date"
                accentColor="blue"
              />
            </div>

            <div className="flex-1 min-w-[170px] max-w-full">
              <label className="mb-1 block text-xs font-semibold text-slate-700">End Date</label>
              <DatePickerInput
                value={endDate}
                onChange={setEndDate}
                placeholder="End Date"
                accentColor="blue"
              />
            </div>

            <div className="flex-1 min-w-[170px] max-w-full">
              <label className="mb-1 block text-xs font-semibold text-slate-700">All Order Type</label>
              <OrderTypeMultiSelect
                selected={orderTypeFilter}
                onChange={setOrderTypeFilter}
                focusColor="blue"
              />
            </div>

            <div className="flex items-center gap-2 shrink-0 w-full sm:w-auto pt-1 sm:pt-0 justify-between sm:justify-start">
              <button
                onClick={() => setShowMoreFilters(true)}
                className="flex-1 sm:flex-initial whitespace-nowrap rounded-xl border border-slate-200 bg-white px-3.5 sm:px-4 py-2 text-xs sm:text-sm font-medium text-slate-700 hover:bg-slate-50 transition shadow-2xs cursor-pointer text-center"
              >
                More Filters
              </button>
              <button className="flex-1 sm:flex-initial whitespace-nowrap rounded-xl border border-blue-500 bg-white px-4 sm:px-5 py-2 text-xs sm:text-sm font-medium text-blue-600 hover:bg-blue-50 transition shadow-2xs cursor-pointer text-center">
                Search
              </button>
              <button className="flex-1 sm:flex-initial whitespace-nowrap rounded-xl border border-slate-200 bg-white px-3.5 sm:px-4 py-2 text-xs sm:text-sm font-medium text-slate-700 hover:bg-slate-50 transition shadow-2xs cursor-pointer text-center">
                Show All
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Filters Controls Bar - Expanded View */}
      {showMoreFilters && (
        <div className="rounded-2xl border border-slate-200 bg-white p-3.5 sm:p-4 shadow-xs space-y-3.5">
          {/* Row 1 */}
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-6 gap-3">
            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">Start Date</label>
              <DatePickerInput
                value={startDate}
                onChange={setStartDate}
                placeholder="Start Date"
                accentColor="blue"
              />
            </div>

            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">End Date</label>
              <DatePickerInput
                value={endDate}
                onChange={setEndDate}
                placeholder="End Date"
                accentColor="blue"
              />
            </div>

            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">All Order Type</label>
              <OrderTypeMultiSelect
                selected={orderTypeFilter}
                onChange={setOrderTypeFilter}
                focusColor="blue"
              />
            </div>

            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">Kot ID</label>
              <input
                type="text"
                placeholder=""
                className="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs text-slate-700 focus:border-blue-500 focus:ring-1 focus:ring-blue-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">Customer Name</label>
              <input
                type="text"
                placeholder=""
                className="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs text-slate-700 focus:border-blue-500 focus:ring-1 focus:ring-blue-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="mb-1 block text-xs font-semibold text-slate-700">Customer Phone</label>
              <input
                type="text"
                placeholder=""
                className="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs text-slate-700 focus:border-blue-500 focus:ring-1 focus:ring-blue-500 focus:outline-none"
              />
            </div>
          </div>

          {/* Row 2 */}
          <div className="flex flex-wrap items-end gap-3 pt-1">
            <div className="flex-1 min-w-[150px] max-w-full">
              <label className="mb-1 block text-xs font-semibold text-slate-700">Table No.</label>
              <input
                type="text"
                placeholder=""
                className="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs text-slate-700 focus:border-blue-500 focus:ring-1 focus:ring-blue-500 focus:outline-none"
              />
            </div>

            <div className="flex-1 min-w-[150px] max-w-full">
              <label className="mb-1 block text-xs font-semibold text-slate-700">Status</label>
              <FilterSelect
                value={kotStatusFilter}
                onChange={(e) => setKotStatusFilter(e.target.value)}
                placeholder="All"
                focusColor="blue"
                options={[
                  "Pending",
                  "Accepted",
                  "Preparing",
                  "Ready",
                  "Served",
                  "Cancelled",
                ]}
              />
            </div>

            <div className="flex-1 min-w-[150px] max-w-full">
              <label className="mb-1 block text-xs font-semibold text-slate-700">Filter</label>
              <FilterSelect
                value={kotFilterBy}
                onChange={(e) => setKotFilterBy(e.target.value)}
                placeholder="All"
                focusColor="blue"
                options={[
                  { value: "vip", label: "VIP Only" },
                  { value: "delayed", label: "Delayed" },
                  { value: "late", label: "Late Orders" },
                  { value: "bill_printed", label: "Bill Printed" },
                  { value: "no_bill", label: "No Bill Printed" },
                ]}
              />
            </div>

            {/* Action buttons directly inline next to Filter */}
            <div className="flex items-center gap-2 shrink-0 w-full sm:w-auto pt-1 sm:pt-0 justify-between sm:justify-start">
              <button
                onClick={() => setShowMoreFilters(false)}
                className="whitespace-nowrap rounded-xl border border-slate-200 bg-white px-3.5 sm:px-4 py-2 text-xs sm:text-sm font-medium text-slate-700 hover:bg-slate-50 transition shadow-2xs cursor-pointer text-center"
              >
                Less Filters
              </button>
              <button className="whitespace-nowrap rounded-xl border border-blue-500 bg-white px-4 sm:px-5 py-2 text-xs sm:text-sm font-medium text-blue-600 hover:bg-blue-50 transition shadow-2xs cursor-pointer text-center">
                Search
              </button>
              <button className="whitespace-nowrap rounded-xl border border-slate-200 bg-white px-3.5 sm:px-4 py-2 text-xs sm:text-sm font-medium text-slate-700 hover:bg-slate-50 transition shadow-2xs cursor-pointer text-center">
                Show All
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Data Table Section (Exact All Orders Table Design & Responsive Wrapper) */}
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
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">KOT #</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">Order Type</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">Customer Name</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">Customer Phone</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">Table</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800 max-w-[280px]">Items</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800 text-center">No. of Items</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">Status</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">Bill Print Date</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">Prep Duration</th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800">
                  <span className="flex items-center gap-1 cursor-pointer hover:text-slate-900">
                    Created <ArrowDown className="h-3 w-3 text-slate-600" />
                  </span>
                </th>
                <th className="border-y border-slate-200 px-3 py-3 text-xs font-bold text-slate-800 text-center min-w-[210px]">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {kotData
                .filter((row) => {
                  if (orderTypeFilter.length === 0) return true;
                  return orderTypeFilter.some((type) => row.orderType.includes(type));
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
                      #{row.id}
                    </td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs">
                      <div className="font-semibold text-slate-800">{row.orderType}</div>
                      <div className="font-bold italic text-slate-900">{row.subType}</div>
                    </td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs text-slate-700 font-medium">{row.customerName}</td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs text-slate-700">{row.customerPhone}</td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs font-bold text-slate-800">{row.table}</td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs text-slate-800 font-medium leading-normal max-w-[280px]">
                      {row.items}
                    </td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs font-semibold text-slate-800 text-center">{row.itemCount}</td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs">
                      <span className="inline-flex rounded-full bg-[#e8f8f0] px-3 py-1 text-xs font-bold text-[#10b981]">
                        {row.status}
                      </span>
                    </td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs text-slate-700 font-medium">{row.billPrintDate}</td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs">
                      <span className={`inline-flex items-center gap-1 font-semibold ${row.late ? "text-red-500" : "text-emerald-600"}`}>
                        <Clock className="w-3.5 h-3.5" />
                        {row.prep}
                      </span>
                    </td>
                    <td className="border-b border-slate-200 px-3 py-4 text-xs font-semibold text-slate-800 leading-tight">
                      <div>{row.createdAtDate}</div>
                      <div className="text-slate-600 font-medium mt-0.5">{row.createdAtTime}</div>
                    </td>
                    <td className="border-b border-slate-200 px-3 py-4 text-center min-w-[210px]">
                      <div className="flex items-center justify-center gap-1.5">
                        {/* 1. Eye View Icon */}
                        <div className="relative group">
                          <button
                            onClick={() => handleView(row)}
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

                        {/* 2. Receipt Ticket Icon */}
                        <div className="relative group">
                          <button
                            onClick={() => handleView(row)}
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

                        {/* 3. Pencil Line Edit Icon */}
                        <div className="relative group">
                          <button className="h-8 w-8 rounded-lg border border-slate-200 bg-[#f8fafc] text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition flex items-center justify-center shadow-2xs cursor-pointer" title="Edit">
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

                        {/* 4. Cancel / Refund Document Icon */}
                        <div className="relative group">
                          <button className="h-8 w-8 rounded-lg border border-slate-200 bg-[#f8fafc] text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition flex items-center justify-center shadow-2xs cursor-pointer" title="Cancel">
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

                        {/* 5. Card Edit Icon */}
                        <div className="relative group">
                          <button className="h-8 w-8 rounded-lg border border-slate-200 bg-[#f8fafc] text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition flex items-center justify-center shadow-2xs cursor-pointer" title="Change Payment Type">
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
          <div className="text-slate-600 font-medium text-center lg:text-left">Showing 1 to 4 of 4 records</div>

          {/* Pagination Controls */}
          <div className="flex items-center justify-center gap-1.5">
            <button className="h-8 w-8 rounded-lg bg-red-600 text-white font-bold text-xs flex items-center justify-center shadow-xs">
              1
            </button>
            <button className="h-8 w-8 rounded-lg border border-slate-200 bg-white text-slate-600 hover:bg-slate-50 text-xs flex items-center justify-center font-medium">
              2
            </button>
            <button className="rounded-lg border border-slate-200 bg-white px-3 py-1 text-xs text-slate-600 hover:bg-slate-50 font-medium">
              Next
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

      {/* Slide-over Drawer Details Modal */}
      <KotDetailModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        order={selectedOrder}
        onPrint={() => window.print()}
        onMarkAllReady={() => {
          setModalOpen(false);
        }}
      />
    </div>
  );
}