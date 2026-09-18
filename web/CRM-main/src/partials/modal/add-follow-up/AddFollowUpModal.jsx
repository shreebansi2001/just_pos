import { useState, useEffect } from "react";
import { CustomModal } from "../../../components/custom-modal/CustomModal";
import { GetAllMemberByUserId } from "@/services/apiServices";

const FOLLOW_UP_TYPES = ["Call", "WhatsApp", "Email"];

const HOURS = Array.from({ length: 12 }, (_, i) => String(i + 1).padStart(2, "0")); // 01..12
const MINUTES = Array.from({ length: 12 }, (_, i) => String(i * 5).padStart(2, "0")); // 00,05,...,55

const DEFAULT_FORM = {
  customerName: "",
  assignMember: "",
  assignMemberId: 0,
  description: "",
  followUpType: "Call",
  followUpDate: "",    
  followUpHour: "09",   
  followUpMinute: "00", 
  followUpPeriod: "AM", 
};

export default function AddFollowUpModal({ open, onClose, onSave, defaultCustomerName = "" }) {
  const [form, setForm] = useState(DEFAULT_FORM);
  const [memberList, setMemberList] = useState([]);
  const userId = Number(localStorage.getItem("mainId") || 0);


  useEffect(() => {
    if (open) {
      setForm((prev) => ({ ...prev, customerName: defaultCustomerName }));
    }
  }, [open, defaultCustomerName]);

useEffect(() => {
    if (!window.__assignListCache) {
      window.__assignListCache = {};
    }
    const cache = window.__assignListCache;

    if (cache.list) {
      setMemberList(cache.list.map((m) => ({ id: m.id, label: m.label })));
      return;
    }

    if (!cache.promise) {
      cache.promise = GetAllMemberByUserId(userId).then((res) => {
        const raw = res?.data?.data?.userDetails?.UserDetails ?? [];
        cache.list = raw.map((u) => ({
          id: u.id,
          label: `${u.firstName || ""} ${u.lastName || ""}`.trim() || u.email,
        }));
        return cache.list;
      });
    }

    cache.promise.then((list) => setMemberList(list));
  }, [userId]);

  const handleChange = (field, value) =>
    setForm((prev) => ({ ...prev, [field]: value }));

  const buildCombinedDateTime = () => {
    if (!form.followUpDate) return "";
    let hour24 = parseInt(form.followUpHour, 10);
    if (form.followUpPeriod === "PM" && hour24 !== 12) hour24 += 12;
    if (form.followUpPeriod === "AM" && hour24 === 12) hour24 = 0;
    const hourStr = String(hour24).padStart(2, "0");
    return `${form.followUpDate}T${hourStr}:${form.followUpMinute}:00`;
  };

  const handleSave = () => {
    const combinedDateTime = buildCombinedDateTime();
    const followUpTimeDisplay = form.followUpDate
      ? `${form.followUpHour}:${form.followUpMinute} ${form.followUpPeriod}`
      : "";

    onSave({
      ...form,
      followUpDate: combinedDateTime,        
      followUpTimeDisplay,                   
    });
    setForm(DEFAULT_FORM);
    onClose();
  };

  const footer = (
    <>
      <button onClick={onClose} className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50">
        Close
      </button>
      <button onClick={handleSave} className="ml-2 px-4 py-2 text-sm bg-blue-500 text-white rounded-md hover:bg-blue-600">
        Save
      </button>
    </>
  );

  return (
    <CustomModal open={open} onClose={onClose} title="Add Follow Up" footer={footer} width={520}>
      <div className="flex flex-col gap-4">
        <div className="flex flex-col gap-1">
          <label className="text-sm font-medium">Customer Name</label>
          <input
            type="text"
            placeholder="Enter customer name"
            value={form.customerName}
            onChange={(e) => handleChange("customerName", e.target.value)}
            className="border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-300"
          />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-sm font-medium">Assign Member</label>
          <select
            value={form.assignMemberId}
            onChange={(e) => {
              const selected = memberList.find((m) => m.id === Number(e.target.value));
              handleChange("assignMemberId", Number(e.target.value));
              handleChange("assignMember", selected?.label || "");
            }}
            className="border border-gray-300 rounded-md px-3 py-2 text-sm text-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-300"
          >
            <option value={0}>Select Member</option>
            {memberList.map((m) => (
              <option key={m.id} value={m.id}>{m.label}</option>
            ))}
          </select>
        </div>

        <textarea
          placeholder="Follow Up Description"
          rows={4}
          value={form.description}
          onChange={(e) => handleChange("description", e.target.value)}
          className="border border-gray-300 rounded-md px-3 py-2 text-sm resize-y focus:outline-none focus:ring-2 focus:ring-blue-300"
        />

        <div className="flex flex-col gap-2">
          <label className="text-sm font-medium">Follow Up Type</label>
          <div className="flex border border-gray-300 rounded-md overflow-hidden">
            {FOLLOW_UP_TYPES.map((type) => (
              <button
                key={type}
                type="button"
                onClick={() => handleChange("followUpType", type)}
                className={`flex-1 py-2 text-sm font-medium transition-colors ${
                  form.followUpType === type ? "bg-green-500 text-white" : "bg-white text-gray-500 hover:bg-gray-50"
                }`}
              >
                {type}
              </button>
            ))}
          </div>
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-sm font-medium">Followup Date &amp; Time</label>
          <div className="flex flex-col sm:flex-row gap-2">
            <input
              type="date"
              value={form.followUpDate}
              onChange={(e) => handleChange("followUpDate", e.target.value)}
              className="border border-gray-300 rounded-md px-3 py-2 text-sm text-gray-700 flex-1 focus:outline-none focus:ring-2 focus:ring-blue-300"
            />

            <div className="flex items-center gap-1.5">
              <select
                value={form.followUpHour}
                onChange={(e) => handleChange("followUpHour", e.target.value)}
                className="border border-gray-300 rounded-md px-2 py-2 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-300"
              >
                {HOURS.map((h) => (
                  <option key={h} value={h}>{h}</option>
                ))}
              </select>

              <span className="text-sm text-gray-400">:</span>

              <select
                value={form.followUpMinute}
                onChange={(e) => handleChange("followUpMinute", e.target.value)}
                className="border border-gray-300 rounded-md px-2 py-2 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-300"
              >
                {MINUTES.map((m) => (
                  <option key={m} value={m}>{m}</option>
                ))}
              </select>

              <div className="flex border border-gray-300 rounded-md overflow-hidden">
                {["AM", "PM"].map((p) => (
                  <button
                    key={p}
                    type="button"
                    onClick={() => handleChange("followUpPeriod", p)}
                    className={`px-2.5 py-2 text-sm font-medium transition-colors ${
                      form.followUpPeriod === p ? "bg-blue-500 text-white" : "bg-white text-gray-500 hover:bg-gray-50"
                    }`}
                  >
                    {p}
                  </button>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </CustomModal>
  );
}