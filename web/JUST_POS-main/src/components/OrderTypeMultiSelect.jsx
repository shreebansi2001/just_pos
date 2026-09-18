import React, { useState, useRef, useEffect } from "react";
import { ChevronDown, X, Check } from "lucide-react";

const ORDER_TYPE_OPTIONS = ["Dine In", "Takeaway", "Delivery"];

/**
 * OrderTypeMultiSelect
 *
 * Props:
 *   selected   - string[]  currently selected types (empty = "All")
 *   onChange   - (string[]) => void
 *   focusColor - "red" | "blue"  (matches the page's accent, default "red")
 */
export default function OrderTypeMultiSelect({ selected = [], onChange, focusColor = "red" }) {
  const [open, setOpen] = useState(false);
  const containerRef = useRef(null);

  const ringClass =
    focusColor === "blue"
      ? "border-blue-500 ring-1 ring-blue-500"
      : "border-red-500 ring-1 ring-red-500";

  // Close on outside click
  useEffect(() => {
    function handleClickOutside(e) {
      if (containerRef.current && !containerRef.current.contains(e.target)) {
        setOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const toggle = (option) => {
    if (selected.includes(option)) {
      onChange(selected.filter((o) => o !== option));
    } else {
      onChange([...selected, option]);
    }
  };

  const removeTag = (e, option) => {
    e.stopPropagation();
    onChange(selected.filter((o) => o !== option));
  };

  const clearAll = (e) => {
    e.stopPropagation();
    onChange([]);
  };

  const isAllSelected = selected.length === 0;
  const displayLabel =
    selected.length === 0
      ? "All Order Type"
      : null; // tags are rendered inline

  return (
    <div ref={containerRef} className="relative w-full">
      {/* Trigger button */}
      <button
        type="button"
        onClick={() => setOpen((v) => !v)}
        className={`flex min-h-[36px] w-full flex-wrap items-center gap-1.5 rounded-xl border bg-white px-3 py-1.5 text-left text-xs sm:text-sm text-slate-700 cursor-pointer transition focus:outline-none ${
          open ? ringClass : "border-slate-200 hover:border-slate-300"
        }`}
      >
        {/* "All Order Type" placeholder or selected tags */}
        {isAllSelected ? (
          <span className="text-slate-500 select-none">{displayLabel}</span>
        ) : (
          <>
            {selected.map((opt) => (
              <span
                key={opt}
                className="inline-flex items-center gap-0.5 rounded-md bg-slate-100 px-1.5 py-0.5 text-xs font-medium text-slate-700"
              >
                {opt}
                <button
                  type="button"
                  onClick={(e) => removeTag(e, opt)}
                  className="ml-0.5 text-slate-400 hover:text-slate-700"
                  tabIndex={-1}
                  aria-label={`Remove ${opt}`}
                >
                  <X className="h-3 w-3" />
                </button>
              </span>
            ))}
            {/* Clear-all icon */}
            <button
              type="button"
              onClick={clearAll}
              className="ml-auto pl-1 text-slate-400 hover:text-slate-700 shrink-0"
              tabIndex={-1}
              aria-label="Clear all"
            >
              <X className="h-3.5 w-3.5" />
            </button>
          </>
        )}

        {/* Chevron — push to far right when no tags */}
        {isAllSelected && (
          <ChevronDown
            className={`ml-auto h-4 w-4 shrink-0 text-slate-400 transition-transform ${open ? "rotate-180" : ""}`}
          />
        )}
      </button>

      {/* Dropdown panel */}
      {open && (
        <div className="absolute z-50 mt-1 w-full rounded-xl border border-slate-200 bg-white shadow-lg overflow-hidden">
          {/* "All" option */}
          <button
            type="button"
            onClick={() => { onChange([]); setOpen(false); }}
            className={`flex w-full items-center justify-between px-3 py-2 text-xs sm:text-sm font-medium transition hover:bg-slate-50 ${
              isAllSelected ? "text-slate-900" : "text-slate-600"
            }`}
          >
            <span>All Order Type</span>
            {isAllSelected && <Check className="h-3.5 w-3.5 text-slate-700" />}
          </button>

          <div className="border-t border-slate-100" />

          {ORDER_TYPE_OPTIONS.map((opt) => {
            const checked = selected.includes(opt);
            return (
              <button
                key={opt}
                type="button"
                onClick={() => toggle(opt)}
                className="flex w-full items-center justify-between px-3 py-2 text-xs sm:text-sm transition hover:bg-slate-50"
              >
                <div className="flex items-center gap-2">
                  {/* Checkbox visual */}
                  <span
                    className={`flex h-4 w-4 shrink-0 items-center justify-center rounded border transition ${
                      checked
                        ? "border-primary bg-primary"
                        : "border-slate-300 bg-white"
                    }`}
                  >
                    {checked && <Check className="h-3 w-3 text-white" strokeWidth={3} />}
                  </span>
                  <span className={checked ? "font-semibold text-slate-900" : "text-slate-700"}>
                    {opt}
                  </span>
                </div>
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}
