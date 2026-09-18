import { useEffect, useState } from "react";
import { Select } from "antd";
import { CustomModal } from "@/components/custom-modal/CustomModal";

const floorOptions = [
  { value: "ground-floor", label: "Ground Floor" },
  { value: "first-floor", label: "First Floor" },
  { value: "rooftop", label: "Rooftop" },
  { value: "terrace", label: "Terrace" },
];

const AddTableModal = ({ open, onClose, onSave, initialData }) => {
  const [tableName, setTableName] = useState("");
  const [shortcode, setShortcode] = useState("");
  const [floor, setFloor] = useState(undefined);
  const [capacity, setCapacity] = useState("");
  const [status, setStatus] = useState("active");
  const isEditMode = Boolean(initialData);

  // Prefill form when opening in edit mode
  useEffect(() => {
    if (open && initialData) {
      setTableName(initialData.tableName || "");
      setShortcode(initialData.shortcode || "");
      setFloor(
        floorOptions.find((opt) => opt.label === initialData.floor)?.value ??
          initialData.floor
      );
      setCapacity(initialData.capacity ?? "");
      setStatus(initialData.status || "active");
    } else if (open && !initialData) {
      setTableName("");
      setShortcode("");
      setFloor(undefined);
      setCapacity("");
      setStatus("active");
    }
  }, [open, initialData]);

  const handleSave = () => {
    if (!tableName.trim() || !shortcode.trim() || !floor) return;
    const selectedFloor = floorOptions.find((o) => o.value === floor);
    onSave?.({
      tableName,
      shortcode,
      floor: selectedFloor?.label || floor,
      capacity: capacity ? Number(capacity) : null,
      status,
    });
    handleReset();
  };

  const handleReset = () => {
    setTableName("");
    setShortcode("");
    setFloor(undefined);
    setCapacity("");
    setStatus("active");
    onClose();
  };

  return (
    <CustomModal
      open={open}
      onClose={handleReset}
      width={480}
      centered
      title={null} // custom header below, since header needs subtitle text
      footer={
        <div className="flex justify-between items-center px-6 pb-6">
          <button
            onClick={handleReset}
            className="px-5 py-2 rounded-lg bg-red-50 text-primary-active font-medium transition-colors"
          >
            Cancel
          </button>
          <button
            onClick={handleSave}
            className="flex items-center gap-2 px-5 py-2 rounded-lg bg-primary text-primary-inverse font-medium hover:bg-primary-active transition-colors"
          >
            <i className="ki-filled ki-note text-base"></i>
            {isEditMode ? "Update Table" : "Save Table"}
          </button>
        </div>
      }
    >
      <div className="px-2 pt-2 pb-4">
        {/* Header */}
        <div className="flex justify-between items-start mb-5">
          <div>
            <h2 className="text-xl font-semibold text-primary">
              {isEditMode ? "Edit Table" : "Add Table"}
            </h2>
            <p className="text-sm text-gray-500 mt-1">
              {isEditMode
                ? "Update this table's name, shortcode, floor, or capacity."
                : "Create a new table for seating and order assignment."}
            </p>
          </div>
          <button
            onClick={handleReset}
            className="text-gray-500 hover:text-gray-700 mt-1"
          >
            <i className="ki-filled ki-cross text-lg"></i>
          </button>
        </div>

        <hr className="border-t border-gray-200 mb-5" />

        {/* Table Name */}
        <div className="mb-4">
          <label className="flex items-center gap-1 text-sm font-medium text-gray-800 mb-2">
            <i className="ki-filled ki-designtools text-sm"></i>
            Table Name
          </label>
          <div className="relative">
            <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400">
              #
            </span>
            <input
              type="text"
              value={tableName}
              onChange={(e) => setTableName(e.target.value)}
              placeholder="Table Name"
              className="w-full pl-9 pr-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm"
            />
          </div>
          <p className="text-xs text-gray-400 mt-1">
            E.g. "Table 1", "T-12", "Booth A"
          </p>
        </div>

        {/* Shortcode and Capacity side by side */}
        <div className="mb-4 grid grid-cols-2 gap-3">
          <div>
            <label className="flex items-center gap-1 text-sm font-medium text-gray-800 mb-2">
              <i className="ki-filled ki-code text-sm"></i>
              Shortcode
            </label>
            <input
              type="text"
              value={shortcode}
              onChange={(e) => setShortcode(e.target.value.toUpperCase())}
              placeholder="e.g. T01"
              maxLength={10}
              className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm uppercase"
            />
          </div>

          <div>
            <label className="flex items-center gap-1 text-sm font-medium text-gray-800 mb-2">
              <i className="ki-filled ki-people text-sm"></i>
              Capacity
            </label>
            <input
              type="number"
              min="1"
              value={capacity}
              onChange={(e) => setCapacity(e.target.value)}
              placeholder="e.g. 4"
              className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm"
            />
          </div>
        </div>
        <p className="text-xs text-gray-400 -mt-3 mb-4">
          Shortcode is used for quick search; capacity is the number of guests this table seats.
        </p>

        {/* Floor */}
        <div className="mb-4">
          <label className="flex items-center gap-1 text-sm font-medium text-gray-800 mb-2">
            <i className="ki-filled ki-home-2 text-sm"></i>
            Floor
          </label>
          <Select
            value={floor}
            onChange={setFloor}
            placeholder="Select a floor..."
            className="w-full custom-category-select"
            size="large"
            options={floorOptions}
          />
        </div>

        {/* Status */}
        <div>
          <label className="flex items-center gap-1 text-sm font-medium text-gray-800 mb-2">
            <i className="ki-filled ki-toggle-on text-sm"></i>
            Status
          </label>
          <button
            type="button"
            onClick={() =>
              setStatus((prev) => (prev === "active" ? "inactive" : "active"))
            }
            className="flex items-center gap-2"
          >
            <span
              className={`relative inline-flex h-5 w-9 items-center rounded-full transition-colors ${
                status === "active" ? "bg-primary" : "bg-gray-200"
              }`}
            >
              <span
                className={`inline-block h-3.5 w-3.5 transform rounded-full bg-white transition-transform ${
                  status === "active" ? "translate-x-4.5" : "translate-x-1"
                }`}
              />
            </span>
            <span
              className={`text-sm font-medium ${
                status === "active" ? "text-gray-800" : "text-gray-400"
              }`}
            >
              {status === "active" ? "Active" : "Inactive"}
            </span>
          </button>
        </div>
      </div>
    </CustomModal>
  );
};

export { AddTableModal };