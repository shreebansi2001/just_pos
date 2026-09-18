import React, { useState } from "react";
import { DatePicker, Select, InputNumber, Input as AntInput } from "antd";
import {
  User,
  CalendarDays,
  Gift,
  Wallet,
  X,
  Armchair,
} from "lucide-react";
import { CustomModal } from "@/components/custom-modal/CustomModal";

const { TextArea } = AntInput;

const FLOORS = ["Ground", "Mezzanine", "Rooftop"];

const TABLES = [
  { id: "T1", disabled: false },
  { id: "T2", disabled: false },
  { id: "T3", disabled: false },
  { id: "T4", disabled: true }, 
  { id: "T5", disabled: false },
  { id: "T6", disabled: false },
  { id: "T7", disabled: false },
  { id: "T8", disabled: false },
];

const STATUSES = [
  { key: "confirmed", label: "Confirmed" },
  { key: "pending", label: "Pending" },
  { key: "waitlist", label: "Waitlist" },
];

const SectionHeader = ({ icon: Icon, label, tone = "primary" }) => (
  <div className="mb-3 flex items-center gap-2 border-b border-gray-100 pb-2">
    <Icon
  className={`h-4 w-4 ${tone === "primary" ? "text-primary" : "text-[#374151]"}`}
/>

    <span className="text-sm font-semibold text-gray-800">{label}</span>
  </div>
);

const Field = ({ label, children, className = "" }) => (
  <div className={className}>
    <label className="mb-1 block text-xs font-medium text-gray-500">
      {label}
    </label>
    {children}
  </div>
);

export default function NewReservationModal({ open, onClose, onSave ,selectedTable }) {
  const [form, setForm] = useState({
    customerName: "",
    mobile: "",
    email: "",
    date: null,
    time: "18:00",
    guests: 2,
    floor: "Ground",
    table: "T2",
    occasion: "Standard Dining",
    source: "Online Portal",
    requests: "",
    status: "confirmed",
    deposit: 0,
  });

  const set = (key) => (val) => setForm((f) => ({ ...f, [key]: val }));

  const estRevenue = 120.0;
  const minSpendPerPerson = 50.0;

  const inputClass =
    "w-full rounded-md border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-700 placeholder:text-gray-400  focus:bg-white focus:outline-none focus:ring-1 focus:ring-gray/30";

  return (
    <CustomModal
      open={open}
      onClose={onClose}
      title="New Reservation"
      width={950}
      centered
      footer={
        <div className="flex items-center justify-between">
          <button
            onClick={onClose}
            className="text-sm font-medium text-gray-400 hover:text-gray-600"
          >
       
          </button>
          <div className="flex gap-2">
            <button
              onClick={onClose}
              className="rounded-md border border-gray-200 px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-50"
            >
              Cancel
            </button>
            <button
              onClick={() => onSave?.(form)}
              className="rounded-md px-5 py-2 bg-primary text-sm font-semibold text-white shadow-sm hover:opacity-90"
              
            >
              Save Reservation
            </button>
          </div>
        </div>
      }
    >
      <div className="max-h-[70vh] space-y-6 overflow-y-auto pr-1">
        {/* Customer Information */}
        <section>
          <SectionHeader icon={User} label="Customer Information" />
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
            <Field label="Customer Name">
              <input
                className={inputClass}
                placeholder="e.g. Jonathan Smith"
                value={form.customerName}
                onChange={(e) => set("customerName")(e.target.value)}
              />
            </Field>
            <Field label="Mobile Number">
              <input
                className={inputClass}
                placeholder="+1 (555) 000-0000"
                value={form.mobile}
                onChange={(e) => set("mobile")(e.target.value)}
              />
            </Field>
            <Field label="Email Address">
              <input
                className={inputClass}
                placeholder="jonathan@example.com"
                value={form.email}
                onChange={(e) => set("email")(e.target.value)}
              />
            </Field>
          </div>
        </section>

        {/* Reservation Details */}
        <section>
          <SectionHeader icon={CalendarDays} label="Reservation Details" />
          <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
            <Field label="Reservation Date">
              <DatePicker
                className="w-full"
                format="MM/DD/YYYY"
                value={form.date}
                onChange={set("date")}
              />
            </Field>
            <Field label="Reservation Time">
              <Select
                className="w-full"
                value={form.time}
                onChange={set("time")}
                options={["17:00", "17:30", "18:00", "18:30", "19:00"].map(
                  (t) => ({ value: t, label: t })
                )}
              />
            </Field>
            <Field label="Guests">
              <InputNumber
                className="w-full"
                min={1}
                value={form.guests}
                onChange={set("guests")}
              />
            </Field>
            <Field label="Preferred Floor">
              <div className="flex gap-1.5">
                {FLOORS.map((f) => {
                  const active = form.floor === f;
                  return (
                    <button
                      key={f}
                      type="button"
                      onClick={() => set("floor")(f)}
                      className="flex-1 rounded-md border px-2 py-2 text-xs font-medium transition-colors"
                      style={
                        active
                          ? {
                              backgroundColor: "#fbe4d5",
                              borderColor: "#e29a6c",
                              color: "#8c3a1c",
                            }
                          : {
                              backgroundColor: "#fff",
                              borderColor: "#e5e7eb",
                              color: "#6b7280",
                            }
                      }
                    >
                      {f}
                    </button>
                  );
                })}
              </div>
            </Field>
          </div>

          <div className="mt-4">
            <label className="mb-2 block text-xs font-medium text-gray-500">
              Preferred Table
            </label>
            <div className="grid grid-cols-4 gap-2.5 sm:grid-cols-8">
              {TABLES.map((t) => {
                const active = form.table === t.id;
                return (
                  <button
                    key={t.id}
                    type="button"
                    disabled={t.disabled}
                    onClick={() => set("table")(t.id)}
                    className="flex h-14 flex-col items-center justify-center gap-1 rounded-md border text-[11px] font-semibold transition-colors disabled:cursor-not-allowed"
                    style={
                      t.disabled
                        ? {
                            backgroundColor: "#f3f4f6",
                            borderColor: "#e5e7eb",
                            color: "#9ca3af",
                          }
                        : active
                        ? {
                            backgroundColor: "#fbe4d5",
                            borderColor: "#e29a6c",
                            color: "#8c3a1c",
                          }
                        : {
                            backgroundColor: "#fff",
                            borderColor: "#e5e7eb",
                            color: "#6b7280",
                          }
                    }
                  >
                    <Armchair className="h-3.5 w-3.5" />
                    {t.id}
                  </button>
                );
              })}
            </div>
          </div>
        </section>

        <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
          {/* Booking Details */}
          <section>
            <SectionHeader icon={Gift} label="Booking Details" />
            <div className="space-y-4">
              <Field label="Occasion">
                <Select
                  className="w-full"
                  value={form.occasion}
                  onChange={set("occasion")}
                  options={[
                    "Standard Dining",
                    "Birthday",
                    "Anniversary",
                    "Business Meeting",
                  ].map((v) => ({ value: v, label: v }))}
                />
              </Field>
             
              <Field label="Special Requests">
                <TextArea
                  rows={3}
                  placeholder="Dietary restrictions, preferred server, etc."
                  value={form.requests}
                  onChange={(e) => set("requests")(e.target.value)}
                />
              </Field>
            </div>
          </section>

          {/* Status & Finance */}
          <section>
            <SectionHeader icon={Wallet} label="Status & Finance" tone="dark" />
            <div className="space-y-4">
              <Field label="Reservation Status">
                <div className="flex gap-1.5">
                  {STATUSES.map((s) => {
                    const active = form.status === s.key;
                    return (
                      <button
                        key={s.key}
                        type="button"
                        onClick={() => set("status")(s.key)}
                        className="flex-1 rounded-md border px-2 py-2 text-xs font-semibold transition-colors"
                        style={
                          active
                            ? {
                                backgroundColor: "#1f3d2e",
                                borderColor: "#1f3d2e",
                                color: "#fff",
                              }
                            : {
                                backgroundColor: "#fff",
                                borderColor: "#e5e7eb",
                                color: "#6b7280",
                              }
                        }
                      >
                        {s.label}
                      </button>
                    );
                  })}
                </div>
              </Field>

             

             
            </div>
          </section>
        </div>
      </div>
    </CustomModal>
  );
}   