import React, { useState, useRef, useEffect } from "react";
import { DayPicker } from "react-day-picker";
import { format, isValid, parse } from "date-fns";
import { Calendar, ChevronLeft, ChevronRight } from "lucide-react";
import "react-day-picker/dist/style.css";

/**
 * DatePickerInput
 *
 * A calendar-popover date picker that visually matches the project's
 * rounded-xl filter bar style.
 *
 * Props:
 *   value        - Date | null       currently selected date
 *   onChange     - (Date | null) => void
 *   placeholder  - string            shown when no date is selected
 *   accentColor  - "red" | "blue"    popover nav button accent (default "red")
 *   className    - string            extra classes on the trigger button
 */
export default function DatePickerInput({
  value,
  onChange,
  placeholder = "Select date",
  accentColor = "red",
  className = "",
}) {
  const [open, setOpen] = useState(false);
  const [month, setMonth] = useState(value ?? new Date());
  const containerRef = useRef(null);

  // Keep displayed month in sync if value changes externally
  useEffect(() => {
    if (value && isValid(value)) setMonth(value);
  }, [value]);

  // Close on outside click
  useEffect(() => {
    function onOutside(e) {
      if (containerRef.current && !containerRef.current.contains(e.target)) {
        setOpen(false);
      }
    }
    document.addEventListener("mousedown", onOutside);
    return () => document.removeEventListener("mousedown", onOutside);
  }, []);

  const displayText = value && isValid(value)
    ? format(value, "d MMM yyyy") + " 06:00:00"
    : placeholder;

  const accentBtn =
    accentColor === "blue"
      ? "bg-blue-600 hover:bg-blue-700"
      : "bg-red-600 hover:bg-red-700";

  const accentSelected =
    accentColor === "blue"
      ? "[&_.rdp-day_button.rdp-day_selected]:bg-blue-600 [&_.rdp-day_button.rdp-day_selected]:text-white"
      : "[&_.rdp-day_button.rdp-day_selected]:bg-red-600 [&_.rdp-day_button.rdp-day_selected]:text-white";

  return (
    <div ref={containerRef} className={`relative w-full ${className}`}>
      {/* Trigger */}
      <button
        type="button"
        onClick={() => setOpen((v) => !v)}
        className={`flex w-full items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 text-left text-xs sm:text-sm text-slate-700 cursor-pointer transition hover:border-slate-300 focus:outline-none ${
          open
            ? accentColor === "blue"
              ? "border-blue-500 ring-1 ring-blue-500"
              : "border-red-500 ring-1 ring-red-500"
            : ""
        }`}
      >
        <Calendar className="h-4 w-4 text-slate-400 shrink-0" />
        <span className={`truncate font-medium ${value ? "text-slate-700" : "text-slate-400"}`}>
          {displayText}
        </span>
      </button>

      {/* Calendar popover */}
      {open && (
        <div className="absolute z-50 mt-1 rounded-2xl border border-slate-200 bg-white shadow-xl p-3 min-w-[280px]">
          {/* Custom nav header */}
          <div className="flex items-center justify-between mb-2 px-1">
            <button
              type="button"
              onClick={() => {
                const prev = new Date(month);
                prev.setMonth(prev.getMonth() - 1);
                setMonth(prev);
              }}
              className="h-7 w-7 flex items-center justify-center rounded-lg text-slate-500 hover:bg-slate-100 transition"
              aria-label="Previous month"
            >
              <ChevronLeft className="h-4 w-4" />
            </button>
            <span className="text-sm font-semibold text-slate-800">
              {format(month, "MMMM yyyy")}
            </span>
            <button
              type="button"
              onClick={() => {
                const next = new Date(month);
                next.setMonth(next.getMonth() + 1);
                setMonth(next);
              }}
              className="h-7 w-7 flex items-center justify-center rounded-lg text-slate-500 hover:bg-slate-100 transition"
              aria-label="Next month"
            >
              <ChevronRight className="h-4 w-4" />
            </button>
          </div>

          <DayPicker
            mode="single"
            selected={value ?? undefined}
            month={month}
            onMonthChange={setMonth}
            onSelect={(date) => {
              onChange(date ?? null);
              if (date) setOpen(false);
            }}
            showOutsideDays
            classNames={{
              months: "flex flex-col",
              month: "space-y-1",
              caption: "hidden", // we render our own header above
              caption_label: "hidden",
              nav: "hidden",
              table: "w-full border-collapse",
              head_row: "flex",
              head_cell:
                "text-slate-400 text-[11px] font-semibold uppercase w-9 text-center pb-1",
              row: "flex w-full",
              cell: "text-center p-0",
              day: "h-9 w-9 mx-auto rounded-lg text-xs font-medium text-slate-700 hover:bg-slate-100 transition cursor-pointer flex items-center justify-center focus:outline-none",
              day_selected: `h-9 w-9 mx-auto rounded-lg text-xs font-semibold text-white ${
                accentColor === "blue" ? "!bg-blue-600" : "!bg-red-600"
              } hover:opacity-90`,
              day_today:
                "text-slate-900 font-bold underline underline-offset-2",
              day_outside: "text-slate-300",
              day_disabled: "opacity-30 cursor-not-allowed",
            }}
          />

          {/* Footer actions */}
          <div className="flex items-center justify-between pt-2 border-t border-slate-100 mt-1">
            <button
              type="button"
              onClick={() => { onChange(null); }}
              className="text-xs text-slate-500 hover:text-slate-700 transition"
            >
              Clear
            </button>
            <button
              type="button"
              onClick={() => { onChange(new Date()); setOpen(false); }}
              className={`text-xs font-semibold text-white px-3 py-1 rounded-lg transition ${accentBtn}`}
            >
              Today
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
