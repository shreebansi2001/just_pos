import { Utensils, CheckCircle2, PauseCircle, IndianRupee, Eye, Edit, Trash2 } from "lucide-react";

export const PAGE_HEADER = {
  title: "Item Master",
  description:
    "Manage menu items used across POS billing and kitchen tickets for accurate order processing.",
  addButtonLabel: "Add Item",
};

export const STATS_CARDS = [
  {
    key: "total",
    label: "Total Items",
    value: "86",
    subtext: "+6% this month",
    icon: Utensils,
  },
  {
    key: "active",
    label: "Active Items",
    value: "79",
    subtext: "Currently on menu",
    icon: CheckCircle2,
  },
  {
    key: "inactive",
    label: "Inactive Items",
    value: "7",
    subtext: "Pending review",
    icon: PauseCircle,
  },
  {
    key: "avgPrice",
    label: "Avg. Price",
    value: "₹245",
    subtext: "Across all items",
    icon: IndianRupee,
  },
];

export const STATUS_FILTER_OPTIONS = [
  { label: "All Status", value: "" },
  { label: "Active", value: "active" },
  { label: "Inactive", value: "inactive" },
];

export const ITEM_NAME_FILTER_OPTIONS = [
  { label: "All Items", value: "" },
  { label: "Paneer Butter Masala", value: "paneer-butter-masala" },
  { label: "Cold Coffee", value: "cold-coffee" },
  { label: "Veg Manchurian", value: "veg-manchurian" },
  { label: "Gulab Jamun", value: "gulab-jamun" },
  { label: "Margherita Pizza", value: "margherita-pizza" },
];

// Returns a display-ready price string for a row, accounting for both
// "override" variation groups (e.g. Size, which replaces the base price)
// and "addon" groups (e.g. Extra Cheese, which adds on top of it).
const getDisplayPrice = (record) => {
  const basePrice = Number(record.price ?? 0);

  if (!record.hasVariations || !record.variationGroups?.length) {
    return `₹${basePrice.toFixed(2)}`;
  }

  const overrideGroup = record.variationGroups.find((g) => g.pricingType === "override");
  const hasAddonGroup = record.variationGroups.some((g) => g.pricingType === "addon");

  let priceLabel;
  if (overrideGroup) {
    const prices = (overrideGroup.options || [])
      .map((o) => Number(o.price))
      .filter((n) => !Number.isNaN(n));
    if (!prices.length) {
      priceLabel = `₹${basePrice.toFixed(2)}`;
    } else {
      const min = Math.min(...prices);
      const max = Math.max(...prices);
      priceLabel = min === max ? `₹${min.toFixed(2)}` : `₹${min.toFixed(2)} - ₹${max.toFixed(2)}`;
    }
  } else {
    // No override group — base price stands on its own, addons stack on top of it.
    priceLabel = `₹${basePrice.toFixed(2)}`;
  }

  return hasAddonGroup ? `${priceLabel} + add-ons` : priceLabel;
};

export const getItemColumns = ({ onView, onEdit, onDelete, onToggleStatus }) => [
  {
    id: "srNo",
    accessorKey: "srNo",
    header: "SR. NO.",
    size: 90,
    cell: ({ getValue }) => <span>{getValue()}</span>,
  },
  {
    id: "itemName",
    accessorKey: "itemName",
    header: "ITEM NAME",
    cell: ({ row }) => (
      <div className="flex items-center gap-2">
        {/* Veg / Non-Veg Icon */}
        {row.original.foodType === "non-veg" ? (
          <span className="inline-flex items-center justify-center w-4 h-4 rounded-sm border border-rose-700 flex-shrink-0">
            <span className="w-1.5 h-1.5 rounded-full bg-rose-700" />
          </span>
        ) : (
          <span className="inline-flex items-center justify-center w-4 h-4 rounded-sm border border-green-600 flex-shrink-0">
            <span className="w-1.5 h-1.5 rounded-full bg-green-600" />
          </span>
        )}
        <span>{row.original.itemName}</span>
      </div>
    ),
  },
  {
    id: "shortcode",
    accessorKey: "shortcode",
    header: "SHORTCODE",
    cell: ({ getValue }) => (
      <span className="inline-block rounded-md bg-red-50 px-2.5 py-1  font-medium text-primary-active">
        {getValue()}
      </span>
    ),
  },
  {
    id: "variations",
    header: "VARIATIONS",
    cell: ({ row }) => {
      const record = row.original;
      if (!record.hasVariations || !record.variationGroups?.length) {
        return <span className="text-xs text-gray-400">—</span>;
      }
      const totalOptions = record.variationGroups.reduce(
        (sum, g) => sum + (g.options?.length || 0),
        0
      );
      return (
        <span className="inline-block rounded-md bg-blue-50 px-2.5 py-1 text-xs font-medium text-blue-700">
          {record.variationGroups.length} group
          {record.variationGroups.length > 1 ? "s" : ""} · {totalOptions} option
          {totalOptions > 1 ? "s" : ""}
        </span>
      );
    },
  },
  {
    id: "price",
    accessorKey: "price",
    header: "PRICE",
    cell: ({ row }) => (
      <span >{getDisplayPrice(row.original)}</span>
    ),
  },
  {
    id: "status",
    accessorKey: "status",
    header: "STATUS",
    cell: ({ row }) => {
      const record = row.original;
      return (
        <button
          type="button"
          onClick={() => onToggleStatus?.(record)}
          className="flex items-center gap-2"
        >
          <span
            className={`relative inline-flex h-5 w-9 items-center rounded-full transition-colors ${
              record.status === "active" ? "bg-primary" : "bg-gray-200"
            }`}
          >
            <span
              className={`inline-block h-3.5 w-3.5 transform rounded-full bg-white transition-transform ${
                record.status === "active" ? "translate-x-4.5" : "translate-x-1"
              }`}
            />
          </span>
          <span
            className={`text-sm font-medium ${
              record.status === "active" ? "text-gray-800" : "text-gray-400"
            }`}
          >
            {record.status === "active" ? "Active" : "Inactive"}
          </span>
        </button>
      );
    },
  },
  {
    id: "actions",
    header: "ACTIONS",
    enableSorting: false,
    cell: ({ row }) => {
      const record = row.original;
      return (
        <div className="flex items-center justify-start gap-3 text-gray-400">
          <button type="button" onClick={() => onView?.(record)} className="text-green-700 hover:text-green-800">
            <Eye size={18} />
          </button>
          <button type="button" onClick={() => onEdit?.(record)} className="text-blue-700 hover:text-blue-800">
            <Edit size={18} />
          </button>
          <button type="button" onClick={() => onDelete?.(record)} className="text-red-700 hover:text-red-800">
            <Trash2 size={18} />
          </button>
        </div>
      );
    },
  }
];

export const ITEM_TABLE_DATA = [
  {
    id: 1,
    srNo: "01",
    foodType: "veg",
    itemName: "Paneer Butter Masala",
    itemDescription: "Cottage cheese in a rich tomato-butter gravy",
    shortcode: "PBM",
    price: 280,
    status: "active",
    hasVariations: false,
    variationGroups: [],
  },
  {
    id: 2,
    srNo: "02",
    itemName: "Cold Coffee",
    foodType: "non-veg",
    itemDescription: "Chilled coffee blended with milk and ice cream",
    shortcode: "CLDCF",
    price: 140,
    status: "active",
    hasVariations: false,
    variationGroups: [],
  },
  {
    id: 3,
    srNo: "03",
    foodType: "veg",
    itemName: "Veg Manchurian",
    itemDescription: "Deep-fried veg balls tossed in a spicy Indo-Chinese sauce",
    shortcode: "VMANCH",
    price: 220,
    status: "active",
    hasVariations: true,
    variationGroups: [
      {
        id: "grp_manch_addons",
        groupName: "Add-ons",
        isRequired: false,
        selectionType: "multiple",
        pricingType: "addon",
        options: [
          { id: "opt_gravy", name: "Extra Gravy", price: 20, isDefault: false },
          { id: "opt_chilli", name: "Extra Chilli Flakes", price: 0, isDefault: false },
        ],
      },
    ],
  },
  {
    id: 4,
    srNo: "04",
    foodType: "veg",
    itemName: "Gulab Jamun",
    itemDescription: "",
    shortcode: "GLBJMN",
    price: 90,
    status: "active",
    hasVariations: false,
    variationGroups: [],
  },
  {
    id: 5,
    srNo: "05",
    foodType: "veg",
    itemName: "Tandoori Roti",
    itemDescription: "",
    shortcode: "TNDROT",
    price: 30,
    status: "active",
    hasVariations: false,
    variationGroups: [],
  },
  {
    id: 6,
    srNo: "06",
    foodType: "veg",
    itemName: "Mango Shake (Seasonal)",
    itemDescription: "Available only during summer season",
    shortcode: "MNGSHK",
    price: 160,
    status: "inactive",
    hasVariations: false,
    variationGroups: [],
  },
  {
    id: 7,
    srNo: "07",
    foodType: "veg",
    itemName: "Margherita Pizza",
    itemDescription: "Classic cheese pizza with a tomato base",
    shortcode: "MARPZA",
    price: 199,
    status: "active",
    hasVariations: true,
    variationGroups: [
      {
        id: "grp_size",
        groupName: "Size",
        isRequired: true,
        selectionType: "single",
        pricingType: "override",
        options: [
          { id: "opt_s", name: "Small", price: 199, isDefault: true },
          { id: "opt_m", name: "Medium", price: 299, isDefault: false },
          { id: "opt_l", name: "Large", price: 399, isDefault: false },
        ],
      },
      {
        id: "grp_toppings",
        groupName: "Extra Toppings",
        isRequired: false,
        selectionType: "multiple",
        pricingType: "addon",
        options: [
          { id: "opt_cheese", name: "Extra Cheese", price: 40, isDefault: false },
          { id: "opt_spicy", name: "Spicy Seasoning", price: 0, isDefault: false },
        ],
      },
    ],
  },
];

export const DEFAULT_PAGINATION_SIZE = 10;
export const DEFAULT_SORTING = { field: "srNo", order: "asc" };