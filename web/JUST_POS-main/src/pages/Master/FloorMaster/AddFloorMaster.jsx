import { useEffect, useState } from "react";
import { CustomModal } from "@/components/custom-modal/CustomModal";

const AddFloorModal = ({ open, onClose, onSave, initialData }) => {
  const [floorName, setFloorName] = useState("");
  const [floorDescription, setFloorDescription] = useState("");
  const [status, setStatus] = useState("active");
  const isEditMode = Boolean(initialData);

  // Prefill form when opening in edit mode
  useEffect(() => {
    if (open && initialData) {
      setFloorName(initialData.floorName || "");
      setFloorDescription(initialData.floorDescription || "");
      setStatus(initialData.status || "active");
    } else if (open && !initialData) {
      setFloorName("");
      setFloorDescription("");
      setStatus("active");
    }
  }, [open, initialData]);

  const handleSave = () => {
    if (!floorName.trim()) return;
    onSave?.({
      floorName,
      floorDescription,
      status,
    });
    handleReset();
  };

  const handleReset = () => {
    setFloorName("");
    setFloorDescription("");
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
            className="px-5 py-2 rounded-lg bg-[#F7E5EA] text-primary font-medium hover:bg-[#f0d3dc] transition-colors"
          >
            Cancel
          </button>
          <button
            onClick={handleSave}
            className="flex items-center gap-2 px-5 py-2 rounded-lg bg-primary text-white font-medium hover:bg-primary-active transition-colors"
          >
            <i className="ki-filled ki-note text-base"></i>
            {isEditMode ? "Update Floor" : "Save Floor"}
          </button>
        </div>
      }
    >
      <div className="px-2 pt-2 pb-4">
        {/* Header */}
        <div className="flex justify-between items-start mb-5">
          <div>
            <h2 className="text-xl font-semibold text-primary">
              {isEditMode ? "Edit Floor" : "Add Floor"}
            </h2>
            <p className="text-sm text-gray-500 mt-1">
              {isEditMode
                ? "Update this floor's name, description, or status."
                : "Create a new floor to organize tables across the restaurant."}
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

        {/* Floor Name */}
        <div className="mb-4">
          <label className="flex items-center gap-1 text-sm font-medium text-gray-800 mb-2">
            <i className="ki-filled ki-home-2 text-sm"></i>
            Floor Name
          </label>
          <div className="relative">
            <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400">
              #
            </span>
            <input
              type="text"
              value={floorName}
              onChange={(e) => setFloorName(e.target.value)}
              placeholder="Floor Name"
              className="w-full pl-9 pr-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm"
            />
          </div>
          <p className="text-xs text-gray-400 mt-1">
            E.g. "Ground Floor", "Rooftop", "Terrace"
          </p>
        </div>

        {/* Description (optional) */}
        <div className="mb-4">
          <label className="flex items-center gap-1 text-sm font-medium text-gray-800 mb-2">
            <i className="ki-filled ki-note-2 text-sm"></i>
            Description
            <span className="text-xs font-normal text-gray-400">(optional)</span>
          </label>
          <textarea
            value={floorDescription}
            onChange={(e) => setFloorDescription(e.target.value)}
            placeholder="Briefly describe this floor or seating area..."
            rows={3}
            className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm resize-none"
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
                status === "active" ? "bg-rose-800" : "bg-gray-200"
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

export { AddFloorModal };