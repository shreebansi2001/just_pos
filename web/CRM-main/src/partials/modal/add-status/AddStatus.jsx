"use client";

import React, { useEffect, useState } from "react";
import {
  CheckCircle2,
  Loader2,
  Save,
  ShieldAlert,
  Tag,
  Target,
  X,
  XCircle,
  ChevronDown,
  Check,
  Pipette,
} from "lucide-react";
import Swal from "sweetalert2";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { CustomModal } from "../../../components/custom-modal/CustomModal"; // adjust this path to match your project structure
import { Addleadstatus, updatestatus } from "@/services/apiServices";

// ---- data ---------------------------------------------------------------

// NOTE: `id` here is the lead_status_type_id sent to the API.
// Adjust these if your backend uses a different numbering for open/won/lost/junk.
const STATUS_TYPE_OPTIONS = [
  { value: "open", label: "Open (Active Pipeline)", icon: Target, tone: "bg-primary-600", id: 1 },
  { value: "won", label: "Won", icon: CheckCircle2, tone: "bg-emerald-500", id: 2 },
  { value: "lost", label: "Lost", icon: XCircle, tone: "bg-rose-500", id: 3 },
  { value: "junk", label: "Junk", icon: ShieldAlert, tone: "bg-orange-500", id: 4 },
];

const COLOR_OPTIONS = [
  "#7C3AED",
  "#3B82F6",
  "#22C55E",
  "#F97316",
  "#EF4444",
  "#EC4899",
  "#06B6D4",
  "#1E3A8A",
];

// ---- small primitives -----------------------------------------------------

const FieldLabel = ({ children, required }) => (
  <label className="block text-sm font-medium text-gray-700 mb-1.5">
    {children}
    {required && <span className="text-rose-500 ml-0.5">*</span>}
  </label>
);

const StatusTypeSelect = ({ value, onChange }) => {
  const [open, setOpen] = useState(false);
  const selected = STATUS_TYPE_OPTIONS.find((o) => o.value === value);

  return (
    <div className="relative">
      <button
        type="button"
        onClick={() => setOpen((prev) => !prev)}
        className="w-full inline-flex items-center justify-between gap-2 rounded-lg border border-gray-300 bg-white px-3.5 h-11 text-sm text-gray-900"
      >
        <span className="inline-flex items-center gap-2">
          {selected ? (
            <span
              className={cn(
                "w-5 h-5 rounded-full flex items-center justify-center text-white shrink-0",
                selected.tone,
              )}
            >
              <selected.icon className="h-3 w-3" />
            </span>
          ) : (
            <Target className="h-4 w-4 text-gray-400" />
          )}
          {selected ? selected.label : "Select status type"}
        </span>
        <ChevronDown className="h-3.5 w-3.5 text-gray-400 shrink-0" />
      </button>

      {open && (
        <div className="absolute z-20 mt-1.5 w-full rounded-xl border border-gray-200 bg-white p-1.5 shadow-lg">
          {STATUS_TYPE_OPTIONS.map((opt) => (
            <button
              key={opt.value}
              type="button"
              onClick={() => {
                onChange(opt.value);
                setOpen(false);
              }}
              className="w-full flex items-center gap-3 rounded-lg px-3 py-1 text-sm text-gray-800 hover:bg-gray-50 transition-colors"
            >
              <span
                className={cn(
                  "w-6 h-6 rounded-full flex items-center justify-center text-white shrink-0",
                  opt.tone,
                )}
              >
                <opt.icon className="h-3.5 w-3.5" />
              </span>
              {opt.label}
            </button>
          ))}
        </div>
      )}
    </div>
  );
};

// ---- modal ----------------------------------------------------------------

const AddStatus = ({ open, onClose, onSave, editingStatus }) => {
  const [statusName, setStatusName] = useState("");
  const [statusType, setStatusType] = useState("open");
  const [statusColor, setStatusColor] = useState(COLOR_OPTIONS[0]);
  const [saving, setSaving] = useState(false);

const [isActive, setIsActive] = useState(true);

  const isEditing = Boolean(editingStatus);

  // populate fields when editing, reset when adding fresh
 useEffect(() => {
  if (!open) return;
  if (editingStatus) {
    setStatusName(editingStatus.name || "");
    setStatusType(editingStatus.statusType || "open");
    setStatusColor(editingStatus.color || COLOR_OPTIONS[0]);
    setIsActive(editingStatus.active ?? true);
  } else {
    setStatusName("");
    setStatusType("open");
    setStatusColor(COLOR_OPTIONS[0]);
    setIsActive(true);
  }
}, [open, editingStatus]);

  const handleSave = async () => {
    const trimmedName = statusName.trim();
    if (!trimmedName) {
      Swal.fire({
        icon: "warning",
        title: "Status name required",
        text: "Please enter a name for this status.",
      });
      return;
    }

    const userId = Number(localStorage.getItem("mainId") || 0);
    const typeOption = STATUS_TYPE_OPTIONS.find((o) => o.value === statusType);

const payload = {
  statusName: trimmedName,
  colorCode: statusColor,
  isActive,
  leadStatusId: 0,
  lead_status_type_id: typeOption?.id ?? 1,
  userId,
  ...(isEditing ? { leadStatusId: editingStatus.id } : {}),
};

    try {
      setSaving(true);
      const res = isEditing
        ? await updatestatus(editingStatus.id, payload)
        : await Addleadstatus(payload);

      if (res?.data?.success) {
        Swal.fire({
          icon: "success",
          title: isEditing ? "Status updated" : "Status added",
          text: res.data.msg,
          timer: 1500,
          showConfirmButton: false,
        });
        onSave?.(payload);
        onClose();
      } else {
        Swal.fire({
          icon: "error",
          title: "Failed",
          text: res?.data?.msg || "Something went wrong.",
        });
      }
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: err?.response?.data?.msg || "Something went wrong.",
      });
    } finally {
      setSaving(false);
    }
  };

  return (
    <CustomModal
      open={open}
      onClose={onClose}
      width={620}
      footer={
        <div className="flex items-center justify-end gap-2">
          <Button variant="outline" size="md" onClick={onClose} disabled={saving}>
            Cancel
          </Button>
          <Button
            size="md"
            className="bg-primary hover:bg-primary/90"
            onClick={handleSave}
            disabled={saving}
          >
            {saving ? (
              <Loader2 className="w-4 h-4 mr-2 animate-spin" />
            ) : (
              <Save className="w-4 h-4 mr-2" />
            )}
            {isEditing ? "Update Status" : "Save Status"}
          </Button>
        </div>
      }
    >
      <div className="-mx-6 -mt-6 mb-5 rounded-t-2xl px-6 py-5 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-11 h-11 rounded-xl bg-primary flex items-center justify-center shrink-0">
            <Tag className="h-5 w-5 text-white" />
          </div>
          <div>
            <h2 className="text-lg font-semibold text-black leading-tight">
              {isEditing ? "Edit Status" : "Add Status"}
            </h2>
            <p className="text-sm text-black">
              {isEditing ? "Update this pipeline status" : "Create a new status for your pipeline"}
            </p>
          </div>
        </div>
        <button
          type="button"
          onClick={onClose}
          className="w-8 h-8 rounded-full bg-black flex items-center justify-center text-white transition-colors shrink-0"
        >
          <X className="h-4 w-4" />
        </button>
      </div>

      {/* Body */}
      <div className="flex flex-col gap-5">
        <div>
          <FieldLabel required>Status Name</FieldLabel>
          <div className="relative flex items-center">
            <Tag className="absolute left-3 h-4 w-4 text-primary" />
            <Input
              value={statusName}
              onChange={(e) => setStatusName(e.target.value)}
              placeholder="Enter status name"
              className="h-11 pl-9 text-sm border-gray-300"
            />
          </div>
          <p className="text-xs text-muted-foreground mt-1.5">
            Enter a clear and concise name for this status.
          </p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <FieldLabel required>Status Type</FieldLabel>
            <StatusTypeSelect value={statusType} onChange={setStatusType} />
          </div>

          <div>
            <FieldLabel required>Status Color</FieldLabel>
            <div className="relative flex items-stretch">
              <span
                className="w-1.5 rounded-l-lg shrink-0"
                style={{ backgroundColor: statusColor }}
              />
              <Input
                value={statusColor}
                onChange={(e) => setStatusColor(e.target.value)}
                className="h-11 rounded-l-none text-sm border-gray-300 border-l-0"
              />
            </div>
          </div>
        </div>
<div className="flex items-center justify-between rounded-lg border border-gray-200 px-3.5 h-11">
  <span className="text-sm font-medium text-gray-700">
    {isActive ? "Active" : "Inactive"}
  </span>
  <button
    type="button"
    onClick={() => setIsActive((prev) => !prev)}
    className={cn(
      "relative inline-flex h-6 w-11 items-center rounded-full transition-colors",
      isActive ? "bg-primary" : "bg-gray-300",
    )}
  >
    <span
      className={cn(
        "inline-block h-5 w-5 transform rounded-full bg-white transition-transform",
        isActive ? "translate-x-5" : "translate-x-1",
      )}
    />
  </button>
</div>
        {/* Color picker + swatches */}
        <div>
          <p className="text-xs text-muted-foreground mb-2">
            Choose a color to represent this status.
          </p>

          <div className="flex items-center gap-2 flex-wrap">
            {/* Preset swatches */}
            {COLOR_OPTIONS.map((color) => {
              const isSelected = statusColor === color;
              return (
                <button
                  key={color}
                  type="button"
                  onClick={() => setStatusColor(color)}
                  className={cn(
                    "w-8 h-8 rounded-lg flex items-center justify-center transition-transform",
                    isSelected && "ring-2 ring-offset-2 ring-gray-300",
                  )}
                  style={{ backgroundColor: color }}
                >
                  {isSelected && <Check className="h-4 w-4 text-white" />}
                </button>
              );
            })}

            {/* Custom color swatch — appears once user picks a non-preset color */}
            {!COLOR_OPTIONS.includes(statusColor) && (
              <button
                type="button"
                className="w-8 h-8 rounded-lg flex items-center justify-center ring-2 ring-offset-2 ring-gray-300"
                style={{ backgroundColor: statusColor }}
              >
                <Check className="h-4 w-4 text-white" />
              </button>
            )}

            {/* Divider */}
            <div className="w-px h-7 bg-gray-200 mx-1" />

            {/* Native color picker trigger */}
            <label
              title="Custom color"
              className="w-8 h-8 rounded-lg border-2 border-dashed border-gray-300 hover:border-gray-400 flex items-center justify-center cursor-pointer relative overflow-hidden"
            >
              <Pipette className="h-4 w-4 text-gray-400" />
              <input
                type="color"
                value={COLOR_OPTIONS.includes(statusColor) ? "#7C3AED" : statusColor}
                onChange={(e) => setStatusColor(e.target.value)}
                className="absolute inset-0 opacity-0 w-full h-full cursor-pointer"
              />
            </label>
          </div>
        </div>
      </div>
    </CustomModal>
  );
};

export default AddStatus;