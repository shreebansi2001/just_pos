import { useState, useEffect, useRef } from "react";
import { CustomModal } from "@/components/custom-modal/CustomModal";
import AddRoleModal from "./AddRoleModal";
import {
  AddRights,
  GetAllRole,
  getUserById,
  GetAllPages,
    GetRightsBYroleId,
} from "@/services/apiServices";
import { ChevronDown } from "lucide-react";
import { cn } from "@/lib/utils";
import Swal from "sweetalert2";
import {  Save } from "lucide-react";

const AddRole = ({ isModalOpen, setIsModalOpen, editData, onRoleAdded }) => {
  const [formData, setFormData] = useState({});
  const [openAddRoleModal, setOpenAddRoleModal] = useState(false);
  const [roles, setRoles] = useState([]);
  const [rights, setRights] = useState({});
  const [modules, setModules] = useState([]);
  const [roleSearch, setRoleSearch] = useState("");
  const [roleDropdownOpen, setRoleDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  const userId = localStorage.getItem("userId");

  /* ---------------- CLICK OUTSIDE ---------------- */
  useEffect(() => {
    const handleOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setRoleDropdownOpen(false);
      }
    };
    document.addEventListener("mousedown", handleOutside);
    return () => document.removeEventListener("mousedown", handleOutside);
  }, []);

  /* ---------------- RESET ---------------- */
  const resetForm = () => {
    setFormData({});
    setRights({});
  };

  /* ---------------- FETCH PAGES ---------------- */
const fetchPages = async (roleId) => {
  try {
    const istrue = roleId !== 1;
    const res = await GetAllPages(istrue, false);
    const allModules = res.data?.data?.ModuleWiseUserRights || [];

    const crmModules = allModules.filter((m) =>
      m.moduleName?.toLowerCase().includes("crm")
    );

    setModules(crmModules);
  } catch (err) {
    setModules([]);
  }
};
  const fetchUserAndPages = async () => {
    try {
      const res = await getUserById(userId);
      const roleId = res.data?.data["User Details"][0].userBasicDetails.role.id;
      await fetchPages(roleId);
    } catch (err) {
      console.error("Error fetching user:", err);
    }
  };

  /* ---------------- FETCH ROLES ---------------- */
  const fetchRoles = async () => {
    try {
      const res = await GetAllRole(userId);
      setRoles(res.data?.data?.["Role Details"] || []);
    } catch {
      setRoles([]);
    }
  };
const fetchRoleRights = async (roleId) => {
  try {
    const res = await GetRightsBYroleId(roleId);
    const data = res.data?.data?.UserRights || [];
    const formatted = {};

    data.forEach((module) => {
      module.userRights.forEach((page) => {
        formatted[page.pageid] = {
          moduleId: module.moduleId,
          pageid: page.pageid,
          view: page.view,
          add: page.add,
          edit: page.edit,
          delete: page.delete,
        };
      });
    });

    setRights(formatted);
  } catch (err) {
    console.error("Error fetching role rights", err);
  }
};
  /* ---------------- EFFECT ---------------- */
 useEffect(() => {
  if (!isModalOpen) return;
  fetchUserAndPages();
  fetchRoles();

  if (editData?.id) {
    setFormData({ role_name: editData.name });
    fetchRoleRights(editData.id);  // ← fetch existing rights
  } else {
    resetForm();
  }
}, [isModalOpen, editData]);
  /* ---------------- HANDLERS ---------------- */
  const handleModalClose = () => setIsModalOpen(false);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleCheckboxChange = (moduleId, pageId, action, checked) => {
    setRights((prev) => ({
      ...prev,
      [pageId]: {
        ...prev[pageId],
        moduleId,
        pageid: pageId,
        [action]: checked,
      },
    }));
  };

  const handleCheckAll = (moduleId, pageId, checked) => {
    setRights((prev) => ({
      ...prev,
      [pageId]: {
        ...prev[pageId],
        moduleId,
        pageid: pageId,
        view: checked,
        edit: checked,
        delete: checked,
        add: checked,
      },
    }));
  };

  const handleRoleAddedFromModal = async () => {
    await fetchRoles();
    onRoleAdded?.();
  };

  const handleAddRole = async () => {
    const role = roles.find((r) => r.name === formData.role_name);
    if (!role) {
      Swal.fire({ icon: "error", title: "Error", text: "Role not selected" });
      return;
    }

    const rightsList = [];
    modules.forEach((module) => {
      module.userRightsPages.forEach((page) => {
        const existing = rights[page.pageId] || {};
        rightsList.push({
          moduleId: module.moduleId,
          pageid: page.pageId,
          view: existing.view || false,
          edit: existing.edit || false,
          delete: existing.delete || false,
          add: existing.add || false,
        });
      });
    });

    const payload = { roleId: role.id, rightsList };

    try {
      const res = await AddRights(payload);
      if (res?.data?.success) {
        Swal.fire({
          icon: "success",
          title: "Success",
          text: res.data.msg,
          timer: 1500,
          showConfirmButton: false,
        });
        resetForm();
        handleModalClose();
        onRoleAdded?.();
      } else {
        Swal.fire({ icon: "error", title: "Error", text: res?.data?.msg || "Something went wrong!" });
      }
    } catch (err) {
      Swal.fire({ icon: "error", title: "Error", text: err?.response?.data?.msg || "Server error" });
    }
  };

  const filteredRoles = roles.filter((r) =>
    r.name.toLowerCase().includes(roleSearch.toLowerCase())
  );

  /* ---------------- UI ---------------- */
  return (
    isModalOpen && (
      <CustomModal
        open={isModalOpen}
        onClose={handleModalClose}
        title="Assign User Rights"
        width="min(650px, 95vw)"
  footer={[
  <div className="flex justify-end gap-3 w-full" key="footer">
    <button
      className="px-4 h-10 rounded-lg text-sm font-medium text-gray-600 hover:bg-gray-100 transition-colors"
      onClick={handleModalClose}
    >
      Cancel
    </button>
    <button
      className="inline-flex items-center gap-2 px-4 h-10 rounded-lg text-sm font-medium bg-primary text-white hover:opacity-90 transition-opacity"
      onClick={handleAddRole}
    >
      <Save className="w-4 h-4" />
      Save Rights
    </button>
  </div>,
]}
      >
        <div className="max-h-[65vh] sm:max-h-[60vh] overflow-y-auto px-1 sm:px-2">
          <div className="flex flex-col gap-4">

            {/* Department Dropdown */}
            <div className="flex flex-col gap-1">
              <label className="form-label">Department</label>
              <div className="relative" ref={dropdownRef}>
                {/* Trigger */}
                <button
                  type="button"
                  onClick={() => setRoleDropdownOpen((p) => !p)}
                  className="w-full inline-flex items-center justify-between gap-2 rounded-lg border border-gray-300 bg-white px-3.5 h-10 text-sm text-left"
                >
                  <span className={cn(!formData.role_name && "text-gray-400")}>
                    {formData.role_name || "Select Department"}
                  </span>
                  <ChevronDown className={cn("h-3.5 w-3.5 text-gray-400 shrink-0 transition-transform", roleDropdownOpen && "rotate-180")} />
                </button>

                {/* Add new role button */}
                <button
                  type="button"
                  onClick={() => setOpenAddRoleModal(true)}
                  className="absolute right-10 top-1/2 -translate-y-1/2 w-7 h-7 bg-primary text-white rounded-full flex items-center justify-center"
                >
                  <i className="ki-filled ki-plus text-xs"></i>
                </button>

                {/* Dropdown */}
                {roleDropdownOpen && (
                  <div className="absolute z-20 mt-1.5 w-full rounded-xl border border-gray-200 bg-white shadow-lg">
                    <div className="p-2 border-b border-gray-100">
                      <input
                        autoFocus
                        value={roleSearch}
                        onChange={(e) => setRoleSearch(e.target.value)}
                        placeholder="Search department..."
                        className="w-full h-8 px-2.5 text-sm rounded-md border border-gray-200 focus:outline-none focus:ring-2 focus:ring-violet-100"
                      />
                    </div>
                    <div className="max-h-52 overflow-y-auto p-1.5">
                      {filteredRoles.length === 0 ? (
                        <div className="px-3 py-2 text-sm text-gray-400">No results found</div>
                      ) : (
                        filteredRoles.map((role) => (
                          <button
                            key={role.id}
                            type="button"
                            onClick={() => {
                              handleInputChange({ target: { name: "role_name", value: role.name } });
                              setRoleDropdownOpen(false);
                              setRoleSearch("");
                            }}
                            className={cn(
                              "w-full text-left rounded-lg px-3 py-1.5 text-sm text-gray-800 hover:bg-gray-50 transition-colors",
                              formData.role_name === role.name && "bg-violet-50 text-violet-700 font-medium"
                            )}
                          >
                            {role.name}
                          </button>
                        ))
                      )}
                    </div>
                  </div>
                )}
              </div>
            </div>

            {/* Rights Table */}
            <div className="border rounded-lg overflow-hidden">
              {modules.map((module) => (
                <div key={module.moduleId} className="mb-4">
                  <div className="bg-gray-200 px-4 py-2 font-bold text-primary">
                    {module.moduleName}
                  </div>
                  <div className="overflow-x-auto">
                    <table className="min-w-full table-auto">
                      <thead className="bg-gray-100">
                        <tr>
                          <th className="p-2 sm:p-3 text-left text-xs sm:text-sm">Page Name</th>
                          <th className="p-2 sm:p-3 text-center text-xs sm:text-sm">All</th>
                          <th className="p-2 sm:p-3 text-center text-xs sm:text-sm">View</th>
                          <th className="p-2 sm:p-3 text-center text-xs sm:text-sm">Edit</th>
                          <th className="p-2 sm:p-3 text-center text-xs sm:text-sm">Delete</th>
                          <th className="p-2 sm:p-3 text-center text-xs sm:text-sm">Add</th>
                        </tr>
                      </thead>
                      <tbody>
                        {module.userRightsPages.map((page) => (
                          <tr key={page.pageId} className="border-t">
                            <td className="p-2 sm:p-3 text-xs sm:text-sm whitespace-nowrap">
                              {page.pagename}
                            </td>
                            <td className="text-center p-1 sm:p-2">
                              <input
                                type="checkbox"
                                className="w-4 h-4 accent-primary cursor-pointer"
                                checked={
                                  Boolean(rights[page.pageId]?.view) &&
                                  Boolean(rights[page.pageId]?.edit) &&
                                  Boolean(rights[page.pageId]?.delete) &&
                                  Boolean(rights[page.pageId]?.add)
                                }
                                onChange={(e) =>
                                  handleCheckAll(module.moduleId, page.pageId, e.target.checked)
                                }
                              />
                            </td>
                            {["view", "edit", "delete", "add"].map((action) => (
                              <td key={action} className="text-center p-1 sm:p-2">
                                <input
                                  type="checkbox"
                                  className="w-4 h-4 accent-primary cursor-pointer"
                                  checked={rights[page.pageId]?.[action] || false}
                                  onChange={(e) =>
                                    handleCheckboxChange(module.moduleId, page.pageId, action, e.target.checked)
                                  }
                                />
                              </td>
                            ))}
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              ))}
            </div>

          </div>
        </div>

        {/* ADD ROLE MODAL */}
        <AddRoleModal
          isModalOpen={openAddRoleModal}
          setIsModalOpen={setOpenAddRoleModal}
          onRoleAdded={handleRoleAddedFromModal}
        />
      </CustomModal>
    )
  );
};

export default AddRole;