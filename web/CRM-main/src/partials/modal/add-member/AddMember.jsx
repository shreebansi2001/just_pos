"use client";

import { useEffect, useState } from "react";
import {
  UserPlus,
  X,
  ChevronDown,
  MapPin,
  Globe2,
  Building2,
  Phone,
  Mail,
  Lock,
  ShieldCheck,
  Loader2,
  Save,
} from "lucide-react";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { CustomModal } from "../../../components/custom-modal/CustomModal";
import {
  GetAllRole,
  AddMember as AddMemberapi,
  UpdateMember,
  getUserById,
  fetchCountries,
  fetchStatesByCountry,
  fetchCitiesByState,
} from "@/services/apiServices";
import Swal from "sweetalert2";

// ---- small primitives -----------------------------------------------------

const FieldLabel = ({ children, required }) => (
  <label className="block text-sm font-medium text-gray-700 mb-1.5">
    {children}
    {required && <span className="text-rose-500 ml-0.5">*</span>}
  </label>
);

// Generic styled dropdown matching the StatusTypeSelect pattern
const SimpleSelect = ({
  value,
  onChange,
  options,
  placeholder,
  icon: Icon,
  disabled,
}) => {
  const [open, setOpen] = useState(false);
  const selected = options.find((o) => String(o.id) === String(value));

  return (
    <div className="relative">
      <button
        type="button"
        disabled={disabled}
        onClick={() => !disabled && setOpen((prev) => !prev)}
        className={cn(
          "w-full inline-flex items-center justify-between gap-2 rounded-lg border border-gray-300 bg-white px-3.5 h-11 text-sm text-gray-900 transition-colors",
          disabled && "bg-gray-50 text-gray-400 cursor-not-allowed",
        )}
      >
        <span className="inline-flex items-center gap-2 truncate">
          {Icon && <Icon className="h-4 w-4 text-gray-400 shrink-0" />}
          <span className={cn(!selected && "text-gray-400")}>
            {selected ? selected.name : placeholder}
          </span>
        </span>
        <ChevronDown className="h-3.5 w-3.5 text-gray-400 shrink-0" />
      </button>

      {open && !disabled && (
        <div className="absolute z-20 mt-1.5 w-full max-h-56 overflow-y-auto rounded-xl border border-gray-200 bg-white p-1.5 shadow-lg">
          {options.length === 0 && (
            <p className="px-3 py-2 text-sm text-gray-400">No options</p>
          )}
          {options.map((opt) => (
            <button
              key={opt.id}
              type="button"
              onClick={() => {
                onChange(opt.id);
                setOpen(false);
              }}
              className="w-full flex items-center gap-2 rounded-lg px-3 py-2 text-sm text-gray-800 hover:bg-gray-50 transition-colors"
            >
              {opt.name}
            </button>
          ))}
        </div>
      )}
    </div>
  );
};

// ---- modal ------------------------------------------------------------------

const AddMember = ({
  isModalOpen,
  setIsModalOpen,
  refreshData = () => {},
  selectedMember,
}) => {
  const [loading, setLoading] = useState(false);

  const [countries, setCountries] = useState([]);
  const [states, setStates] = useState([]);
  const [cities, setCities] = useState([]);

  const [roles, setRoles] = useState([]);
  const [selectedRole, setSelectedRole] = useState("");

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    companyEmail: "",
    contactNo: "",
    countryId: "",
    stateId: "",
    cityId: "",
    password: "",
    confirmpassword: "",
  });

  const isEditing = Boolean(formData.memberid);
  const Id = localStorage.getItem("userId");

  const fetchRoles = async () => {
    try {
      const res = await GetAllRole(Id);
      setRoles(res?.data?.data?.["Role Details"] || []);
    } catch {
      setRoles([]);
    }
  };

  useEffect(() => {
    if (isModalOpen) fetchRoles();
  }, [isModalOpen]);

  useEffect(() => {
    if (isModalOpen) {
      fetchCountries("")
        .then((res) => setCountries(res?.data?.data?.["Country Details"] || []))
        .catch(() => setCountries([]));
    }
  }, [isModalOpen]);

  useEffect(() => {
    if (formData.countryId) {
      fetchStatesByCountry(formData.countryId, "")
        .then((res) => setStates(res?.data?.data?.["state Details"] || []))
        .catch(() => setStates([]));
    }
  }, [formData.countryId]);

  useEffect(() => {
    if (formData.stateId) {
      fetchCitiesByState(formData.stateId, "")
        .then((res) => setCities(res?.data?.data?.["City Details"] || []))
        .catch(() => setCities([]));
    }
  }, [formData.stateId]);

  useEffect(() => {
    const fetchMember = async () => {
      if (!selectedMember?.id) {
        setFormData({
          firstName: "",
          lastName: "",
          email: "",
          companyEmail: "",
          contactNo: "",
          countryId: "",
          stateId: "",
          cityId: "",
          password: "",
          confirmpassword: "",
        });
        setSelectedRole("");
        return;
      }

      try {
        setLoading(true);
        const res = await getUserById(selectedMember.id);
        const member = res?.data?.data?.["User Details"]?.[0];
        if (!member) return;

        setFormData({
          memberid: member.id ?? "",
          firstName: member.firstName ?? "",
          lastName: member.lastName ?? "",
          email: member.email ?? "",
          companyEmail: member.userBasicDetails?.companyEmail ?? "",
          contactNo: member.contactNo ?? "",
          countryId: member.userBasicDetails?.country?.id ?? "",
          stateId: member.userBasicDetails?.state?.id ?? "",
          cityId: member.userBasicDetails?.city?.id ?? "",
        });
        setSelectedRole(member.userBasicDetails?.role?.id || "");
      } catch (err) {
        console.error("Error fetching user:", err);
      } finally {
        setLoading(false);
      }
    };

    if (isModalOpen) fetchMember();
  }, [selectedMember, isModalOpen]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const validateForm = () => {
    if (!formData.lastName?.trim()) return "Last name is required";
    if (!formData.contactNo) return "Contact number is required";
    if (!/^\d{10}$/.test(formData.contactNo)) return "Contact number must be exactly 10 digits";
    if (!formData.email?.trim()) return "Email is required";
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) return "Invalid email format";
    if (!formData.countryId) return "Country is required";
    if (!formData.stateId) return "State is required";
    if (!formData.cityId) return "City is required";
    if (!selectedRole) return "Role is required";

    if (!isEditing) {
      if (!formData.password) return "Password is required";
      if (!formData.confirmpassword) return "Confirm password is required";
      if (formData.password !== formData.confirmpassword)
        return "Password and confirm password do not match";
    }

    return null;
  };

  const handleModalClose = () => setIsModalOpen(false);

  const handleSave = async () => {
    try {
      const validationError = validateForm();
      if (validationError) {
        Swal.fire({ icon: "warning", title: "Missing information", text: validationError });
        return;
      }

      setLoading(true);

      const res = await getUserById(Id);
      const user_Data = res?.data?.data?.["User Details"]?.[0];
      if (!user_Data) {
        Swal.fire({ icon: "error", title: "Error", text: "User not found" });
        return;
      }

      const payload = {
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        companyEmail: formData.companyEmail,
        contactNo: formData.contactNo,
        countryCode: "+91",
        cityId: Number(formData.cityId),
        stateId: Number(formData.stateId),
        countryId: Number(formData.countryId),
        reportingManagerId: 0,
        clientId: user_Data.id,
        planId: user_Data?.plan?.id || null,
        roleId: Number(selectedRole),
        password: isEditing ? null : formData.password,
        confirmPassword: isEditing ? null : formData.confirmpassword,
      };

      const apiRes = isEditing
        ? await UpdateMember(formData.memberid, payload)
        : await AddMemberapi(payload);

      if (apiRes?.data?.success) {
        Swal.fire({
          icon: "success",
          title: isEditing ? "Member updated" : "Member added",
          text: apiRes?.data?.msg || apiRes?.data?.message,
          timer: 1500,
          showConfirmButton: false,
        });
        handleModalClose();
        refreshData(!isEditing);
      } else {
        Swal.fire({
          icon: "error",
          title: "Failed",
          text: apiRes?.data?.msg || apiRes?.data?.message,
        });
      }
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: err?.response?.data?.msg || err?.response?.data?.message || "Something went wrong!",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <CustomModal
      open={isModalOpen}
      onClose={handleModalClose}
      width={760}
      footer={
        <div className="flex items-center justify-end gap-2">
          <Button variant="outline" size="md" onClick={handleModalClose} disabled={loading}>
            Cancel
          </Button>
          <Button
            size="md"
            className="bg-primary hover:bg-primary/90"
            onClick={handleSave}
            disabled={loading}
          >
            {loading ? (
              <Loader2 className="w-4 h-4 mr-2 animate-spin" />
            ) : (
              <Save className="w-4 h-4 mr-2" />
            )}
            {isEditing ? "Update Member" : "Save Member"}
          </Button>
        </div>
      }
    >
      {/* Header */}
      <div className="-mx-6 -mt-6 mb-5 rounded-t-2xl px-6 py-5 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-11 h-11 rounded-xl bg-primary flex items-center justify-center shrink-0">
            <UserPlus className="h-5 w-5 text-white" />
          </div>
          <div>
            <h2 className="text-lg font-semibold text-black leading-tight">
              {isEditing ? "Edit Member" : "Create Member"}
            </h2>
            <p className="text-sm text-black">
              {isEditing ? "Update this member's details" : "Add a new member to your team"}
            </p>
          </div>
        </div>
        <button
          type="button"
          onClick={handleModalClose}
          className="w-8 h-8 rounded-full bg-black flex items-center justify-center text-white transition-colors shrink-0"
        >
          <X className="h-4 w-4" />
        </button>
      </div>

      {/* Body */}
      <div className="flex flex-col gap-5 max-h-[65vh] overflow-y-auto px-0.5">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <FieldLabel>First Name</FieldLabel>
            <Input
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              placeholder="First name"
              className="h-11 text-sm border-gray-300"
            />
          </div>
          <div>
            <FieldLabel required>Last Name</FieldLabel>
            <Input
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              placeholder="Last name"
              className="h-11 text-sm border-gray-300"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <FieldLabel required>Country</FieldLabel>
            <SimpleSelect
              value={formData.countryId}
              onChange={(val) =>
                setFormData((prev) => ({ ...prev, countryId: val, stateId: "", cityId: "" }))
              }
              options={countries}
              placeholder="Select country"
              icon={Globe2}
            />
          </div>
          <div>
            <FieldLabel required>State</FieldLabel>
            <SimpleSelect
              value={formData.stateId}
              onChange={(val) =>
                setFormData((prev) => ({ ...prev, stateId: val, cityId: "" }))
              }
              options={states}
              placeholder="Select state"
              icon={MapPin}
              disabled={!formData.countryId}
            />
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <FieldLabel required>City</FieldLabel>
            <SimpleSelect
              value={formData.cityId}
              onChange={(val) => setFormData((prev) => ({ ...prev, cityId: val }))}
              options={cities}
              placeholder="Select city"
              icon={Building2}
              disabled={!formData.stateId}
            />
          </div>
          <div>
            <FieldLabel required>Mobile No</FieldLabel>
            <div className="relative flex items-center">
              <Phone className="absolute left-3 h-4 w-4 text-gray-400" />
              <Input
                name="contactNo"
                value={formData.contactNo}
                onChange={(e) => {
                  if (/^\d{0,10}$/.test(e.target.value)) handleChange(e);
                }}
                placeholder="10-digit mobile number"
                className="h-11 pl-9 text-sm border-gray-300"
              />
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <FieldLabel required>Role</FieldLabel>
            <SimpleSelect
              value={selectedRole}
              onChange={(val) => setSelectedRole(val)}
              options={roles}
              placeholder="Select role"
              icon={ShieldCheck}
            />
          </div>
          <div>
            <FieldLabel required>Email</FieldLabel>
            <div className="relative flex items-center">
              <Mail className="absolute left-3 h-4 w-4 text-gray-400" />
              <Input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="Email address"
                className="h-11 pl-9 text-sm border-gray-300"
              />
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <FieldLabel>Office Email</FieldLabel>
            <div className="relative flex items-center">
              <Mail className="absolute left-3 h-4 w-4 text-gray-400" />
              <Input
                type="email"
                name="companyEmail"
                value={formData.companyEmail}
                onChange={handleChange}
                placeholder="Office email"
                className="h-11 pl-9 text-sm border-gray-300"
              />
            </div>
          </div>

          {!isEditing && (
            <div>
              <FieldLabel required>Password</FieldLabel>
              <div className="relative flex items-center">
                <Lock className="absolute left-3 h-4 w-4 text-gray-400" />
                <Input
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="Password"
                  className="h-11 pl-9 text-sm border-gray-300"
                />
              </div>
            </div>
          )}
        </div>

        {!isEditing && (
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <FieldLabel required>Confirm Password</FieldLabel>
              <div className="relative flex items-center">
                <Lock className="absolute left-3 h-4 w-4 text-gray-400" />
                <Input
                  name="confirmpassword"
                  value={formData.confirmpassword}
                  onChange={handleChange}
                  placeholder="Confirm password"
                  className="h-11 pl-9 text-sm border-gray-300"
                />
              </div>
            </div>
          </div>
        )}
      </div>
    </CustomModal>
  );
};

export default AddMember;