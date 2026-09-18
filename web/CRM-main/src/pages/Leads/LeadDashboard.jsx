'use client';

import React, { useEffect, useMemo, useState } from 'react';
import Swal from "sweetalert2";

import {
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useReactTable,
} from '@tanstack/react-table';
import {
  AlarmClock,
  CheckCircle,
  ChevronDown,
  Clock,
  Calendar,
  Download,
  Eye,
  Hourglass,
  InfoIcon,
  Layers,
  MoreVertical,
  Plus,
  Search,
  Upload,
  Users,
  X,
  ClipboardList,
  UserRoundCheck,
  
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardFooter,
  CardHeader,
  CardTable,
  CardTitle,
  CardToolbar,
} from '@/components/ui/card';
import { DataGrid } from '@/components/ui/data-grid';
import { DataGridColumnHeader } from '@/components/ui/data-grid-column-header';
import { DataGridPagination } from '@/components/ui/data-grid-pagination';
import {
  DataGridTable,
  DataGridTableRowSelect,
  DataGridTableRowSelectAll,
} from '@/components/ui/data-grid-table';
import { Input } from '@/components/ui/input';
import { ScrollArea, ScrollBar } from '@/components/ui/scroll-area';
import { Skeleton } from '@/components/ui/skeleton';
import FilterPopover from '../../components/ui/FilterPopover';
import AddMember from '../../partials/modal/add-member/AddMember';
import { toAbsoluteUrl } from '@/lib/helpers';
import { useNavigate } from "react-router-dom";
import { GetAllLead , getleadcountbystatus, getallleadsource, getallstatus, GetAllMemberByUserId  , deleteLeadById , changeLeadStatus, assignMultipleLeadToMember , addupdatefollowup  , searchfliterlead ,getOrCreatePartyId   } from "@/services/apiServices";
import { usePermission } from "@/hooks/usePermission";
import AddFollowUpModal from "../../partials/modal/add-follow-up/AddFollowUpModal";




const AVATAR_PALETTE = [
  { bg: 'bg-violet-100', text: 'text-violet-700' },
  { bg: 'bg-blue-100', text: 'text-blue-700' },
  { bg: 'bg-emerald-100', text: 'text-emerald-700' },
  { bg: 'bg-amber-100', text: 'text-amber-700' },
  { bg: 'bg-rose-100', text: 'text-rose-700' },
  { bg: 'bg-cyan-100', text: 'text-cyan-700' },
];
const SOURCE_BADGE_PALETTE = [
  { bg: 'bg-violet-50', text: 'text-violet-700', border: 'border-violet-200' },
  { bg: 'bg-blue-50', text: 'text-blue-700', border: 'border-blue-200' },
  { bg: 'bg-emerald-50', text: 'text-emerald-700', border: 'border-emerald-200' },
  { bg: 'bg-amber-50', text: 'text-amber-700', border: 'border-amber-200' },
  { bg: 'bg-rose-50', text: 'text-rose-700', border: 'border-rose-200' },
  { bg: 'bg-cyan-50', text: 'text-cyan-700', border: 'border-cyan-200' },
  { bg: 'bg-indigo-50', text: 'text-indigo-700', border: 'border-indigo-200' },
  { bg: 'bg-fuchsia-50', text: 'text-fuchsia-700', border: 'border-fuchsia-200' },
];

// Deterministic: same sourceName always gets the same color, no API field needed
const getSourceBadgeStyle = (sourceName = '') => {
  if (!sourceName || sourceName === '-') {
    return { bg: 'bg-gray-50', text: 'text-gray-500', border: 'border-gray-200' };
  }
  let hash = 0;
  for (let i = 0; i < sourceName.length; i++) {
    hash = sourceName.charCodeAt(i) + ((hash << 5) - hash);
  }
  const index = Math.abs(hash) % SOURCE_BADGE_PALETTE.length;
  return SOURCE_BADGE_PALETTE[index];
};
const getInitials = (name = '') =>
  name
    .split(' ')
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();

const getAvatarStyle = (id) => AVATAR_PALETTE[id % AVATAR_PALETTE.length];

const getScoreBadge = (score) => {
  if (score >= 80) return 'bg-emerald-50 text-emerald-700 border-emerald-200';
  if (score >= 60) return 'bg-amber-50 text-amber-700 border-amber-200';
  return 'bg-rose-50 text-rose-700 border-rose-200';
};

const Badge = ({ className, style, children }) => (
  <span
    className={cn(
      'inline-flex items-center justify-center rounded-md border px-2 py-0.5 text-xs font-medium',
      className,
    )}
    style={style}
  >
    {children}
  </span>
);

const Select = ({ label, className }) => (
  <button
    type="button"
    className={cn(
      'inline-flex w-[250px] items-center justify-between gap-2 rounded-xl border border-gray-800 bg-white px-3.5 py-2 text-sm text-gray-900 hover:border-gray-800 transition-colors',
      className,
    )}
  >
    {label}
    <ChevronDown className="h-3.5 w-3.5 text-gray-800" />
  </button>
);


const LeadDashboard = () => {
  const [pagination, setPagination] = useState({
    pageIndex: 0,
    pageSize: 5,
  });
  const [sorting, setSorting] = useState([]);
  const [rowSelection, setRowSelection] = useState({});
  const [searchQuery, setSearchQuery] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedStaff, setSelectedStaff] = useState(null);
  const [appliedFilters, setAppliedFilters] = useState(null);
  const [leadsData, setLeadsData] = useState([]);
  const [leadCounts, setLeadCounts] = useState({
  total: 0, open: 0, won: 0, lost: 0, fup: 0,
});
const [sourceList, setSourceList]   = useState([]);
const [statusList, setStatusList]   = useState([]);
const [userList, setUserList]       = useState([]);
const [filterStatus,   setFilterStatus]   = useState("");
const [filterPriority, setFilterPriority] = useState("");
const [filterSource,   setFilterSource]   = useState("");
const [filterUser,     setFilterUser]     = useState("");
const userId = Number(localStorage.getItem("mainId") || 0);
const mainId = Number(localStorage.getItem("userId") || 0);
const authStorage = (() => {
  try {
    return JSON.parse(localStorage.getItem("auth-storage") || "{}");
  } catch {
    return {};
  }
})();
const [redirectingLead, setRedirectingLead] = useState(null);
const roleId = authStorage?.state?.user?.roleId;
const assignId = roleId === 2 ? -1 : userId;
  const [deletingId, setDeletingId] = useState(null);
const [bulkAssignUser, setBulkAssignUser] = useState("");
const [bulkStatus, setBulkStatus] = useState("");
const [bulkUpdating, setBulkUpdating] = useState(false);
const [assignModal, setAssignModal] = useState(false);
const [loadingUsers, setLoadingUsers] = useState(true);
const [followUpModalOpen, setFollowUpModalOpen] = useState(false);
const [followUpLeadId, setFollowUpLeadId] = useState(null);
const [followUpSaving, setFollowUpSaving] = useState(false);
const [viewFollowUpsLead, setViewFollowUpsLead] = useState(null);
const [convertingId, setConvertingId] = useState(null);
const { view, add, edit, delete: canDelete } = usePermission("Crm All Leads");
const navigate = useNavigate();


useEffect(() => {
  getleadcountbystatus(mainId)
    .then((res) => {
      const d = res?.data?.data || {};
      setLeadCounts({
        total: d["Total Leads"] ?? 0,
        open:  d["Open"]        ?? 0,
        won:   d["Won"]         ?? 0,
        lost:  d["Lost"]        ?? 0,
        fup:   d["Today's FUP"] ?? 0,
      });
    })
    .catch((err) => console.error("Lead count fetch error:", err));
}, [userId]);

useEffect(() => {
  getallstatus(mainId)
    .then((res) => setStatusList(res?.data?.data || []))
    .catch((err) => console.error("Status fetch error:", err));

  getallleadsource(mainId)
    .then((res) => setSourceList(res?.data?.data || []))
    .catch((err) => console.error("Source fetch error:", err));

setLoadingUsers(true);

GetAllMemberByUserId(userId)
  .then((res) => {
    const raw = res?.data?.data;
    const list =
      raw?.userDetails?.UserDetails ||
      raw?.UserDetails ||
      (Array.isArray(raw) ? raw : []);
    setUserList(list);
  })
  .catch((err) => console.error("User fetch error:", err))
  .finally(() => setLoadingUsers(false));
}, [userId]);

useEffect(() => {
  const fetchLeads = async () => {
    try {
      const hasFilter = filterUser || filterPriority || filterSource || filterStatus;

      if (hasFilter) {
        const res = await searchfliterlead(
          filterUser ? Number(filterUser) : "",
          filterPriority ? filterPriority.toLowerCase() : "",
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
        const res = await GetAllLead(mainId, assignId);
const leads = res?.data?.data?.["All Leads"] ?? [];
console.log("leadSource sample:", JSON.stringify(leads[0]?.leadSource, null, 2)); // TEMP - remove after checking
setLeadsData(leads); // TEMP - remove after checking
      }
    } catch (err) {
      console.error("Lead fetch error:", err);
      setLeadsData([]);
    }
  };

  fetchLeads();
}, [filterUser, filterPriority, filterSource, filterStatus, userId, assignId]);

const mappedData = useMemo(() =>
  leadsData.map((lead) => ({
    id: lead.id,
    name: lead.clientName,
    role: lead.leadSource?.sourceName || "-",
    totalTasks: lead.contactNumber,
    performanceScore: 0,
    overdue: 0,
    pending: lead.followUpDetails?.length || 0,
    inProgress: 0,
    joinedDate: lead.inquiryDate,
    joinedTime: new Date(lead.createdAt).toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit" }),
    leadCode: lead.leadCode,
    leadStatus: lead.leadStatus?.statusName || "-",
    leadStatusColor: lead.leadStatus?.colorCode || "#ccc",
    leadPriority: lead.leadPriority || "-",
    assignedTo: lead.leadAssignName || "-",
    source: lead.leadSource?.sourceName || "-",
    dealValue: lead.dealValue,
    eventType: lead.eventTypeName || "-",
    city: lead.cityName || "-",
  })),
[leadsData]);

  const handleViewStaff = (staff) => {
    setSelectedStaff(staff);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedStaff(null);
  };

  const handleApplyFilter = (filters) => {
    setAppliedFilters(filters);
  };

const searchFilteredData = useMemo(() => {
  return mappedData.filter((item) => {
    const matchSearch =
      !searchQuery ||
      item.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      item.role.toLowerCase().includes(searchQuery.toLowerCase());

    return matchSearch;
  });
}, [mappedData, searchQuery]);

  const filteredData = useMemo(() => {
    if (!appliedFilters) return searchFilteredData;

    return searchFilteredData.filter((staff) => {
      const staffMatch =
        appliedFilters.assignees.length === 0 ||
        appliedFilters.assignees.includes(staff.id);

      return staffMatch;
    });
  }, [searchFilteredData, appliedFilters]);

const stats = useMemo(() => [
  { label: 'Total Leads',     count: leadCounts.total, icon: '/media/icon/totalleads.png',    iconBg: 'bg-violet-100' },
  { label: 'New Leads',       count: leadCounts.open,  icon: '/media/icon/newleads.png',      iconBg: 'bg-blue-100'   },
  { label: 'Won Leads',       count: leadCounts.won,   icon: '/media/icon/wonlead.png',       iconBg: 'bg-emerald-100'},
  { label: 'Lost Leads',      count: leadCounts.lost,  icon: '/media/icon/lostlead.png',      iconBg: 'bg-rose-100'   },
  { label: 'Today Follow-up', count: leadCounts.fup,   icon: '/media/icon/todayfollow-up.png',iconBg: 'bg-orange-100' },
], [leadCounts]);

  const columns = useMemo(
    () => [

      {
        accessorKey: 'id',
        accessorFn: (row) => row.id,
        header: () => <DataGridTableRowSelectAll />,
        cell: ({ row }) => <DataGridTableRowSelect row={row} />,
        enableSorting: false,
        enableHiding: false,
        enableResizing: false,
        size: 44,
        meta: { cellClassName: '' },
      },
       {
        id: 'leadCode',
        accessorFn: (row) => row.leadCode,
        header: ({ column }) => (
          <DataGridColumnHeader title="leadCode" column={column} />
        ),

        cell: ({ row }) => (
          <span className="font-medium text-gray-700">
            {row.original.leadCode}
          </span>
        ),
        enableSorting: true,
        size: 110,
        meta: { skeleton: <Skeleton className="h-5 w-[40px]" /> },
      },
      {
        id: 'name',
        accessorFn: (row) => row.name,
        header: ({ column }) => (
          <DataGridColumnHeader title="Customer name " column={column} />
        ),
        cell: ({ row }) => {
          const avatarStyle = getAvatarStyle(row.original.id);
          return (
            <div className="flex items-center gap-3">
              <div
                className={cn(
                  'w-9 h-9 rounded-full flex items-center justify-center text-xs font-semibold shrink-0',
                  avatarStyle.bg,
                  avatarStyle.text,
                )}
              >
                {getInitials(row.original.name)}
              </div>
              <div className="flex flex-col">
                <span className="leading-tight font-medium text-sm text-gray-900 hover:text-violet-600 cursor-pointer">
                  {row.original.name}
                </span>
                <span className="text-xs text-muted-foreground leading-tight">
                  {row.original.role}
                </span>
              </div>
            </div>
          );
        },
        enableSorting: true,
        size: 260,
        meta: {
          skeleton: (
            <div className="flex items-center gap-3">
              <Skeleton className="h-9 w-9 rounded-full" />
              <div className="flex flex-col gap-2">
                <Skeleton className="h-4 w-[125px]" />
                <Skeleton className="h-2.5 w-[90px]" />
              </div>
            </div>
          ),
        },
      },
      {
        id: 'Contact',
        accessorFn: (row) => row.totalTasks,
        header: ({ column }) => (
          <DataGridColumnHeader title="Contact" column={column} />
        ),
        cell: ({ row }) => (
          <span className="font-medium text-gray-700">
            {row.original.totalTasks}
          </span>
        ),
        enableSorting: true,
        size: 110,
        meta: { skeleton: <Skeleton className="h-5 w-[40px]" /> },
      },
   {
  id: 'performanceScore',
  accessorFn: (row) => row.source,
  header: ({ column }) => (
    <DataGridColumnHeader title="Source" column={column} />
  ),
  cell: ({ row }) => {
    const style = getSourceBadgeStyle(row.original.source);
    return (
      <Badge className={cn(style.bg, style.text, style.border)}>
        {row.original.source}
      </Badge>
    );
  },
  enableSorting: true,
  size: 130,
  meta: { skeleton: <Skeleton className="h-5 w-[80px]" /> },
},
{
  id: 'overdue',
  accessorFn: (row) => row.leadPriority,
  header: ({ column }) => (
    <DataGridColumnHeader title="Priority" column={column} />
  ),
  cell: ({ row }) => (
    <Badge className={cn(
      row.original.leadPriority === 'High'   ? 'bg-rose-50 text-rose-600 border-rose-200' :
      row.original.leadPriority === 'Medium' ? 'bg-amber-50 text-amber-600 border-amber-200' :
      row.original.leadPriority === 'Low'    ? 'bg-blue-50 text-blue-600 border-blue-200' :
      'bg-gray-50 text-gray-500 border-gray-200'
    )}>
      {row.original.leadPriority || '-'}
    </Badge>
  ),
  enableSorting: true,
  size: 110,
  meta: { skeleton: <Skeleton className="h-5 w-[60px]" /> },
},
{
  id: 'pending',
  accessorFn: (row) => row.assignedTo,
  header: ({ column }) => (
    <DataGridColumnHeader title="Assigned To" column={column} />
  ),
  cell: ({ row }) => (
    <span className="text-sm text-gray-700">{row.original.assignedTo}</span>
  ),
  enableSorting: true,
  size: 140,
  meta: { skeleton: <Skeleton className="h-5 w-[100px]" /> },
},
{
  id: 'inProgress',
  accessorFn: (row) => row.leadStatus,
  header: ({ column }) => (
    <DataGridColumnHeader title="Status" column={column} />
  ),
  cell: ({ row }) => (
    <Badge style={{
      backgroundColor: row.original.leadStatusColor + '20',
      color: row.original.leadStatusColor,
      borderColor: row.original.leadStatusColor + '60',
    }}>
      {row.original.leadStatus}
    </Badge>
  ),
  enableSorting: true,
  size: 120,
  meta: { skeleton: <Skeleton className="h-5 w-[80px]" /> },
},
     
      {
        id: 'joinedDate',
        accessorFn: (row) => row.joinedDate,
        header: ({ column }) => (
          <DataGridColumnHeader title="Created Date" column={column} />
        ),
        cell: ({ row }) => (
          <div className="flex flex-col leading-tight">
            <span className="text-sm text-gray-700">{row.original.joinedDate}</span>
            <span className="text-xs text-muted-foreground">{row.original.joinedTime}</span>
          </div>
        ),
        enableSorting: true,
        size: 140,
        meta: { skeleton: <Skeleton className="h-5 w-[80px]" /> },
      },
     {
  id: 'actions',
  header: () => <span>Actions</span>,
  cell: ({ row }) => (
    <div className="flex items-center gap-1">
      {/* <Button
        mode="icon"
        variant="ghost"
        size="sm"
        title="View"
        onClick={() => handleViewStaff(row.original)}
      >
        <Eye className="w-4 h-4" />
      </Button> */}
{/* <Button
  mode="icon"
  variant="ghost"
  size="sm"
  title="Add Follow-up"
  onClick={() => handleOpenAddFollowUp(row.original.id)}
>
  <ClipboardList className="w-4 h-4 text-emerald-600" />
</Button> */}

{edit && (
<Button
  mode="icon"
  variant="ghost"
  size="sm"
  title="Convert to Client"
  disabled={convertingId === row.original.id}
  onClick={() => {
    const rawLead = leadsData.find((l) => l.id === row.original.id);
    handleConvertToClient(rawLead);
  }}
>
  <UserRoundCheck className="w-4 h-4 text-emerald-600" />
</Button>
 )}

 {edit && (
<Button
  mode="icon"
  variant="ghost"
  size="sm"
  title="View Follow-ups"
  onClick={() => {
    const rawLead = leadsData.find((l) => l.id === row.original.id);
    handleViewFollowUps(rawLead);
  }}
>
  <ClipboardList className="w-4 h-4 text-emerald-600" />
</Button>
 )}
      {edit && (
      <Button
        mode="icon"
        variant="ghost"
        size="sm"
        title="Edit"
        onClick={() => handleEditLead(row.original)}
      >
        <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4 text-blue-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
          <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
        </svg>
      </Button>
      )}
      {canDelete && (
      <Button
        mode="icon"
        variant="ghost"
        size="sm"
        title="Delete"
        disabled={deletingId === row.original.id}
        onClick={() => handleDeleteLead(row.original.id)}
      >
        <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4 text-red-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <polyline points="3 6 5 6 21 6"/>
          <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/>
          <path d="M10 11v6M14 11v6"/>
          <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/>
        </svg>
      </Button>
      )}
    </div>
  ),
  enableSorting: false,
  size: 110,
  meta: { skeleton: <Skeleton className="h-8 w-20 rounded" /> },
},
    ],
    [handleViewStaff],
  );

  const table = useReactTable({
    columns,
    data: filteredData,
    pageCount: Math.ceil((filteredData?.length || 0) / pagination.pageSize),
    getRowId: (row) => String(row.id),
    state: {
      pagination,
      sorting,
      rowSelection,
    },
    columnResizeMode: 'onChange',
    onPaginationChange: setPagination,
    onSortingChange: setSorting,
    enableRowSelection: true,
    onRowSelectionChange: setRowSelection,
    getCoreRowModel: getCoreRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(),
  });

  const hasActiveFilters =
    appliedFilters &&
    (appliedFilters.assignees.length > 0 || appliedFilters.creators.length > 0);

  const selectedCount = Object.keys(rowSelection).length;



const handleEditLead = (mappedRow) => {
  const rawLead = leadsData.find((l) => l.id === mappedRow.id);
  navigate("/leads/addlead", { state: { leadData: rawLead, isEdit: true } });
};

const handleOpenAddFollowUp = (leadId) => {
  setFollowUpLeadId(leadId);
  setFollowUpModalOpen(true);
};

const handleViewFollowUps = (lead) => {
  setViewFollowUpsLead(lead);
};

const handleSaveFollowUp = async (followUp) => {
  const lead = leadsData.find((l) => l.id === followUpLeadId);
  if (!lead) return;

  const existingPayload = (lead.followUpDetails || []).map((f) => ({
    clientRemarks: f.clientRemarks || "",
    employeeRemarks: f.employeeRemarks || "",
    followUpDate: f.followUpDate || "",
    followUpStatus: f.followUpStatus || "",
    followUpType: f.followUpType || "",
    id: f.id || 0,
    leadId: lead.id,
    memberId: f.memberId || 0,
  }));

  // Format new entry: "DD/MM/YYYY HH:mm AM/PM" (matches AddLead's followUpDetails format)
  const newEntry = {
    clientRemarks: followUp.description || "",
    employeeRemarks: "",
    followUpDate: followUp.followUpDate
      ? (() => {
          const d = new Date(followUp.followUpDate);
          const dd = String(d.getDate()).padStart(2, "0");
          const mm = String(d.getMonth() + 1).padStart(2, "0");
          const yyyy = d.getFullYear();
          let hours = d.getHours();
          const min = String(d.getMinutes()).padStart(2, "0");
          const period = hours >= 12 ? "PM" : "AM";
          hours = hours % 12 || 12;
          const hh = String(hours).padStart(2, "0");
          return `${dd}/${mm}/${yyyy} ${hh}:${min} ${period}`;
        })()
      : "",
    followUpStatus: "",
    followUpType: followUp.followUpType || "",
    id: 0,
    leadId: lead.id,
    memberId: followUp.assignMemberId || userId,
  };

  const payload = [...existingPayload, newEntry];

  try {
    setFollowUpSaving(true);
    const res = await addupdatefollowup(payload);

    if (res?.data?.success) {
      Swal.fire({ icon: "success", title: res.data.msg || "Follow-up added!", timer: 1500, showConfirmButton: false });
      const refreshed = await GetAllLead(userId, assignId);
      setLeadsData(refreshed?.data?.data?.["All Leads"] ?? []);
    } else {
      Swal.fire({ icon: "error", title: res?.data?.msg || "Failed to add follow-up" });
    }
  } catch (err) {
    Swal.fire({ icon: "error", title: err?.response?.data?.msg || "Failed to add follow-up" });
  } finally {
    setFollowUpSaving(false);
    setFollowUpModalOpen(false);
    setFollowUpLeadId(null);
  }
};

const handleDeleteLead = async (id) => {
  const result = await Swal.fire({
    title: "Delete Lead?",
    text: "This action cannot be undone.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#d33",
    cancelButtonColor: "#6b7280",
    confirmButtonText: "Yes, Delete",
  });
  if (!result.isConfirmed) return;

  try {
    setDeletingId(id);
    const response = await deleteLeadById(id);

    if (response?.data?.success || response?.success) {
      setLeadsData((prev) => prev.filter((l) => l.id !== id));
      Swal.fire({
        title: "Deleted!",
        text: "Lead has been removed successfully.",
        icon: "success",
        timer: 1500,
        showConfirmButton: false,
      });
    } else {
      Swal.fire({
        title: "Failed",
        text: response?.data?.msg || "Failed to delete lead.",
        icon: "error",
      });
    }
  } catch (err) {
    console.error("Delete failed:", err);
    Swal.fire({
      title: "Error",
      text: err?.data?.msg || "Something went wrong while deleting the lead.",
      icon: "error",
    });
  } finally {
    setDeletingId(null);
  }
};

const handleBulkUpdate = async () => {
  const selectedIds = Object.keys(rowSelection);
  if (selectedIds.length === 0) return;

  try {
    setBulkUpdating(true);

    if (bulkStatus) {
      const leadIdsStr = selectedIds.join(",");
      const statusRes = await changeLeadStatus(leadIdsStr, Number(bulkStatus));

      if (statusRes?.data?.success === false) {
        Swal.fire({ icon: "error", title: statusRes?.data?.msg || "Status update failed" });
        setBulkUpdating(false);
        return;
      }
    }

    if (bulkAssignUser) {
      setAssignModal(true);
      setBulkUpdating(false);
      return;
    }

    Swal.fire({ icon: "success", title: "Status updated!", timer: 1200, showConfirmButton: false });
    setRowSelection({});
    setBulkStatus("");
    const res = await GetAllLead(userId, assignId);
    setLeadsData(res?.data?.data?.["All Leads"] ?? []);

  } catch (err) {
    Swal.fire({ icon: "error", title: err?.response?.data?.msg || "Bulk update failed" });
  } finally {
    setBulkUpdating(false);
  }
};

const finishBulkUpdate = async () => {
  const selectedIds = Object.keys(rowSelection).map(Number);
  try {
    setBulkUpdating(true);

    if (bulkAssignUser) {
      const res = await assignMultipleLeadToMember(
        "",
        "",
        selectedIds,
        Number(bulkAssignUser)
      );

      if (res?.data?.success) {
        Swal.fire({
          icon: "success",
          title: res.data.msg || "Updated successfully!",
          timer: 1500,
          showConfirmButton: false,
        });
      } else {
        Swal.fire({ icon: "error", title: res?.data?.msg || "Bulk assign failed" });
        return;
      }
    }

    setAssignModal(false);
    setRowSelection({});
    setBulkAssignUser("");
    setBulkStatus("");
    const res = await GetAllLead(userId, assignId);
    setLeadsData(res?.data?.data?.["All Leads"] ?? []);
  } catch (err) {
    Swal.fire({ icon: "error", title: err?.response?.data?.msg || "Bulk update failed" });
  } finally {
    setBulkUpdating(false);
  }
};


const handleConvertToClient = async (lead) => {
  if (!lead) return;
  try {
    setConvertingId(lead.id);
    const res = await getOrCreatePartyId(lead.id);

    if (res?.data?.success) {
      setRedirectingLead(lead);

      const token = localStorage.getItem("userToken");
      const userId = localStorage.getItem("mainId");

      setTimeout(() => {
        if (token && userId) {
          const jcxUrl = `http://localhost:5173/sso-login?token=${encodeURIComponent(token)}&userId=${encodeURIComponent(userId)}`;

          if (window.opener && !window.opener.closed) {
            // This CRM tab was opened FROM a JCX tab — reuse it instead of
            // spawning a new one.
            window.opener.location.href = jcxUrl;
            window.opener.focus();
          } else {
            // CRM was opened directly (not via JCX) — no existing tab to reuse.
            window.open(jcxUrl, "_blank");
          }
        } else {
          console.warn("Convert to client succeeded but session token/userId was missing.");
        }
        setRedirectingLead(null);
      }, 1200);
    } else {
      Swal.fire({
        title: "Failed",
        text: res?.data?.message || "Failed to convert lead to client.",
        icon: "error",
        confirmButtonColor: "#d33",
      });
    }
  } catch (err) {
    Swal.fire({
      title: "Error",
      text: err?.response?.data?.message || "Something went wrong while converting the lead.",
      icon: "error",
      confirmButtonColor: "#d33",
    });
  } finally {
    setConvertingId(null);
  }
};

  return (
    <div className="min-h-screen ">
      <main className="px-6 space-y-5">
        {/* Page header */}
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-semibold text-gray-900">
              Lead Management
            </h1>
            <p className="text-sm text-muted-foreground mt-0.5">
              <span className="text-violet-600">Home</span> / Lead
            </p>
          </div>
          <div className="flex items-center gap-2">
            {/* <Button variant="outline" size="md">
              <Upload className="w-4 h-4 mr-2" />
              Import
            </Button> */}
            {/* <Button variant="outline" size="md">
              <Download className="w-4 h-4 mr-2" />
              Export
            </Button> */}
           {add && (
            <Button
  size="md"
  className="bg-primary"
  onClick={() => navigate("/leads/addlead")}
>
  <Plus className="w-4 h-4 mr-2" />
  Add Lead
</Button>
     )}    
          </div>
        </div>

{/* Stats Cards */}
<div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
  {stats.map((stat, index) => (
    <div
      key={index}
      className="rounded-2xl p-4 bg-white shadow-sm border border-gray-100 flex items-center gap-3"
    >
      <div
        className={cn(
          'w-12 h-12 rounded-xl flex items-center justify-center shrink-0',
          stat.iconBg,
        )}
      >
        <img
          src={toAbsoluteUrl(stat.icon)}
          alt={stat.label}
          className="w-5 h-5 object-contain"
        />
      </div>
      <div className="flex flex-col">
        <span className="text-sm text-muted-foreground mb-0.5">
          {stat.label}
        </span>
        <span className="text-xl font-bold text-gray-900 leading-tight">
          {String(stat.count).padStart(2, '0')}
        </span>
      </div>
    </div>
  ))}
</div>

        {/* Filter row */}
      {/* Filter row */}
<div className="rounded-xl border border-gray-100 bg-white p-4 flex items-center gap-3 flex-wrap">

  {/* Status */}
  <select
    value={filterStatus}
    onChange={(e) => setFilterStatus(e.target.value)}
    className="inline-flex w-[200px] items-center rounded-xl border border-gray-200 bg-white px-3.5 py-2 text-sm text-gray-600 hover:border-gray-300 transition-colors"
  >
    <option value="">All Status</option>
{statusList.map((s) => (
  <option key={s.leadStatusId} value={s.leadStatusId}>{s.statusName}</option>
))}
  </select>

  {/* Priority */}
  <select
    value={filterPriority}
    onChange={(e) => setFilterPriority(e.target.value)}
    className="inline-flex w-[200px] items-center rounded-xl border border-gray-200 bg-white px-3.5 py-2 text-sm text-gray-600 hover:border-gray-300 transition-colors"
  >
    <option value="">All Priority</option>
    <option value="High">High</option>
    <option value="Medium">Medium</option>
    <option value="Low">Low</option>
  </select>

  {/* Source */}
  {/* Source */}
<select
  value={filterSource}
  onChange={(e) => setFilterSource(e.target.value)}
  className="inline-flex w-[200px] items-center rounded-xl border border-gray-200 bg-white px-3.5 py-2 text-sm text-gray-600 hover:border-gray-300 transition-colors"
>
  <option value="">All Source</option>
  {sourceList.map((s) => (
    <option key={s.leadSourceId} value={s.leadSourceId}>{s.sourceName}</option>
  ))}
</select>

  {/* Users */}
{/* Users */}
<select
  value={filterUser}
  onChange={(e) => setFilterUser(e.target.value)}
  disabled={loadingUsers}
  className="inline-flex w-[200px] items-center rounded-xl border border-gray-200 bg-white px-3.5 py-2 text-sm text-gray-600 hover:border-gray-300 transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
>
  <option value="">{loadingUsers ? "Loading users..." : "All Users"}</option>
  {!loadingUsers &&
    userList.map((u) => {
      const id = u.id ?? u.userId;
      const name = [u.firstName, u.lastName].filter(Boolean).join(" ").trim()
        || u.userName || u.name || `Member #${id}`;
      return <option key={id} value={id}>{name}</option>;
    })}
</select>

</div>

        {/* Bulk action bar */}
     <div className="rounded-xl border border-gray-100 bg-white p-4 flex items-center gap-3 flex-wrap">
<select
    value={bulkAssignUser}
    onChange={(e) => setBulkAssignUser(e.target.value)}
    disabled={loadingUsers}
    className="inline-flex w-[200px] items-center rounded-xl border border-gray-200 bg-white px-3.5 py-2 text-sm text-gray-600 hover:border-gray-300 transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
  >
    <option value="">{loadingUsers ? "Loading users..." : "Assign To"}</option>
    {!loadingUsers &&
      userList.map((u) => {
        const id = u.id ?? u.userId;
        const name = [u.firstName, u.lastName].filter(Boolean).join(" ").trim()
          || u.userName || u.name || `Member #${id}`;
        return <option key={id} value={id}>{name}</option>;
      })}
  </select>

  <select
    value={bulkStatus}
    onChange={(e) => setBulkStatus(e.target.value)}
    className="inline-flex w-[200px] items-center rounded-xl border border-gray-200 bg-white px-3.5 py-2 text-sm text-gray-600 hover:border-gray-300 transition-colors"
  >
    <option value="">Change Status</option>
{statusList.map((s) => (
  <option key={s.leadStatusId} value={s.leadStatusId}>{s.statusName}</option>
))}
  </select>
{edit && (
  <Button
    size="md"
    className="bg-primary"
    disabled={selectedCount === 0 || bulkUpdating || (!bulkAssignUser && !bulkStatus)}
    onClick={handleBulkUpdate}
  >
    {bulkUpdating ? "Updating..." : "Update"}
  </Button>
)}
{edit && (
  <Button
    size="lg"
    className="bg-rose-600 text-white ml-auto"
    onClick={() => { setRowSelection({}); setBulkAssignUser(""); setBulkStatus(""); }}
    disabled={selectedCount === 0}
  >
    Cancel
  </Button>
  )}
</div>


        {/* Staff Productivity Table */}
        <DataGrid
          table={table}
          recordCount={filteredData?.length || 0}
          tableLayout={{
            columnsPinnable: true,
            columnsMovable: true,
            columnsVisibility: true,
            cellBorder: true,
          }}
        >
          <Card className="border border-gray-100 shadow-sm">
            <CardHeader className="py-3.5">
              <div className="flex items-center justify-between w-full">
                <div>
                  <CardTitle>
                   Leads({filteredData?.length || 0})
                  </CardTitle>
                 
                </div>
                {/* <div className="flex items-center gap-2">
                 
                  <Select label="Newest First" />
                </div> */}
              </div>

              {hasActiveFilters && (
                <div className="mt-3 flex items-center gap-2 flex-wrap">
                  <span className="text-sm text-muted-foreground">
                    Active filters:
                  </span>
                  {appliedFilters.assignees.length > 0 && (
                    <div className="inline-flex items-center gap-1 px-3 py-1 bg-primary border  rounded-full text-sm">
                      <span className="font-medium">Staff:</span>
                      <span>{appliedFilters.assignees.length}</span>
                    </div>
                  )}
                  {appliedFilters.creators.length > 0 && (
                    <div className="inline-flex items-center gap-1 px-3 py-1 bg-primary border  rounded-full text-sm">
                      <span className="font-medium">Creators:</span>
                      <span>{appliedFilters.creators.length}</span>
                    </div>
                  )}
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setAppliedFilters(null)}
                    className="h-7 text-xs text-gray-500 hover:text-gray-700"
                  >
                    <X className="h-3 w-3 mr-1" />
                    Clear all filters
                  </Button>
                </div>
              )}

              <CardToolbar className="relative mt-4">
                <Search className="size-4 text-muted-foreground absolute start-3 top-1/2 -translate-y-1/2" />
                <Input
                  placeholder="Search team member..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="ps-9 w-64"
                />
                {searchQuery.length > 0 && (
                  <Button
                    mode="icon"
                    variant="ghost"
                    className="absolute end-1.5 top-1/2 -translate-y-1/2 h-6 w-6"
                    onClick={() => setSearchQuery('')}
                  >
                    <X />
                  </Button>
                )}
              </CardToolbar>
            </CardHeader>
            <CardTable>
              <ScrollArea>
                <DataGridTable />
                <ScrollBar orientation="horizontal" />
              </ScrollArea>
            </CardTable>
            <CardFooter>
              <DataGridPagination />
            </CardFooter>
          </Card>
        </DataGrid>
      </main>

      {/* Staff Analytics Modal */}
      <AddMember
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        staffData={selectedStaff}
      />
{redirectingLead && (
  <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/50">
    <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm mx-4 p-8 flex flex-col items-center gap-4">
      <div className="w-14 h-14 rounded-full bg-emerald-50 flex items-center justify-center">
        <UserRoundCheck className="w-7 h-7 text-emerald-600" />
      </div>
      <div className="text-center">
        <p className="text-base font-semibold text-gray-900">
          {redirectingLead.clientName || "Customer"} converted successfully
        </p>
        <p className="text-sm text-gray-500 mt-1">
          Redirecting to Add Event...
        </p>
      </div>
      <div className="w-6 h-6 border-2 border-gray-200 border-t-emerald-600 rounded-full animate-spin" />
    </div>
  </div>
)}
   {/* Assign Modal */}
      {assignModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md p-6 flex flex-col gap-4">
            <h2 className="text-lg font-semibold text-gray-900">Assign Leads</h2>

            <div className="bg-gray-50 rounded-lg px-3 py-2 text-sm text-gray-600">
              <span className="font-medium">Assigning to: </span>
              {loadingUsers ? (
                <span className="inline-flex items-center gap-1.5 text-gray-400">
                  <span className="animate-spin w-3 h-3 border-2 border-gray-300 border-t-gray-600 rounded-full inline-block" />
                  Loading...
                </span>
              ) : (
                (() => {
                  const u = userList.find((u) => String(u.id ?? u.userId) === String(bulkAssignUser));
                  if (!u) return bulkAssignUser;
                  return [u.firstName, u.lastName].filter(Boolean).join(" ").trim() || u.userName || u.name;
                })()
              )}
              <span className="ml-3 font-medium">Leads: </span>
              {Object.keys(rowSelection).map((id) => {
                const lead = leadsData.find((l) => String(l.id) === String(id));
                return lead?.leadCode || id;
              }).join(", ")}
            </div>

            <div className="flex justify-end gap-2 mt-1">
              <Button
                className="btn btn-light text-sm"
                onClick={() => setAssignModal(false)}
              >
                Cancel
              </Button>
              <Button
                className="btn btn-primary text-sm flex items-center gap-2 disabled:opacity-50"
                disabled={bulkUpdating}
                onClick={finishBulkUpdate}
              >
                {bulkUpdating && <span className="animate-spin w-3 h-3 border-2 border-white border-t-transparent rounded-full inline-block" />}
                Confirm Assign
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* Add Follow-up Modal — always mounted, controlled by its own state */}
      <AddFollowUpModal
        open={followUpModalOpen}
        onClose={() => { setFollowUpModalOpen(false); setFollowUpLeadId(null); }}
        defaultCustomerName={
          leadsData.find((l) => l.id === followUpLeadId)?.clientName || ""
        }
        onSave={handleSaveFollowUp}
      />

    {/* View Follow-ups List */}
      {viewFollowUpsLead && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
          <div
            className="bg-white rounded-2xl shadow-xl w-full max-w-lg flex flex-col"
            style={{ maxHeight: "80vh" }}
          >
            <div
              className="flex items-center justify-between p-6 pb-4 border-b"
              style={{ flex: "0 0 auto" }}
            >
              <h2 className="text-lg font-semibold text-gray-900">
                Follow-ups — {viewFollowUpsLead.clientName}
              </h2>
              <button
                onClick={() => setViewFollowUpsLead(null)}
                className="p-1.5 rounded-md hover:bg-gray-100 text-gray-400 hover:text-gray-600"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div
              className="px-6 py-4 follow-up-scroll"
              style={{ flex: "1 1 auto", minHeight: 0, overflowY: "auto" }}
            >
            {(!viewFollowUpsLead.followUpDetails || viewFollowUpsLead.followUpDetails.length === 0) ? (
              <div className="text-center py-8 text-sm text-gray-400">
                No follow-ups scheduled yet.
              </div>
            ) : (
                           <div className="flex flex-col gap-3">
                {[...viewFollowUpsLead.followUpDetails].reverse().map((f, idx) => {
                  const typeStyles = {
                    Call: "bg-blue-100 text-blue-700",
                    WhatsApp: "bg-emerald-100 text-emerald-700",
                    Email: "bg-amber-100 text-amber-700",
                  };
                  const statusStyles = {
                    Completed: "bg-emerald-50 text-emerald-600 border-emerald-200",
                    Pending: "bg-amber-50 text-amber-600 border-amber-200",
                  };
                  return (
                    <div
                      key={f.id || idx}
                      className="flex flex-col gap-2 rounded-xl border border-gray-100 bg-white px-4 py-3 shadow-sm"
                    >
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2">
                          <span className={cn("text-[11px] font-medium px-2 py-0.5 rounded-full", typeStyles[f.followUpType] || "bg-gray-100 text-gray-600")}>
                            {f.followUpType || "—"}
                          </span>
                          {f.followUpStatus && (
                            <span className={cn("text-[11px] font-medium px-2 py-0.5 rounded-full border", statusStyles[f.followUpStatus] || "bg-gray-50 text-gray-500 border-gray-200")}>
                              {f.followUpStatus}
                            </span>
                          )}
                        </div>
                      </div>

                      {f.followUpDate && (
                        <div className="flex items-center gap-1.5 text-xs text-gray-600">
                          <Calendar className="w-3.5 h-3.5 text-gray-400" />
                          <span className="font-medium">Scheduled:</span>
                          <span>{f.followUpDate}</span>
                        </div>
                      )}

                      {f.memberName && (
                        <div className="flex items-center gap-1.5 text-xs text-gray-600">
                          <Users className="w-3.5 h-3.5 text-gray-400" />
                          <span className="font-medium">Assigned to:</span>
                          <span>{f.memberName}</span>
                        </div>
                      )}

                      {f.clientRemarks && (
                        <div className="text-xs text-gray-600 bg-gray-50 rounded-lg px-2.5 py-1.5">
                          {f.clientRemarks}
                        </div>
                      )}
                      {f.employeeRemarks && (
                        <div className="text-xs text-gray-500 italic">
                          Employee note: {f.employeeRemarks}
                        </div>
                      )}

                      {f.createdAt && (
                        <div className="flex justify-end">
                          <span className="text-[11px] text-gray-400">
                            Created: {f.createdAt}
                          </span>
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>
        
            )}
            </div>

            <div
              className="flex justify-end gap-2 p-6 pt-4 border-t"
              style={{ flex: "0 0 auto" }}
            >
              <Button
                size="sm"
                className="bg-primary"
                onClick={() => {
                  handleOpenAddFollowUp(viewFollowUpsLead.id);
                  setViewFollowUpsLead(null);
                }}
              >
                <Plus className="w-3.5 h-3.5 mr-1.5" />
                Add Follow-up
              </Button>
            </div>
          </div>

          <style>{`
            .follow-up-scroll {
              scrollbar-width: thin;
              scrollbar-color: #d1d5db transparent;
            }
            .follow-up-scroll::-webkit-scrollbar {
              width: 6px;
            }
            .follow-up-scroll::-webkit-scrollbar-track {
              background: transparent;
            }
            .follow-up-scroll::-webkit-scrollbar-thumb {
              background-color: #d1d5db;
              border-radius: 9999px;
            }
            .follow-up-scroll::-webkit-scrollbar-thumb:hover {
              background-color: #9ca3af;
            }
          `}</style>
        </div>
      )}
    </div>
  );
};

export default LeadDashboard;