"use client";

import React, { useState, useRef, useEffect } from "react";
import Swal from "sweetalert2";
import {
  ChevronRight, Pencil, Trash2, ChevronDown,
} from "lucide-react";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
  getUserById,
  fetchstatebycontry,
  fetchcitybystateid,
  updateusermaster
  // updateUserProfile,   // 👈 plug in your actual update endpoint here
} from "@/services/apiServices";

const DEFAULT_COUNTRY_ID = 1;

const TABS = ["Profile"];

const filterByQuery = (list, query) => {
  if (!query) return list;
  const q = query.toLowerCase();
  return list.filter((o) => o.label.toLowerCase().includes(q));
};

const useDebouncedValue = (value, delay = 300) => {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => {
    const t = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(t);
  }, [value, delay]);
  return debounced;
};

/* Same async-search dropdown pattern used in AddLead.jsx */
const AsyncSearchSelect = ({
  label, placeholder = "Select", disabled = false, disabledMessage,
  value, onChange, fetchOptions, className,
}) => {
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState("");
  const [options, setOptions] = useState([]);
  const [loading, setLoading] = useState(false);
  const wrapperRef = useRef(null);
  const debouncedQuery = useDebouncedValue(query, 300);

  useEffect(() => {
    const handleOutside = (e) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener("mousedown", handleOutside);
    return () => document.removeEventListener("mousedown", handleOutside);
  }, []);

  useEffect(() => {
    if (!open || disabled) return;
    let active = true;
    setLoading(true);
    fetchOptions(debouncedQuery)
      .then((opts) => { if (active) setOptions(opts || []); })
      .catch(() => { if (active) setOptions([]); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [open, debouncedQuery, disabled]);

  return (
    <div className={cn("relative", className)} ref={wrapperRef}>
      <label className="block text-sm text-gray-700 mb-1.5">{label}</label>
      <button
        type="button"
        disabled={disabled}
        onClick={() => setOpen((p) => !p)}
        className={cn(
          "w-full inline-flex items-center justify-between gap-2 rounded-lg border border-gray-200 bg-gray-50 px-3.5 h-11 text-sm text-left transition-colors",
          disabled && "text-gray-400 cursor-not-allowed"
        )}
      >
        <span className={cn("truncate", !value && "text-gray-400")}>
          {value?.label || (disabled ? disabledMessage || placeholder : placeholder)}
        </span>
        <ChevronDown className="h-3.5 w-3.5 text-gray-400 shrink-0" />
      </button>

      {open && !disabled && (
        <div className="absolute z-20 mt-1.5 w-full rounded-xl border border-gray-200 bg-white shadow-lg">
          <div className="p-2 border-b border-gray-100">
            <input
              autoFocus
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Type to search..."
              className="w-full h-8 px-2.5 text-sm rounded-md border border-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-100"
            />
          </div>
          <div className="max-h-56 overflow-y-auto p-1.5">
            {loading ? (
              <div className="px-3 py-2 text-sm text-gray-400">Loading...</div>
            ) : options.length === 0 ? (
              <div className="px-3 py-2 text-sm text-gray-400">No results found</div>
            ) : (
              options.map((opt) => (
                <button
                  key={opt.id}
                  type="button"
                  onClick={() => { onChange?.(opt); setOpen(false); setQuery(""); }}
                  className="w-full text-left rounded-lg px-3 py-1.5 text-sm text-gray-800 hover:bg-gray-50 transition-colors"
                >
                  {opt.label}
                </button>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
};

const FormField = ({ label, value, onChange, editable, placeholder, type = "text" }) => (
  <div>
    <label className="block text-sm text-gray-700 mb-1.5">{label}</label>
    <input
      type={type}
      value={value || ""}
      onChange={(e) => onChange?.(e.target.value)}
      readOnly={!editable}
      placeholder={placeholder}
      className={cn(
        "w-full h-11 px-3.5 rounded-lg border border-gray-200 text-sm text-gray-800 placeholder:text-gray-400",
        editable ? "bg-white focus:outline-none focus:ring-2 focus:ring-blue-100" : "bg-gray-50 cursor-default"
      )}
    />
  </div>
);

const DEFAULT_PROFILE = {
  firstName: "",
  lastName: "",
  email: "",
  phoneNumber: "",
  companyName: "",
  companyEmailId: "",
  officeNumber: "",
  gstNumber: "",
  panNumber: "",
  address: "",
  companyAddress: "",
  country: null,
  state: null,
  city: null,
  planName: "Lite",
  planId: null,
  roleId: null,
  accountId: "",
  language: "English",
  roleName: "",
  image: "",
};

const AccountUserProfilePage = () => {
  const [activeTab, setActiveTab] = useState("Profile");
  const [isEditing, setIsEditing] = useState(false);
  const [profileData, setProfileData] = useState(DEFAULT_PROFILE);
  const [isSaving, setIsSaving] = useState(false);

  const userMasterId = Number(localStorage.getItem("mainId") || 0);

const fetchUserProfile = async () => {
  if (!userMasterId) return;
  try {
    const res = await getUserById(userMasterId);
    const user = res?.data?.data?.["User Details"]?.[0];
    if (user) {
      const b = user.userBasicDetails || {};
      setProfileData((prev) => ({
        ...prev,
        firstName: user.firstName || "",
        lastName: user.lastName || "",
        email: user.email || "",
        phoneNumber: user.contactNo || "",
        companyAddress: b.address || "",
        companyName: b.companyName || "",
        companyEmailId: b.companyEmail || user.email || "",
        officeNumber: b.officeNo || "",
        gstNumber: user.gstNumber || "",
        panNumber: user.panNumber || "",
        address: b.address || "",
        country: b.country
          ? { id: b.country.id, label: b.country.name, code: b.country.code || "91" }
          : prev.country,
        state: b.state ? { id: b.state.id, label: b.state.name } : prev.state,
        city: b.city ? { id: b.city.id, label: b.city.name } : prev.city,
        planName: user.plan?.name || "Lite",
        planId: user.plan?.id ?? prev.planId,
        roleId: b.role?.id ?? prev.roleId,
        accountId: user.userCode || "ID-45453423",
        language: "English",
        roleName: b.role?.name || "",
        image: user.logo || prev.image,
      }));
    }
  } catch (error) {
    console.error("Failed to fetch user profile:", error);
  }
};

  useEffect(() => {
    fetchUserProfile();
  }, [userMasterId]);

  const fetchStateOptions = async (query) => {
    const res = await fetchstatebycontry(DEFAULT_COUNTRY_ID, query);
    const list = res?.data?.data?.["state Details"] ?? [];
    return list.map((s) => ({ id: s.id, label: s.name }));
  };

  const fetchCityOptions = async (query) => {
    if (!profileData.state?.id) return [];
    const res = await fetchcitybystateid(profileData.state.id, query);
    const list = res?.data?.data?.["City Details"] ?? [];
    return list.map((c) => ({ id: c.id, label: c.name }));
  };

  const setField = (field, value) =>
    setProfileData((prev) => ({ ...prev, [field]: value }));

const handleSave = async () => {
  if (!profileData.planId || !profileData.roleId) {
    console.error("Missing planId or roleId, cannot save");
    return;
  }

  const rawCode = profileData.country?.code || "91";
  const countryCode = rawCode.startsWith("+") ? rawCode : `+${rawCode}`;

  const payload = {
    firstName: profileData.firstName,
    lastName: profileData.lastName,
    email: profileData.email,
    password: "",
    confirmPassword: "",
    contactNo: profileData.phoneNumber,
    companyName: profileData.companyName,
    companyEmail: profileData.companyEmailId,
    address: profileData.address,
    officeNo: profileData.officeNumber,
    gstNumber: profileData.gstNumber || "",
    panNumber: profileData.panNumber || "",
    countryId: profileData.country?.id || DEFAULT_COUNTRY_ID,
    countryCode,
    stateId: profileData.state?.id || 0,
    cityId: profileData.city?.id || 0,
    planId: profileData.planId,
    roleId: profileData.roleId,
    remarks: "",
    isAttendanceLeaveAccess: true,
    isTaskAccess: true,
    clientId: 0,
    reportingManagerId: 0,
  };

  setIsSaving(true);
  try {
    const res = await updateusermaster(userMasterId, payload);
    const success = res?.data?.success;
    const msg = res?.data?.msg || (success ? "Profile updated successfully" : "Update failed");

    if (success) {
      Swal.fire({
        icon: "success",
        title: msg,
        timer: 2000,
        showConfirmButton: false,
      });
      setIsEditing(false);
      await fetchUserProfile();
    } else {
      Swal.fire({
        icon: "error",
        title: msg,
      });
    }
  } catch (err) {
    console.error("Failed to save profile:", err);
    const errMsg = err?.response?.data?.msg || "Update failed. Please try again.";
    Swal.fire({
      icon: "error",
      title: errMsg,
    });
  } finally {
    setIsSaving(false);
  }
};

  return (
    <div className="min-h-screen">
      <div className="px-6 py-4 flex items-center justify-between">
        <h1 className="text-xl font-bold text-gray-900">User Profile</h1>
        <div className="flex items-center gap-2 text-sm">
          <span className="text-primary font-medium cursor-pointer">Dashboard</span>
          <ChevronRight className="h-3.5 w-3.5 text-gray-400" />
          <span className="text-gray-500">User Profile</span>
        </div>
      </div>

      <main className="px-6 pb-8 grid grid-cols-1 lg:grid-cols-[300px_1fr] gap-5">

        {/* Sidebar */}
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 flex flex-col items-center text-center h-fit">
          {profileData.image ? (
            <img src={profileData.image} alt="Logo" className="h-16 mb-3 object-contain" />
          ) : (
            <div className="h-16 w-16 mb-3 rounded-full bg-gray-100" />
          )}
          <h2 className="text-lg font-bold text-gray-900">{profileData.companyName}</h2>
          <p className="text-sm text-gray-400 mb-4">{profileData.roleName}</p>

          <div className="w-full border-t border-dashed border-gray-200 pt-4 text-left">
            <p className="text-sm font-semibold text-gray-900 mb-2">Details</p>
            <div className="flex items-center gap-3 mb-3">
              <span className="text-xs font-medium bg-gray-900 text-white px-3 py-1.5 rounded-full">
                {profileData.planName === "Lite" ? "Onboarding Plan" : profileData.planName}
              </span>
            </div>
            <div className="rounded-xl bg-blue-50 p-3 text-center mb-4">
              <p className="text-sm font-semibold text-gray-800">Upgrade Your Plan</p>
              <p className="text-xs text-gray-500 mt-0.5 mb-2">
                Go Pro for more Features and better support.
              </p>
              <Button size="sm" className="w-full bg-primary">Upgrade Now</Button>
            </div>
          </div>

          <div className="w-full border-t border-dashed border-gray-200 pt-4 text-left space-y-3">
            <div>
              <p className="text-sm font-semibold text-gray-900">Account ID</p>
              <p className="text-sm text-gray-400">{profileData.accountId}</p>
            </div>
            <div>
              <p className="text-sm font-semibold text-gray-900">Company Address</p>
              <p className="text-sm text-gray-400">{profileData.companyAddress || "—"}</p>
            </div>
            <div>
              <p className="text-sm font-semibold text-gray-900">Language</p>
              <p className="text-sm text-gray-400">{profileData.language}</p>
            </div>
          </div>

          {/* <button
            type="button"
            className="mt-5 w-full flex items-center justify-center gap-2 rounded-lg bg-rose-50 text-rose-600 text-sm font-medium py-2.5 hover:bg-rose-100 transition-colors"
          >
            <Trash2 className="w-4 h-4" />
            Delete Account
          </button> */}
        </div>

        {/* Right panel */}
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
          <div className="flex items-center justify-between border-b border-gray-100 mb-6">
            <div className="flex items-center gap-6">
              {TABS.map((tab) => (
                <button
                  key={tab}
                  type="button"
                  onClick={() => setActiveTab(tab)}
                  className={cn(
                    "pb-3 text-sm font-medium transition-colors border-b-2 -mb-px",
                    activeTab === tab
                      ? "text-primary border-primary"
                      : "text-gray-400 border-transparent hover:text-gray-600"
                  )}
                >
                  {tab}
                </button>
              ))}
            </div>
          {activeTab === "Profile" && (
  isEditing ? (
    <div className="flex items-center gap-2 mb-2">
      <Button size="sm" variant="outline" onClick={() => setIsEditing(false)}>Cancel</Button>
      <Button size="sm" className="bg-primary" onClick={handleSave} disabled={isSaving}>
        {isSaving ? "Saving..." : "Save"}
      </Button>
    </div>
  ) : (
    <Button size="sm" variant="outline" className="mb-2" onClick={() => setIsEditing(true)}>
      <Pencil className="w-3.5 h-3.5 mr-1.5" />
      Edit
    </Button>
  )
)}
          </div>

          {activeTab === "Profile" && (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-5">
              <FormField label="First Name" editable={isEditing}
                value={profileData.firstName} onChange={(v) => setField("firstName", v)} />
              <FormField label="Last Name" editable={isEditing}
                value={profileData.lastName} onChange={(v) => setField("lastName", v)} />

              <FormField label="Email" editable={isEditing}
                value={profileData.email} onChange={(v) => setField("email", v)} />
              <FormField label="Phone Number" editable={isEditing}
                value={profileData.phoneNumber} onChange={(v) => setField("phoneNumber", v)} />

              <FormField label="Company Name" editable={isEditing}
                value={profileData.companyName} onChange={(v) => setField("companyName", v)} />
              <FormField label="Company Email ID" editable={isEditing}
                value={profileData.companyEmailId} onChange={(v) => setField("companyEmailId", v)} />

              <FormField label="Office Number" editable={isEditing}
                value={profileData.officeNumber} onChange={(v) => setField("officeNumber", v)} />
              <FormField label="GST Number" editable={isEditing} placeholder="Enter your GST number.."
                value={profileData.gstNumber} onChange={(v) => setField("gstNumber", v)} />

              <FormField label="PAN Number" editable={isEditing} placeholder="Enter your Pan number.."
                value={profileData.panNumber} onChange={(v) => setField("panNumber", v)} />
              <FormField label="Address" editable={isEditing} placeholder="Enter your address"
                value={profileData.address} onChange={(v) => setField("address", v)} />

              <AsyncSearchSelect
                label="Country"
                placeholder="Select country"
                value={profileData.country}
                disabled={!isEditing}
fetchOptions={async () => [{ id: DEFAULT_COUNTRY_ID, label: "India", code: "91" }]}                onChange={(opt) => setField("country", opt)}
              />
              <AsyncSearchSelect
                label="State"
                placeholder="Select state"
                value={profileData.state}
                disabled={!isEditing}
                fetchOptions={fetchStateOptions}
                onChange={(opt) => {
                  setField("state", opt);
                  setField("city", null);
                }}
              />
              <AsyncSearchSelect
                label="City"
                placeholder="Select city"
                value={profileData.city}
                disabled={!isEditing || !profileData.state?.id}
                disabledMessage={!profileData.state?.id ? "Select a state first" : undefined}
                fetchOptions={fetchCityOptions}
                onChange={(opt) => setField("city", opt)}
              />
            </div>
          )}

          {activeTab !== "Profile" && (
            <div className="flex items-center justify-center h-40 text-sm text-gray-400">
              {activeTab} — coming soon
            </div>
          )}
        </div>
      </main>
    </div>
  );
};

export default AccountUserProfilePage;