import { Layers, CheckCircle2, PauseCircle, BarChart3, Eye, Pencil, Trash2, Edit } from "lucide-react";

export const PAGE_HEADER = {
  title: "Category Master",
  description:
    "Manage menu categories used across POS orders and kitchen tickets for accurate billing and reporting.",
  addButtonLabel: "Add Category",
};

export const STATS_CARDS = [
  {
    key: "total",
    label: "Total Categories",
    value: "18",
    subtext: "+2% this month",
    icon: Layers,
  },
  {
    key: "active",
    label: "Active Categories",
    value: "16",
    subtext: "Currently on menu",
    icon: CheckCircle2,
  },
  {
    key: "inactive",
    label: "Inactive Categories",
    value: "2",
    subtext: "Pending review",
    icon: PauseCircle,
  },
  {
    key: "usage",
    label: "Order Coverage",
    value: "97%",
    subtext: "Across all orders",
    icon: BarChart3,
  },
];

export const STATUS_FILTER_OPTIONS = [
  { label: "All Status", value: "" },
  { label: "Active", value: "active" },
  { label: "Inactive", value: "inactive" },
];

export const CATEGORY_NAME_FILTER_OPTIONS = [
  { label: "All Categories", value: "" },
  { label: "Starters", value: "starters" },
  { label: "Main Course", value: "main-course" },
  { label: "Beverages", value: "beverages" },
  { label: "Desserts", value: "desserts" },
];

export const getCategoryColumns = ({ onView, onEdit, onDelete, onToggleStatus }) => [
  {
    id: "srNo",
    accessorKey: "srNo",
    header: "SR. NO.",
    size: 90,
    cell: ({ getValue }) => <span className="">{getValue()}</span>,
  },
  {
    id: "categoryName",
    accessorKey: "categoryName",
    header: "CATEGORY",
    cell: ({ row }) => (
      <p >{row.original.categoryName}</p>
    ),
  },
  {
    id: "categoryDescription",
    accessorKey: "categoryDescription",
    header: "DESCRIPTION",
    cell: ({ getValue }) => <span>{getValue()}</span>,
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
              record.status === "active" ? "text-primary" : "text-gray-400"
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
  },
];

export const CATEGORY_TABLE_DATA = [
  {
    id: 1,
    srNo: "01",
    categoryName: "Starters",
    categoryDescription: "Appetizers and small plates served before the main course",
    status: "active",
  },
  {
    id: 2,
    srNo: "02",
    categoryName: "Main Course",
    categoryDescription: "Primary entrees including curries, rice, and breads",
    status: "active",
  },
  {
    id: 3,
    srNo: "03",
    categoryName: "Beverages",
    categoryDescription: "Soft drinks, mocktails, and hot beverages",
    status: "active",
  },
  {
    id: 4,
    srNo: "04",
    categoryName: "Desserts",
    categoryDescription: "Sweet dishes served at the end of the meal",
    status: "active",
  },
  {
    id: 5,
    srNo: "05",
    categoryName: "Salads",
    categoryDescription: "Fresh, light dishes served as a side or starter",
    status: "active",
  },
  {
    id: 6,
    srNo: "06",
    categoryName: "Seasonal Specials",
    categoryDescription: "Limited-time items pulled during off-season",
    status: "inactive",
  },
];

export const DEFAULT_PAGINATION_SIZE = 10;
export const DEFAULT_SORTING = { field: "srNo", order: "asc" };