import { useEffect, useState } from "react";
import { CustomModal } from "@/components/custom-modal/CustomModal";

const AddCategoryModal = ({ open, onClose, onSave, initialData }) => {
  const [categoryName, setCategoryName] = useState("");
  const [categoryDescription, setCategoryDescription] = useState("");
  const [status, setStatus] = useState("active");
  const isEditMode = Boolean(initialData);

  // Prefill form when opening in edit mode
  useEffect(() => {
    if (open && initialData) {
      setCategoryName(initialData.categoryName || "");
      setCategoryDescription(initialData.categoryDescription || "");
      setStatus(initialData.status || "active");
    } else if (open && !initialData) {
      setCategoryName("");
      setCategoryDescription("");
      setStatus("active");
    }
  }, [open, initialData]);

  const handleSave = () => {
    if (!categoryName.trim()) return;
    onSave?.({
      categoryName,
      categoryDescription,
      status,
    });
    handleReset();
  };

  const handleReset = () => {
    setCategoryName("");
    setCategoryDescription("");
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
            className="px-5 py-2 rounded-lg bg-[#F7E5EA] text-primary font-medium  transition-colors"
          >
            Cancel
          </button>
          <button
            onClick={handleSave}
            className="flex items-center gap-2 px-5 py-2 rounded-lg bg-primary text-white font-medium hover:bg-primary-active transition-colors"
          >
            <i className="ki-filled ki-note text-base"></i>
            {isEditMode ? "Update Category" : "Save Category"}
          </button>
        </div>
      }
    >
      <div className="px-2 pt-2 pb-4">
        {/* Header */}
        <div className="flex justify-between items-start mb-5">
          <div>
            <h2 className="text-xl font-semibold text-primary">
              {isEditMode ? "Edit Category" : "Add Category"}
            </h2>
            <p className="text-sm text-gray-500 mt-1">
              {isEditMode
                ? "Update this menu category's name, description, or status."
                : "Create a new menu category for organizing items and orders."}
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

        {/* Category Name */}
        <div className="mb-4">
          <label className="flex items-center gap-1 text-sm font-medium text-gray-800 mb-2">
            <i className="ki-filled ki-category text-sm"></i>
            Category Name
          </label>
          <div className="relative">
            <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400">
              #
            </span>
            <input
              type="text"
              value={categoryName}
              onChange={(e) => setCategoryName(e.target.value)}
              placeholder="Category Name"
              className="w-full pl-9 pr-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-1 focus:ring-gray-700 focus:border-gray-700 text-sm"
            />
          </div>
          <p className="text-xs text-gray-700 mt-1">
            E.g. "Starters", "Main Course", "Beverages"
          </p>
        </div>

        {/* Description */}
        <div className="mb-4">
          <label className="flex items-center gap-1 text-sm font-medium text-gray-800 mb-2">
            <i className="ki-filled ki-note-2 text-sm"></i>
            Description
          </label>
          <textarea
            value={categoryDescription}
            onChange={(e) => setCategoryDescription(e.target.value)}
            placeholder="Briefly describe what this category includes..."
            rows={3}
            className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-1 focus:ring-gray-700 focus:border-gray-700 text-sm resize-none"
          />
          <p className="text-xs text-gray-700 mt-1">
            E.g. "Appetizers and small plates served before the main course"
          </p>
        </div>

        {/* Status */}
        <div>
          <div className="flex items-center gap-2">
            <button
              type="button"
              onClick={() =>
                setStatus((prev) => (prev === "active" ? "inactive" : "active"))
              }
              className="flex items-center gap-2"
            >          
            </button>
          </div>
        </div>
      </div>
    </CustomModal>
  );
};

export { AddCategoryModal };