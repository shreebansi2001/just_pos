'use client';

import React, { useMemo, useState  , useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useReactTable,
} from '@tanstack/react-table';
import {
  ChevronLeft,
  ChevronRight,
  Download,
  Eye,
  MessageSquare,
  MoreVertical,
  Pencil,
  Plus,
  Search,
  Trash2,
  Upload,
  X,
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
import { toAbsoluteUrl } from '@/lib/helpers';
import AddMember from '../../partials/modal/add-member/AddMember';
import { getleadcountbystatus, GetAllLead } from "@/services/apiServices";
// ── Static data ────────────────────────────────────────────────────────────────



const STATUS_STYLES = {
  Qualified: 'bg-emerald-50 text-emerald-700 border-emerald-200',
  New:       'bg-blue-50   text-blue-700   border-blue-200',
  Pending:   'bg-amber-50  text-amber-700  border-amber-200',
  Lost:      'bg-rose-50   text-rose-700   border-rose-200',
  Won:       'bg-emerald-50 text-emerald-700 border-emerald-200',
};

const AVATAR_COLORS = {
  violet: { bg: 'bg-violet-100', text: 'text-violet-700' },
  amber:  { bg: 'bg-amber-100',  text: 'text-amber-700'  },
  blue:   { bg: 'bg-blue-100',   text: 'text-blue-700'   },
  emerald:{ bg: 'bg-emerald-100',text: 'text-emerald-700'},
};

const getInitials = (name = '') =>
  name.split(' ').map((p) => p[0]).join('').slice(0, 2).toUpperCase();

const StatusBadge = ({ status }) => (
  <span
    className={cn(
      'inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-medium',
      STATUS_STYLES[status] ?? 'bg-gray-50 text-gray-600 border-gray-200',
    )}
  >
    {status}
  </span>
);

const Avatar = ({ initial, color = 'violet', size = 7 }) => {
  const c = AVATAR_COLORS[color] ?? AVATAR_COLORS.violet;
  return (
    <div
      className={cn(
        `w-${size} h-${size} rounded-full flex items-center justify-center text-xs font-semibold shrink-0`,
        c.bg, c.text,
      )}
    >
      {initial}
    </div>
  );
};

const EmptyState = ({ message, sub }) => (
  <div className="flex flex-col items-center justify-center py-16 gap-3 text-center">
    <div className="w-12 h-12 rounded-xl bg-gray-100 flex items-center justify-center">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" className="text-gray-400">
        <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
      </svg>
    </div>
    <div>
      <p className="text-sm font-medium text-gray-700">{message}</p>
      <p className="text-xs text-muted-foreground mt-0.5">{sub}</p>
    </div>
  </div>
);


// ── Main component ─────────────────────────────────────────────────────────────

const Dashboard = () => {
  const navigate = useNavigate();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedStaff, setSelectedStaff] = useState(null);
  const [leadsPage, setLeadsPage] = useState(1);
  const [leadsData, setLeadsData] = useState([]);
  const [followUpsData, setFollowUpsData] = useState([]);
  const [leadCounts, setLeadCounts] = useState({
    total: 0, open: 0, won: 0, lost: 0, fup: 0,
  });
const [followUpsPage, setFollowUpsPage] = useState(1);
const followUpsPerPage = 5;

  const userId = Number(localStorage.getItem("mainId") || 0);
  const assignId = Number(localStorage.getItem("assignId") || -1);
  const memberId = Number(localStorage.getItem("userId") || -1);
  const statCards = useMemo(() => [
    { label: 'Total Leads',      value: String(leadCounts.total), sub: 'All time',            icon: '/media/icon/totalleads.png',     iconBg: 'bg-violet-100' },
    { label: 'New Leads',        value: String(leadCounts.open),  sub: 'Awaiting action',     icon: '/media/icon/newleads.png',       iconBg: 'bg-blue-100'   },
    { label: 'Won Leads',        value: String(leadCounts.won),   sub: 'Closed successfully', icon: '/media/icon/wonlead.png',        iconBg: 'bg-emerald-100'},
    { label: 'Lost Leads',       value: String(leadCounts.lost),  sub: 'Not converted',       icon: '/media/icon/lostlead.png',       iconBg: 'bg-rose-100'   },
    { label: 'Follow-ups Today', value: String(leadCounts.fup),   sub: 'Due today',           icon: '/media/icon/todayfollow-up.png', iconBg: 'bg-orange-100' },
  ], [leadCounts]);

  const leadsPerPage = 5;
  const totalLeadsPages = Math.ceil(leadsData.length / leadsPerPage);

  const pagedLeads = leadsData.slice(
    (leadsPage - 1) * leadsPerPage,
    leadsPage * leadsPerPage,
  );




const hasFetchedRef = React.useRef(false);

useEffect(() => {
  if (hasFetchedRef.current) return;
  hasFetchedRef.current = true;

  let cancelled = false;

  getleadcountbystatus(userId)
    .then((res) => {
      if (cancelled) return;
      const d = res?.data?.data || {};
      setLeadCounts({
        total: d["Total Leads"] ?? 0,
        open:  d["Open"]        ?? 0,
        won:   d["Won"]         ?? 0,
        lost:  d["Lost"]        ?? 0,
        fup:   d["Today's FUP"] ?? 0,
      });
    })
    .catch((err) => console.error("Lead count error:", err));

  GetAllLead(userId, assignId)
    .then((res) => {
      if (cancelled) return;
      const list = res?.data?.data?.["All Leads"] ?? [];
      setLeadsData(list);
    })
    .catch((err) => console.error("Leads fetch error:", err));

  return () => {
    cancelled = true;
  };
}, [userId]);

  const allFollowUps = useMemo(() => {
  return leadsData.flatMap((lead) =>
    (lead.followUpDetails || []).map((f) => ({
      ...f,
      customerName: lead.clientName,
      assignMember: lead.leadAssignName,
      leadCreatedAt: lead.createdAt,
    })),
  );
}, [leadsData]);

const totalFollowUpsPages = Math.ceil(allFollowUps.length / followUpsPerPage);
const pagedFollowUps = allFollowUps.slice(
  (followUpsPage - 1) * followUpsPerPage,
  followUpsPage * followUpsPerPage,
);
  return (
    <div className="min-h-screen ">
      <main className="px-6 space-y-5">

        <div>
          <h1 className="text-xl font-semibold text-gray-900">Dashboard</h1>
          <p className="text-xs text-muted-foreground mt-0.5">
            <span className="text-violet-600">Home</span> / Dashboard
          </p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {statCards.map((s, i) => (
            <div
              key={i}
              className="rounded-2xl bg-white border border-gray-100 shadow-sm p-4 flex items-center gap-4"
            >
              <div
                className={cn(
                  'w-12 h-12 rounded-xl flex items-center justify-center shrink-0',
                  s.iconBg,
                )}
              >
                <img
                  src={toAbsoluteUrl(s.icon)}
                  alt={s.label}
                  className="w-6 h-6 object-contain"
                />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">{s.label}</p>
                <p className="text-2xl font-bold text-gray-900 leading-tight">{s.value}</p>
                <p className="text-xs text-muted-foreground">{s.sub}</p>
              </div>
            </div>
          ))}
        </div>

        <div className="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden">
        <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
  <h2 className="text-sm font-semibold text-gray-900">Recent Leads</h2>
  <button
    onClick={() => navigate('/leads/dashboard')}
    className="text-xs text-violet-600 hover:underline font-medium"
  >
    View all leads
  </button>
</div>

          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-100 text-xs text-muted-foreground font-medium">
                  <th className="px-5 py-3 text-left w-8">#</th>
                  <th className="px-3 py-3 text-left">LEAD NAME</th>
                  <th className="px-3 py-3 text-left">PHONE</th>
                 
                  <th className="px-3 py-3 text-left">STATUS</th>
                  <th className="px-3 py-3 text-left">ASSIGNED TO</th>
                  <th className="px-3 py-3 text-left">SOURCE</th>
                  <th className="px-3 py-3 text-left">CREATED DATE</th>
                </tr>
              </thead>
            <tbody>
  {pagedLeads.length === 0 ? (
    <tr><td colSpan={9}><EmptyState message="No leads yet." sub="Your leads will appear here." /></td></tr>
  ) : pagedLeads.map((lead, idx) => (
    <tr key={lead.id} className="border-b border-gray-50 hover:bg-gray-50/60 transition-colors">
  <td className="px-5 py-3 text-xs text-muted-foreground">{idx + 1}</td>
  <td className="px-3 py-3">
    <div className="flex items-center gap-2">
      <Avatar initial={getInitials(lead.clientName)} color="violet" size={7} />
      <div>
        <p className="font-medium text-gray-900 text-xs leading-tight">{lead.clientName}</p>
        <p className="text-xs text-muted-foreground leading-tight">{lead.leadSource?.sourceName || "-"}</p>
      </div>
    </div>
  </td>
  <td className="px-3 py-3 text-xs text-gray-700">{lead.contactNumber || "-"}</td>
  <td className="px-3 py-3">
    <span className="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-medium"
      style={{
        backgroundColor: (lead.leadStatus?.colorCode || "#ccc") + "20",
        color: lead.leadStatus?.colorCode || "#666",
        borderColor: (lead.leadStatus?.colorCode || "#ccc") + "60",
      }}>
      {lead.leadStatus?.statusName || "-"}
    </span>
  </td>
  <td className="px-3 py-3">
    <div className="flex items-center gap-1.5">
      <Avatar initial={getInitials(lead.leadAssignName || "?")} color="amber" size={6} />
      <span className="text-xs text-gray-700">{lead.leadAssignName || "-"}</span>
    </div>
  </td>
  <td className="px-3 py-3 text-xs text-gray-700">{lead.leadSource?.sourceName || "-"}</td>
  <td className="px-3 py-3">
    <div className="flex flex-col leading-tight">
      <span className="text-xs text-gray-700">{lead.inquiryDate || "-"}</span>
      <span className="text-xs text-muted-foreground">
        {lead.createdAt ? new Date(lead.createdAt).toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit" }) : ""}
      </span>
    </div>
  </td>
</tr>
  ))}
</tbody>
            </table>
          </div>

          {/* pagination footer */}
          <div className="px-5 py-3 flex items-center justify-between border-t border-gray-100">
         <span className="text-xs text-muted-foreground">
  Showing {leadsData.length === 0 ? 0 : Math.min((leadsPage - 1) * leadsPerPage + 1, leadsData.length)}–
  {Math.min(leadsPage * leadsPerPage, leadsData.length)} of {leadsData.length} leads
</span>

            <div className="flex items-center gap-1">
              <button
                onClick={() => setLeadsPage((p) => Math.max(1, p - 1))}
                disabled={leadsPage === 1}
                className="p-1 rounded hover:bg-gray-100 disabled:opacity-30 transition-colors"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
              {Array.from({ length: totalLeadsPages }, (_, i) => (
                <button
                  key={i}
                  onClick={() => setLeadsPage(i + 1)}
                  className={cn(
                    'w-6 h-6 text-xs rounded flex items-center justify-center transition-colors',
                    leadsPage === i + 1
                      ? 'bg-violet-600 text-white'
                      : 'hover:bg-gray-100 text-gray-600',
                  )}
                >
                  {i + 1}
                </button>
              ))}
              <button
                onClick={() => setLeadsPage((p) => Math.min(totalLeadsPages, p + 1))}
                disabled={leadsPage === totalLeadsPages}
                className="p-1 rounded hover:bg-gray-100 disabled:opacity-30 transition-colors"
              >
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>

    

        {/* Follow-up */}
     {/* Follow-up */}
<div className="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden">
  <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
    <h2 className="text-sm font-semibold text-gray-900">Follow-up</h2>
  </div>
  <div className="overflow-x-auto">
    <table className="w-full text-sm">
      <thead>
        <tr className="border-b border-gray-100 text-xs text-muted-foreground font-medium">
          <th className="px-5 py-3 text-left w-8">#</th>
          <th className="px-3 py-3 text-left">CUSTOMER NAME</th>
          <th className="px-3 py-3 text-left">ASSIGN MEMBER</th>
          <th className="px-3 py-3 text-left">FOLLOWUP DATE</th>
          <th className="px-3 py-3 text-left">FOLLOW UP TYPE</th>
          <th className="px-3 py-3 text-left">CREATED AT</th>
        </tr>
      </thead>
      <tbody>
        {pagedFollowUps.length === 0 ? (
          <tr>
            <td colSpan={6}>
              <EmptyState message="No follow-up made yet." sub="Your follow-up will appear here." />
            </td>
          </tr>
        ) : pagedFollowUps.map((f, idx) => (
          <tr key={f.id ?? idx} className="border-b border-gray-50 hover:bg-gray-50/60">
            <td className="px-5 py-3 text-xs text-muted-foreground">
              {(followUpsPage - 1) * followUpsPerPage + idx + 1}
            </td>
            <td className="px-3 py-3 text-xs text-gray-700">{f.customerName || "-"}</td>
            <td className="px-3 py-3 text-xs text-gray-700">{f.assignMember || "-"}</td>
            <td className="px-3 py-3 text-xs text-gray-700">{f.followUpDate || "-"}</td>
            <td className="px-3 py-3 text-xs text-gray-700">{f.followUpType || "-"}</td>
            <td className="px-3 py-3 text-xs text-gray-700">
              {f.leadCreatedAt
                ? new Date(f.leadCreatedAt).toLocaleString("en-IN", {
                    day: "2-digit",
                    month: "short",
                    year: "numeric",
                    hour: "2-digit",
                    minute: "2-digit",
                  })
                : "-"}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>

  {/* pagination footer */}
  <div className="px-5 py-3 flex items-center justify-between border-t border-gray-100">
    <span className="text-xs text-muted-foreground">
      Showing {allFollowUps.length === 0 ? 0 : Math.min((followUpsPage - 1) * followUpsPerPage + 1, allFollowUps.length)}–
      {Math.min(followUpsPage * followUpsPerPage, allFollowUps.length)} of {allFollowUps.length} follow-ups
    </span>
    <div className="flex items-center gap-1">
      <button
        onClick={() => setFollowUpsPage((p) => Math.max(1, p - 1))}
        disabled={followUpsPage === 1}
        className="p-1 rounded hover:bg-gray-100 disabled:opacity-30 transition-colors"
      >
        <ChevronLeft className="w-4 h-4" />
      </button>
      {Array.from({ length: totalFollowUpsPages }, (_, i) => (
        <button
          key={i}
          onClick={() => setFollowUpsPage(i + 1)}
          className={cn(
            'w-6 h-6 text-xs rounded flex items-center justify-center transition-colors',
            followUpsPage === i + 1
              ? 'bg-violet-600 text-white'
              : 'hover:bg-gray-100 text-gray-600',
          )}
        >
          {i + 1}
        </button>
      ))}
      <button
        onClick={() => setFollowUpsPage((p) => Math.min(totalFollowUpsPages, p + 1))}
        disabled={followUpsPage === totalFollowUpsPages}
        className="p-1 rounded hover:bg-gray-100 disabled:opacity-30 transition-colors"
      >
        <ChevronRight className="w-4 h-4" />
      </button>
    </div>
  </div>
</div>

      </main>

      <AddMember
        isOpen={isModalOpen}
        onClose={() => { setIsModalOpen(false); setSelectedStaff(null); }}
        staffData={selectedStaff}
      />
    </div>
  );
};

export default Dashboard;