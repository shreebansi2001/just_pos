"use client";

import React, { useEffect, useState } from "react";
import {
  ChevronRight, Globe, Info, Layers, Megaphone, Pencil,
  Plus, Save, Share2, Sparkles, Trash2, UserCheck,
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
import Addsource from "../../../partials/modal/add-source/Addsource";
import { getallleadsource, deletebyidsource } from "@/services/apiServices";
import { usePermission } from "@/hooks/usePermission";

const SOURCE_TYPES = [
  { key: "website",  icon: Globe,     label: "Website",     desc: "organic or direct visits" },
  { key: "social",   icon: Share2,    label: "Social Media", desc: "Facebook, Instagram, etc." },
  { key: "referral", icon: UserCheck, label: "Referral",    desc: "word of mouth, partner leads" },
  { key: "campaign", icon: Megaphone, label: "Campaign",    desc: "paid ads or email campaigns" },
];

const SourceAvatar = ({ name, color = "#7C3AED" }) => (
  <div
    className="w-10 h-10 rounded-xl flex items-center justify-center text-sm font-bold shrink-0"
    style={{ backgroundColor: color + "22", color }}
  >
    {name?.charAt(0)?.toUpperCase() ?? "?"}
  </div>
);

const Switch = ({ checked, onChange }) => (
  <button
    type="button"
    role="switch"
    aria-checked={checked}
    onClick={onChange}
    className={cn(
      "relative inline-flex h-6 w-11 items-center rounded-full transition-colors shrink-0",
      checked ? "bg-primary" : "bg-gray-200",
    )}
  >
    <span className={cn("inline-block h-5 w-5 transform rounded-full bg-white shadow transition-transform", checked ? "translate-x-5" : "translate-x-0.5")} />
  </button>
);



const LeadSources = () => {
  const [sources, setSources] = useState([]);
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [editingSource, setEditingSource] = useState(null);
  const [loading, setLoading] = useState(true);
  const { view, add, edit, delete: canDelete } = usePermission("Crm Lead Sources");

  const userId = Number(localStorage.getItem("mainId") || 0);
const SortableRow = ({ source, onToggle, onEdit, onDelete }) => {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } =
    useSortable({ id: source.id });

  return (
    <div
      ref={setNodeRef}
      style={{ transform: CSS.Transform.toString(transform), transition, opacity: isDragging ? 0.45 : 1, zIndex: isDragging ? 10 : undefined }}
      className="flex items-center gap-3 rounded-xl border border-gray-100 px-4 py-3 bg-white"
    >
      <button className="cursor-grab active:cursor-grabbing touch-none text-gray-300 hover:text-gray-400 shrink-0" {...attributes} {...listeners}>
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <circle cx="9" cy="5" r="1" /><circle cx="9" cy="12" r="1" /><circle cx="9" cy="19" r="1" />
          <circle cx="15" cy="5" r="1" /><circle cx="15" cy="12" r="1" /><circle cx="15" cy="19" r="1" />
        </svg>
      </button>

      <SourceAvatar name={source.name} color={source.color} />

      <div className="flex-1 min-w-0">
        <span className="text-sm font-semibold text-gray-900">{source.name}</span>
        {source.active && (
          <span className="block mt-0.5 inline-flex items-center rounded px-1.5 py-0 text-[10px] font-semibold tracking-wider bg-emerald-100 text-emerald-700">
        
          </span>
        )}
      </div>

      {/* <Switch checked={source.active} onChange={() => onToggle(source.id)} /> */}
{edit && (
      <Button mode="icon" variant="ghost" size="sm" className="text-primary hover:bg-primary/10" onClick={() => onEdit(source)}>
        <Pencil className="w-4 h-4" />
      </Button>
)}
{canDelete && (
      <Button mode="icon" variant="ghost" size="sm" className="text-rose-500 hover:bg-rose-50" onClick={() => onDelete(source)}>
        <Trash2 className="w-4 h-4" />
      </Button>
)}
    </div>
  );
};
const fetchSources = async () => {
  try {
    setLoading(true);
    const res = await getallleadsource(userId);
    if (res?.data?.success) {
      const raw = res.data.data ?? [];
      setSources(
        raw.map((s) => ({
          id: s.leadSourceId,     
          name: s.sourceName,       
          color: "#7C3AED",
          active: !s.isDeleted,     
        }))
      );
    }
  } catch {
    // silent
  } finally {
    setLoading(false);
  }
};

  useEffect(() => { fetchSources(); }, []);

  // ── dnd ────────────────────────────────────────────────────────────────
  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, { coordinateGetter: sortableKeyboardCoordinates }),
  );

  const handleDragEnd = ({ active, over }) => {
    if (over && active.id !== over.id) {
      setSources((prev) => {
        const oldIndex = prev.findIndex((s) => s.id === active.id);
        const newIndex = prev.findIndex((s) => s.id === over.id);
        return arrayMove(prev, oldIndex, newIndex);
      });
    }
  };

  // ── toggle active (local only) ─────────────────────────────────────────
  const toggleSource = (id) =>
    setSources((prev) => prev.map((s) => (s.id === id ? { ...s, active: !s.active } : s)));

  // ── edit ───────────────────────────────────────────────────────────────
  const handleEdit = (source) => {
    setEditingSource(source);
    setIsAddOpen(true);
  };

  // ── delete ─────────────────────────────────────────────────────────────
  const handleDelete = async (source) => {
    const confirm = await Swal.fire({
      title: "Delete Source?",
      text: `"${source.name}" will be permanently removed.`,
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#EF4444",
      cancelButtonColor: "#6B7280",
      confirmButtonText: "Yes, delete",
    });

    if (!confirm.isConfirmed) return;

    try {
      const res = await deletebyidsource(source.id);
      if (res?.data?.success) {
        setSources((prev) => prev.filter((s) => s.id !== source.id));
        Swal.fire({ icon: "success", title: "Deleted", text: res.data.msg, timer: 1500, showConfirmButton: false });
      } else {
        Swal.fire({ icon: "error", title: "Failed", text: res?.data?.msg || "Failed to delete." });
      }
    } catch (err) {
      Swal.fire({ icon: "error", title: "Error", text: err?.response?.data?.msg || "Something went wrong." });
    }
  };

  // ── after add/edit success → refetch ───────────────────────────────────
  const handleSave = () => {
    fetchSources();
    setIsAddOpen(false);
    setEditingSource(null);
  };

  return (
    <div className="min-h-screen">
      <main className="px-6 space-y-5">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Lead Source</h1>
            <p className="text-sm text-muted-foreground mt-0.5 flex items-center gap-1">
              <span className="text-primary">Leads</span>
              <ChevronRight className="h-3.5 w-3.5 text-gray-400" />
              <span className="text-gray-400">Manage Source</span>
            </p>
          </div>
          {(add && 
          <Button size="md" className="bg-primary hover:bg-primary/90" onClick={() => { setEditingSource(null); setIsAddOpen(true); }}>
            <Plus className="w-4 h-4 mr-2" />
            Add Source
          </Button>
          )}
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">
          {/* Sources list */}
          <div className="lg:col-span-2 rounded-2xl border border-gray-100 bg-white p-5 shadow-sm">
            <div className="flex items-start justify-between mb-5">
              <div className="flex items-start gap-3">
                <div className="w-11 h-11 rounded-xl bg-primary/10 flex items-center justify-center shrink-0">
                  <Layers className="h-5 w-5 text-primary" />
                </div>
                <div>
                  <h2 className="text-base font-semibold text-gray-900">Your Lead Sources</h2>
                  <p className="text-sm text-muted-foreground mt-0.5">Drag to reorder • sources appear in lead form & filters</p>
                </div>
              </div>
            </div>

            {loading ? (
              <div className="text-center py-10 text-sm text-muted-foreground">Loading...</div>
            ) : sources.length === 0 ? (
              <div className="text-center py-10 text-sm text-muted-foreground">No sources found. Add one to get started.</div>
            ) : (
              <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
                <SortableContext items={sources.map((s) => s.id)} strategy={verticalListSortingStrategy}>
                  <div className="flex flex-col gap-3">
                    {sources.map((source) => (
                      <SortableRow key={source.id} source={source} onToggle={toggleSource} onEdit={handleEdit} onDelete={handleDelete} />
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
                <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center shrink-0">
                  <Info className="h-4 w-4 text-primary" />
                </div>
                <h3 className="text-base font-semibold text-gray-900">About Sources</h3>
              </div>
              <p className="text-sm text-muted-foreground mb-5 mt-1">
                Sources track where your leads come from — useful for measuring which marketing channels perform best.
              </p>
              <div className="flex flex-col gap-4">
                {SOURCE_TYPES.map((type) => (
                  <div key={type.key} className="flex items-start gap-3">
                    <div className="w-9 h-9 rounded-lg bg-primary/10 flex items-center justify-center shrink-0">
                      <type.icon className="h-4 w-4 text-primary" />
                    </div>
                    <p className="text-sm text-gray-700 leading-snug mt-1.5">
                      <span className="font-semibold">{type.label}</span>{" — "}{type.desc}
                    </p>
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
                The{" "}
                <span className="font-medium text-primary bg-primary/10 px-1.5 py-0.5 rounded text-xs">Medium</span>
                {" "}field is optional — use it for UTM medium values (e.g.{" "}
                <span className="font-medium text-primary bg-primary/10 px-1.5 py-0.5 rounded text-xs">cpc</span>,{" "}
                <span className="font-medium text-primary bg-primary/10 px-1.5 py-0.5 rounded text-xs">email</span>).
              </p>
            </div>
          </div>
        </div>

        <Addsource
          open={isAddOpen}
          onClose={() => { setIsAddOpen(false); setEditingSource(null); }}
          onSave={handleSave}
          editingSource={editingSource}
        />
      </main>
    </div>
  );
};

export default LeadSources;