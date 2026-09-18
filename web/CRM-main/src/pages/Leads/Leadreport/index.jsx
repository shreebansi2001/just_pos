"use client";

import React, { useState, useRef, useEffect, useMemo } from "react";
import {
  ChevronRight, ChevronLeft, Download, Users, CheckCircle2, XCircle,
  Star, Zap, DollarSign, Filter, Calendar,
} from "lucide-react";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
  LineChart, Line, XAxis, YAxis, Tooltip,
  ResponsiveContainer, PieChart, Pie, Cell,
} from "recharts";
import {
  getleadcountbystatus,
  GetAllLead,
  getallleadsource,
  getallstatus,
  GetAllMemberByUserId,
  AddExclusiveReport , 
  searchfliterlead
} from "@/services/apiServices";
import SelectLeadReport from "../../../partials/modal/report/SelectLeadReport";
import { usePermission } from "@/hooks/usePermission";


const WEEKDAYS = ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"];

const isSameDay = (a, b) =>
  a.getFullYear() === b.getFullYear() &&
  a.getMonth() === b.getMonth() &&
  a.getDate() === b.getDate();

const formatShort = (d) =>
  d.toLocaleDateString("en-IN", { day: "2-digit", month: "short", year: "numeric" });


const InlineDatePicker = ({ label, value, onChange }) => {
  const [open, setOpen] = useState(false);
  const [viewDate, setViewDate] = useState(value || new Date());
  const wrapperRef = useRef(null);

  useEffect(() => {
    const handleOutside = (e) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener("mousedown", handleOutside);
    return () => document.removeEventListener("mousedown", handleOutside);
  }, []);

  const changeMonth = (delta) =>
    setViewDate((prev) => new Date(prev.getFullYear(), prev.getMonth() + delta, 1));

  const monthLabel = viewDate.toLocaleDateString("en-IN", { month: "long", year: "numeric" });
  const firstOfMonth = new Date(viewDate.getFullYear(), viewDate.getMonth(), 1);
  const daysInMonth = new Date(viewDate.getFullYear(), viewDate.getMonth() + 1, 0).getDate();
  const leadingBlanks = firstOfMonth.getDay();

  const handlePick = (day) => {
    const picked = new Date(viewDate.getFullYear(), viewDate.getMonth(), day);
    onChange(picked);
    setOpen(false);
  };

  return (
    <div className="flex flex-col gap-1 relative" ref={wrapperRef}>
      <label className="text-[11px] font-semibold tracking-wider text-gray-400 uppercase">{label}</label>
      <button
        type="button"
        onClick={() => setOpen((p) => !p)}
        className="flex items-center gap-2 h-9 px-3 rounded-lg border border-gray-200 text-sm text-gray-700 bg-white cursor-pointer hover:border-primary/50 transition-colors min-w-[160px]"
      >
        <Calendar className="w-4 h-4 text-primary shrink-0" />
        <span className={value ? "text-gray-700" : "text-gray-400"}>
          {value ? formatShort(value) : "Select date"}
        </span>
      </button>

      {open && (
        <div className="absolute top-full left-0 z-50 mt-2 w-72 rounded-xl border border-gray-200 bg-white p-3 shadow-xl">
          <div className="flex items-center justify-between mb-2">
            <button type="button" onClick={() => changeMonth(-1)}
              className="h-7 w-7 flex items-center justify-center rounded-md hover:bg-gray-100 transition-colors">
              <ChevronLeft className="h-4 w-4 text-gray-600" />
            </button>
            <span className="text-sm font-semibold text-gray-900">{monthLabel}</span>
            <button type="button" onClick={() => changeMonth(1)}
              className="h-7 w-7 flex items-center justify-center rounded-md hover:bg-gray-100 transition-colors">
              <ChevronRight className="h-4 w-4 text-gray-600" />
            </button>
          </div>
          <div className="grid grid-cols-7 mb-1">
            {WEEKDAYS.map((d) => (
              <span key={d} className="text-[11px] text-center text-gray-400 font-semibold py-1">{d}</span>
            ))}
          </div>
          <div className="grid grid-cols-7 gap-y-1">
            {Array.from({ length: leadingBlanks }).map((_, i) => <span key={`b-${i}`} />)}
            {Array.from({ length: daysInMonth }).map((_, i) => {
              const day = i + 1;
              const cellDate = new Date(viewDate.getFullYear(), viewDate.getMonth(), day);
              const isSelected = value && isSameDay(cellDate, value);
              const isToday = isSameDay(cellDate, new Date());
              return (
                <button key={day} type="button" onClick={() => handlePick(day)}
                  className={cn(
                    "h-8 w-8 mx-auto rounded-full text-sm flex items-center justify-center transition-colors",
                    isSelected ? "bg-primary text-white font-semibold"
                      : "text-gray-700 hover:bg-violet-50 hover:text-primary",
                    !isSelected && isToday && "border border-primary/40 text-primary",
                  )}>
                  {day}
                </button>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
};


const FUNNEL_COLORS = ["#4F46E5", "#6366F1", "#818CF8", "#10B981", "#F97316", "#FBBF24", "#EF4444"];
const SOURCE_COLORS = ["#4F46E5", "#6366F1", "#F97316", "#FBBF24", "#10B981", "#EF4444", "#8B5CF6"];

const AVATAR_COLORS = [
  "bg-violet-200 text-violet-700",
  "bg-blue-200 text-blue-700",
  "bg-emerald-200 text-emerald-700",
  "bg-amber-200 text-amber-700",
];

const getInitials = (name = "") =>
  name.split(" ").map((p) => p[0]).join("").slice(0, 2).toUpperCase();


const CustomTooltip = ({ active, payload, label }) => {
  if (!active || !payload?.length) return null;
  return (
    <div className="bg-white border border-gray-100 rounded-xl shadow-lg px-4 py-3 text-sm">
      <p className="font-semibold text-gray-700 mb-1">{label}</p>
      {payload.map((p) => (
        <p key={p.name} style={{ color: p.color }} className="text-xs">
          {p.name}: <span className="font-semibold">{p.value.toLocaleString()}</span>
        </p>
      ))}
    </div>
  );
};


const LeadReports = () => {
  const [fromDate, setFromDate] = useState(null);
  const [toDate,   setToDate]   = useState(null);
  const [chartMode, setChartMode] = useState("Month");

  const [filterUser,   setFilterUser]   = useState("");
  const [filterSource, setFilterSource] = useState("");
const [filterStatus,   setFilterStatus]   = useState("");
const [filterPriority, setFilterPriority] = useState("");
  const [leadCounts, setLeadCounts] = useState({ total: 0, open: 0, won: 0, lost: 0, fup: 0 });
  const [leadsData,  setLeadsData]  = useState([]);
  const [sourceList, setSourceList] = useState([]);
  const [statusList, setStatusList] = useState([]);
  const [userList,   setUserList]   = useState([]);
  const [loading,    setLoading]    = useState(true);

  const userId   = Number(localStorage.getItem("mainId")  || 0);
  const assignId = Number(localStorage.getItem("assignId") || -1);
const [isExporting, setIsExporting] = useState(false);
const [isReportModalOpen, setIsReportModalOpen] = useState(false);
  const { view, add, edit, delete: canDelete } = usePermission("Crm Report");

  const PRIORITY_OPTIONS = [
  { id: "High",   label: "High" },
  { id: "Medium", label: "Medium" },
  { id: "Low",    label: "Low" },
];
useEffect(() => {
  setLoading(true);
  Promise.all([
    getleadcountbystatus(userId),
    getallleadsource(userId),
    getallstatus(userId),
    GetAllMemberByUserId(userId),
  ])
    .then(([countRes, sourceRes, statusRes, memberRes]) => {
      const d = countRes?.data?.data || {};
      setLeadCounts({
        total: d["Total Leads"] ?? 0,
        open:  d["Open"]        ?? 0,
        won:   d["Won"]         ?? 0,
        lost:  d["Lost"]        ?? 0,
        fup:   d["Today's FUP"] ?? 0,
      });

      setSourceList(sourceRes?.data?.data || []);
      setStatusList(statusRes?.data?.data || []);

      const raw = memberRes?.data?.data;
      const list =
        raw?.userDetails?.UserDetails ||
        raw?.UserDetails ||
        (Array.isArray(raw) ? raw : []);
      setUserList(list);
    })
    .catch((err) => console.error("Report fetch error:", err))
    .finally(() => setLoading(false));
}, [userId]);

useEffect(() => {
  const fetchLeads = async () => {
    try {
      const hasFilter = filterUser || filterSource || filterStatus || filterPriority;

      if (hasFilter) {
        const res = await searchfliterlead(
          filterUser ? Number(filterUser) : "",
          filterPriority || "",
          filterSource ? Number(filterSource) : "",
          filterStatus ? Number(filterStatus) : "",
        );
        const list = Array.isArray(res?.data)
          ? res.data
          : Array.isArray(res?.data?.data)
          ? res.data.data
          : [];
        setLeadsData(list);
      } else {
        const res = await GetAllLead(userId, assignId);
        setLeadsData(res?.data?.data?.["All Leads"] ?? []);
      }
    } catch (err) {
      console.error("Lead fetch error:", err);
      setLeadsData([]);
    }
  };

  fetchLeads();
}, [filterUser, filterSource, filterStatus, filterPriority, userId, assignId]);

const filteredLeads = useMemo(() => {
  return leadsData.filter((lead) => {
    let matchDate = true;
    if (fromDate || toDate) {
      const created = lead.inquiryDate ? new Date(lead.inquiryDate) : null;
      if (created) {
        if (fromDate && created < fromDate) matchDate = false;
        if (toDate) {
          const toEnd = new Date(toDate);
          toEnd.setHours(23, 59, 59, 999);
          if (created > toEnd) matchDate = false;
        }
      }
    }
    return matchDate;
  });
}, [leadsData, fromDate, toDate]);

  const statCards = useMemo(() => {
    const hasFilter = filterUser || filterSource || fromDate || toDate;
    const base = hasFilter ? filteredLeads : leadsData;

    const wonCount  = base.filter((l) => l.leadStatus?.statusName?.toLowerCase() === "won").length;
    const lostCount = base.filter((l) => l.leadStatus?.statusName?.toLowerCase() === "lost").length;
    const newCount  = base.filter((l) => l.leadStatus?.statusName?.toLowerCase() === "new" ||
                                         l.leadStatus?.statusName?.toLowerCase() === "open").length;
    const activeCount = base.filter((l) => {
      const s = l.leadStatus?.statusName?.toLowerCase() || "";
      return s !== "won" && s !== "lost";
    }).length;

    const totalDeal = base.reduce((sum, l) => sum + (Number(l.dealValue) || 0), 0);

    return [
      { label: "Total Leads", value: hasFilter ? String(base.length) : String(leadCounts.total),
        delta: "+12.5%", icon: Users,        iconBg: "bg-violet-100 text-violet-600",   positive: true  },
      { label: "Won",         value: hasFilter ? String(wonCount)  : String(leadCounts.won),
        delta: "+8.4%",  icon: CheckCircle2, iconBg: "bg-emerald-100 text-emerald-600", positive: true  },
      { label: "Lost",        value: hasFilter ? String(lostCount) : String(leadCounts.lost),
        delta: "+3.1%",  icon: XCircle,      iconBg: "bg-rose-100 text-rose-600",       positive: false },
      { label: "New",         value: hasFilter ? String(newCount)  : String(leadCounts.open),
        delta: "+15.3%", icon: Star,         iconBg: "bg-amber-100 text-amber-500",     positive: true  },
      { label: "Active",      value: String(activeCount),
        delta: "+10.7%", icon: Zap,          iconBg: "bg-blue-100 text-blue-600",       positive: true  },
      { label: "Deal Value",  value: totalDeal > 0 ? `₹${totalDeal.toLocaleString("en-IN")}` : "₹0",
        delta: "+18.6%", icon: DollarSign,   iconBg: "bg-green-100 text-green-600",     positive: true  },
    ];
  }, [filteredLeads, leadsData, leadCounts, filterUser, filterSource, fromDate, toDate]);

  const lineData = useMemo(() => {
    if (chartMode === "Month") {
      const monthMap = {};
      filteredLeads.forEach((lead) => {
        const date = lead.inquiryDate ? new Date(lead.inquiryDate) : null;
        if (!date || isNaN(date)) return;
        const key = date.toLocaleDateString("en-IN", { month: "short", year: "2-digit" });
        if (!monthMap[key]) monthMap[key] = { label: key, leads: 0, won: 0 };
        monthMap[key].leads += 1;
        if (lead.leadStatus?.statusName?.toLowerCase() === "won") monthMap[key].won += 1;
      });
      return Object.values(monthMap).slice(-6);
    } else {
      const yearMap = {};
      filteredLeads.forEach((lead) => {
        const date = lead.inquiryDate ? new Date(lead.inquiryDate) : null;
        if (!date || isNaN(date)) return;
        const key = String(date.getFullYear());
        if (!yearMap[key]) yearMap[key] = { label: key, leads: 0, won: 0 };
        yearMap[key].leads += 1;
        if (lead.leadStatus?.statusName?.toLowerCase() === "won") yearMap[key].won += 1;
      });
      return Object.values(yearMap);
    }
  }, [filteredLeads, chartMode]);

  const funnelData = useMemo(() => {
    const total = filteredLeads.length || 1;
    const countMap = {};
    filteredLeads.forEach((lead) => {
      const name = lead.leadStatus?.statusName || "Unknown";
      const color = lead.leadStatus?.colorCode || "#9CA3AF";
      if (!countMap[name]) countMap[name] = { name, value: 0, color };
      countMap[name].value += 1;
    });
    return Object.values(countMap).map((item) => ({
      ...item,
      pct: ((item.value / total) * 100).toFixed(1) + "%",
    }));
  }, [filteredLeads]);

  const sourceData = useMemo(() => {
    const total = filteredLeads.length || 1;
    const srcMap = {};
    filteredLeads.forEach((lead) => {
      const name = lead.leadSource?.sourceName || "Unknown";
      if (!srcMap[name]) srcMap[name] = { name, value: 0 };
      srcMap[name].value += 1;
    });
    return Object.values(srcMap).map((item, i) => ({
      ...item,
      color: SOURCE_COLORS[i % SOURCE_COLORS.length],
      pct: ((item.value / total) * 100).toFixed(1) + "%",
    }));
  }, [filteredLeads]);

  const repPerformance = useMemo(() => {
    const repMap = {};
    filteredLeads.forEach((lead) => {
      const name = lead.leadAssignName || "Unassigned";
      const initials = getInitials(name);
      if (!repMap[name]) {
        repMap[name] = { name, avatar: initials, total: 0, new: 0, contacted: 0, qualified: 0, won: 0, lost: 0, deal: 0 };
      }
      repMap[name].total += 1;
      const status = lead.leadStatus?.statusName?.toLowerCase() || "";
      if (status === "won")        repMap[name].won       += 1;
      else if (status === "lost")  repMap[name].lost      += 1;
      else if (status === "new" || status === "open") repMap[name].new += 1;
      else if (status === "contacted")   repMap[name].contacted  += 1;
      else if (status === "qualified")   repMap[name].qualified  += 1;
      repMap[name].deal += Number(lead.dealValue) || 0;
    });
    return Object.values(repMap)
      .sort((a, b) => b.total - a.total)
      .slice(0, 10);
  }, [filteredLeads]);

const handleClear = () => {
  setFromDate(null);
  setToDate(null);
  setFilterUser("");
  setFilterSource("");
  setFilterStatus("");
  setFilterPriority("");
};
const formatForApi = (date) => {
  if (!date) return "";
  const d = String(date.getDate()).padStart(2, "0");
  const m = String(date.getMonth() + 1).padStart(2, "0");
  const y = date.getFullYear();
  return `${d}/${m}/${y}`;
};

const handleExportReport = async () => {
  const payload = {
    eventId: 0,
    partyId: -1,
    eventFunctionId: -1,
    eventFunctionIds: [],
    adminTemplateModuleId: 0,
    type: null,
    userId,
    lang: 0,

    isCategoryImage: -1,
    isCategoryInstruction: -1,
    isCategorySlogan: -1,
    isItemImage: -1,
    isCombo: -1,
    isItemInstruction: -1,
    isItemSlogan: -1,
    isCompanyDetails: -1,
    isCompanyLogo: -1,
    isPartyDetails: -1,
    isWithQty: -1,
    pageSize: "",
    isWithPrice: -1,
    agencyId: [],
    managerIds: [],
    itemId: [],
    rawMaterialCatIds: [],
    catFontId: -1,
    itemFontId: -1,
    sloganFontId: -1,
    catFontSize: -1,
    itemFontSize: -1,
    sloganFontSize: -1,
    isTermsCond: -1,
    isExtraCharges: -1,
    isHalfPax: -1,
    isDoc: -1,
    is3Column: -1,
    isFunctionNextPage: -1,
    isAddDecoration: -1,
    isShowEventRemarks: -1,

    startDate: formatForApi(fromDate),
    endDate: formatForApi(toDate),
    eventStatus: [],

    leadAssignId: filterUser ? Number(filterUser) : 0,
    sourceId: filterSource ? Number(filterSource) : 0,
    statusId: filterStatus ? Number(filterStatus) : 0,
priority: filterPriority || "",
    advancePaymentId: 0,
    customPackageId: 0,
  };

  const formData = new FormData();
  Object.entries(payload).forEach(([key, value]) => {
    if (Array.isArray(value)) {
      value.forEach((v) => formData.append(`${key}[]`, v));
    } else {
      formData.append(key, value === true ? "1" : value === false ? "0" : (value ?? ""));
    }
  });

  const newTab = window.open("", "_blank");

  setIsExporting(true);
  try {
    const { data } = await AddExclusiveReport(formData);
    if (data?.success && data?.report_path) {
      if (newTab) {
        newTab.location.href = data.report_path;
      } else {
        window.open(data.report_path, "_blank");
      }
    } else {
      newTab?.close();
      Swal.fire({ icon: "error", title: "Error", text: data?.msg || "Failed to generate report" });
    }
  } catch (err) {
    newTab?.close();
    Swal.fire({ icon: "error", title: "Error", text: err?.response?.data?.msg || "Something went wrong" });
  } finally {
    setIsExporting(false);
  }
};
  const totalLeads = filteredLeads.length;

  return (
    <div className="min-h-screen">
      <main className="p-5 space-y-5  mx-auto">

        {/* Header */}
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Lead Reports</h1>
            <p className="text-sm text-muted-foreground mt-0.5 flex items-center gap-1">
              <span className="text-primary">Home</span>
              <ChevronRight className="h-3.5 w-3.5 text-gray-400" />
              <span className="text-gray-400">Reports</span>
            </p>
          </div>
          {add && (
        <Button size="md" className="bg-primary hover:bg-primary/90" onClick={() => setIsReportModalOpen(true)}>
  <Download className="w-4 h-4 mr-2" />
  Export Report
</Button>
       
          )}
          </div>

        {/* Filters */}
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm px-5 py-4">
          <div className="flex flex-wrap items-end justify-between gap-4">
            <div className="flex flex-wrap items-end gap-4">

              {/* <InlineDatePicker label="From" value={fromDate}
                onChange={(d) => { setFromDate(d); if (toDate && d > toDate) setToDate(d); }} />

              <InlineDatePicker label="To" value={toDate}
                onChange={(d) => { if (fromDate && d < fromDate) return; setToDate(d); }} /> */}

              {/* User filter */}
              <div className="flex flex-col gap-1">
                <label className="text-[11px] font-semibold tracking-wider text-gray-400 uppercase">User</label>
                <select
                  value={filterUser}
                  onChange={(e) => setFilterUser(e.target.value)}
                  className="h-9 px-3 rounded-lg border border-gray-200 text-sm text-gray-700 bg-white hover:border-primary/50 transition-colors min-w-[150px]"
                >
                  <option value="">All Users</option>
                  {userList.map((u) => {
                    const id   = u.id ?? u.userId;
                    const name = [u.firstName, u.lastName].filter(Boolean).join(" ").trim() || u.userName || u.name || `#${id}`;
                    return <option key={id} value={id}>{name}</option>;
                  })}
                </select>
              </div>

<div className="flex flex-col gap-1">
  <label className="text-[11px] font-semibold tracking-wider text-gray-400 uppercase">Source</label>
  <select
    value={filterSource}
    onChange={(e) => setFilterSource(e.target.value)}
    className="h-9 px-3 rounded-lg border border-gray-200 text-sm text-gray-700 bg-white hover:border-primary/50 transition-colors min-w-[150px]"
  >
    <option value="">All Sources</option>
    {sourceList.map((s) => (
      <option key={s.leadSourceId} value={s.leadSourceId}>{s.sourceName}</option>
    ))}
  </select>
</div>
<div className="flex flex-col gap-1">
  <label className="text-[11px] font-semibold tracking-wider text-gray-400 uppercase">Status</label>
  <select
    value={filterStatus}
    onChange={(e) => setFilterStatus(e.target.value)}
    className="h-9 px-3 rounded-lg border border-gray-200 text-sm text-gray-700 bg-white hover:border-primary/50 transition-colors min-w-[150px]"
  >
    <option value="">All Statuses</option>
    {statusList.map((s) => (
      <option key={s.leadStatusId} value={s.leadStatusId}>{s.statusName}</option>
    ))}
  </select>
</div>

<div className="flex flex-col gap-1">
  <label className="text-[11px] font-semibold tracking-wider text-gray-400 uppercase">Priority</label>
  <select
    value={filterPriority}
    onChange={(e) => setFilterPriority(e.target.value)}
    className="h-9 px-3 rounded-lg border border-gray-200 text-sm text-gray-700 bg-white hover:border-primary/50 transition-colors min-w-[150px]"
  >
    <option value="">All Priorities</option>
    {PRIORITY_OPTIONS.map((p) => (
      <option key={p.id} value={p.id}>{p.label}</option>
    ))}
  </select>
</div>
<InlineDatePicker
  label="From"
  value={fromDate}
  onChange={(d) => {
    setFromDate(d);
    if (toDate && d > toDate) setToDate(d);
  }}
/>

<InlineDatePicker
  label="To"
  value={toDate}
  onChange={(d) => {
    if (fromDate && d < fromDate) return;
    setToDate(d);
  }}
/>

              {/* Group By
              <div className="flex flex-col gap-1">
                <label className="text-[11px] font-semibold tracking-wider text-gray-400 uppercase">Group By</label>
                <select
                  value={chartMode}
                  onChange={(e) => setChartMode(e.target.value)}
                  className="h-9 px-3 rounded-lg border border-gray-200 text-sm text-gray-700 bg-white hover:border-primary/50 transition-colors min-w-[150px]"
                >
                  <option value="Month">Month</option>
                  <option value="Year">Year</option>
                </select>
              </div> */}
            </div>

        
          </div>
        </div>

        {/* Stat cards */}
        <div className="grid grid-cols-2 sm:grid-cols-3 xl:grid-cols-6 gap-4">
          {statCards.map((card) => (
            <div key={card.label} className="bg-white rounded-2xl border border-gray-100 shadow-sm px-4 py-4">
              <div className="flex items-center justify-between mb-3">
                <span className="text-[11px] font-semibold tracking-wider text-gray-400 uppercase">{card.label}</span>
                <div className={`w-8 h-8 rounded-lg flex items-center justify-center ${card.iconBg}`}>
                  <card.icon className="w-4 h-4" />
                </div>
              </div>
              <p className="text-2xl font-bold text-gray-900">{loading ? "—" : card.value}</p>
              {/* <p className={`text-xs mt-1 font-medium ${card.positive ? "text-emerald-600" : "text-rose-500"}`}>
                {card.delta}{" "}
                <span className="text-gray-400 font-normal">vs last month</span>
              </p> */}
            </div>
          ))}
        </div>

        {/* Charts row */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">

          {/* Line chart */}
          {/* <div className="lg:col-span-2 bg-white rounded-2xl border border-gray-100 shadow-sm p-5">
            <div className="flex items-center justify-between mb-3">
              <h2 className="text-sm font-semibold text-gray-900">Lead Volume Over Time</h2>
              <div className="flex items-center bg-gray-100 rounded-lg p-0.5 gap-0.5">
                {["Month", "Year"].map((mode) => (
                  <button key={mode} onClick={() => setChartMode(mode)}
                    className={`text-xs px-3 py-1.5 rounded-md font-medium transition-all ${
                      chartMode === mode ? "bg-white text-primary shadow-sm" : "text-gray-500 hover:text-gray-700"
                    }`}>
                    {mode}
                  </button>
                ))}
              </div>
            </div>
            <div className="flex items-center gap-4 mb-4">
              <span className="flex items-center gap-1.5 text-xs text-gray-500">
                <span className="w-3 h-0.5 rounded bg-indigo-500 inline-block" /> Total Leads
              </span>
              <span className="flex items-center gap-1.5 text-xs text-gray-500">
                <span className="w-3 h-0.5 rounded bg-emerald-500 inline-block" /> Won
              </span>
            </div>
            {lineData.length === 0 ? (
              <div className="flex items-center justify-center h-[240px] text-sm text-gray-400">
                No data for selected range
              </div>
            ) : (
              <ResponsiveContainer width="100%" height={240}>
                <LineChart data={lineData} margin={{ top: 4, right: 8, bottom: 0, left: -20 }}>
                  <XAxis dataKey="label" tick={{ fontSize: 11, fill: "#9CA3AF" }} axisLine={false} tickLine={false} />
                  <YAxis tick={{ fontSize: 11, fill: "#9CA3AF" }} axisLine={false} tickLine={false} />
                  <Tooltip content={<CustomTooltip />} />
                  <Line type="monotone" dataKey="leads" stroke="#6366F1" strokeWidth={2.5}
                    dot={{ r: 4, fill: "#6366F1", strokeWidth: 0 }} activeDot={{ r: 6 }} name="Total Leads" />
                  <Line type="monotone" dataKey="won" stroke="#10B981" strokeWidth={2}
                    dot={{ r: 4, fill: "#10B981", strokeWidth: 0 }} activeDot={{ r: 6 }} name="Won" />
                </LineChart>
              </ResponsiveContainer>
            )}
          </div> */}

          {/* Pipeline Funnel */}
          <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5">
            <h2 className="text-sm font-semibold text-gray-900 mb-4">Status </h2>
            {funnelData.length === 0 ? (
              <div className="flex items-center justify-center h-[180px] text-sm text-gray-400">No data</div>
            ) : (
              <>
                <div className="relative flex items-center justify-center">
                  <PieChart width={180} height={180}>
                    <Pie data={funnelData} cx={85} cy={85} innerRadius={58} outerRadius={85}
                      dataKey="value" strokeWidth={0}>
                      {funnelData.map((entry, i) => (
                        <Cell key={i} fill={entry.color || FUNNEL_COLORS[i % FUNNEL_COLORS.length]} />
                      ))}
                    </Pie>
                  </PieChart>
                  <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
                    <span className="text-2xl font-bold text-gray-900">{totalLeads.toLocaleString()}</span>
                    <span className="text-[10px] tracking-wider text-gray-400 uppercase font-semibold">Total</span>
                  </div>
                </div>
                <div className="mt-4 flex flex-col gap-2">
                  {funnelData.map((item, i) => (
                    <div key={item.name} className="flex items-center justify-between text-sm">
                      <div className="flex items-center gap-2">
                        <span className="w-2.5 h-2.5 rounded-full shrink-0"
                          style={{ backgroundColor: item.color || FUNNEL_COLORS[i % FUNNEL_COLORS.length] }} />
                        <span className="text-gray-600">{item.name}</span>
                      </div>
                      <span className="text-gray-400 text-xs">
                        {item.value.toLocaleString()}{" "}
                        <span className="text-gray-300">({item.pct})</span>
                      </span>
                    </div>
                  ))}
                </div>
              </>
            )}
          </div>
            <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5">
            <h2 className="text-sm font-semibold text-gray-900 mb-4">Leads by Source</h2>
            {sourceData.length === 0 ? (
              <div className="flex items-center justify-center h-[160px] text-sm text-gray-400">No data</div>
            ) : (
              <>
                <div className="relative flex items-center justify-center">
                  <PieChart width={160} height={160}>
                    <Pie data={sourceData} cx={75} cy={75} innerRadius={50} outerRadius={75}
                      dataKey="value" strokeWidth={0}>
                      {sourceData.map((entry, i) => (
                        <Cell key={i} fill={entry.color} />
                      ))}
                    </Pie>
                  </PieChart>
                  <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
                    <span className="text-xl font-bold text-gray-900">{totalLeads.toLocaleString()}</span>
                    <span className="text-[10px] tracking-wider text-gray-400 uppercase font-semibold">Total</span>
                  </div>
                </div>
                <div className="mt-4 flex flex-col gap-2">
                  {sourceData.map((item) => (
                    <div key={item.name} className="flex items-center justify-between text-sm">
                      <div className="flex items-center gap-2">
                        <span className="w-2.5 h-2.5 rounded-full shrink-0"
                          style={{ backgroundColor: item.color }} />
                        <span className="text-gray-600">{item.name}</span>
                      </div>
                      <span className="text-gray-400 text-xs">
                        {item.pct}{" "}
                        <span className="text-gray-300">({item.value.toLocaleString()})</span>
                      </span>
                    </div>
                  ))}
                </div>
              </>
            )}
          </div>
        </div>

      
          <div className=" bg-white rounded-2xl border border-gray-100 shadow-sm p-5">
            <h2 className="text-sm font-semibold text-gray-900 mb-4">Sales Rep Performance</h2>
            {repPerformance.length === 0 ? (
              <div className="flex items-center justify-center h-[120px] text-sm text-gray-400">No data</div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-gray-100">
                      {["Rep", "Total", "New", "Contacted", "Qualified", "Won", "Lost", "Deal Value"].map((h) => (
                        <th key={h}
                          className="text-left text-[11px] font-semibold tracking-wider text-gray-400 uppercase pb-3 pr-4 last:pr-0 whitespace-nowrap">
                          {h}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-50">
                    {repPerformance.map((rep, i) => (
                      <tr key={rep.name} className="hover:bg-gray-50/50 transition-colors">
                        <td className="py-3 pr-4">
                          <div className="flex items-center gap-2.5">
                            <div className={`w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold shrink-0 ${AVATAR_COLORS[i % AVATAR_COLORS.length]}`}>
                              {rep.avatar}
                            </div>
                            <span className="font-medium text-gray-800 whitespace-nowrap">{rep.name}</span>
                          </div>
                        </td>
                        <td className="py-3 pr-4 text-gray-600 font-medium">{rep.total}</td>
                        <td className="py-3 pr-4 text-gray-500">{rep.new}</td>
                        <td className="py-3 pr-4 text-gray-500">{rep.contacted}</td>
                        <td className="py-3 pr-4 text-gray-500">{rep.qualified}</td>
                        <td className="py-3 pr-4">
                          <span className="text-emerald-600 font-semibold">{rep.won}</span>
                        </td>
                        <td className="py-3 pr-4">
                          <span className="text-rose-500 font-semibold">{rep.lost}</span>
                        </td>
                        <td className="py-3 font-semibold text-gray-800">
                          {rep.deal > 0 ? `₹${rep.deal.toLocaleString("en-IN")}` : "—"}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        
<SelectLeadReport
  open={isReportModalOpen}
  onClose={() => setIsReportModalOpen(false)}
  leadAssignId={filterUser ? Number(filterUser) : 0}
  leadSourceId={filterSource ? Number(filterSource) : 0}
  leadStatusId={filterStatus ? Number(filterStatus) : 0}
  leadPriority={filterPriority || ""}
  fromDate={formatForApi(fromDate)}
  endDate={formatForApi(toDate)}
/>
      </main>
    </div>
  );
};

export default LeadReports;