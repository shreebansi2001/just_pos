import React from 'react';
import { Receipt, CheckCircle, AlertCircle, Ban } from 'lucide-react';

export function BillingView({ invoices, onSelectInvoice }) {
  const formatMoney = (n) => '₹' + Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 });
  const sorted = Object.values(invoices).sort((a, b) => b.createdAt - a.createdAt);

  return (
    <div className="space-y-4">
      <div className="text-xs font-bold uppercase tracking-wider text-gray-500 dark:text-gray-400">
        Invoices — Today
      </div>

      {sorted.length === 0 ? (
        <div className="text-center py-16 bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-xl">
          <Receipt className="w-10 h-10 mx-auto text-gray-400 mb-2 opacity-60" />
          <p className="text-sm font-semibold text-gray-600 dark:text-gray-300">No invoices generated yet.</p>
          <p className="text-xs text-gray-400 mt-1">Send a KOT and generate an invoice from the Order Desk.</p>
        </div>
      ) : (
        <div className="space-y-2.5">
          {sorted.map((inv) => {
            const ctx = inv.tableLabel || inv.type?.toUpperCase();
            const itemCount = inv.items?.reduce((s, i) => s + i.qty, 0) || 0;

            return (
              <div
                key={inv.id}
                onClick={() => onSelectInvoice(inv.id)}
                className="bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 hover:border-[#017A9C] rounded-xl p-4 flex items-center justify-between gap-4 cursor-pointer shadow-sm hover:shadow transition-all"
              >
                <div className="w-24 font-serif font-bold text-sm text-gray-900 dark:text-white">
                  {inv.id}
                </div>

                <div className="flex-1 min-w-0">
                  <div className="text-xs font-semibold text-gray-800 dark:text-gray-200">
                    {ctx} · {itemCount} items
                  </div>
                  <div className="text-[11px] text-gray-400">
                    {new Date(inv.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    {inv.paymentMode ? ` · Paid via ${inv.paymentMode}` : ''}
                  </div>
                </div>

                <div>
                  {inv.status === 'paid' && (
                    <span className="inline-flex items-center gap-1 text-xs font-bold text-emerald-600 bg-emerald-50 dark:bg-emerald-950/40 px-2.5 py-1 rounded-full">
                      <CheckCircle className="w-3.5 h-3.5" /> Paid
                    </span>
                  )}
                  {inv.status === 'unpaid' && (
                    <span className="inline-flex items-center gap-1 text-xs font-bold text-orange-600 bg-orange-50 dark:bg-orange-950/40 px-2.5 py-1 rounded-full">
                      <AlertCircle className="w-3.5 h-3.5" /> Unpaid
                    </span>
                  )}
                  {inv.status === 'voided' && (
                    <span className="inline-flex items-center gap-1 text-xs font-bold text-gray-500 bg-gray-100 dark:bg-gray-800 px-2.5 py-1 rounded-full line-through">
                      <Ban className="w-3.5 h-3.5" /> Voided
                    </span>
                  )}
                </div>

                <div className="w-28 text-right font-extrabold text-sm text-gray-900 dark:text-white">
                  {formatMoney(inv.total)}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
