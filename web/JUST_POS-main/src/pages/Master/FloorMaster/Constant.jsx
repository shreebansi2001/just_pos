import { Building2, CheckCircle2, PauseCircle, Percent, Eye, Edit, Trash2 } from "lucide-react";

export const PAGE_HEADER = {
  title: "Floor Master",
  description:
    "Manage floors and seating areas used across table assignments and order records.",
  addButtonLabel: "Add Floor",
};

export const STATS_CARDS = [
  {
    key: "total",
    label: "Total Floors",
    value: "5",
    subtext: "+1 this month",
    icon: Building2,
  },
  {
    key: "active",
    label: "Active Floors",
    value: "4",
    subtext: "Currently in use",
    icon: CheckCircle2,
  },
  {
    key: "inactive",
    label: "Inactive Floors",
    value: "1",
    subtext: "Pending review",
    icon: PauseCircle,
  },
  {
    key: "activeRatio",
    label: "Active Ratio",
    value: "80%",
    subtext: "Of all floors",
    icon: Percent,
  },
];

export const STATUS_FILTER_OPTIONS = [
  { label: "All Status", value: "" },
  { label: "Active", value: "active" },
  { label: "Inactive", value: "inactive" },
];

export const FLOOR_NAME_FILTER_OPTIONS = [
  { label: "All Floors", value: "" },
  { label: "Ground Floor", value: "ground-floor" },
  { label: "First Floor", value: "first-floor" },
  { label: "Rooftop", value: "rooftop" },
  { label: "Terrace", value: "terrace" },
];

export const getFloorColumns = ({ onView, onEdit, onDelete, onToggleStatus }) => [
  {
    id: "srNo",
    accessorKey: "srNo",
    header: "SR. NO.",
    size: 90,
    cell: ({ getValue }) => <span >{getValue()}</span>,
  },
  {
    id: "floorName",
    accessorKey: "floorName",
    header: "FLOOR NAME",
    cell: ({ row }) => (
      <div>
        <p >{row.original.floorName}</p>
        
      </div>
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
  },
];

export const FLOOR_TABLE_DATA = [
  {
    id: 1,
    srNo: "01",
    floorName: "Ground Floor",
    floorDescription: "Main dining area with 12 tables",
    status: "active",
  },
  {
    id: 2,
    srNo: "02",
    floorName: "First Floor",
    floorDescription: "Family seating and private cabins",
    status: "active",
  },
  {
    id: 3,
    srNo: "03",
    floorName: "Rooftop",
    floorDescription: "Open-air seating with skyline view",
    status: "active",
  },
  {
    id: 4,
    srNo: "04",
    floorName: "Terrace",
    floorDescription: "Covered outdoor seating",
    status: "active",
  },
  {
    id: 5,
    srNo: "05",
    floorName: "Basement Banquet",
    floorDescription: "Closed for renovation",
    status: "inactive",
  },
];

export const DEFAULT_PAGINATION_SIZE = 10;
export const DEFAULT_SORTING = { field: "srNo", order: "asc" };