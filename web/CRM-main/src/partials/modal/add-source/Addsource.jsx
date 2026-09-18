"use client";

import React, { useEffect, useState } from "react";
import { Save, Tag, X } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { CustomModal } from "../../../components/custom-modal/CustomModal";
import { Addleadsource, updateleadsource } from "@/services/apiServices";
import Swal from "sweetalert2";

const FieldLabel = ({ children, required }) => (
  <label className="block text-sm font-medium text-gray-700 mb-1.5">
    {children}
    {required && <span className="text-rose-500 ml-0.5">*</span>}
  </label>
);

const Addsource = ({ open, onClose, onSave, editingSource }) => {
  const [sourceName, setSourceName] = useState("");
  const [medium, setMedium] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const userId = Number(localStorage.getItem("mainId") || 0);
  const isEditing = !!editingSource;

  // populate fields when editing
  useEffect(() => {
    if (editingSource) {
      setSourceName(editingSource.name || "");
      setMedium(editingSource.medium || "");
    } else {
      setSourceName("");
      setMedium("");
    }
  }, [editingSource, open]);

  const handleSave = async () => {
    if (!sourceName.trim()) return;
    try {
      setIsSubmitting(true);

      let res;
      if (isEditing) {
res = await updateleadsource(editingSource.id, sourceName.trim(), userId);    
  } else {
        res = await Addleadsource(sourceName.trim(), userId);
      }

      if (res?.data?.success) {
        await Swal.fire({
          icon: "success",
          title: "Success",
          text: res.data.msg,
          timer: 2000,
          showConfirmButton: false,
        });
        onSave?.();
      } else {
        Swal.fire({ icon: "error", title: "Failed", text: res?.data?.msg || "Failed to save source." });
      }
    } catch (err) {
      Swal.fire({ icon: "error", title: "Error", text: err?.response?.data?.msg || "Something went wrong." });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <CustomModal
      open={open}
      onClose={onClose}
      width={620}
      footer={
        <div className="flex items-center justify-end gap-2">
          <Button variant="outline" size="md" onClick={onClose}>Cancel</Button>
          <Button
            size="md"
            className="bg-primary hover:bg-primary/90"
            onClick={handleSave}
            disabled={isSubmitting || !sourceName.trim()}
          >
            <Save className="w-4 h-4 mr-2" />
            {isSubmitting ? "Saving..." : isEditing ? "Update Source" : "Save Source"}
          </Button>
        </div>
      }
    >
      {/* Header */}
      <div className="-mx-6 -mt-6 mb-5 rounded-t-2xl px-6 py-5 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-11 h-11 rounded-xl bg-primary flex items-center justify-center shrink-0">
            <Tag className="h-5 w-5 text-white" />
          </div>
          <h2 className="text-lg font-semibold text-black leading-tight">
            {isEditing ? "Edit Source" : "Add Source"}
          </h2>
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
          <FieldLabel required>Source Name</FieldLabel>
          <div className="relative flex items-center">
            <Tag className="absolute left-3 h-4 w-4 text-primary" />
            <Input
              value={sourceName}
              onChange={(e) => setSourceName(e.target.value)}
              placeholder="Enter source name"
              className="h-11 pl-9 text-sm border-gray-300"
            />
          </div>
          <p className="text-xs text-muted-foreground mt-1.5">
            Enter a clear and recognizable name for this source.
          </p>
        </div>

        
      </div>
    </CustomModal>
  );
};

export default Addsource;