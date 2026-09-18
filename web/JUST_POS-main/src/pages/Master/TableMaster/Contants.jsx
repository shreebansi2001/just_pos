import { LayoutGrid, CheckCircle2, PauseCircle, Users, Eye, Edit, Trash2 } from "lucide-react";

export const PAGE_HEADER = {
  title: "Table Master",
  description:
    "Manage tables and seating capacity used across floor assignments and order records.",
  addButtonLabel: "Add Table",
};

export const STATS_CARDS = [
  {
    key: "total",
    label: "Total Tables",
    value: "24",
    subtext: "+3 this month",
    icon: LayoutGrid,
  },
  {
    key: "active",
    label: "Active Tables",
    value: "21",
    subtext: "Currently in use",
    icon: CheckCircle2,
  },
  {
    key: "inactive",
    label: "Inactive Tables",
    value: "3",
    subtext: "Pending review",
    icon: PauseCircle,
  },
  {
    key: "totalSeating",
    label: "Total Seating",
    value: "96",
    subtext: "Across all tables",
    icon: Users,
  },
];

export const STATUS_FILTER_OPTIONS = [
  { label: "All Status", value: "" },
  { label: "Active", value: "active" },
  { label: "Inactive", value: "inactive" },
];

export const TABLE_NAME_FILTER_OPTIONS = [
  { label: "All Tables", value: "" },
  { label: "Table 1", value: "table-1" },
  { label: "Table 2", value: "table-2" },
  { label: "Table 3", value: "table-3" },
  { label: "Booth A", value: "booth-a" },
];

export const getTableColumns = ({ onView, onEdit, onDelete, onToggleStatus }) => [
  {
    id: "srNo",
    accessorKey: "srNo",
    header: "SR. NO.",
    size: 90,
    cell: ({ getValue }) => <span>{getValue()}</span>,
  },
  {
    id: "tableName",
    accessorKey: "tableName",
    header: "TABLE NAME",
    cell: ({ getValue }) => <p >{getValue()}</p>,
  },
  {
    id: "shortcode",
    accessorKey: "shortcode",
    header: "SHORTCODE",
    cell: ({ getValue }) => (
      <span className="inline-block rounded-md bg-red-50 px-2.5 py-1 font-medium text-primary-active">
        {getValue()}
      </span>
    ),
  },
  {
    id: "floor",
    accessorKey: "floor",
    header: "FLOOR",
    cell: ({ getValue }) => <span >{getValue()}</span>,
  },
  {
    id: "capacity",
    accessorKey: "capacity",
    header: "CAPACITY",
    cell: ({ getValue }) => (
      <span >
        {getValue() ? `${getValue()} seats` : "—"}
      </span>
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


export const TABLE_TABLE_DATA = [
  {
    id: 1,
    srNo: "01",
    tableName: "Table 1",
    shortcode: "T01",
    floor: "Ground Floor",
    capacity: 4,
    status: "active",
  },
  {
    id: 2,
    srNo: "02",
    tableName: "Table 2",
    shortcode: "T02",
    floor: "Ground Floor",
    capacity: 4,
    status: "active",
  },
  {
    id: 3,
    srNo: "03",
    tableName: "Booth A",
    shortcode: "BTHA",
    floor: "First Floor",
    capacity: 6,
    status: "active",
  },
  {
    id: 4,
    srNo: "04",
    tableName: "Rooftop 1",
    shortcode: "RT01",
    floor: "Rooftop",
    capacity: 2,
    status: "active",
  },
  {
    id: 5,
    srNo: "05",
    tableName: "Terrace 3",
    shortcode: "TR03",
    floor: "Terrace",
    capacity: 8,
    status: "inactive",
  },
];

export const DEFAULT_PAGINATION_SIZE = 10;
export const DEFAULT_SORTING = { field: "srNo", order: "asc" };