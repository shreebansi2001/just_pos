"use client";

import React, { useEffect, useState } from "react";
import {
  ChevronRight, Layers, Lightbulb, Lock, Pencil, Plus,
  Save, ShieldAlert, Sparkles, Target, Trash2, Trophy, XCircle,
} from "lucide-react";
import {
  DndContext, closestCenter, KeyboardSensor, PointerSensor, useSensor, useSensors,
} from "@dnd-kit/core";
import {
  arrayMove, SortableContext, sortableKeyboardCoordinates,
  useSortable, verticalListSortingStrategy,
} from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import Swal from "sweetalert2";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import AddStatus from "../../../partials/modal/add-status/AddStatus";
import { getallstatus, DELETEstatus } from "@/services/apiServices";
import { usePermission } from "@/hooks/usePermission";

const STATUS_TYPES = [
  { key: "open", label: "OPEN",  icon: Target,     iconBg: "bg-blue-100 text-blue-600",    labelColor: "text-blue-600",    description: 'Active leads in progress. Counted toward "active pipeline" in reports.' },
  { key: "won",  label: "WON",   icon: Trophy,     iconBg: "bg-emerald-100 text-emerald-600", labelColor: "text-emerald-600", description: "Deals closed successfully. Counted in win rate and revenue." },
  { key: "lost", label: "LOST",  icon: XCircle,    iconBg: "bg-rose-100 text-rose-600",    labelColor: "text-rose-600",    description: "Deals not converted. Counted in loss rate." },
  { key: "junk", label: "JUNK",  icon: ShieldAlert,iconBg: "bg-gray-100 text-gray-500",    labelColor: "text-gray-500",    description: "Invalid or spam leads. Excluded from all reports." },
];

const Switch = ({ checked, onChange }) => (
  <button
    type="button"
    role="switch"
    aria-checked={checked}
    onClick={onChange}
    className={cn("relative inline-flex h-6 w-11 items-center rounded-full transition-colors shrink-0", checked ? "bg-primary" : "bg-gray-200")}
  >
    <span className={cn("inline-block h-5 w-5 transform rounded-full bg-white shadow transition-transform", checked ? "translate-x-5" : "translate-x-0.5")} />
  </button>
);



const LeadStatuses = () => {
  const [statuses, setStatuses]         = useState([]);
  const [isAddStatusOpen, setIsAddStatusOpen] = useState(false);
  const [editingStatus, setEditingStatus]     = useState(null);
  const [loading, setLoading]           = useState(true);
  const { view, add, edit, delete: canDelete } = usePermission("Crm Lead Status");

  const userId = Number(localStorage.getItem("mainId") || 0);
const SortableRow = ({ status, onToggle, onEdit, onDelete }) => {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } =
    useSortable({ id: status.id });

  return (
    <div
      ref={setNodeRef}
      style={{ transform: CSS.Transform.toString(transform), transition, opacity: isDragging ? 0.5 : 1, zIndex: isDragging ? 10 : undefined }}
      className="flex items-center gap-3 rounded-xl border border-gray-100 px-4 py-3 bg-white"
    >
      <button className="cursor-grab active:cursor-grabbing touch-none text-gray-300 hover:text-gray-400 shrink-0" {...attributes} {...listeners}>
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <circle cx="9" cy="5" r="1"/><circle cx="9" cy="12" r="1"/><circle cx="9" cy="19" r="1"/>
          <circle cx="15" cy="5" r="1"/><circle cx="15" cy="12" r="1"/><circle cx="15" cy="19" r="1"/>
        </svg>
      </button>

      <span className="h-2.5 w-2.5 rounded-full shrink-0" style={{ backgroundColor: status.color }} />

      <span className="text-sm font-semibold text-gray-900 w-32 shrink-0">{status.name}</span>

      <div className="flex items-center gap-1.5 flex-1">
        <span className="inline-flex items-center rounded-md px-2 py-0.5 text-[11px] font-semibold tracking-wide bg-gray-100 text-gray-600">
          {status.name.toUpperCase().replace(/\s+/g, "_")}
        </span>
        <span className="inline-flex items-center rounded-md px-2 py-0.5 text-[11px] font-semibold tracking-wide bg-violet-100 text-violet-700">
          {status.statusType?.toUpperCase()}
        </span>
      </div>

 <span
  className={cn(
    "inline-flex items-center rounded-full px-2.5 py-1 text-[11px] font-semibold tracking-wide shrink-0",
    status.active
      ? "bg-emerald-100 text-emerald-700"
      : "bg-gray-100 text-gray-500",
  )}
>
  {status.active ? "Active" : "Inactive"}
</span>
{edit && (
      <Button mode="icon" variant="ghost" size="sm" className="text-primary hover:bg-primary/10" onClick={() => onEdit(status)}>
        <Pencil className="w-4 h-4" />
      </Button>
)}
{canDelete && (
      <Button mode="icon" variant="ghost" size="sm" className="text-rose-500 hover:bg-rose-50" onClick={() => onDelete(status)}>
        <Trash2 className="w-4 h-4" />
      </Button>
)}
    </div>
  );
};
  // ── fetch ──────────────────────────────────────────────────────────────
  const fetchStatuses = async () => {
    try {
      setLoading(true);
      const res = await getallstatus(userId);
      if (res?.data?.success) {
        const raw = res.data.data ?? [];
        setStatuses(
          raw.map((s) => ({
            id: s.leadStatusId,
            name: s.statusName,
            color: s.colorCode || "#7C3AED",
            active: s.isActive,
            statusType: s.statusTypeName || "open",
            lead_status_type_id: s.lead_status_type_id,
          }))
        );
      }
    } catch {
      // silent
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchStatuses(); }, []);

  // ── dnd ────────────────────────────────────────────────────────────────
  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, { coordinateGetter: sortableKeyboardCoordinates }),
  );

  const handleDragEnd = ({ active, over }) => {
    if (over && active.id !== over.id) {
      setStatuses((prev) => {
        const oldIndex = prev.findIndex((s) => s.id === active.id);
        const newIndex = prev.findIndex((s) => s.id === over.id);
        return arrayMove(prev, oldIndex, newIndex);
      });
    }
  };

  const toggleStatus = (id) =>
    setStatuses((prev) => prev.map((s) => (s.id === id ? { ...s, active: !s.active } : s)));

  const handleEdit = (status) => {
    setEditingStatus(status);
    setIsAddStatusOpen(true);
  };

  // ── delete ─────────────────────────────────────────────────────────────
  const handleDelete = async (status) => {
    const confirm = await Swal.fire({
      title: "Delete Status?",
      text: `"${status.name}" will be permanently removed.`,
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#EF4444",
      cancelButtonColor: "#6B7280",
      confirmButtonText: "Yes, delete",
    });
    if (!confirm.isConfirmed) return;

    try {
      const res = await DELETEstatus(status.id);
      if (res?.data?.success) {
        setStatuses((prev) => prev.filter((s) => s.id !== status.id));
        Swal.fire({ icon: "success", title: "Deleted", text: res.data.msg, timer: 1500, showConfirmButton: false });
      } else {
        Swal.fire({ icon: "error", title: "Failed", text: res?.data?.msg || "Failed to delete." });
      }
    } catch (err) {
      Swal.fire({ icon: "error", title: "Error", text: err?.response?.data?.msg || "Something went wrong." });
    }
  };

  const handleSave = () => {
    fetchStatuses();
    setIsAddStatusOpen(false);
    setEditingStatus(null);
  };

  return (
    <div className="min-h-screen">
      <main className="px-6 space-y-5">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Lead Statuses</h1>
            <p className="text-sm text-muted-foreground mt-0.5 flex items-center gap-1">
              <span className="text-primary">Leads</span>
              <ChevronRight className="h-3.5 w-3.5 text-gray-400" />
              <span className="text-gray-400">Manage Statuses</span>
            </p>
          </div>
          {add && (
          <Button size="md" className="bg-primary hover:bg-primary/90" onClick={() => { setEditingStatus(null); setIsAddStatusOpen(true); }}>
            <Plus className="w-4 h-4 mr-2" />
            Add Status
          </Button>
          )}
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">
          {/* List */}
          <div className="lg:col-span-2 rounded-2xl border border-gray-100 bg-white p-5 shadow-sm">
            <div className="flex items-start justify-between mb-5">
              <div className="flex items-start gap-3">
                <div className="w-11 h-11 rounded-xl bg-primary/10 flex items-center justify-center shrink-0">
                  <Layers className="h-5 w-5 text-primary" />
                </div>
                <div>
                  <h2 className="text-base font-semibold text-gray-900">Manage your lead pipeline</h2>
                  <p className="text-sm text-muted-foreground mt-0.5">Drag to reorder statuses.</p>
                </div>
              </div>
            </div>

            {loading ? (
              <div className="text-center py-10 text-sm text-muted-foreground">Loading...</div>
            ) : statuses.length === 0 ? (
              <div className="text-center py-10 text-sm text-muted-foreground">No statuses found. Add one to get started.</div>
            ) : (
              <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
                <SortableContext items={statuses.map((s) => s.id)} strategy={verticalListSortingStrategy}>
                  <div className="flex flex-col gap-3">
                    {statuses.map((status) => (
                      <SortableRow key={status.id} status={status} onToggle={toggleStatus} onEdit={handleEdit} onDelete={handleDelete} />
                    ))}
                  </div>
                </SortableContext>
              </DndContext>
            )}
          </div>

          {/* Right info */}
          <div className="flex flex-col gap-5">
            <div className="rounded-2xl border border-gray-100 bg-white p-5 shadow-sm">
              <div className="flex items-center gap-2 mb-1">
                <Lightbulb className="h-4 w-4 text-primary" />
                <h3 className="text-sm font-semibold text-gray-900">Understand Status Types</h3>
              </div>
              <p className="text-sm text-muted-foreground mb-4">Statuses help you track leads across your pipeline.</p>
              <div className="flex flex-col gap-4">
                {STATUS_TYPES.map((type) => (
                  <div key={type.key} className="flex items-start gap-3">
                    <div className={cn("w-9 h-9 rounded-lg flex items-center justify-center shrink-0", type.iconBg)}>
                      <type.icon className="h-4 w-4" />
                    </div>
                    <div>
                      <p className={cn("text-xs font-bold tracking-wide", type.labelColor)}>{type.label}</p>
                      <p className="text-sm text-gray-600 leading-snug mt-0.5">{type.description}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="rounded-2xl border border-primary/20 bg-primary/5 p-5">
              <div className="flex items-center gap-2 mb-2">
                <Sparkles className="h-4 w-4 text-primary" />
                <h3 className="text-sm font-semibold text-gray-900">Good to know</h3>
              </div>
              <p className="text-sm text-gray-600 leading-snug">
                System statuses (won, lost, new, etc.) cannot be deleted as they are used in reporting logic.
              </p>
            </div>
          </div>
        </div>

        <AddStatus
          open={isAddStatusOpen}
          onClose={() => { setIsAddStatusOpen(false); setEditingStatus(null); }}
          onSave={handleSave}
          editingStatus={editingStatus}
        />
      </main>
    </div>
  );
};

export default LeadStatuses;