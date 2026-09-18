import { useMemo, useState } from "react";
import { Plus, Search, RefreshCcw, Share2, Columns3, ChevronDown } from "lucide-react";
import { TableComponent } from "@/components/table/TableComponent";
import { AddFloorModal } from "./AddFloorMaster";
import {
  PAGE_HEADER,
  STATS_CARDS,
  STATUS_FILTER_OPTIONS,
  FLOOR_NAME_FILTER_OPTIONS,
  FLOOR_TABLE_DATA,
  getFloorColumns,
  DEFAULT_PAGINATION_SIZE,
  DEFAULT_SORTING,
} from "./constant";

const FloorListing = () => {
  const [tableData, setTableData] = useState(FLOOR_TABLE_DATA);
  const [searchText, setSearchText] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [floorFilter, setFloorFilter] = useState("");
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [editingFloor, setEditingFloor] = useState(null);

  const handleToggleStatus = (record) => {
    setTableData((prev) =>
      prev.map((row) =>
        row.id === record.id
          ? { ...row, status: row.status === "active" ? "inactive" : "active" }
          : row
      )
    );
  };

  const handleView = (record) => console.log("View floor:", record);

  // Edit now opens AddFloorModal, prefilled with the selected row
  const handleEdit = (record) => {
    setEditingFloor(record);
    setIsAddModalOpen(true);
  };

  const handleDelete = (record) => console.log("Delete floor:", record);

  const handleAddFloor = () => {
    setEditingFloor(null);
    setIsAddModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsAddModalOpen(false);
    setEditingFloor(null);
  };

  const handleSaveFloor = ({ floorName, floorDescription, status }) => {
    if (editingFloor) {
      // Update existing row
      setTableData((prev) =>
        prev.map((row) =>
          row.id === editingFloor.id
            ? { ...row, floorName, floorDescription, status }
            : row
        )
      );
    } else {
      // Create new row
      const newRow = {
        id: Date.now(), // replace with real id from API response
        srNo: String(tableData.length + 1).padStart(2, "0"),
        floorName,
        floorDescription,
        status: status || "active",
      };
      setTableData((prev) => [newRow, ...prev]);
    }
    handleCloseModal();
  };

  const columns = useMemo(
    () =>
      getFloorColumns({
        onView: handleView,
        onEdit: handleEdit,
        onDelete: handleDelete,
        onToggleStatus: handleToggleStatus,
      }),
    []
  );

  const filteredData = useMemo(() => {
    return tableData.filter((row) => {
      const matchesSearch = row.floorName
        .toLowerCase()
        .includes(searchText.toLowerCase());
      const matchesStatus = statusFilter ? row.status === statusFilter : true;
      return matchesSearch && matchesStatus;
    });
  }, [tableData, searchText, statusFilter]);

const toolbar = (
  <div className="flex flex-wrap items-center justify-between gap-3 rounded-xl  p-3">
    <div className="relative w-full max-w-xs">
      <Search
        size={16}
        className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
      />
      <input
        type="text"
        value={searchText}
        onChange={(e) => setSearchText(e.target.value)}
        placeholder="Search Floor..."
        className="w-full rounded-lg border border-gray-200 bg-white py-2 pl-9 pr-3 text-sm text-gray-700 placeholder:text-gray-400 focus:outline-none focus:border-gray-300"
      />
    </div>

    <div className="flex flex-wrap items-center gap-2">
      <FilterDropdown
        label="Status"
        value={statusFilter}
        options={STATUS_FILTER_OPTIONS}
        onChange={setStatusFilter}
      />
      <FilterDropdown
        label="Floor Name"
        value={floorFilter}
        options={FLOOR_NAME_FILTER_OPTIONS}
        onChange={setFloorFilter}
      />
    </div>
  </div>
);

  return (
    <div className="min-h-screen bg-white px-6">
      {/* Page header */}
      <div className="mb-6 flex flex-wrap items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-primary">{PAGE_HEADER.title}</h1>
          <p className="mt-1 max-w-xl text-sm text-gray-600">{PAGE_HEADER.description}</p>
        </div>
        <button
          type="button"
          onClick={handleAddFloor}
          className="flex items-center gap-2 rounded-lg bg-primary px-4 py-2.5 text-sm font-medium text-primary-inverse shadow-sm transition hover:bg-primary-active"
        >
          <Plus size={16} />
          {PAGE_HEADER.addButtonLabel}
        </button>
      </div>

      {/* Stat cards */}


      {/* Table */}
      <TableComponent
        columns={columns}
        data={filteredData}
        tableData={filteredData}
        paginationSize={DEFAULT_PAGINATION_SIZE}
        defaultSorting={DEFAULT_SORTING}
        toolbar={toolbar}
      />

      {/* Add / Edit Floor Modal */}
      <AddFloorModal
        open={isAddModalOpen}
        onClose={handleCloseModal}
        onSave={handleSaveFloor}
        initialData={editingFloor}
      />
    </div>
  );
};

// ---------------------------------------------------------------------------
// Local presentational helpers
// ---------------------------------------------------------------------------
const IconButton = ({ children, onClick }) => (
  <button
    type="button"
    onClick={onClick}
    className="flex h-9 w-9 items-center justify-center rounded-lg border border-primary-lighter bg-white text-gray-500 transition hover:bg-primary-lighter hover:text-primary-active"
  >
    {children}
  </button>
);

const FilterDropdown = ({ label, value, options, onChange }) => (
  <div className="relative">
    <select
      value={value}
      onChange={(e) => onChange(e.target.value)}
      className="appearance-none rounded-lg border border-gray-200 bg-white py-2 pl-3 pr-8 text-sm text-gray-600 focus:outline-none focus:border-gray-300"
    >
      <option value="" disabled hidden>
        {label}
      </option>
      {options.map((opt) => (
        <option key={opt.value} value={opt.value}>
          {opt.label}
        </option>
      ))}
    </select>
    <ChevronDown
      size={14}
      className="pointer-events-none absolute right-2.5 top-1/2 -translate-y-1/2 text-gray-400"
    />
  </div>
);
export default FloorListing;