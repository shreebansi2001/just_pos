import { useState } from "react";

const UserManagement = () => {
  const [search, setSearch] = useState("");
  const [role, setRole] = useState("All Roles");
  const [status, setStatus] = useState("All Status");

  const stats = [
    {
      label: "TOTAL USERS",
      value: 0,
      sub: "All registered users",
      icon: (
        <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a4 4 0 00-3-3.87M9 20H4v-2a4 4 0 013-3.87m9-5.13a4 4 0 11-8 0 4 4 0 018 0zM3 7a4 4 0 118 0A4 4 0 013 7z" />
        </svg>
      ),
      highlight: true,
    },
    {
      label: "ACTIVE USERS",
      value: 0,
      sub: "Currently active users",
      icon: (
        <svg className="w-6 h-6 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
        </svg>
      ),
    },
    {
      label: "MANAGERS",
      value: 0,
      sub: "Users with manager role",
      icon: (
        <svg className="w-6 h-6 text-orange-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
        </svg>
      ),
    },
    {
      label: "SALES REPS",
      value: 0,
      sub: "Users with sales role",
      icon: (
        <svg className="w-6 h-6 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
        </svg>
      ),
    },
  ];

  const tableHeaders = ["#", "USER", "LOGIN ID", "ROLE", "LEADS", "STATUS", "CREATED", "ACTIONS"];
  const perfHeaders = ["USER", "ROLE", "TOTAL", "NEW", "WON", "LOST", "DUE TODAY", "OVERDUE", "WIN RATE"];

  return (
    <div className="  px-6 font-sans">
      {/* Header */}
      <div className="flex items-center justify-between mb-1">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">User Management</h1>
          <nav className="text-sm text-gray-500 mt-0.5">
            <span className="text-indigo-500 cursor-pointer hover:underline">Home</span>
            <span className="mx-1">›</span>
            <span>Users</span>
          </nav>
        </div>
        <button className="flex items-center gap-2 bg-primary  text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors">
          <span className="text-lg leading-none">+</span> Add User
        </button>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mt-5">
        {stats.map((s, i) => (
          <div
            key={i}
            className={`rounded-xl p-5 flex items-center gap-4 ${
              s.highlight
                ? "bg-primary text-white"
                : "bg-white border border-gray-100 shadow-sm"
            }`}
          >
            <div
              className={`w-12 h-12 rounded-xl flex items-center justify-center flex-shrink-0 ${
                s.highlight ? "bg-white/20" : "bg-gray-50"
              }`}
            >
              {s.icon}
            </div>
            <div>
              <p className={`text-xs font-semibold tracking-wide ${s.highlight ? "text-indigo-200" : "text-gray-400"}`}>
                {s.label}
              </p>
              <p className={`text-3xl font-bold leading-tight ${s.highlight ? "text-white" : "text-gray-800"}`}>
                {s.value}
              </p>
              <p className={`text-xs mt-0.5 ${s.highlight ? "text-indigo-200" : "text-gray-400"}`}>
                {s.sub}
              </p>
            </div>
          </div>
        ))}
      </div>

      {/* Table Card */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 mt-5">
        {/* Filters */}
        <div className="flex flex-col sm:flex-row items-center gap-3 p-4 border-b border-gray-100">
          <div className="relative flex-1 w-full">
            <svg className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-4.35-4.35M17 11A6 6 0 115 11a6 6 0 0112 0z" />
            </svg>
            <input
              type="text"
              placeholder="Search by email or name..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-9 pr-3 py-2 text-sm border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-300 bg-gray-50"
            />
          </div>
          <select
            value={role}
            onChange={(e) => setRole(e.target.value)}
            className="text-sm border border-gray-200 rounded-lg px-3 py-2 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-300 text-gray-600 cursor-pointer"
          >
            <option>All Roles</option>
            <option>Manager</option>
            <option>Sales Rep</option>
          </select>
          <select
            value={status}
            onChange={(e) => setStatus(e.target.value)}
            className="text-sm border border-gray-200 rounded-lg px-3 py-2 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-300 text-gray-600 cursor-pointer"
          >
            <option>All Status</option>
            <option>Active</option>
            <option>Inactive</option>
          </select>
          <span className="text-sm text-gray-400 whitespace-nowrap ml-auto">Showing 1-0 of 0</span>
        </div>

        {/* Table */}
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-100">
                {tableHeaders.map((h) => (
                  <th key={h} className="text-left text-xs font-semibold text-gray-400 tracking-wide px-4 py-3">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              <tr>
                <td colSpan={8} className="py-16 text-center">
                  <div className="flex flex-col items-center gap-3 text-gray-400">
                    <div className="w-16 h-16 rounded-full bg-indigo-50 flex items-center justify-center">
                      <svg className="w-8 h-8 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M17 20h5v-2a4 4 0 00-3-3.87M9 20H4v-2a4 4 0 013-3.87m9-5.13a4 4 0 11-8 0 4 4 0 018 0z" />
                      </svg>
                    </div>
                    <p className="text-gray-700 font-semibold">No users found.</p>
                    <p className="text-gray-400 text-sm">Add a new user to get started.</p>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      {/* User Lead Performance */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 mt-5">
        <div className="flex items-center gap-2 px-5 py-4 border-b border-gray-100">
          <svg className="w-5 h-5 text-indigo-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
          </svg>
          <h2 className="font-semibold text-gray-800">User Lead Performance</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-100">
                {perfHeaders.map((h) => (
                  <th key={h} className="text-left text-xs font-semibold text-gray-400 tracking-wide px-4 py-3">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              <tr>
                <td colSpan={9} className="py-10 text-center">
                  <div className="flex items-center justify-center gap-2 text-gray-400 text-sm">
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    No data to display.
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default UserManagement;