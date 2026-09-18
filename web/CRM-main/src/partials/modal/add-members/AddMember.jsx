import { useEffect, useState } from "react";
import { CustomModal } from "../../../components/custom-modal/CustomModal";
import AddRole from "@/partials/modals/add-role-master/AddRole";

import {
  GetAllRole,
  AddMember as AddMemberapi,
  UpdateMember,
  getUserById,
  fetchCountries,
  fetchStatesByCountry,
  fetchCitiesByState,
} from "@/services/apiServices";
import Select from "react-select";
import Swal from "sweetalert2";

const AddMember = ({
  isModalOpen,
  setIsModalOpen,
  refreshData = () => {},
  selectedMember,
}) => {
  const [openAddRoleModal, setOpenAddRoleModal] = useState(false);
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
    if (!openAddRoleModal && isModalOpen) fetchRoles();
  }, [openAddRoleModal]);

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

    if (!formData.memberid) {
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
        Swal.fire({ icon: "error", title: "Validation Error", text: validationError });
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
        password: formData.memberid ? null : formData.password,
        confirmPassword: formData.memberid ? null : formData.confirmpassword,
      };

      const apiRes = formData.memberid
        ? await UpdateMember(formData.memberid, payload)
        : await AddMemberapi(payload);

      if (apiRes?.data?.success) {
        Swal.fire({
          icon: "success",
          title: "Success",
          text: apiRes?.data?.msg || apiRes?.data?.message,
        });
        handleModalClose();
        refreshData(!formData.memberid);
      } else {
        Swal.fire({
          icon: "error",
          title: "Error",
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
console.log("AddMember render — isModalOpen:", isModalOpen);
return (
  isModalOpen && (
    <div style={{position:'fixed', inset:0, background:'rgba(0,0,0,0.5)', zIndex:9999, display:'flex', alignItems:'center', justifyContent:'center'}}>
      <div style={{background:'white', padding:24, borderRadius:8}}>
        TEST MODAL — isModalOpen is true
        <button onClick={handleModalClose}>Close</button>
      </div>
    </div>
  )
);
  return (
    
    isModalOpen && (
      
      <CustomModal
      
        open={isModalOpen}
        onClose={handleModalClose}
        width="min(1000px, 95vw)"
        title={selectedMember ? "Edit Member" : "Create Member"}
        footer={[
          <div
            className="flex flex-col-reverse sm:flex-row sm:justify-between gap-2 w-full"
            key="footer-buttons"
          >
            <button className="btn btn-light flex justify-center w-full sm:w-auto" onClick={handleModalClose}>
              Cancel
            </button>
            <button
              className="btn btn-success flex justify-center w-full sm:w-auto"
              onClick={handleSave}
              disabled={loading}
            >
              {loading ? "Please wait..." : selectedMember ? "Update" : "Save"}
            </button>
          </div>,
        ]}
      >
        {loading && (
          <div className="absolute inset-0 bg-white/60 flex items-center justify-center z-50">
            <div className="loader"></div>
          </div>
        )}

        <div className="flex flex-col gap-y-2 max-h-[70vh] sm:max-h-none overflow-y-auto px-1">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-4 gap-y-2">
            <div className="flex flex-col">
              <label className="form-label">First Name</label>
              <input
                type="text"
                name="firstName"
                value={formData.firstName}
                onChange={handleChange}
                placeholder="First name"
                className="input"
              />
            </div>
            <div className="flex flex-col">
              <label className="form-label">Last Name <span className="text-red-500">*</span></label>
              <input
                type="text"
                name="lastName"
                value={formData.lastName}
                onChange={handleChange}
                placeholder="Last name"
                className="input"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-4 gap-y-2">
            <div className="flex flex-col">
              <label className="form-label">Country <span className="text-red-500">*</span></label>
              <Select
                options={countries.map((c) => ({ value: c.id, label: c.name }))}
                value={
                  countries.find((c) => c.id === formData.countryId)
                    ? { value: formData.countryId, label: countries.find((c) => c.id === formData.countryId)?.name }
                    : null
                }
                onChange={(selected) =>
                  setFormData((prev) => ({ ...prev, countryId: selected?.value || "", stateId: "", cityId: "" }))
                }
                placeholder="Select country..."
                isClearable
              />
            </div>
            <div className="flex flex-col">
              <label className="form-label">State <span className="text-red-500">*</span></label>
              <Select
                options={states.map((s) => ({ value: s.id, label: s.name }))}
                value={
                  states.find((s) => s.id === formData.stateId)
                    ? { value: formData.stateId, label: states.find((s) => s.id === formData.stateId)?.name }
                    : null
                }
                onChange={(selected) =>
                  setFormData((prev) => ({ ...prev, stateId: selected?.value || "", cityId: "" }))
                }
                placeholder="Select state..."
                isClearable
                isDisabled={!formData.countryId}
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-4 gap-y-2">
            <div className="flex flex-col">
              <label className="form-label">City <span className="text-red-500">*</span></label>
              <Select
                options={cities.map((ct) => ({ value: ct.id, label: ct.name }))}
                value={
                  cities.find((ct) => ct.id === formData.cityId)
                    ? { value: formData.cityId, label: cities.find((ct) => ct.id === formData.cityId)?.name }
                    : null
                }
                onChange={(selected) => setFormData((prev) => ({ ...prev, cityId: selected?.value || "" }))}
                placeholder="Select city..."
                isClearable
                isDisabled={!formData.stateId}
              />
            </div>
            <div className="flex flex-col">
              <label className="form-label">Mobile No <span className="text-red-500">*</span></label>
              <input
                type="text"
                name="contactNo"
                value={formData.contactNo}
                onChange={(e) => {
                  if (/^\d{0,10}$/.test(e.target.value)) handleChange(e);
                }}
                className="input"
                placeholder="Mobile No"
              />
            </div>
          </div>

          <div className="flex flex-col">
            <label className="form-label">Role <span className="text-red-500">*</span></label>
            <div className="relative flex items-center">
              <select
                className="select w-full pr-12"
                value={selectedRole}
                onChange={(e) => setSelectedRole(e.target.value)}
              >
                <option value="">Select Role</option>
                {roles.map((role) => (
                  <option key={role.id} value={role.id}>{role.name}</option>
                ))}
              </select>
              <button
                type="button"
                onClick={() => setOpenAddRoleModal(true)}
                className="absolute right-2 top-1/2 -translate-y-1/2 w-8 h-8 bg-primary text-white rounded-full flex items-center justify-center hover:bg-primary/90 transition-colors z-10"
                title="Add New Role"
              >
                <i className="ki-filled ki-plus text-xs"></i>
              </button>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-4 gap-y-2">
            <div className="flex flex-col">
              <label className="form-label">Email <span className="text-red-500">*</span></label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                className="input"
                placeholder="Email"
              />
            </div>
            <div className="flex flex-col">
              <label className="form-label">Office Email</label>
              <input
                type="email"
                name="companyEmail"
                value={formData.companyEmail}
                onChange={handleChange}
                className="input"
                placeholder="Office Email"
              />
            </div>
          </div>

          {!formData.memberid && (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-4 gap-y-2">
              <div className="flex flex-col">
                <label className="form-label">Password <span className="text-red-500">*</span></label>
                <input
                  type="text"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  className="input"
                  placeholder="Password"
                />
              </div>
              <div className="flex flex-col">
                <label className="form-label">Confirm Password <span className="text-red-500">*</span></label>
                <input
                  type="text"
                  name="confirmpassword"
                  value={formData.confirmpassword}
                  onChange={handleChange}
                  className="input"
                  placeholder="Confirm password"
                />
              </div>
            </div>
          )}
        </div>

        <AddRole
          isModalOpen={openAddRoleModal}
          setIsModalOpen={setOpenAddRoleModal}
          onRoleAdded={fetchRoles}
        />
      </CustomModal>
    )
  );
};

export default AddMember;