import React from 'react';
import { X, Receipt, CheckCircle, CreditCard, Banknote, QrCode } from 'lucide-react';

export function InvoiceModal({ isOpen, onClose, invoice, onSettlePayment }) {
  if (!isOpen || !invoice) return null;

  const formatMoney = (n) => '₹' + Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 });
  const ctx = invoice.tableLabel || invoice.type?.toUpperCase();

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-xs p-4">
      <div className="w-full max-w-md bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-2xl shadow-xl overflow-hidden animate-in fade-in zoom-in-95">
        {/* Header */}
        <div className="p-4 border-b border-gray-100 dark:border-gray-800 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Receipt className="w-4 h-4 text-[#017A9C]" />
            <h3 className="font-serif font-bold text-base text-gray-900 dark:text-white">
              Invoice #{invoice.id}
            </h3>
          </div>
          <button
            onClick={onClose}
            className="w-7 h-7 rounded-full bg-gray-100 dark:bg-gray-800 flex items-center justify-center text-gray-400 hover:text-gray-700 dark:hover:text-gray-200"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Invoice Printable Receipt Area */}
        <div className="p-5 overflow-y-auto max-h-[70vh] space-y-4">
          <div className="text-center pb-3 border-b border-dashed border-gray-200 dark:border-gray-700">
            <h2 className="font-serif font-bold text-xl text-gray-900 dark:text-white">
              Saffron Catering Co.
            </h2>
            <div className="text-xs text-gray-400 mt-1">
              JC Hospitality Portal · Floor &amp; Order Desk
            </div>
            <div className="text-xs font-semibold text-gray-600 dark:text-gray-300 mt-0.5">
              {ctx} · {new Date(invoice.createdAt).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' })}
            </div>
            {invoice.customerName && (
              <div className="text-xs text-gray-500 mt-1">
                Guest: {invoice.customerName} {invoice.customerPhone ? `· ${invoice.customerPhone}` : ''}
              </div>
            )}
          </div>

          {/* Itemized Table */}
          <table className="w-full text-xs text-left">
            <thead>
              <tr className="border-b border-gray-200 dark:border-gray-700 uppercase tracking-wider text-gray-400 font-bold text-[10px]">
                <th className="py-2">Item</th>
                <th className="py-2 text-right">Qty</th>
                <th className="py-2 text-right">Rate</th>
                <th className="py-2 text-right">Amount</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 dark:divide-gray-800/80">
              {invoice.items?.map((it, idx) => (
                <tr key={idx} className="text-gray-800 dark:text-gray-200">
                  <td className="py-2 font-medium">{it.name}</td>
                  <td className="py-2 text-right font-mono">{it.qty}</td>
                  <td className="py-2 text-right font-mono">{formatMoney(it.price)}</td>
                  <td className="py-2 text-right font-bold font-mono">{formatMoney(it.price * it.qty)}</td>
                </tr>
              ))}
            </tbody>
          </table>

          {/* Totals */}
          <div className="pt-2 border-t border-gray-200 dark:border-gray-700 space-y-1.5 text-xs">
            <div className="flex justify-between text-gray-600 dark:text-gray-400">
              <span>Subtotal</span>
              <span className="font-semibold text-gray-900 dark:text-white">{formatMoney(invoice.sub)}</span>
            </div>
            {invoice.disc > 0 && (
              <div className="flex justify-between text-emerald-600 font-medium">
                <span>Discount</span>
                <span>−{formatMoney(invoice.disc)}</span>
              </div>
            )}
            <div className="flex justify-between text-gray-600 dark:text-gray-400">
              <span>CGST (2.5%)</span>
              <span>{formatMoney(invoice.cgst)}</span>
            </div>
            <div className="flex justify-between text-gray-600 dark:text-gray-400">
              <span>SGST (2.5%)</span>
              <span>{formatMoney(invoice.sgst)}</span>
            </div>
            <div className="flex justify-between text-base font-extrabold text-gray-900 dark:text-white pt-2 border-t border-dashed border-gray-200 dark:border-gray-700">
              <span>Grand Total</span>
              <span className="text-[#017A9C]">{formatMoney(invoice.total)}</span>
            </div>
          </div>

          {/* Settlement Status Tag */}
          <div className="pt-2 text-center">
            {invoice.status === 'paid' ? (
              <div className="inline-flex items-center gap-1 text-xs font-bold text-emerald-600 bg-emerald-50 dark:bg-emerald-950/40 px-3 py-1 rounded-full">
                <CheckCircle className="w-3.5 h-3.5" />
                Settled via {invoice.paymentMode}
              </div>
            ) : invoice.status === 'voided' ? (
              <div className="inline-block text-xs font-bold text-gray-400 line-through">
                Voided — Order Cancelled
              </div>
            ) : (
              <div className="inline-block text-xs font-bold text-orange-600 bg-orange-50 dark:bg-orange-950/40 px-3 py-1 rounded-full">
                Payment Pending
              </div>
            )}
          </div>
        </div>

        {/* Footer with Settlement Triggers */}
        <div className="p-4 border-t border-gray-100 dark:border-gray-800 bg-gray-50/50 dark:bg-gray-800/30">
          {invoice.status === 'unpaid' ? (
            <div className="space-y-2">
              <div className="text-[11px] font-bold uppercase tracking-wider text-gray-400 text-center">
                Select Payment Mode to Settle
              </div>
              <div className="grid grid-cols-3 gap-2">
                <button
                  onClick={() => onSettlePayment(invoice.id, 'Cash')}
                  className="py-2.5 px-3 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs flex items-center justify-center gap-1.5 shadow-sm transition-all"
                >
                  <Banknote className="w-4 h-4" /> Cash
                </button>
                <button
                  onClick={() => onSettlePayment(invoice.id, 'Card')}
                  className="py-2.5 px-3 rounded-xl bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs flex items-center justify-center gap-1.5 shadow-sm transition-all"
                >
                  <CreditCard className="w-4 h-4" /> Card
                </button>
                <button
                  onClick={() => onSettlePayment(invoice.id, 'UPI')}
                  className="py-2.5 px-3 rounded-xl bg-[#017A9C] hover:bg-[#016582] text-white font-bold text-xs flex items-center justify-center gap-1.5 shadow-sm transition-all"
                >
                  <QrCode className="w-4 h-4" /> UPI
                </button>
              </div>
            </div>
          ) : (
            <button
              onClick={onClose}
              className="w-full py-2.5 rounded-xl bg-gray-900 dark:bg-white text-white dark:text-gray-900 font-bold text-xs shadow-sm"
            >
              Close Receipt
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
