'use client';

import React, { useMemo, useState  , useEffect} from 'react';
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
import {
  GetAllMemberByUserId,
  
} from "@/services/apiServices";
import Swal from "sweetalert2";
import { usePermission } from "@/hooks/usePermission";
// ── Static data ────────────────────────────────────────────────────────────────

const recentLeads = [
  {
    id: 1,
    name: 'Manan Gandhi',
    source: 'Instagram',
    phone: '08886889580',
    email: 'info@justwedding.in',
    status: 'Qualified',
    statusColor: 'emerald',
    assignedTo: 'Swapnil',
    assignedInitial: 'S',
    assignedColor: 'violet',
    createdDate: '15 Jun 2026',
    createdTime: '10:24 AM',
  },
  {
    id: 2,
    name: 'Swapnil',
    source: 'Website',
    phone: '123456789',
    email: 'swapnil1010@gmail.com',
    status: 'New',
    statusColor: 'blue',
    assignedTo: 'Manan',
    assignedInitial: 'M',
    assignedColor: 'amber',
    createdDate: '09 Jun 2026',
    createdTime: '11:15 AM',
  },
  {
    id: 3,
    name: 'Rakesh Jain',
    source: 'Referral',
    phone: '9876543210',
    email: 'rakesh@gmail.com',
    status: 'Pending',
    statusColor: 'amber',
    assignedTo: 'Swapnil',
    assignedInitial: 'S',
    assignedColor: 'violet',
    createdDate: '07 Jun 2026',
    createdTime: '09:30 AM',
  },
  {
    id: 4,
    name: 'Priya Sharma',
    source: 'Facebook',
    phone: '7894561230',
    email: 'priya.sharma@gmail.com',
    status: 'Lost',
    statusColor: 'rose',
    assignedTo: 'Manan',
    assignedInitial: 'M',
    assignedColor: 'amber',
    createdDate: '05 Jun 2026',
    createdTime: '11:20 AM',
  },
];

const paymentHistory = []; 
const followUps = [];        


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






// ── Main component ─────────────────────────────────────────────────────────────

const AllMember = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedStaff, setSelectedStaff] = useState(null);
const [tableData, setTableData] = useState([]);
const [loading, setLoading] = useState(false);
const Id = localStorage.getItem("mainId");
const [selectedMember, setSelectedMember] = useState(null);
  const [leadsPage, setLeadsPage] = useState(1);
  const leadsPerPage = 4;
  const totalLeadsPages = Math.ceil(recentLeads.length / leadsPerPage);
  const pagedLeads = recentLeads.slice(
    (leadsPage - 1) * leadsPerPage,
    leadsPage * leadsPerPage,
  );
    const { view, add, edit, delete: canDelete } = usePermission("Crm User Master");

const FetchMembers = () => {
  setLoading(true);

  GetAllMemberByUserId(Id)
    .then((res) => {
      const userDetails = res?.data?.data?.userDetails?.UserDetails;

      if (userDetails && Array.isArray(userDetails)) {
        const formatted = userDetails.map((member, index) => ({
          id: member.id,
          sr_no: index + 1,
          full_name:
            `${member.firstName || ""} ${member.lastName || ""}`.trim() || "-",
          city: member?.userBasicDetails?.city?.name || "-",
          state: member?.userBasicDetails?.state?.name || "-",
          mobile_no: member.contactNo || "-",
          role: member?.userBasicDetails?.role?.name || "-",
          email: member.email || "-",
        }));

        setTableData(formatted);
      } else {
        setTableData([]);
      }
    })
    .catch((err) => {
      console.error(err);
      setTableData([]);
    })
    .finally(() => {
      setLoading(false);
    });
};
useEffect(() => {
  FetchMembers();
}, []);


  return (
    <div className="min-h-screen ">
      <main className="px-6 space-y-5">

        {/* Page header */}
     <div className="flex items-center justify-between mb-5">
  <h1 className="text-2xl font-semibold text-gray-900">
    User Master
  </h1>

     {add && (
      <Button size="md" onClick={() => {
        setSelectedMember(null);
        setIsModalOpen(true);
      }} className="bg-primary" type="submit">
        
  Add Memeber
            <ChevronRight className="w-4 h-4 ml-1" />
          </Button>
     )}
</div>

       

        {/* Recent Leads */}
        <div className="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden">
          {/* header */}
         

          {/* table */}
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-100 text-xs text-muted-foreground font-medium">
                  <th className="px-5 py-3 text-left w-8">SR NO.</th>
                  <th className="px-3 py-3 text-left">FULL NAME</th>
                  <th className="px-3 py-3 text-left">CITY</th>
                  <th className="px-3 py-3 text-left">STATE</th>
                  <th className="px-3 py-3 text-left">MOBILE NO.</th>
                  <th className="px-3 py-3 text-left">ROLE</th>
                  <th className="px-3 py-3 text-left">EMAIL</th>
                  <th className="px-3 py-3 text-left">ACTIONS</th>
                </tr>
              </thead>
              <tbody>
                {tableData.map((member) => (
                  <tr
  key={member.id}
  className="border-b border-gray-50 hover:bg-gray-50"
>
  <td className="px-4 py-3">
    {member.sr_no}
  </td>

  <td className="px-4 py-3">
    {member.full_name}
  </td>

  <td className="px-4 py-3">
    {member.city}
  </td>

  <td className="px-4 py-3">
    {member.state}
  </td>

  <td className="px-4 py-3">
    {member.mobile_no}
  </td>

  <td className="px-4 py-3">
    {member.role}
  </td>

  <td className="px-4 py-3">
    {member.email}
  </td>

  <td className="px-4 py-3">
    <div className="flex items-center gap-2">
      { edit && (
      <button
        onClick={() => {
          setSelectedMember(member);
          setIsModalOpen(true);
        }}
        className="p-1 rounded hover:bg-gray-100"
      >
        <Pencil className="w-4 h-4" />
      </button>
      )}

     
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
              Showing {Math.min((leadsPage - 1) * leadsPerPage + 1, recentLeads.length)}–
              {Math.min(leadsPage * leadsPerPage, recentLeads.length)} of {recentLeads.length} leads
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

     
      

      </main>

      <AddMember
  isModalOpen={isModalOpen}
  setIsModalOpen={setIsModalOpen}
  selectedMember={selectedMember}
//   refreshData={getAllMembers}
/>
    </div>
  );
};

export default AllMember;