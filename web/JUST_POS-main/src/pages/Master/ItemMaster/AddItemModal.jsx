import { useEffect, useState } from "react";
import { Plus, Trash2 } from "lucide-react";
import { CustomModal } from "@/components/custom-modal/CustomModal";

// ── Veg / Non-Veg Icons ──────────────────────────────────────
const VegIcon = () => (
  <span className="inline-flex items-center justify-center w-5 h-5 rounded-sm border-2 border-green-600 flex-shrink-0">
    <span className="w-2 h-2 rounded-full bg-green-600" />
  </span>
);

const NonVegIcon = () => (
  <span className="inline-flex items-center justify-center w-5 h-5 rounded-sm border-2 border-rose-700 flex-shrink-0">
    <span className="w-2 h-2 rounded-full bg-rose-700" />
  </span>
);

const makeId = (prefix) => `${prefix}_${Date.now()}_${Math.random().toString(36).slice(2, 7)}`;

const createEmptyGroup = () => ({
  id: makeId("grp"),
  groupName: "",
  isRequired: true,
  selectionType: "single",
  pricingType: "override",
  options: [createEmptyOption(true)],
});

function createEmptyOption(isDefault = false) {
  return {
    id: makeId("opt"),
    name: "",
    price: "",
    isDefault,
  };
}

const AddItemModal = ({ open, onClose, onSave, initialData }) => {
  const [itemName, setItemName] = useState("");
  const [price, setPrice] = useState("");
  const [shortcode, setShortcode] = useState("");
  const [itemDescription, setItemDescription] = useState("");
  const [status, setStatus] = useState("active");
  const [foodType, setFoodType] = useState("veg");
  const [hasVariations, setHasVariations] = useState(false);
  const [variationGroups, setVariationGroups] = useState([]);
  const isEditMode = Boolean(initialData);

  // Prefill form when opening in edit mode
  useEffect(() => {
    if (open && initialData) {
      setItemName(initialData.itemName || "");
      setPrice(initialData.price ?? "");
      setShortcode(initialData.shortcode || "");
      setItemDescription(initialData.itemDescription || "");
      setStatus(initialData.status || "active");
      setFoodType(initialData.foodType || "veg");
      setHasVariations(Boolean(initialData.hasVariations));
      setVariationGroups(
        initialData.variationGroups?.length
          ? initialData.variationGroups.map((g) => ({
              pricingType: g.selectionType === "multiple" ? "addon" : "override",
              ...g,
            }))
          : []
      );
    } else if (open && !initialData) {
      setItemName("");
      setPrice("");
      setShortcode("");
      setItemDescription("");
      setStatus("active");
      setFoodType("veg");
      setHasVariations(false);
      setVariationGroups([]);
    }
  }, [open, initialData]);

  // --- Variation group helpers -------------------------------------------

  const addVariationGroup = () => {
    setVariationGroups((prev) => [...prev, createEmptyGroup()]);
  };

  const removeVariationGroup = (groupId) => {
    setVariationGroups((prev) => prev.filter((g) => g.id !== groupId));
  };

  const updateGroupField = (groupId, field, value) => {
    setVariationGroups((prev) =>
      prev.map((g) => (g.id === groupId ? { ...g, [field]: value } : g))
    );
  };

  const addOption = (groupId) => {
    setVariationGroups((prev) =>
      prev.map((g) =>
        g.id === groupId ? { ...g, options: [...g.options, createEmptyOption()] } : g
      )
    );
  };

  const removeOption = (groupId, optionId) => {
    setVariationGroups((prev) =>
      prev.map((g) =>
        g.id === groupId
          ? { ...g, options: g.options.filter((o) => o.id !== optionId) }
          : g
      )
    );
  };

  const updateOption = (groupId, optionId, field, value) => {
    setVariationGroups((prev) =>
      prev.map((g) =>
        g.id === groupId
          ? {
              ...g,
              options: g.options.map((o) =>
                o.id === optionId ? { ...o, [field]: value } : o
              ),
            }
          : g
      )
    );
  };

  const setDefaultOption = (groupId, optionId) => {
    setVariationGroups((prev) =>
      prev.map((g) =>
        g.id === groupId
          ? {
              ...g,
              options: g.options.map((o) => ({ ...o, isDefault: o.id === optionId })),
            }
          : g
      )
    );
  };

  // --- Validation + save ---------------------------------------------------

  const isVariationsValid = () => {
    if (!hasVariations) return true;
    if (variationGroups.length === 0) return false;
    return variationGroups.every(
      (g) =>
        g.groupName.trim() &&
        g.options.length > 0 &&
        g.options.every((o) => o.name.trim() && o.price.toString().trim())
    );
  };

  const handleSave = () => {
    if (!itemName.trim() || !shortcode.trim() || !price.toString().trim()) return;
    if (!isVariationsValid()) return;

    onSave?.({
      itemName,
      price: Number(price),
      shortcode,
      itemDescription,
      status,
      foodType,
      hasVariations,
      variationGroups: hasVariations
        ? variationGroups.map((g) => ({
            ...g,
            options: g.options.map((o) => ({ ...o, price: Number(o.price) })),
          }))
        : [],
    });
    handleReset();
  };

  const handleReset = () => {
    setItemName("");
    setPrice("");
    setShortcode("");
    setItemDescription("");
    setStatus("active");
    setFoodType("veg");
    setHasVariations(false);
    setVariationGroups([]);
    onClose();
  };

  return (
    <CustomModal
      open={open}
      onClose={handleReset}
      width={650}
      centered
      title={null}
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
            {isEditMode ? "Update Item" : "Save Item"}
          </button>
        </div>
      }
    >
      <div className="px-2 pt-2 pb-4 max-h-[70vh] overflow-y-auto">
        {/* Header */}
        <div className="flex justify-between items-start ">
          <div>
            <h2 className="text-xl font-semibold text-primary">
              {isEditMode ? "Edit Item" : "Add Item"}
            </h2>
            <p className="text-sm text-gray-600 mt-1">
              {isEditMode
                ? "Update this item's name, price, shortcode, description, or variations."
                : "Create a new menu item for billing and kitchen tickets."}
            </p>
          </div>
          <button
            onClick={handleReset}
            className="text-gray-600 hover:text-gray-700 mt-1"
          >
            <i className="ki-filled ki-cross text-lg"></i>
          </button>
        </div>

        <hr className="border-t border-gray-200 mb-5" />

        {/* Food Type — Veg / Non-Veg */}
        <div className="mb-4">
          <label className="flex items-center gap-1 text-sm font-medium text-gray-800 mb-3">
            Food Type
          </label>
          <div className="flex gap-3">
            <button
              type="button"
              onClick={() => setFoodType("veg")}
              className={`flex items-center gap-2.5 px-4 py-2.5 rounded-lg border-2 transition-all ${
                foodType === "veg"
                  ? "border-green-600 bg-green-50 shadow-sm"
                  : "border-gray-200 bg-white hover:border-gray-300"
              }`}
            >
              <VegIcon />
              <span className={`text-sm font-semibold ${foodType === "veg" ? "text-green-700" : "text-gray-600"}`}>
                Veg
              </span>
            </button>

            <button
              type="button"
              onClick={() => setFoodType("non-veg")}
              className={`flex items-center gap-2.5 px-4 py-2.5 rounded-lg border-2 transition-all ${
                foodType === "non-veg"
                  ? "border-rose-700 bg-rose-50 shadow-sm"
                  : "border-gray-200 bg-white hover:border-gray-300"
              }`}
            >
              <NonVegIcon />
              <span className={`text-sm font-semibold ${foodType === "non-veg" ? "text-rose-700" : "text-gray-600"}`}>
                Non-Veg
              </span>
            </button>
          </div>
        </div>

        {/* Item Name */}
        <div className="mb-4">
          <label className="flex items-center gap-1 text-sm font-medium text-gray-800 mb-2">
            Item Name
          </label>
          <div className="relative">
            <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500">
              #
            </span>
            <input
              type="text"
              value={itemName}
              onChange={(e) => setItemName(e.target.value)}
              placeholder="Item Name"
              className="w-full pl-9 pr-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm"
            />
          </div>
          <p className="text-xs text-gray-500 mt-1">
            E.g. "Paneer Butter Masala", "Margherita Pizza"
          </p>
        </div>

        {/* Has Variations toggle */}
        <div className="mb-4 flex items-center justify-between rounded-lg border border-gray-200 bg-gray-50 px-4 py-3">
          <div>
            <p className="text-sm font-medium text-gray-800">This item has variations</p>
            <p className="text-xs text-gray-500 mt-0.5">
              E.g. Size (Small/Medium/Large), Crust, Spice Level
            </p>
          </div>
          <button
            type="button"
            onClick={() => setHasVariations((v) => !v)}
            className="flex items-center gap-2"
          >
            <span
              className={`relative inline-flex h-5 w-9 items-center rounded-full transition-colors ${
                hasVariations ? "bg-rose-800" : "bg-gray-200"
              }`}
            >
              <span
                className={`inline-block h-3.5 w-3.5 transform rounded-full bg-white transition-transform ${
                  hasVariations ? "translate-x-4.5" : "translate-x-1"
                }`}
              />
            </span>
          </button>
        </div>

        {/* Price and Shortcode side by side */}
        <div className="mb-4 grid grid-cols-2 gap-3">
          <div>
            <label className="flex items-center gap-1 text-sm font-medium text-gray-800 mb-2">
              {hasVariations ? "Base Price" : "Price"}
            </label>
            <div className="relative">
              <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500">
                ₹
              </span>
              <input
                type="number"
                min="0"
                step="0.01"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
                placeholder="0.00"
                className="w-full pl-8 pr-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm"
              />
            </div>
          </div>

          <div>
            <label className="flex items-center gap-1 text-sm font-medium text-gray-800 mb-2">
              Shortcode
            </label>
            <input
              type="text"
              value={shortcode}
              onChange={(e) => setShortcode(e.target.value.toUpperCase())}
              placeholder="e.g. PBM"
              maxLength={10}
              className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm uppercase"
            />
          </div>
        </div>
        <p className="text-xs text-gray-500 -mt-3 mb-4">
          {hasVariations
            ? "Shortcode is used for quick search and POS billing lookups. Base Price is used as-is for \"Adds to price\" groups, and is replaced by the selected option for \"Overrides price\" groups."
            : "Shortcode is used for quick search and POS billing lookups."}
        </p>

        {/* Variation Groups */}
        {hasVariations && (
          <div className="mb-4">
            <div className="flex items-center justify-between mb-2">
              <label className="text-sm font-medium text-gray-800">Variation Groups</label>
              <button
                type="button"
                onClick={addVariationGroup}
                className="flex items-center gap-1 text-xs font-medium text-primary hover:text-primary-active"
              >
                <Plus size={14} /> Add Group
              </button>
            </div>

            {variationGroups.length === 0 && (
              <p className="text-xs text-gray-500 border border-dashed border-gray-300 rounded-lg px-4 py-3 text-center">
                No variation groups yet. Add one for Size, Crust, Spice Level, etc.
              </p>
            )}

            <div className="space-y-3">
              {variationGroups.map((group) => (
                <div key={group.id} className="rounded-lg border border-gray-200 p-3">
                  <div className="flex items-start justify-between gap-3 mb-2.5">
                    <input
                      type="text"
                      value={group.groupName}
                      onChange={(e) => updateGroupField(group.id, "groupName", e.target.value)}
                      placeholder="Group Name e.g. Size"
                      className="flex-1 px-3 py-2 rounded-md border border-gray-300 text-sm font-medium focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary"
                    />
                    <button
                      type="button"
                      onClick={() => removeVariationGroup(group.id)}
                      className="text-gray-500 hover:text-red-500 mt-2"
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>

                  <div className="flex flex-wrap items-center gap-4 mb-3">
                    <label className="flex items-center gap-1.5 text-xs text-gray-600">
                      <input
                        type="checkbox"
                        checked={group.isRequired}
                        onChange={(e) => updateGroupField(group.id, "isRequired", e.target.checked)}
                        className="rounded border-gray-300 text-primary focus:ring-primary"
                      />
                      Required at order time
                    </label>
                    <label className="flex items-center gap-1.5 text-xs text-gray-600">
                      Selection:
                      <select
                        value={group.selectionType}
                        onChange={(e) => updateGroupField(group.id, "selectionType", e.target.value)}
                        className="rounded-md border border-gray-300 px-2 py-1 text-xs focus:outline-none focus:ring-1 focus:ring-primary"
                      >
                        <option value="single">Single choice (radio)</option>
                        <option value="multiple">Multiple choice (checkbox)</option>
                      </select>
                    </label>
                    <label className="flex items-center gap-1.5 text-xs text-gray-600">
                      Pricing:
                      <select
                        value={group.pricingType}
                        onChange={(e) => updateGroupField(group.id, "pricingType", e.target.value)}
                        className="rounded-md border border-gray-300 px-2 py-1 text-xs focus:outline-none focus:ring-1 focus:ring-primary"
                      >
                        <option value="override">Overrides item price (e.g. Size)</option>
                        <option value="addon">Adds to item price (e.g. Extra Cheese)</option>
                      </select>
                    </label>
                  </div>

                  <div className="space-y-2">
                    {group.options.map((opt) => (
                      <div key={opt.id} className="flex items-center gap-2">
                        {group.selectionType === "single" ? (
                          <input
                            type="radio"
                            name={`default-${group.id}`}
                            checked={opt.isDefault}
                            onChange={() => setDefaultOption(group.id, opt.id)}
                            title="Set as default selection"
                            className="text-primary focus:ring-primary"
                          />
                        ) : (
                          <span className="w-4" />
                        )}
                        <input
                          type="text"
                          value={opt.name}
                          onChange={(e) => updateOption(group.id, opt.id, "name", e.target.value)}
                          placeholder="Option name e.g. Small"
                          className="flex-1 px-3 py-1.5 rounded-md border border-gray-300 text-sm focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary"
                        />
                        <div className="relative w-24">
                          <span className="absolute left-2.5 top-1/2 -translate-y-1/2 text-gray-500 text-xs">
                            ₹
                          </span>
                          <input
                            type="number"
                            min="0"
                            step="0.01"
                            value={opt.price}
                            onChange={(e) => updateOption(group.id, opt.id, "price", e.target.value)}
                            placeholder="0.00"
                            className="w-full pl-5 pr-2 py-1.5 rounded-md border border-gray-300 text-sm focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary"
                          />
                        </div>
                        <button
                          type="button"
                          onClick={() => removeOption(group.id, opt.id)}
                          className="text-gray-500 hover:text-red-500"
                        >
                          <Trash2 size={14} />
                        </button>
                      </div>
                    ))}
                  </div>

                  <button
                    type="button"
                    onClick={() => addOption(group.id)}
                    className="mt-2 flex items-center gap-1 text-xs font-medium text-primary hover:text-primary-active"
                  >
                    <Plus size={14} /> Add Option
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Description (optional) */}
        <div className="mb-4">
          <label className="flex items-center gap-1 text-sm font-medium text-gray-800 mb-2">
            Description
            <span className="text-xs font-normal text-gray-500">(optional)</span>
          </label>
          <textarea
            value={itemDescription}
            onChange={(e) => setItemDescription(e.target.value)}
            placeholder="Briefly describe this item..."
            rows={3}
            className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm resize-none"
          />
        </div>

        {/* Status */}
        <div>
          <label className="flex items-center gap-1 text-sm font-medium text-gray-800 mb-2">
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
                status === "active" ? "text-gray-800" : "text-gray-500"
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

export { AddItemModal };
