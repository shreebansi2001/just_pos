import React from "react";
import { X, Printer, CheckCircle2 } from "lucide-react";

const sampleKotList = [
  { id: "37", qty: 1, items: "Banta Soda", biller: "biller", created: "9 Sep 2026 22:17:21", info: "--" },
  { id: "36", qty: 3, items: "Maaza Small, Mineral Water Bottle 1l, Thumps Up Small", biller: "biller", created: "9 Sep 2026 22:14:27", info: "--" },
  { id: "33", qty: 1, items: "Butter Sada Dosa", biller: "biller", created: "9 Sep 2026 22:10:37", info: "--" },
  { id: "31", qty: 1, items: "Sprite Small", biller: "biller", created: "9 Sep 2026 22:00:37", info: "--" },
  { id: "25", qty: 1, items: "Butter Sada Dosa", biller: "biller", created: "9 Sep 2026 21:50:46", info: "--" },
  { id: "24", qty: 1, items: "Butter Sada Dosa", biller: "biller", created: "9 Sep 2026 21:47:44", info: "--" },
  { id: "22", qty: 1, items: "Butter Sada Dosa", biller: "biller", created: "9 Sep 2026 21:44:15", info: "--" },
  { id: "17", qty: 1, items: "Butter Sada Dosa", biller: "biller", created: "9 Sep 2026 21:33:32", info: "--" },
  { id: "16", qty: 2, items: "Jini Dosa, Pahadi Dosa", biller: "biller", created: "9 Sep 2026 21:33:08", info: "--" },
  { id: "15", qty: 2, items: "Paneer Cheese Chilly Dosa ( Special Dosa), Matka Gravy Dosa", biller: "biller", created: "9 Sep 2026 21:31:53", info: "--" },
];

export default function KotDetailModal({
  open,
  onClose,
  order,
  onPrint,
  onMarkAllReady,
}) {
  return (
    <>
      {/* Right Side Slide-Over Drawer Backdrop Overlay */}
      {open && (
        <div
          className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs transition-opacity duration-300"
          onClick={onClose}
        />
      )}

      {/* Right Side Slide-Over Drawer Panel with Top/Bottom Margins & Rounded Corners */}
      <div
        className={`fixed inset-y-0 right-0 sm:top-3 sm:bottom-3 sm:right-3 z-50 w-full sm:max-w-2xl md:max-w-3xl bg-white shadow-2xl border-0 sm:border border-slate-200 sm:rounded-2xl overflow-hidden transition-transform duration-300 ease-in-out flex flex-col ${
          open && order ? "translate-x-0" : "translate-x-full"
        }`}
      >
        {open && order && (
          <>
            {/* Header */}
            <div className="flex items-center justify-between border-b border-slate-200 px-6 py-4 bg-white sticky top-0 z-10">
              <h2 className="text-lg font-bold text-slate-900">
                KOT Details [Order No :- {order.ticketId || order.id || "69211"}]
              </h2>
              <button
                onClick={onClose}
                className="rounded-lg p-1 text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition cursor-pointer"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            {/* Scrollable Content */}
            <div className="flex-1 overflow-y-auto p-6 space-y-6">
              {/* Order Info Key-Value Table Grid */}
              <div className="overflow-x-auto rounded-lg border border-slate-200">
                <table className="w-full min-w-[580px] border-collapse text-xs">
                  <tbody>
                    <tr className="divide-x divide-slate-200 border-b border-slate-200">
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Order No.:</span> <span className="text-slate-700">{order.ticketId || order.id || "69211"}</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Billing User:</span> <span className="text-slate-700">{order.billingUser || "biller"}</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Customer Name:</span> <span className="text-slate-700">{order.customerName || "-"}</span></td>
                    </tr>
                    <tr className="divide-x divide-slate-200 border-b border-slate-200">
                      <td className="p-2.5"><span className="font-bold text-slate-800">Customer Phone:</span> <span className="text-slate-700">{order.phoneNumber || "-"}</span></td>
                      <td className="p-2.5"><span className="font-bold text-slate-800">Customer Address:</span> <span className="text-slate-700">{order.address || "-"}</span></td>
                      <td className="p-2.5"><span className="font-bold text-slate-800">Customer Locality:</span> <span className="text-slate-700">-</span></td>
                    </tr>
                    <tr className="divide-x divide-slate-200 border-b border-slate-200">
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">No. of Persons:</span> <span className="text-slate-700">{order.guests || "-"}</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Order Type:</span> <span className="text-slate-700">{order.orderType || "Dine In"}{order.tableNo ? ` (${order.tableNo})` : ""}</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Assign to:</span> <span className="text-slate-700">{order.captain || "-"}</span></td>
                    </tr>
                    <tr className="divide-x divide-slate-200 border-b border-slate-200">
                      <td className="p-2.5"><span className="font-bold text-slate-800">Total Tax:</span> <span className="text-slate-700">₹ 58.50</span></td>
                      <td className="p-2.5"><span className="font-bold text-slate-800">Total Discount:</span> <span className="text-slate-700">₹ (0.00)</span></td>
                      <td className="p-2.5"><span className="font-bold text-slate-800">Grand Total:</span> <span className="text-slate-700">₹ 1,399.00</span></td>
                    </tr>
                    <tr className="divide-x divide-slate-200 border-b border-slate-200">
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Settlement Amount:</span> <span className="text-slate-700">₹ 0.00</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Order Status:</span> <span className="text-slate-700">{order.status || "Completed"}</span></td>
                      <td className="p-2.5 bg-slate-50/50">
                        <span className="font-bold text-slate-800">Printed:</span> <span className="text-slate-700">Yes (1 time(s))</span>
                        <div className="text-slate-600 text-[11px]">({order.createdAt || "29 Jul 2026 19:20"})</div>
                      </td>
                    </tr>
                    <tr className="divide-x divide-slate-200 border-b border-slate-200">
                      <td className="p-2.5"><span className="font-bold text-slate-800">Paid:</span> <span className="text-slate-700">Yes</span></td>
                      <td className="p-2.5"><span className="font-bold text-slate-800">Payment Type:</span> <span className="text-slate-700">Cash</span></td>
                      <td className="p-2.5"><span className="font-bold text-slate-800">Coupon Code:</span> <span className="text-slate-700"></span></td>
                    </tr>
                    <tr className="divide-x divide-slate-200">
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Tip:</span> <span className="text-slate-700">-</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Sub Order Type:</span> <span className="text-slate-700">Dine in</span></td>
                      <td className="p-2.5 bg-slate-50/50"><span className="font-bold text-slate-800">Sequence Name:</span> <span className="text-slate-700">-</span></td>
                    </tr>
                  </tbody>
                </table>
              </div>

              {/* KOT Data Table */}
              <div>
                <h3 className="text-sm font-bold text-slate-900 mb-3">KOT List</h3>
                <div className="overflow-x-auto rounded-lg border border-slate-200">
                  <table className="w-full min-w-[520px] text-left text-xs border-collapse">
                    <thead className="bg-red-50/60 border-b border-slate-200">
                      <tr>
                        <th className="p-2.5 font-bold text-slate-800">KOT ID</th>
                        <th className="p-2.5 font-bold text-slate-800">No. Of Items</th>
                        <th className="p-2.5 font-bold text-slate-800 max-w-[320px]">Items</th>
                        <th className="p-2.5 font-bold text-slate-800">Biller</th>
                        <th className="p-2.5 font-bold text-slate-800">Created</th>
                        <th className="p-2.5 font-bold text-slate-800">Information</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-200 text-slate-700">
                      {sampleKotList.map((kot) => (
                        <tr key={kot.id} className="hover:bg-slate-50">
                          <td className="p-2.5 font-semibold text-slate-800">{kot.id}</td>
                          <td className="p-2.5 text-slate-800 font-medium">{kot.qty}</td>
                          <td className="p-2.5 font-medium text-slate-800 max-w-[320px] leading-relaxed">{kot.items}</td>
                          <td className="p-2.5 text-slate-600">{kot.biller}</td>
                          <td className="p-2.5 text-slate-700 font-medium whitespace-nowrap">{kot.created}</td>
                          <td className="p-2.5 text-slate-500">{kot.info}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>

              {/* Drawer Bottom Note & Actions */}
              <div className="flex flex-wrap items-center justify-between gap-3 pt-4 border-t border-slate-200">
                <div className="flex items-center gap-2">
                  <span className="flex items-center gap-1.5 rounded-lg bg-red-50 px-3 py-1.5 text-xs font-semibold text-red-600 border border-red-100">
                    <svg className="h-4 w-4 text-red-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
                      <circle cx="9" cy="7" r="4" />
                      <path d="M22 21v-2a4 4 0 0 3-3.87" />
                      <path d="M16 3.13a4 4 0 0 1 0 7.75" />
                    </svg>
                    <span>Modified KOT</span>
                  </span>
                </div>

                <div className="flex items-center gap-2.5">
                  <button
                    onClick={() => onPrint?.(order)}
                    className="inline-flex items-center gap-1.5 rounded-xl border border-red-200 bg-red-50/60 px-4 py-2 text-xs sm:text-sm font-semibold text-red-600 hover:bg-red-100/60 transition cursor-pointer"
                  >
                    <Printer className="w-4 h-4" />
                    <span>Print KOT</span>
                  </button>
                  <button
                    onClick={() => onMarkAllReady?.(order)}
                    className="inline-flex items-center gap-1.5 rounded-xl bg-red-600 px-4 py-2 text-xs sm:text-sm font-semibold text-white hover:bg-red-700 transition cursor-pointer shadow-xs"
                  >
                    <CheckCircle2 className="w-4 h-4" />
                    <span>Mark All Ready</span>
                  </button>
                </div>
              </div>
            </div>
          </>
        )}
      </div>
    </>
  );
}