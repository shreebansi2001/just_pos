"use client";

import React, { useEffect, useRef, useState } from "react";
import Swal from "sweetalert2";
import {
  Bookmark,
  Calendar,
  ChevronDown,
  ChevronLeft,
  ChevronRight,
  ClipboardList,
  IndianRupee,
  Mail,
  Navigation,
  Plus,
  Tag,
  User,
  UserPlus,
  Users,
  X,
  Hash,
} from "lucide-react";
import { useForm, Controller } from "react-hook-form";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  AddLead as AddLeadAPI,
  fetchstatebycontry,
  fetchcitybystateid,
  getallstatus,
  getallleadsource,
  GetEventType,
  GetLeadCode,
Fetchmanager,
  updatelead , 
} from "@/services/apiServices";
import AddFollowUpModal from "../../partials/modal/add-follow-up/AddFollowUpModal";
import { useNavigate  , useLocation} from "react-router-dom";


const DEFAULT_COUNTRY_ID = 1;
const WEEKDAYS = ["S", "M", "T", "W", "T", "F", "S"];


const LEAD_QUALITY_OPTIONS = [
  { id: "hot", label: "Hot" },
  { id: "cold", label: "Cold" },
];

const PRIORITY_OPTIONS = [
  { id: "high", label: "High" },
  { id: "medium", label: "Medium" },
  { id: "low", label: "Low" },
];

const filterByQuery = (list, query) => {
  if (!query) return list;
  const q = query.toLowerCase();
  return list.filter((o) => o.label.toLowerCase().includes(q));
};

const fetchLeadQualityOptions = (query) =>
  Promise.resolve(filterByQuery(LEAD_QUALITY_OPTIONS, query));

const fetchPriorityOptions = (query) =>
  Promise.resolve(filterByQuery(PRIORITY_OPTIONS, query));

const formatDMY = (date) => {
  const d = String(date.getDate()).padStart(2, "0");
  const m = String(date.getMonth() + 1).padStart(2, "0");
  const y = date.getFullYear();
  return `${d}/${m}/${y}`;
};
const formatFollowUpDisplay = (dateStr) => {
  if (!dateStr) return "";
  try {
    const d = new Date(dateStr);
    const date = d.toLocaleDateString("en-IN", { day: "2-digit", month: "short", year: "numeric" });
    let hours = d.getHours();
    const mins = String(d.getMinutes()).padStart(2, "0");
    const period = hours >= 12 ? "PM" : "AM";
    hours = hours % 12 || 12;
    return `${date}, ${String(hours).padStart(2, "0")}:${mins} ${period}`;
  } catch {
    return dateStr;
  }
};
const formatShort = (date) =>
  date.toLocaleDateString("en-IN", { day: "2-digit", month: "short", year: "numeric" });

const isSameDay = (a, b) =>
  a.getFullYear() === b.getFullYear() &&
  a.getMonth() === b.getMonth() &&
  a.getDate() === b.getDate();

// Backend sometimes sends "YYYY-MM-DD" (ISO) and sometimes "DD/MM/YYYY".
// Handle both so edit-mode dates don't come out as Invalid Date.
const parseFlexibleDate = (value) => {
  if (!value) return null;
  if (value.includes("-")) {
    const [year, month, day] = value.split("-");
    const d = new Date(Number(year), Number(month) - 1, Number(day));
    return isNaN(d.getTime()) ? null : d;
  }
  if (value.includes("/")) {
    const [day, month, year] = value.split("/");
    const d = new Date(Number(year), Number(month) - 1, Number(day));
    return isNaN(d.getTime()) ? null : d;
  }
  return null;
};
const FieldLabel = ({ children, required }) => (
  <label className="block text-sm font-medium text-gray-700 mb-1.5">
    {children}
    {required && <span className="text-rose-500 ml-0.5">*</span>}
  </label>
);

const FormInput = React.forwardRef(function FormInput(
  { label, required, prefix, icon: Icon, className, error, ...props },
  ref
) {
  return (
    <div className={className}>
      <FieldLabel required={required}>{label}</FieldLabel>
      <div className="relative flex items-stretch">
        {prefix && (
          <span className="inline-flex items-center px-3 rounded-l-lg border border-r-0 border-gray-300 bg-gray-50 text-sm text-gray-900">
            {prefix}
          </span>
        )}
        {Icon && !prefix && (
          <Icon className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-900" />
        )}
        <Input
          ref={ref}
          {...props}
          className={cn(
            "h-10 text-sm border-gray-300",
            prefix && "rounded-l-none",
            Icon && !prefix && "pl-9",
            error && "border-rose-400 focus-visible:ring-rose-200"   // 👈 error styling
          )}
        />
      </div>
      {error && <p className="mt-1 text-xs text-rose-500">{error}</p>}  {/* 👈 error text */}
    </div>
  );
});

const SectionCard = ({ icon: Icon, title, action, children, className }) => (
  <div className={cn("rounded-xl border border-gray-100 bg-white p-5 shadow-sm", className)}>
    <div className="flex items-center justify-between mb-4">
      <div className="flex items-center gap-2">
        <Icon className="h-4 w-4 text-primary" />
        <h3 className="text-sm font-semibold text-gray-900">{title}</h3>
      </div>
      {action}
    </div>
    {children}
  </div>
);

const FormTextarea = ({ label, required, placeholder, maxLength = 500, className, value, onChange }) => (
  <div className={className}>
    <FieldLabel required={required}>{label}</FieldLabel>
    <div className="relative">
      <textarea
        value={value || ""}
        onChange={(e) => onChange?.(e.target.value.slice(0, maxLength))}
        placeholder={placeholder}
        rows={3}
        className="w-full rounded-lg border border-gray-300 px-3.5 py-2.5 text-sm text-gray-700 placeholder:text-gray-400 resize-none focus:outline-none focus:ring-2 focus:ring-violet-100 transition-colors"
      />
      <span className="absolute bottom-2 right-3 text-[11px] text-gray-400">
        {(value || "").length}/{maxLength}
      </span>
    </div>
  </div>
);


const useDebouncedValue = (value, delay = 300) => {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => {
    const t = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(t);
  }, [value, delay]);
  return debounced;
};


const AsyncSearchSelect = ({
  label,
  required,
  placeholder = "Select",
  disabled = false,
  disabledMessage,
  value,
  onChange,
  fetchOptions,
  className,
  error,
}) => {
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState("");
  const [options, setOptions] = useState([]);
  const [loading, setLoading] = useState(false);
  const wrapperRef = useRef(null);
  const debouncedQuery = useDebouncedValue(query, 300);

  useEffect(() => {
    const handleOutside = (e) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener("mousedown", handleOutside);
    return () => document.removeEventListener("mousedown", handleOutside);
  }, []);

  useEffect(() => {
    if (!open || disabled) return;
    let active = true;
    setLoading(true);
    fetchOptions(debouncedQuery)
      .then((opts) => { if (active) setOptions(opts || []); })
      .catch(() => { if (active) setOptions([]); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [open, debouncedQuery, disabled]);

  return (
    <div className={cn("relative", className)} ref={wrapperRef}>
      <FieldLabel required={required}>{label}</FieldLabel>
      <button
        type="button"
        disabled={disabled}
        onClick={() => setOpen((p) => !p)}
        className={cn(
          "w-full inline-flex items-center justify-between gap-2 rounded-lg border bg-white px-3.5 h-10 text-sm text-left transition-colors",
          error ? "border-rose-400" : "border-gray-300",   // 👈 error styling
          disabled && "bg-gray-50 text-gray-400 cursor-not-allowed"
        )}
      >
        <span className={cn("truncate", !value && "text-gray-400")}>
          {value?.label || (disabled ? disabledMessage || placeholder : placeholder)}
        </span>
        <ChevronDown className="h-3.5 w-3.5 text-gray-400 shrink-0" />
      </button>

      {open && !disabled && (
        <div className="absolute z-20 mt-1.5 w-full rounded-xl border border-gray-200 bg-white shadow-lg">
          <div className="p-2 border-b border-gray-100">
            <input
              autoFocus
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Type to search..."
              className="w-full h-8 px-2.5 text-sm rounded-md border border-gray-200 focus:outline-none focus:ring-2 focus:ring-violet-100"
            />
          </div>
          <div className="max-h-56 overflow-y-auto p-1.5">
            {loading ? (
              <div className="px-3 py-2 text-sm text-gray-400">Loading...</div>
            ) : options.length === 0 ? (
              <div className="px-3 py-2 text-sm text-gray-400">No results found</div>
            ) : (
              options.map((opt) => (
                <button
                  key={opt.id}
                  type="button"
                  onClick={() => { onChange?.(opt); setOpen(false); setQuery(""); }}
                  className="w-full text-left rounded-lg px-3 py-1.5 text-sm text-gray-800 hover:bg-gray-50 transition-colors"
                >
                  {opt.label}
                </button>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
};


const DatePicker = ({ label, required, placeholder = "Select date", multiple = false, className, value, onChange }) => {
  const [open, setOpen] = useState(false);
  const [viewDate, setViewDate] = useState(new Date());
  const wrapperRef = useRef(null);
  const selectedDates = multiple ? (value || []) : value ? [value] : [];

  useEffect(() => {
    const handleOutside = (e) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener("mousedown", handleOutside);
    return () => document.removeEventListener("mousedown", handleOutside);
  }, []);

  const changeMonth = (delta) =>
    setViewDate((prev) => new Date(prev.getFullYear(), prev.getMonth() + delta, 1));

  const handlePick = (day) => {
    const picked = new Date(viewDate.getFullYear(), viewDate.getMonth(), day);
    if (multiple) {
      const next = selectedDates.some((d) => isSameDay(d, picked))
        ? selectedDates.filter((d) => !isSameDay(d, picked))
        : [...selectedDates, picked].sort((a, b) => a - b);
      onChange?.(next);
    } else {
      onChange?.(picked);
      setOpen(false);
    }
  };

  const firstOfMonth = new Date(viewDate.getFullYear(), viewDate.getMonth(), 1);
  const daysInMonth = new Date(viewDate.getFullYear(), viewDate.getMonth() + 1, 0).getDate();
  const leadingBlanks = firstOfMonth.getDay();
  const displayValue = selectedDates.length ? selectedDates.map(formatShort).join(", ") : "";

  return (
    <div className={cn("relative", className)} ref={wrapperRef}>
      <FieldLabel required={required}>{label}</FieldLabel>
      <button
        type="button"
        onClick={() => setOpen((p) => !p)}
        className="w-full inline-flex items-center gap-2 rounded-lg border border-gray-300 bg-white px-3.5 h-10 text-sm text-left transition-colors"
      >
        <Calendar className="h-4 w-4 text-gray-400 shrink-0" />
        <span className={cn("truncate", !displayValue && "text-gray-400")}>
          {displayValue || placeholder}
        </span>
      </button>

      {open && (
        <div className="absolute z-20 mt-2 w-72 rounded-xl border border-gray-200 bg-white p-3 shadow-lg">
          <div className="flex items-center justify-between mb-2">
            <button type="button" onClick={() => changeMonth(-1)} className="h-7 w-7 inline-flex items-center justify-center rounded-md hover:bg-gray-100">
              <ChevronLeft className="h-4 w-4 text-gray-600" />
            </button>
            <span className="text-sm font-medium text-gray-900">
              {viewDate.toLocaleDateString("en-IN", { month: "long", year: "numeric" })}
            </span>
            <button type="button" onClick={() => changeMonth(1)} className="h-7 w-7 inline-flex items-center justify-center rounded-md hover:bg-gray-100">
              <ChevronRight className="h-4 w-4 text-gray-600" />
            </button>
          </div>
          <div className="grid grid-cols-7 mb-1">
            {WEEKDAYS.map((d, i) => (
              <span key={i} className="text-[11px] text-center text-gray-400 font-medium py-1">{d}</span>
            ))}
          </div>
          <div className="grid grid-cols-7 gap-y-1">
            {Array.from({ length: leadingBlanks }).map((_, i) => <span key={`b-${i}`} />)}
            {Array.from({ length: daysInMonth }).map((_, i) => {
              const day = i + 1;
              const cellDate = new Date(viewDate.getFullYear(), viewDate.getMonth(), day);
              const isSelected = selectedDates.some((d) => isSameDay(d, cellDate));
              const isToday = isSameDay(cellDate, new Date());
              return (
                <button
                  key={day}
                  type="button"
                  onClick={() => handlePick(day)}
                  className={cn(
                    "h-8 w-8 mx-auto rounded-full text-sm flex items-center justify-center transition-colors",
                    isSelected ? "bg-primary text-white font-medium" : "text-gray-700 hover:bg-violet-50",
                    !isSelected && isToday && "border border-violet-300"
                  )}
                >
                  {day}
                </button>
              );
            })}
          </div>
          {multiple && (
            <div className="flex items-center justify-between mt-3 pt-2 border-t border-gray-100">
              <span className="text-xs text-gray-400">
                {selectedDates.length} date{selectedDates.length !== 1 ? "s" : ""} selected
              </span>
              <Button size="sm" className="h-7 text-xs bg-primary" onClick={() => setOpen(false)}>Done</Button>
            </div>
          )}
        </div>
      )}
    </div>
  );
};


const DEFAULT_VALUES = {
  clientName: "",
  leadCode: "",
  contactNumber: "",
  alternateNumber: "",
  emailId: "",
  cityId: null,
  stateId: null,
  leadRemark: "",
  leadStatus: null,
  leadType: "",
  leadAssignId: null,
  leadSourceId: null,
  leadSubSourceId: "",
  estimateAmount: "",
  closeDate: null,
  eventTypeId: null,
  minPax: "",
  maxPax: "",
  leadQuality: null,
  priority: null,
  tentEventDate: [],
};


const AddLead = ({ onClose }) => {
  const [hasFunction, setHasFunction] = useState("yes");
  const [followUps, setFollowUps] = useState([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [followUpModalOpen, setFollowUpModalOpen] = useState(false);
  const userId = Number(localStorage.getItem("mainId") || 0);
  const mainId = Number(localStorage.getItem("userId") || 0);


  const { register, control, handleSubmit, setValue, watch , formState: { errors }, } = useForm({
    defaultValues: DEFAULT_VALUES,
  });
const navigate = useNavigate();
const location = useLocation();
const { leadData, isEdit } = location.state || {};
  const selectedState = watch("stateId");

  const statusListRef = useRef(null);
  const leadSourceListRef = useRef(null);
  const eventTypeListRef = useRef(null);
  const assignListRef = useRef(null);
const assignListPromiseRef = useRef(null);
//   useEffect(() => {
//     GetLeadCode(userId)
//       .then((res) => {
// const code = res?.data?.data || "";        if (code) setValue("leadCode", code);
//       })
//       .catch((err) => console.error("Lead code error:", err));
//   }, []);

  const fetchStateOptions = async (query) => {
    const res = await fetchstatebycontry(DEFAULT_COUNTRY_ID, query);
    const list = res?.data?.data?.["state Details"] ?? [];
    return list.map((s) => ({ id: s.id, label: s.name }));
  };

  const fetchCityOptions = async (query) => {
    if (!selectedState?.id) return [];
    const res = await fetchcitybystateid(selectedState.id, query);
    const list = res?.data?.data?.["City Details"] ?? [];
    return list.map((c) => ({ id: c.id, label: c.name }));
  };

const fetchStatusOptions = async (query) => {
  if (!statusListRef.current) {
    const res = await getallstatus(mainId);
    const raw = res?.data?.data ?? [];
    statusListRef.current = raw
      .filter((s) => !s.isDeleted)   
      .map((s) => ({ id: s.leadStatusId, label: s.statusName, color: s.colorCode }));
  }
  return filterByQuery(statusListRef.current, query);
};

  const fetchLeadSourceOptions = async (query) => {
    if (!leadSourceListRef.current) {
      const res = await getallleadsource(mainId);
      const raw = res?.data?.data ?? [];
      leadSourceListRef.current = raw.map((s) => ({
        id: s.leadSourceId ?? s.id,
        label: s.leadSourceName ?? s.sourceName ?? s.name,
      }));
    }
    return filterByQuery(leadSourceListRef.current, query);
  };

  const fetchEventTypeOptions = async (query) => {
    if (!eventTypeListRef.current) {
      const res = await GetEventType(mainId);
      const raw = res?.data?.data?.["EventTypes Details"] ?? [];
      eventTypeListRef.current = raw.map((e) => ({
        id: e.id,
        label: e.nameEnglish || e.event_type || "",
      }));
    }
    return filterByQuery(eventTypeListRef.current, query);
  };

const fetchAssignOptions = async (query) => {
  if (!assignListRef.current) {
    if (!assignListPromiseRef.current) {
      assignListPromiseRef.current = Fetchmanager(userId).then((res) => {
        const raw = res?.data?.data?.userDetails  ?? [];
        assignListRef.current = raw.map((u) => ({
          id: u.id,
          label: `${u.firstName || u.first_name || ""} ${u.lastName || u.last_name || ""}`.trim() || u.email,
        }));
        return assignListRef.current;
      });
    }
    await assignListPromiseRef.current;
  }
  return filterByQuery(assignListRef.current, query);
};

useEffect(() => {
  if (!isEdit || !leadData) return;

  setValue("clientName",      leadData.clientName     || "");
  setValue("contactNumber",   leadData.contactNumber  || "");
  setValue("alternateNumber", leadData.alternateNumber|| "");
  setValue("emailId",         leadData.emailId        || "");
  setValue("leadRemark",      leadData.leadRemark      || "");
  setValue("estimateAmount",  leadData.dealValue       || "");
  setValue("minPax",          leadData.minPax ? String(leadData.minPax) : "");
  setValue("maxPax",          leadData.maxPax ? String(leadData.maxPax) : "");
  setValue("medium",          leadData.medium          || "");
  setValue("campaign",        leadData.campaign        || "");
  setValue("leadCode",        leadData.leadCode        || "");

  setHasFunction(leadData.anyFunctionWithUs ? "yes" : "no");

  if (leadData.leadStatus) {
    setValue("leadStatus", {
      id:    leadData.leadStatus.leadStatusId,
      label: leadData.leadStatus.statusName,
    });
  }
  if (leadData.leadSource) {
    setValue("leadSourceId", {
      id:    leadData.leadSource.leadSourceId ?? leadData.leadSource.id,
      label: leadData.leadSource.sourceName   ?? leadData.leadSource.leadSourceName,
    });
  }
  if (leadData.leadPriority) {
    setValue("priority", {
      id:    leadData.leadPriority,
      label: leadData.leadPriority,
    });
  }
  if (leadData.leadQuality) {
    setValue("leadQuality", {
      id:    leadData.leadQuality,
      label: leadData.leadQuality,
    });
  }
  if (leadData.leadAssignId) {
    setValue("leadAssignId", {
      id:    leadData.leadAssignId,
      label: leadData.leadAssignName || String(leadData.leadAssignId),
    });
  }
  if (leadData.stateId) {
    setValue("stateId", { id: leadData.stateId, label: leadData.stateName || "" });
  }
  if (leadData.cityId) {
    setValue("cityId", { id: leadData.cityId, label: leadData.cityName || "" });
  }
  if (leadData.eventTypeId) {
    setValue("eventTypeId", { id: leadData.eventTypeId, label: leadData.eventTypeName || "" });
  }
 if (leadData.tentEventDate?.length) {
  const parsed = leadData.tentEventDate
    .map(parseFlexibleDate)
    .filter(Boolean);
  setValue("tentEventDate", parsed);
}
if (leadData.closeDate) {
  const parsed = parseFlexibleDate(leadData.closeDate);
  if (parsed) setValue("closeDate", parsed);
}
  if (leadData.followUpDetails?.length) {
    setFollowUps(leadData.followUpDetails.map((f, i) => ({
      id: f.id || Date.now() + i,
      followUpType: f.followUpType,
      followUpDate: f.followUpDate,
      description:  f.clientRemarks,
    })));
  }
}, [isEdit, leadData]);
const onSubmit = async (formData) => {
  try {
    setIsSubmitting(true);

    // Generate lead code only when creating a new lead, right at save time
    let leadCodeValue = formData.leadCode;
    if (!isEdit) {
      const codeRes = await GetLeadCode(userId);
      leadCodeValue = codeRes?.data?.data || "";
      setValue("leadCode", leadCodeValue);
    }

    const payload = {
      alternateNumber: formData.alternateNumber || "",
      anyFunctionWithUs: hasFunction === "yes",
      campaign: formData.campaign || "",
      cityId: formData.cityId?.id || 0,
      clientName: formData.clientName || "",
      closeDate: formData.closeDate ? formatDMY(formData.closeDate) : "",
      companyName: formData.companyName || "",
      contactNumber: formData.contactNumber || "",
      dealValue: formData.estimateAmount || "",
      emailId: formData.emailId || "",
      eventTypeId: formData.eventTypeId?.id || 0,
followUpDetails: followUps.map((f) => {
  // followUps loaded from an existing lead (edit mode) already carry
  // "DD/MM/YYYY HH:mm AM/PM" strings. Follow-ups just added via the modal
  // carry a raw date value that still needs formatting.
  const alreadyFormatted = typeof f.followUpDate === "string" && f.followUpDate.includes("/");

  const followUpDate = f.followUpDate
    ? (alreadyFormatted
        ? f.followUpDate
        : (() => {
            const d = new Date(f.followUpDate);
            const dd = String(d.getDate()).padStart(2, "0");
            const mm = String(d.getMonth() + 1).padStart(2, "0");
            const yyyy = d.getFullYear();
            let hours = d.getHours();
            const min = String(d.getMinutes()).padStart(2, "0");
            const period = hours >= 12 ? "PM" : "AM";
            hours = hours % 12 || 12;
            const hh = String(hours).padStart(2, "0");
            return `${dd}/${mm}/${yyyy} ${hh}:${min} ${period}`;
          })())
    : "";

  return {
    clientRemarks: f.description || f.clientRemarks || "",
    employeeRemarks: f.employeeRemarks || "",
    followUpDate,
    followUpStatus: f.followUpStatus || "",
    followUpType: f.followUpType || "",
    id: f.id || 0,
    leadId: isEdit && leadData?.id ? leadData.id : 0,
    memberId: f.assignMemberId || f.memberId || userId,
  };
}),      inquiryDate: formatDMY(new Date()),
      leadAssignId: formData.leadAssignId?.id || 0,
      leadCode: leadCodeValue || "",   // 👈 use the freshly-generated code
      leadPriority: formData.priority?.id || "",
      leadQuality: formData.leadQuality?.id || "",
      leadRemark: formData.leadRemark || "",
      leadSourceId: formData.leadSourceId?.id || 0,
      leadStatusId: formData.leadStatus?.id || 0,
      leadType: "",
      maxPax: Number(formData.maxPax) || 0,
      medium: formData.medium || "",
      minPax: Number(formData.minPax) || 0,
      planId: 0,
      stateId: formData.stateId?.id || 0,
      tentEventDate: (formData.tentEventDate || []).map(formatDMY),
      userId,
    };

    const res = isEdit && leadData?.id
      ? await updatelead(leadData.id, payload)
      : await AddLeadAPI(payload);

    if (res?.data?.success) {
      Swal.fire({ icon: "success", title: res.data.msg || res.data.message, timer: 1500, showConfirmButton: false });
      navigate("/leads/dashboard");
    } else {
      Swal.fire({ icon: "error", title: res?.data?.msg || res?.data?.message });
    }
  } catch (err) {
    Swal.fire({ icon: "error", title: err?.response?.data?.msg || err?.response?.data?.message });
  } finally {
    setIsSubmitting(false);
  }
};

  return (
    <div className="min-h-screen">
      <div className="px-6 py-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-primary flex items-center justify-center">
              <UserPlus className="h-5 w-5 text-white" />
            </div>
            <div>
<h1 className="text-lg font-semibold text-black leading-tight">
  {isEdit ? "Edit Lead" : "Add New Lead"}
</h1>              <p className="text-sm text-black">Capture and manage potential customers</p>
            </div>
          </div>
       <Button size="md" className="bg-primary" type="submit" form="add-lead-form">
  {isEdit ? "Update Lead" : "Create Lead"}
</Button>
        </div>
      </div>

      <form id="add-lead-form" onSubmit={handleSubmit(onSubmit)}>
        <main className="p-6 space-y-4 pb-24">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">

            <SectionCard icon={UserPlus} title="Lead Information" className="lg:col-span-2">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <FormInput
  label="Lead Code"
  icon={Hash}
  placeholder={isEdit ? "Auto-generated" : "Will be generated on save"}
  readOnly
  {...register("leadCode")}
/>
                <FormInput
                  label="Full Name"
                  icon={User}
                  required
                  placeholder="Enter full name"
                   error={errors.clientName?.message}
  {...register("clientName", { required: "Full name is required" })}
                />
             
              
                <FormInput
                  label="Mobile Number"
                  required
                  prefix="+91"
                  placeholder="10-digit mobile number"
                  {...register("contactNumber")}
                />
                <FormInput
                  label="Alternate Number"
                  prefix="+91"
                  placeholder="Enter alternate number"
                  {...register("alternateNumber")}
                />
                <FormInput
                  label="Email"
                  icon={Mail}
                  placeholder="name@company.com"
                  {...register("emailId")}
                />

                <Controller
                  name="stateId"
                  control={control}
                  render={({ field }) => (
                    <AsyncSearchSelect
                      label="State"
                      placeholder="Select state"
                      value={field.value}
                      fetchOptions={fetchStateOptions}
                      onChange={(opt) => {
                        field.onChange(opt);
                        setValue("cityId", null);
                      }}
                    />
                  )}
                />

                <Controller
                  name="cityId"
                  control={control}
                  render={({ field }) => (
                    <AsyncSearchSelect
                      label="City"
                      placeholder="Select city"
                      disabled={!selectedState?.id}
                      disabledMessage="Select a state first"
                      value={field.value}
                      fetchOptions={fetchCityOptions}
                      onChange={field.onChange}
                    />
                  )}
                />

                <Controller
                  name="leadRemark"
                  control={control}
                  render={({ field }) => (
                    <FormTextarea
                      label="Remarks"
                      placeholder="Enter lead remarks, requirements, or special instructions..."
                      className="sm:col-span-2"
                      value={field.value}
                      onChange={field.onChange}
                    />
                  )}
                />
              </div>
            </SectionCard>

            <div className="flex flex-col gap-4">
              <SectionCard icon={Tag} title="Lead Classification">
                <div className="flex flex-col gap-4">
                <Controller
  name="leadStatus"
  control={control}
  rules={{ required: "Status is required" }}
  render={({ field }) => (
    <AsyncSearchSelect
      label="Status"
      required
      placeholder="Select status"
      value={field.value}
      fetchOptions={fetchStatusOptions}
      onChange={field.onChange}
      error={errors.leadStatus?.message}
    />
  )}
/>
                  {/* <Controller
                    name="leadQuality"
                    control={control}
                    render={({ field }) => (
                      <AsyncSearchSelect
                        label="Lead Quality"
                        placeholder="Select lead quality"
                        value={field.value}
                        fetchOptions={fetchLeadQualityOptions}
                        onChange={field.onChange}
                      />
                    )}
                  /> */}
                  <Controller
                    name="priority"
                    control={control}
                    render={({ field }) => (
                      <AsyncSearchSelect
                        label="Priority"
                       
                        placeholder="Select priority"
                        value={field.value}
                        fetchOptions={fetchPriorityOptions}
                        onChange={field.onChange}
                      />
                    )}
                  />
                </div>
              </SectionCard>

              <SectionCard icon={Users} title="Assignment" className="flex-1 flex flex-col">
               <Controller
  name="leadAssignId"
  control={control}
  rules={{ required: "Assign to is required" }}
  render={({ field }) => (
    <AsyncSearchSelect
      label="Assign To"
      required
      placeholder="Select user"
      value={field.value}
      fetchOptions={fetchAssignOptions}
      onChange={field.onChange}
      error={errors.leadAssignId?.message}
    />
  )}
/>
              </SectionCard>
            </div>

            <SectionCard icon={Navigation} title="Lead Source" className="lg:col-span-2">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
             <Controller
  name="leadSourceId"
  control={control}
  rules={{ required: "Lead source is required" }}
  render={({ field }) => (
    <AsyncSearchSelect
      label="Lead Source"
      required
      placeholder="Select lead source"
      value={field.value}
      fetchOptions={fetchLeadSourceOptions}
      onChange={field.onChange}
      error={errors.leadSourceId?.message}
    />
  )}
/>
                <FormInput
                  label="Medium"
                  placeholder="Enter medium"
                  {...register("medium")}
                />
                <FormInput
                  label="Campaign"
                  placeholder="Enter campaign"
                  className="sm:col-span-2"
                  {...register("campaign")}
                />
              </div>
            </SectionCard>

            <SectionCard icon={IndianRupee} title="Deal Information">
              <div className="flex flex-col gap-4">
                <FormInput
                  label="Deal Value (₹)"
                  icon={IndianRupee}
                  placeholder="Enter deal value"
                  {...register("estimateAmount")}
                />
                <Controller
                  name="closeDate"
                  control={control}
                  render={({ field }) => (
                    <DatePicker
                      label="Expected Close Date"
                      placeholder="Select date"
                      value={field.value}
                      onChange={field.onChange}
                    />
                  )}
                />
              </div>
            </SectionCard>
          </div>

          <SectionCard icon={Bookmark} title="Event Information">
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <Controller
  name="eventTypeId"
  control={control}
  rules={{ required: "Event type is required" }}
  render={({ field }) => (
    <AsyncSearchSelect
      label="Event Type"
      required
      placeholder="Select event type"
      value={field.value}
      fetchOptions={fetchEventTypeOptions}
      onChange={field.onChange}
      error={errors.eventTypeId?.message}
    />
  )}
/>
              <FormInput
                label="Min Pax"
                
                placeholder="Enter minimum pax"
                {...register("minPax")}
              />
              <FormInput
                label="Max Pax"
                
                placeholder="Enter maximum pax"
                {...register("maxPax")}
              />
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-4">
              <div>
                <FieldLabel required>Any Function with Us?</FieldLabel>
                <div className="flex items-center gap-3">
                  {["yes", "no"].map((opt) => (
                    <button
                      key={opt}
                      type="button"
                      onClick={() => setHasFunction(opt)}
                      className={cn(
                        "flex-1 inline-flex items-center gap-2 rounded-lg border px-4 h-10 text-sm font-medium transition-colors",
                        hasFunction === opt
                          ? "border-violet-300 bg-violet-50/60 text-gray-900"
                          : "border-gray-300 bg-white text-gray-700 hover:border-gray-400"
                      )}
                    >
                      <span className={cn("w-4 h-4 rounded-full border-2 flex items-center justify-center shrink-0", hasFunction === opt ? "border-primary" : "border-gray-300")}>
                        {hasFunction === opt && <span className="w-2 h-2 rounded-full bg-violet-600" />}
                      </span>
                      <span className="capitalize">{opt}</span>
                    </button>
                  ))}
                </div>
              </div>
              <Controller
                name="tentEventDate"
                control={control}
                render={({ field }) => (
                  <DatePicker
                    label="Tentative Date(s)"
                    
                    multiple
                    placeholder="Select tentative event date(s)"
                    value={field.value}
                    onChange={field.onChange}
                  />
                )}
              />
            </div>
          </SectionCard>

          <SectionCard
            icon={ClipboardList}
            title="Follow-up Management"
            action={
              <Button size="sm" className="bg-primary" type="button" onClick={() => setFollowUpModalOpen(true)}>
                <Plus className="w-3.5 h-3.5 mr-1.5" />
                Add Follow-up
              </Button>
            }
          >
            {followUps.length === 0 ? (
              <div className="text-center py-8 text-sm text-gray-400">
                No follow-ups scheduled yet. Add one to stay on top of this lead.
              </div>
            ) : (
              <div className="flex flex-col gap-3">
                {followUps.map((f, idx) => {
                  const typeStyles = {
                    Call: "bg-blue-100 text-blue-700",
                    WhatsApp: "bg-emerald-100 text-emerald-700",
                    Email: "bg-amber-100 text-amber-700",
                  };
                  const initials = (f.customerName || "F")
                    .split(" ").map((w) => w[0]).slice(0, 2).join("").toUpperCase();
                  return (
                    <div key={f.id} className="flex items-center justify-between rounded-xl border border-gray-100 bg-white px-4 py-3 shadow-sm hover:shadow transition-shadow">
                      <div className="flex items-center gap-3">
                        <div className="h-9 w-9 rounded-full bg-violet-100 text-violet-700 text-xs font-semibold flex items-center justify-center shrink-0">
                          {initials}
                        </div>
                        <div className="flex flex-col gap-1">
                          <span className="text-sm font-semibold text-gray-900">
                            {f.customerName || `Follow-up #${idx + 1}`}
                          </span>
                          <div className="flex items-center gap-2">
                            <span className={cn("text-[11px] font-medium px-2 py-0.5 rounded-full", typeStyles[f.followUpType] || "bg-gray-100 text-gray-600")}>
                              {f.followUpType}
                            </span>
{f.followUpDate && (
  <span className="text-xs text-gray-400 inline-flex items-center gap-1">
    <Calendar className="w-3 h-3" />
    {formatFollowUpDisplay(f.followUpDate)}
  </span>
)}
                            {f.assignMember && <span className="text-xs text-gray-400">@ {f.assignMember}</span>}
                          </div>
                          {f.description && <span className="text-xs text-gray-500 line-clamp-1">{f.description}</span>}
                        </div>
                      </div>
                      <button
                        type="button"
                        onClick={() => setFollowUps((prev) => prev.filter((fu) => fu.id !== f.id))}
                        className="p-1.5 rounded-md hover:bg-gray-100 text-gray-400 hover:text-gray-600 transition-colors"
                      >
                        <X className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  );
                })}
              </div>
            )}
          </SectionCard>
        </main>

        <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-100 px-6 py-3 flex items-center justify-end gap-2">
<Button variant="outline" size="md" type="button" onClick={() => navigate("/leads/dashboard")}>
  Cancel
</Button>        <Button size="md" className="bg-primary" type="submit" disabled={isSubmitting}>
  {isSubmitting ? (isEdit ? "Updating..." : "Creating...") : (isEdit ? "Update Lead" : "Create Lead")}
  <ChevronRight className="w-4 h-4 ml-1" />
</Button>
        </div>

       <AddFollowUpModal
  open={followUpModalOpen}
  onClose={() => setFollowUpModalOpen(false)}
  defaultCustomerName={watch("clientName") || ""}  
  onSave={(followUp) => {
    setFollowUps((prev) => [...prev, { id: 0, ...followUp }]);  
    setFollowUpModalOpen(false);
  }}
/>
      </form>
    </div>
  );
};

export default AddLead;