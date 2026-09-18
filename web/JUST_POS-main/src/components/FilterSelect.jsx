import React from "react";

/**
 * FilterSelect — a styled native <select> that matches the project's
 * rounded-xl filter bar aesthetic.
 *
 * Props:
 *   value       - string  currently selected value
 *   onChange    - (e) => void  or  (value) => void
 *   options     - Array<string | { value, label }>
 *   placeholder - string  shown as the first "All / Any" option (default "All")
 *   focusColor  - "red" | "blue"  (default "red")
 *   className   - optional extra classes on the <select>
 */
export default function FilterSelect({
  value,
  onChange,
  options = [],
  placeholder = "All",
  focusColor = "red",
  className = "",
}) {
  const focusClass =
    focusColor === "blue"
      ? "focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
      : "focus:border-red-500 focus:ring-1 focus:ring-red-500";

  const handleChange = (e) => {
    if (typeof onChange === "function") {
      // support both (e) => void and (value) => void signatures
      onChange(e.target ? e : e.target.value);
      if (e.target) onChange(e);
    }
  };

  return (
    <select
      value={value}
      onChange={(e) => onChange(e)}
      className={`w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs text-slate-700 cursor-pointer focus:outline-none appearance-none ${focusClass} ${className}`}
      style={{
        backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%2394a3b8' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'/%3E%3C/svg%3E")`,
        backgroundRepeat: "no-repeat",
        backgroundPosition: "right 10px center",
        paddingRight: "2rem",
      }}
    >
      <option value="">{placeholder}</option>
      {options.map((opt) => {
        const val = typeof opt === "string" ? opt : opt.value;
        const label = typeof opt === "string" ? opt : opt.label;
        return (
          <option key={val} value={val}>
            {label}
          </option>
        );
      })}
    </select>
  );
}
