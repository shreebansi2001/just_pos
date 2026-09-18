"use client";

import { useEffect, useState } from "react";
import { ChevronLeft, ChevronRight, Pencil, Trash2 } from "lucide-react";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import Swal from "sweetalert2";
import { GetAllRole, DeleteRole } from "@/services/apiServices";
import AddRole from "../../partials/modal/add-role/AddRole";
import { usePermission } from "@/hooks/usePermission";

const EmptyState = ({ message, sub }) => (
  <div className="flex flex-col items-center justify-center py-16 gap-3 text-center">
    <div className="w-12 h-12 rounded-xl bg-gray-100 flex items-center justify-center">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" className="text-gray-400">
        <path
          d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
          stroke="currentColor"
          strokeWidth="1.5"
          strokeLinecap="round"
        />
      </svg>
    </div>
    <div>
      <p className="text-sm font-medium text-gray-700">{message}</p>
      <p className="text-xs text-muted-foreground mt-0.5">{sub}</p>
    </div>
  </div>
);

const RoleMaster = () => {
  const { view, add, edit, delete: canDelete } = usePermission("Crm Department");
  const [isRoleModalOpen, setIsRoleModalOpen] = useState(false);
  const [roles, setRoles] = useState([]);
  const [selectedRole, setSelectedRole] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [page, setPage] = useState(1);
  const perPage = 5;

  const fetchRoles = async () => {
    try {
      const userId = localStorage.getItem("mainId");
      const res = await GetAllRole(userId);
      setRoles(res?.data?.data?.["Role Details"] || []);
    } catch (err) {
      console.error("Error fetching roles:", err);
      setRoles([]);
    }
  };

  useEffect(() => {
    fetchRoles();
  }, []);

  const filteredRoles = roles.filter((r) =>
    r.name?.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  const totalPages = Math.max(1, Math.ceil(filteredRoles.length / perPage));
  const pagedRoles = filteredRoles.slice((page - 1) * perPage, page * perPage);

  const handleEdit = (role) => {
    setSelectedRole(role);
    setIsRoleModalOpen(true);
  };

  const handleDelete = async (roleId) => {
    try {
      await DeleteRole(roleId);
      Swal.fire({
        icon: "success",
        title: "Role Deleted",
        timer: 1500,
        showConfirmButton: false,
      });
      fetchRoles();
    } catch (err) {
      console.error("Error deleting role:", err);
      Swal.fire({ icon: "error", title: "Error", text: "Failed to delete role" });
    }
  };

  return (
    <div className="min-h-screen">
      <main className="px-6 space-y-5">
        {/* Page header */}
        <div className="flex items-center justify-between mb-5">
          <h1 className="text-2xl font-semibold text-gray-900">Role Master</h1>
{add && (
          <Button
            size="md"
            onClick={() => {
              setSelectedRole(null);
              setIsRoleModalOpen(true);
            }}
            className="bg-primary"
            type="button"
          >
            Add Role
            <ChevronRight className="w-4 h-4 ml-1" />
          </Button>
)}
        </div>

        {/* Search */}
   <div className="relative w-full sm:w-72">
  <input
  
    className="w-full h-10 pl-9 pr-3 rounded-lg border border-gray-300 bg-white text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-violet-100 focus:border-violet-300 transition-colors"
    placeholder="Search Role"
    type="text"
    
    value={searchTerm}
    onChange={(e) => {
      setSearchTerm(e.target.value);
      setPage(1);
    }}
  />
</div>

        {/* Table card */}
        <div className="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-100 text-xs text-muted-foreground font-medium">
                  <th className="px-5 py-3 text-left w-8">#</th>
                  <th className="px-3 py-3 text-left">ROLE NAME</th>
                  <th className="px-3 py-3 text-left">ACTIONS</th>
                </tr>
              </thead>
              <tbody>
                {pagedRoles.map((role, idx) => (
                  <tr
                    key={role.id}
                    className="border-b border-gray-50 hover:bg-gray-50/60 transition-colors"
                  >
                    <td className="px-5 py-3 text-xs text-muted-foreground">
                      {(page - 1) * perPage + idx + 1}
                    </td>
                    <td className="px-3 py-3">
                      <p className="font-medium text-gray-900 text-xs leading-tight">
                        {role.name}
                      </p>
                    </td>
                    <td className="px-3 py-3">
                      <div className="flex items-center gap-0.5">
                        {edit && (
                        <button
                          onClick={() => handleEdit(role)}
                          className="p-1 rounded hover:bg-gray-100 text-gray-400 hover:text-gray-600 transition-colors"
                        >
                          <Pencil className="w-3.5 h-3.5" />
                        </button>
                        )}
                        {canDelete && (
                        <button
                          onClick={() => handleDelete(role.id)}
                          className="p-1 rounded hover:bg-gray-100 text-gray-400 hover:text-rose-500 transition-colors"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        
                        </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            {filteredRoles.length === 0 && (
              <EmptyState message="No roles found" sub="Try a different search or add a new role." />
            )}
          </div>

          {/* pagination footer */}
          {filteredRoles.length > 0 && (
            <div className="px-5 py-3 flex items-center justify-between border-t border-gray-100">
              <span className="text-xs text-muted-foreground">
                Showing {Math.min((page - 1) * perPage + 1, filteredRoles.length)}–
                {Math.min(page * perPage, filteredRoles.length)} of {filteredRoles.length} roles
              </span>
              <div className="flex items-center gap-1">
                <button
                  onClick={() => setPage((p) => Math.max(1, p - 1))}
                  disabled={page === 1}
                  className="p-1 rounded hover:bg-gray-100 disabled:opacity-30 transition-colors"
                >
                  <ChevronLeft className="w-4 h-4" />
                </button>
                {Array.from({ length: totalPages }, (_, i) => (
                  <button
                    key={i}
                    onClick={() => setPage(i + 1)}
                    className={cn(
                      "w-6 h-6 text-xs rounded flex items-center justify-center transition-colors",
                      page === i + 1
                        ? "bg-violet-600 text-white"
                        : "hover:bg-gray-100 text-gray-600",
                    )}
                  >
                    {i + 1}
                  </button>
                ))}
                <button
                  onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
                  disabled={page === totalPages}
                  className="p-1 rounded hover:bg-gray-100 disabled:opacity-30 transition-colors"
                >
                  <ChevronRight className="w-4 h-4" />
                </button>
              </div>
            </div>
          )}
        </div>
      </main>

      <AddRole
        isModalOpen={isRoleModalOpen}
        setIsModalOpen={setIsRoleModalOpen}
        editData={selectedRole}
        onRoleAdded={fetchRoles}
      />
    </div>
  );
};

export default RoleMaster;