import { Clock, Eye } from "lucide-react";

export const getKotHistoryColumns = (onView) => [
  {
    accessorKey: "id",
    header: "KOT #",
    cell: ({ row }) => (
      <span className="font-semibold text-slate-800">#KOT-{row.original.id}</span>
    ),
  },
  {
    accessorKey: "orderType",
    header: "Order Type",
  },
  {
    accessorKey: "customerName",
    header: "Customer Name",
  },
  {
    accessorKey: "customerPhone",
    header: "Customer Phone",
  },
  {
    accessorKey: "items",
    header: "Items",
    cell: ({ row }) => (
      <span className="text-xs text-slate-500">
        {row.original.items?.map((i) => i.name).join(", ")}
      </span>
    ),
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => (
      <span className="inline-flex items-center text-xs font-medium px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-700">
        {row.original.status}
      </span>
    ),
  },
  {
    accessorKey: "billPrintDate",
    header: "Bill Print Date",
  },
  {
    accessorKey: "itemCount",
    header: "No. of Items",
  },
  {
    accessorKey: "table",
    header: "Table",
  },
  {
    accessorKey: "completeDuration",
    header: "Complete Duration",
  },
  {
    accessorKey: "createdAt",
    header: "Created At",
  },
  {
    accessorKey: "prep",
    header: "Prep Duration",
    cell: ({ row }) => (
      <span
        className={`inline-flex items-center gap-1 font-medium ${
          row.original.late ? "text-red-500" : "text-emerald-600"
        }`}
      >
        <Clock className="w-3.5 h-3.5" />
        {row.original.prep}
      </span>
    ),
  },
  {
    accessorKey: "view",
    header: "Action",
    cell: ({ row }) => (
      <button
        onClick={() => onView(row.original)}
        className="inline-flex items-center gap-1 text-xs font-medium text-success hover:text-blue-700"
      >
        <Eye className="w-5 h-5" />
        
      </button>
    ),
  },
];

export const KOT_HISTORY_DEFAULT_SORTING = [{ id: "id", desc: true }];