import React, { useState } from 'react';
import { X, Receipt, CheckCircle, CreditCard, Banknote, QrCode, Printer, Share2 } from 'lucide-react';

function numberToWordsINR(amount) {
  const ones = ['', 'One', 'Two', 'Three', 'Four', 'Five', 'Six', 'Seven', 'Eight', 'Nine', 'Ten',
    'Eleven', 'Twelve', 'Thirteen', 'Fourteen', 'Fifteen', 'Sixteen', 'Seventeen', 'Eighteen', 'Nineteen'];
  const tens = ['', '', 'Twenty', 'Thirty', 'Forty', 'Fifty', 'Sixty', 'Seventy', 'Eighty', 'Ninety'];

  function convertGroup(n) {
    if (n === 0) return '';
    if (n < 20) return ones[n];
    if (n < 100) return tens[Math.floor(n / 10)] + (n % 10 !== 0 ? ' ' + ones[n % 10] : '');
    return ones[Math.floor(n / 100)] + ' Hundred' + (n % 100 !== 0 ? ' ' + convertGroup(n % 100) : '');
  }

  const n = Math.round(amount);
  if (n === 0) return 'INR Zero Only';

  const crore = Math.floor(n / 10000000);
  const lakh = Math.floor((n % 10000000) / 100000);
  const thousand = Math.floor((n % 100000) / 1000);
  const remainder = n % 1000;

  let res = '';
  if (crore > 0) res += convertGroup(crore) + ' Crore ';
  if (lakh > 0) res += convertGroup(lakh) + ' Lakh ';
  if (thousand > 0) res += convertGroup(thousand) + ' Thousand ';
  if (remainder > 0) res += convertGroup(remainder) + ' ';

  return 'INR ' + res.trim() + ' Only';
}

export function InvoiceModal({ isOpen, onClose, invoice, onSettlePayment }) {
  const [activeTab, setActiveTab] = useState('tax_invoice'); // 'tax_invoice', 'gate_pass', 'digital'

  if (!isOpen || !invoice) return null;

  const formatMoney = (n) => Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  
  // Total & Reverse GST calculation matching Amoncar Live Bill
  const total = Number(invoice.total || invoice.sub || 0);
  const taxableAmount = invoice.taxableAmount ? Number(invoice.taxableAmount) : Math.round((total / 1.05) * 100) / 100;
  const cgst = invoice.cgst ? Number(invoice.cgst) : Math.round((taxableAmount * 0.025) * 100) / 100;
  const sgst = invoice.sgst ? Number(invoice.sgst) : Math.round((taxableAmount * 0.025) * 100) / 100;

  const totalQty = (invoice.items || []).reduce((s, it) => {
    const q = it.unitMode === 'kg' ? (Number(it.weightKg || 1) * Number(it.qty || 1)) : Number(it.qty || 1);
    return s + q;
  }, 0);

  const dateStr = invoice.dateFormatted || new Date(invoice.createdAt || Date.now()).toLocaleDateString('en-GB');
  const customerName = invoice.customerName || 'Vijay Khorjuvekar';
  const customerPhone = invoice.customerPhone || '8830768469';
  const billType = invoice.paymentMode || 'Cash';
  const amountWords = invoice.amountInWords || numberToWordsINR(total);

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-xs p-4 overflow-y-auto">
      {/* 80mm Thermal Print Style Injection */}
      <style>{`
        @media print {
          @page {
            size: 80mm auto;
            margin: 0mm !important;
          }
          html, body {
            width: 80mm !important;
            margin: 0 !important;
            padding: 0 !important;
            background: #ffffff !important;
            color: #000000 !important;
          }
          body * {
            visibility: hidden !important;
          }
          #print80mmAmoncar, #print80mmAmoncar * {
            visibility: visible !important;
          }
          #print80mmAmoncar {
            display: block !important;
            position: absolute !important;
            left: 0 !important;
            top: 0 !important;
            width: 72mm !important;
            max-width: 72mm !important;
            margin: 0 !important;
            padding: 2mm !important;
            box-sizing: border-box !important;
            border: 1.5px solid #000 !important;
            color: #000 !important;
            background: #fff !important;
          }
        }
      `}</style>

      <div className="w-full max-w-md bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-2xl shadow-2xl overflow-hidden animate-in fade-in zoom-in-95 my-auto">
        {/* Modal Header */}
        <div className="p-4 border-b border-gray-100 dark:border-gray-800 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Receipt className="w-4 h-4 text-[#017A9C]" />
            <h3 className="font-serif font-bold text-sm text-gray-900 dark:text-white">
              Tax Invoice · {invoice.id}
            </h3>
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={handlePrint}
              className="p-1.5 rounded-lg bg-gray-100 dark:bg-gray-800 hover:bg-gray-200 text-gray-700 dark:text-gray-200 flex items-center gap-1 text-xs font-bold"
              title="Print 80mm Thermal Slip"
            >
              <Printer className="w-3.5 h-3.5" /> Print
            </button>
            <button
              onClick={onClose}
              className="w-7 h-7 rounded-full bg-gray-100 dark:bg-gray-800 flex items-center justify-center text-gray-400 hover:text-gray-700 dark:hover:text-gray-200"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* View Tabs */}
        <div className="px-4 pt-3 pb-1 border-b border-gray-100 dark:border-gray-800">
          <div className="grid grid-cols-3 gap-1 bg-gray-100 dark:bg-gray-800/80 p-1 rounded-xl text-xs font-bold">
            <button
              type="button"
              onClick={() => setActiveTab('tax_invoice')}
              className={`py-1.5 rounded-lg transition-all text-center ${
                activeTab === 'tax_invoice'
                  ? 'bg-white dark:bg-gray-700 text-[#017A9C] shadow-xs font-extrabold'
                  : 'text-gray-600 dark:text-gray-400'
              }`}
            >
              🧾 Tax Invoice
            </button>
            <button
              type="button"
              onClick={() => setActiveTab('gate_pass')}
              className={`py-1.5 rounded-lg transition-all text-center ${
                activeTab === 'gate_pass'
                  ? 'bg-white dark:bg-gray-700 text-emerald-600 shadow-xs font-extrabold'
                  : 'text-gray-600 dark:text-gray-400'
              }`}
            >
              📋 Gate Pass
            </button>
            <button
              type="button"
              onClick={() => setActiveTab('digital')}
              className={`py-1.5 rounded-lg transition-all text-center ${
                activeTab === 'digital'
                  ? 'bg-white dark:bg-gray-700 text-blue-600 shadow-xs font-extrabold'
                  : 'text-gray-600 dark:text-gray-400'
              }`}
            >
              📲 WhatsApp
            </button>
          </div>
        </div>

        {/* Printable Receipt Body */}
        <div className="p-4 overflow-y-auto max-h-[65vh] bg-gray-50/50 dark:bg-gray-900/30">
          <div
            id="print80mmAmoncar"
            className="bg-white text-black p-3.5 rounded-lg border-[1.5px] border-black text-[11px] leading-tight font-sans shadow-xs mx-auto max-w-[360px]"
          >
            {activeTab === 'gate_pass' ? (
              /* Image 3: Gate Pass / Dispatch Slip */
              <div>
                <table className="w-full border-collapse border-[1.5px] border-black mb-[-1px]">
                  <tbody>
                    <tr>
                      <td className="p-1 border-[1.5px] border-black font-extrabold w-1/3">Invoice No</td>
                      <td className="p-1 border-[1.5px] border-black font-bold">{invoice.id}</td>
                    </tr>
                    <tr>
                      <td className="p-1 border-[1.5px] border-black font-extrabold">Dt</td>
                      <td className="p-1 border-[1.5px] border-black font-bold">{dateStr}</td>
                    </tr>
                    <tr>
                      <td className="p-1 border-[1.5px] border-black font-extrabold">Party</td>
                      <td className="p-1 border-[1.5px] border-black font-bold">{customerName}</td>
                    </tr>
                    <tr>
                      <td className="p-1 border-[1.5px] border-black font-extrabold">Phone</td>
                      <td className="p-1 border-[1.5px] border-black font-bold">{customerPhone}</td>
                    </tr>
                  </tbody>
                </table>

                <table className="w-full border-collapse border-[1.5px] border-black">
                  <thead>
                    <tr className="bg-gray-100 font-extrabold">
                      <th className="p-1.5 border-[1.5px] border-black text-left w-16">S. No.</th>
                      <th className="p-1.5 border-[1.5px] border-black text-left">Item</th>
                      <th className="p-1.5 border-[1.5px] border-black text-right w-20">Qty</th>
                    </tr>
                  </thead>
                  <tbody>
                    {(invoice.items || []).map((it, idx) => {
                      const itQty = it.unitMode === 'kg' ? (Number(it.weightKg || 1) * Number(it.qty || 1)) : Number(it.qty || 1);
                      const itName = it.unitMode === 'kg' && !it.name?.toLowerCase().startsWith('kg') ? `KG-${it.name}` : it.name;
                      return (
                        <tr key={idx}>
                          <td className="p-1 border border-gray-400 font-mono text-left">{idx + 1}</td>
                          <td className="p-1 border border-gray-400 font-bold">{itName}</td>
                          <td className="p-1 border border-gray-400 font-mono text-right">{itQty.toFixed(2)}</td>
                        </tr>
                      );
                    })}
                    <tr className="bg-gray-100 font-extrabold border-t-[1.5px] border-black">
                      <td colSpan="2" className="p-1 border-[1.5px] border-black text-left">Total</td>
                      <td className="p-1 border-[1.5px] border-black font-mono text-right">{totalQty.toFixed(2)}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            ) : (
              /* Image 2: Full Tax Invoice */
              <div>
                <div className="text-center mb-2">
                  <div className="font-extrabold text-[13px] leading-snug">
                    A unit of Amoncar Classic Catering Services Pvt Ltd.
                  </div>
                  <div className="text-[10px] mt-0.5">
                    Athil Peth, Bicholim, GoaBICHOLIM, Goa - 403504, India
                  </div>
                  <div className="text-[10px]">
                    Phone No: 7767004056 | GST No: 30AAQCA8024P1ZC
                  </div>
                  <div className="text-[10px]">
                    PAN : AAQCA8024P
                  </div>
                  <div className="font-black text-[12px] tracking-wider mt-1 uppercase">
                    TAX INVOICE
                  </div>
                </div>

                <table className="w-full border-collapse border-[1.5px] border-black mb-[-1px]">
                  <tbody>
                    <tr>
                      <td className="p-1 border-[1.5px] border-black font-extrabold w-1/3">Invoice No</td>
                      <td className="p-1 border-[1.5px] border-black font-bold">{invoice.id}</td>
                    </tr>
                    <tr>
                      <td className="p-1 border-[1.5px] border-black font-extrabold">Dt</td>
                      <td className="p-1 border-[1.5px] border-black font-bold">{dateStr}</td>
                    </tr>
                    <tr>
                      <td className="p-1 border-[1.5px] border-black font-extrabold">Customer Name</td>
                      <td className="p-1 border-[1.5px] border-black font-bold">{customerName}</td>
                    </tr>
                    <tr>
                      <td className="p-1 border-[1.5px] border-black font-extrabold">Phone :</td>
                      <td className="p-1 border-[1.5px] border-black font-bold">{customerPhone}</td>
                    </tr>
                    <tr>
                      <td className="p-1 border-[1.5px] border-black font-extrabold">Bill Type :</td>
                      <td className="p-1 border-[1.5px] border-black font-bold">{billType}</td>
                    </tr>
                  </tbody>
                </table>

                <table className="w-full border-collapse border-[1.5px] border-black mb-[-1px]">
                  <thead>
                    <tr className="bg-gray-100 font-extrabold">
                      <th className="p-1 border-[1.5px] border-black text-left">Description</th>
                      <th className="p-1 border-[1.5px] border-black text-right w-14">Qty</th>
                      <th className="p-1 border-[1.5px] border-black text-right w-16">Item Rate</th>
                      <th className="p-1 border-[1.5px] border-black text-right w-20">Amount</th>
                    </tr>
                  </thead>
                  <tbody>
                    {(invoice.items || []).map((it, idx) => {
                      const itQty = it.unitMode === 'kg' ? (Number(it.weightKg || 1) * Number(it.qty || 1)) : Number(it.qty || 1);
                      const itRate = it.unitMode === 'kg' ? Number(it.pricePerKg || it.price) : Number(it.price);
                      const itAmt = Number(it.price) * Number(it.qty || 1);
                      const itName = it.unitMode === 'kg' && !it.name?.toLowerCase().startsWith('kg') ? `KG-${it.name}` : it.name;
                      return (
                        <tr key={idx}>
                          <td className="p-1 border border-gray-300 font-medium">
                            <div>{itName}</div>
                            {it.addons && it.addons.length > 0 && (
                              <div className="text-[9px] text-gray-600">
                                + {it.addons.map((a) => a.name).join(', ')}
                              </div>
                            )}
                          </td>
                          <td className="p-1 border border-gray-300 font-mono text-right">{itQty.toFixed(2)}</td>
                          <td className="p-1 border border-gray-300 font-mono text-right">{formatMoney(itRate)}</td>
                          <td className="p-1 border border-gray-300 font-mono font-bold text-right">{formatMoney(itAmt)}</td>
                        </tr>
                      );
                    })}
                    <tr className="bg-gray-100 font-extrabold border-t-[1.5px] border-black">
                      <td className="p-1 border-[1.5px] border-black text-left">Total</td>
                      <td className="p-1 border-[1.5px] border-black font-mono text-right">{totalQty.toFixed(2)}</td>
                      <td className="p-1 border-[1.5px] border-black"></td>
                      <td className="p-1 border-[1.5px] border-black font-mono text-right">{formatMoney(total)}</td>
                    </tr>
                  </tbody>
                </table>

                <table className="w-full border-collapse border-[1.5px] border-black mb-[-1px]">
                  <tbody>
                    <tr>
                      <td className="p-1 border border-gray-300 font-bold">Sub Total</td>
                      <td className="p-1 border border-gray-300 font-mono font-bold text-right">{formatMoney(total)}</td>
                    </tr>
                    <tr>
                      <td className="p-1 border border-gray-300 font-bold">Taxable Amount</td>
                      <td className="p-1 border border-gray-300 font-mono text-right">{formatMoney(taxableAmount)}</td>
                    </tr>
                    <tr>
                      <td className="p-1 border border-gray-300 font-bold">CGST</td>
                      <td className="p-1 border border-gray-300 font-mono text-right">{formatMoney(cgst)}</td>
                    </tr>
                    <tr>
                      <td className="p-1 border border-gray-300 font-bold">SGST/UTGST</td>
                      <td className="p-1 border border-gray-300 font-mono text-right">{formatMoney(sgst)}</td>
                    </tr>
                    <tr className="bg-gray-100 font-black border-t-[1.5px] border-black">
                      <td className="p-1 border-[1.5px] border-black font-black">Bill Total</td>
                      <td className="p-1 border-[1.5px] border-black font-mono font-black text-right">{formatMoney(total)}</td>
                    </tr>
                  </tbody>
                </table>

                <div className="border-[1.5px] border-black p-1.5 font-extrabold text-[9.5px]">
                  {amountWords}
                </div>
              </div>
            )}
          </div>

          {/* Digital WhatsApp dispatch trigger */}
          {activeTab === 'digital' && (
            <div className="mt-3 p-3 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-300 rounded-xl space-y-2">
              <div className="text-xs font-bold text-emerald-800 dark:text-emerald-300 flex items-center justify-between">
                <span>📲 Instant WhatsApp Bill Dispatch</span>
                <span className="text-[10px] bg-emerald-600 text-white px-2 py-0.5 rounded-full font-extrabold">Instant</span>
              </div>
              <div className="flex gap-2">
                <input
                  type="tel"
                  defaultValue={customerPhone}
                  id="waInputPhone"
                  className="flex-1 text-xs font-bold bg-white dark:bg-gray-900 border border-emerald-300 rounded-lg px-2.5 py-1.5"
                  placeholder="Mobile No."
                />
                <button
                  type="button"
                  onClick={() => {
                    const el = document.getElementById('waInputPhone');
                    const ph = el ? el.value.replace(/[^0-9]/g, '') : '918830768469';
                    const text = `🧾 *A unit of Amoncar Classic Catering Services Pvt Ltd.*\n*Tax Invoice:* ${invoice.id}\n*Date:* ${dateStr}\n*Party:* ${customerName}\n*Bill Total:* ₹${formatMoney(total)}\n_${amountWords}_\nThank you for choosing us!`;
                    window.open(`https://api.whatsapp.com/send?phone=91${ph.slice(-10)}&text=${encodeURIComponent(text)}`, '_blank');
                  }}
                  className="px-3 py-1.5 bg-[#25D366] hover:bg-[#1eb857] text-white text-xs font-extrabold rounded-lg flex items-center gap-1 shadow-xs"
                >
                  <Share2 className="w-3.5 h-3.5" /> Send Bill
                </button>
              </div>
            </div>
          )}
        </div>

        {/* Footer with Settlement Triggers */}
        <div className="p-4 border-t border-gray-100 dark:border-gray-800 bg-gray-50/50 dark:bg-gray-800/30">
          {invoice.status === 'unpaid' ? (
            <div className="space-y-2">
              <div className="text-[10px] font-bold uppercase tracking-wider text-gray-400 text-center">
                Select Payment Mode to Settle
              </div>
              <div className="grid grid-cols-3 gap-2">
                <button
                  onClick={() => onSettlePayment(invoice.id, 'Cash')}
                  className="py-2 px-3 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs flex items-center justify-center gap-1.5 shadow-xs transition-all"
                >
                  <Banknote className="w-4 h-4" /> Cash
                </button>
                <button
                  onClick={() => onSettlePayment(invoice.id, 'Card')}
                  className="py-2 px-3 rounded-xl bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs flex items-center justify-center gap-1.5 shadow-xs transition-all"
                >
                  <CreditCard className="w-4 h-4" /> Card
                </button>
                <button
                  onClick={() => onSettlePayment(invoice.id, 'UPI')}
                  className="py-2 px-3 rounded-xl bg-[#017A9C] hover:bg-[#016582] text-white font-bold text-xs flex items-center justify-center gap-1.5 shadow-xs transition-all"
                >
                  <QrCode className="w-4 h-4" /> UPI
                </button>
              </div>
            </div>
          ) : (
            <div className="flex gap-2">
              <button
                onClick={handlePrint}
                className="flex-1 py-2.5 rounded-xl bg-gray-100 dark:bg-gray-800 hover:bg-gray-200 text-gray-800 dark:text-gray-200 font-bold text-xs flex items-center justify-center gap-1.5 shadow-xs"
              >
                <Printer className="w-4 h-4" /> Print (80mm POS-80C)
              </button>
              <button
                onClick={onClose}
                className="flex-1 py-2.5 rounded-xl bg-gray-900 dark:bg-white text-white dark:text-gray-900 font-bold text-xs shadow-xs"
              >
                Close Receipt
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
