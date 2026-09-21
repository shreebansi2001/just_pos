import React, { useState } from 'react';
import { Search, ArrowLeft, Trash2, Send, Receipt, Sparkles, Clock, XCircle } from 'lucide-react';

export function OrderDeskView({
  order,
  categories,
  items,
  tables,
  onBackToFloor,
  onMoveTableClick,
  onCancelOrderClick,
  onChangeItemQty,
  onOpenVariantModal,
  onSendKot,
  onSavePending,
  onGenerateInvoice,
  onClearUnsent,
  onUpdateDiscount,
  onUpdateTax,
  onUpdateCustomer
}) {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCatId, setSelectedCatId] = useState(categories[0]?.id || 'CAT01');

  const formatMoney = (n) => '₹' + Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 });

  if (!order) {
    return (
      <div className="text-center py-20 text-gray-500">
        <p className="text-base font-semibold">No order currently selected.</p>
        <button
          onClick={onBackToFloor}
          className="mt-4 px-4 py-2 rounded-lg bg-[#017A9C] text-white font-bold text-xs"
        >
          Return to floor
        </button>
      </div>
    );
  }

  const table = tables.find((t) => t.id === order.tableId);
  const activeCategories = categories.filter((c) => c.active).sort((a, b) => a.sortOrder - b.sortOrder);
  const filteredItems = items.filter((i) => {
    if (!i.active) return false;
    if (searchQuery.trim()) {
      return i.name.toLowerCase().includes(searchQuery.toLowerCase());
    }
    return i.categoryId === selectedCatId;
  });

  // Calculate cart metrics
  const cartLines = Object.entries(order.items || {});
  let subtotal = 0;
  cartLines.forEach(([, line]) => {
    subtotal += (line.price || 0) * (line.qty || 0);
  });

  let discountAmount = 0;
  if (order.discountVal > 0) {
    if (order.discountType === 'pct') {
      discountAmount = Math.round(subtotal * (order.discountVal / 100) * 100) / 100;
    } else {
      discountAmount = Math.min(order.discountVal, subtotal);
    }
  }

  const taxable = Math.max(subtotal - discountAmount, 0);
  const taxEnabled = order.taxEnabled !== false;
  const taxRate = order.taxRate !== undefined ? Number(order.taxRate) : 5;
  const halfTaxRate = taxRate / 2;
  const cgst = (taxEnabled && taxRate > 0) ? Math.round(taxable * (halfTaxRate / 100) * 100) / 100 : 0;
  const sgst = (taxEnabled && taxRate > 0) ? Math.round(taxable * (halfTaxRate / 100) * 100) / 100 : 0;
  const serviceCharge = order.serviceChargeEnabled ? Math.round(taxable * 0.05 * 100) / 100 : 0;
  const grandTotal = taxable + cgst + sgst + serviceCharge;

  const hasPendingKot = cartLines.some(([, line]) => (line.qty || 0) > (line.sentQty || 0));
  const hasItems = cartLines.length > 0;
  const canInvoice = hasItems && (order.kotIds?.length > 0 || order.type !== 'dine-in');

  return (
    <div className="space-y-4">
      {/* Top Context Bar */}
      <div className="bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-xl p-3.5 shadow-sm flex items-center justify-between gap-4 flex-wrap">
        <div className="flex items-center gap-3 flex-wrap">
          <span className="bg-[#017A9C]/10 text-[#017A9C] font-extrabold text-sm px-3 py-1 rounded-lg">
            {order.type === 'dine-in' ? table?.shortcode || 'Table' : order.type.toUpperCase()}
          </span>
          <span className="text-xs text-gray-500 dark:text-gray-400 font-mono">
            {order.id} · Started {new Date(order.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
          </span>
        </div>

        <div className="flex items-center gap-2">
          {order.type === 'dine-in' && (
            <button
              onClick={onMoveTableClick}
              className="px-3 py-1.5 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-xs font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-100"
            >
              Move Table
            </button>
          )}
          <button
            onClick={() => onCancelOrderClick(order.id)}
            className="px-3 py-1.5 rounded-lg border border-red-200 dark:border-red-900/50 text-xs font-bold text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-950/40"
          >
            Cancel Order
          </button>
          <button
            onClick={onBackToFloor}
            className="px-3 py-1.5 rounded-lg bg-gray-100 dark:bg-gray-800 hover:bg-gray-200 text-xs font-bold text-gray-800 dark:text-gray-200 flex items-center gap-1.5"
          >
            <ArrowLeft className="w-3.5 h-3.5" /> Back to floor
          </button>
        </div>
      </div>

      {/* Customer Fields for non-dine in */}
      {order.type !== 'dine-in' && (
        <div className="bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-xl p-3.5 shadow-sm grid grid-cols-1 md:grid-cols-2 gap-3">
          <input
            type="text"
            placeholder="Customer Name"
            value={order.customerName || ''}
            onChange={(e) => onUpdateCustomer(e.target.value, order.customerPhone)}
            className="px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
          />
          <input
            type="text"
            placeholder="Phone Number"
            value={order.customerPhone || ''}
            onChange={(e) => onUpdateCustomer(order.customerName, e.target.value)}
            className="px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
          />
        </div>
      )}

      {/* Main Split Layout */}
      <div className="flex flex-col lg:flex-row gap-4 items-start">
        {/* Left Menu Section */}
        <div className="flex-1 min-w-0 w-full space-y-3">
          {/* Search Bar */}
          <div className="relative">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Search dishes — paneer, biryani, mocktail, dessert..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-9 pr-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-800 bg-white dark:bg-[#151D28] text-sm focus:outline-none focus:border-[#017A9C] shadow-sm"
            />
          </div>

          {/* Category Tabs */}
          <div className="flex gap-2 overflow-x-auto pb-1 scrollbar-hide">
            {activeCategories.map((c) => (
              <button
                key={c.id}
                onClick={() => {
                  setSelectedCatId(c.id);
                  setSearchQuery('');
                }}
                className={`px-3.5 py-1.5 rounded-full text-xs font-bold transition-all whitespace-nowrap ${
                  selectedCatId === c.id && !searchQuery
                    ? 'bg-gray-900 dark:bg-white text-white dark:text-gray-900'
                    : 'bg-white dark:bg-[#151D28] text-gray-600 dark:text-gray-300 border border-gray-200 dark:border-gray-800 hover:border-gray-400'
                }`}
              >
                {c.name}
              </button>
            ))}
          </div>

          {/* Items Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3">
            {filteredItems.map((item) => {
              const currentLine = order.items?.[item.id];
              const qty = currentLine?.qty || 0;

              return (
                <div
                  key={item.id}
                  className="bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-xl p-3.5 shadow-sm flex flex-col justify-between hover:border-[#017A9C] transition-all"
                >
                  <div>
                    <div className="flex items-start justify-between gap-2">
                      <div className="flex items-center gap-2">
                        <span
                          className={`w-3 h-3 rounded-sm border-2 flex items-center justify-center ${
                            item.veg ? 'border-emerald-600' : 'border-red-600'
                          }`}
                        >
                          <span
                            className={`w-1.5 h-1.5 rounded-full ${
                              item.veg ? 'bg-emerald-600' : 'bg-red-600'
                            }`}
                          ></span>
                        </span>
                        <h4 className="text-sm font-bold text-gray-900 dark:text-white leading-snug">
                          {item.name}
                        </h4>
                      </div>
                    </div>
                    {item.tag && (
                      <p className="text-[11px] text-gray-500 dark:text-gray-400 mt-1 line-clamp-1">
                        {item.tag}
                      </p>
                    )}
                  </div>

                  <div className="mt-3 pt-2 flex items-center justify-between">
                    <span className="text-sm font-extrabold text-gray-900 dark:text-white">
                      {item.pricingType === 'kg'
                        ? `${formatMoney(item.pricePerKg || item.price)} / Kg`
                        : item.pricingType === 'both'
                        ? `${formatMoney(item.price)} / Pl · ${formatMoney(item.pricePerKg)} / Kg`
                        : item.variants
                        ? `from ${formatMoney(item.variants[0][1])}`
                        : formatMoney(item.price)}
                    </span>

                    {item.variants || item.pricingType === 'kg' || item.pricingType === 'both' || item.pricePerKg ? (
                      <button
                        onClick={() => onOpenVariantModal(item)}
                        className={`px-3 py-1 rounded-lg text-xs font-bold transition-colors ${
                          item.pricingType === 'kg'
                            ? 'bg-emerald-500/10 text-emerald-600 hover:bg-emerald-600 hover:text-white'
                            : 'bg-[#017A9C]/10 text-[#017A9C] hover:bg-[#017A9C] hover:text-white'
                        }`}
                      >
                        {item.pricingType === 'kg' ? '⚖️ Weight' : item.pricingType === 'both' ? '⚙️ Plate / KG' : 'Select Size'}
                      </button>
                    ) : qty > 0 ? (
                      <div className="flex items-center gap-2 bg-[#017A9C] text-white rounded-lg px-2 py-0.5">
                        <button
                          onClick={() => onChangeItemQty(item, -1)}
                          className="font-extrabold text-base leading-none px-1"
                        >
                          −
                        </button>
                        <span className="text-xs font-extrabold min-w-[14px] text-center">{qty}</span>
                        <button
                          onClick={() => onChangeItemQty(item, 1)}
                          className="font-extrabold text-base leading-none px-1"
                        >
                          +
                        </button>
                      </div>
                    ) : (
                      <button
                        onClick={() => onChangeItemQty(item, 1)}
                        className="px-3 py-1 rounded-lg bg-[#017A9C]/10 text-[#017A9C] hover:bg-[#017A9C] hover:text-white text-xs font-bold transition-colors"
                      >
                        + Add
                      </button>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Right Cart & Checkout Panel */}
        <div className="w-full lg:w-[360px] flex-shrink-0 bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-xl shadow-sm sticky top-[82px] flex flex-col max-h-[calc(100vh-100px)]">
          {/* Cart Header */}
          <div className="p-4 border-b border-gray-100 dark:border-gray-800">
            <h3 className="font-serif font-bold text-base text-gray-900 dark:text-white">Current Order</h3>
            <div className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">
              {cartLines.reduce((s, [, l]) => s + l.qty, 0)} items in order
            </div>
          </div>

          {/* Cart Item Lines */}
          <div className="flex-1 overflow-y-auto p-4 space-y-3 min-h-[120px]">
            {cartLines.length === 0 ? (
              <div className="text-center py-8 text-gray-400 text-xs">
                Tap dishes from the menu to add them here.
              </div>
            ) : (
              cartLines.map(([key, line]) => {
                const pending = line.qty - (line.sentQty || 0);

                return (
                  <div key={key} className="flex items-start justify-between gap-2 pb-2.5 border-b border-gray-100 dark:border-gray-800/80">
                    <div className="min-w-0 flex-1">
                      <div className="text-xs font-bold text-gray-900 dark:text-white leading-tight">
                        {line.name}
                      </div>
                      <div className="text-[11px] text-gray-500 dark:text-gray-400 mt-0.5">
                        {formatMoney(line.price)} each
                      </div>
                      {line.sentQty > 0 && line.sentQty === line.qty ? (
                        <span className="inline-block text-[10px] font-bold text-blue-600 bg-blue-50 dark:bg-blue-950/40 px-1.5 py-0.5 rounded mt-1">
                          All {line.qty} sent to kitchen
                        </span>
                      ) : line.sentQty > 0 ? (
                        <span className="inline-block text-[10px] font-bold text-amber-600 bg-amber-50 dark:bg-amber-950/40 px-1.5 py-0.5 rounded mt-1">
                          {line.sentQty} sent · {pending} pending
                        </span>
                      ) : null}
                    </div>

                    <div className="flex flex-col items-end gap-1.5">
                      <span className="text-xs font-extrabold text-gray-900 dark:text-white">
                        {formatMoney(line.price * line.qty)}
                      </span>
                      <div className="flex items-center gap-1.5 bg-gray-100 dark:bg-gray-800 rounded px-1.5 py-0.5">
                        <button
                          onClick={() => onChangeItemQty({ id: line.itemId, name: line.name, price: line.price }, -1, key)}
                          className="font-bold text-xs text-gray-700 dark:text-gray-200"
                        >
                          −
                        </button>
                        <span className="text-xs font-bold min-w-[12px] text-center">{line.qty}</span>
                        <button
                          onClick={() => onChangeItemQty({ id: line.itemId, name: line.name, price: line.price }, 1, key)}
                          className="font-bold text-xs text-gray-700 dark:text-gray-200"
                        >
                          +
                        </button>
                      </div>
                    </div>
                  </div>
                );
              })
            )}
          </div>

          {/* Calculations Summary */}
          <div className="p-4 border-t border-gray-100 dark:border-gray-800 space-y-1.5 text-xs text-gray-600 dark:text-gray-300">
            <div className="flex justify-between">
              <span>Subtotal</span>
              <span className="font-semibold text-gray-900 dark:text-white">{formatMoney(subtotal)}</span>
            </div>

            {/* Discount Section */}
            <div className="p-2 bg-gray-50 dark:bg-gray-800/60 rounded-lg border border-gray-100 dark:border-gray-700/60 space-y-1.5">
              <div className="flex items-center justify-between">
                <span className="font-bold text-gray-700 dark:text-gray-200 flex items-center gap-1 text-[11px]">
                  🏷️ Discount
                </span>
                {discountAmount > 0 && (
                  <span className="text-emerald-600 font-bold text-[11px]">−{formatMoney(discountAmount)}</span>
                )}
              </div>
              <div className="flex items-center gap-1.5 flex-wrap">
                <select
                  value={order.discountType || 'pct'}
                  onChange={(e) => onUpdateDiscount(e.target.value, order.discountVal || 0)}
                  className="px-1.5 py-1 rounded border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-[11px] font-semibold focus:outline-none"
                >
                  <option value="pct">% Off</option>
                  <option value="flat">₹ Flat</option>
                </select>
                <input
                  type="number"
                  min="0"
                  value={order.discountVal || 0}
                  onChange={(e) => onUpdateDiscount(order.discountType || 'pct', parseFloat(e.target.value) || 0)}
                  className="w-14 px-1.5 py-1 rounded border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-[11px] font-semibold text-right focus:outline-none"
                />
                <div className="flex gap-1">
                  {[5, 10, 15].map((p) => (
                    <button
                      key={p}
                      type="button"
                      onClick={() => onUpdateDiscount('pct', p)}
                      className={`px-1.5 py-0.5 rounded text-[10px] font-bold border transition-all ${
                        order.discountType === 'pct' && order.discountVal === p
                          ? 'bg-[#017A9C] text-white border-[#017A9C]'
                          : 'bg-white dark:bg-gray-800 border-gray-200 dark:border-gray-700 text-gray-600 dark:text-gray-300 hover:border-[#017A9C]'
                      }`}
                    >
                      {p}%
                    </button>
                  ))}
                  {[50, 100].map((f) => (
                    <button
                      key={f}
                      type="button"
                      onClick={() => onUpdateDiscount('flat', f)}
                      className={`px-1.5 py-0.5 rounded text-[10px] font-bold border transition-all ${
                        order.discountType === 'flat' && order.discountVal === f
                          ? 'bg-[#017A9C] text-white border-[#017A9C]'
                          : 'bg-white dark:bg-gray-800 border-gray-200 dark:border-gray-700 text-gray-600 dark:text-gray-300 hover:border-[#017A9C]'
                      }`}
                    >
                      ₹{f}
                    </button>
                  ))}
                </div>
                {order.discountVal > 0 && (
                  <button
                    type="button"
                    onClick={() => onUpdateDiscount('pct', 0)}
                    className="px-1.5 py-0.5 rounded text-[10px] font-bold text-red-600 bg-red-50 border border-red-200 hover:bg-red-100"
                    title="Remove Discount"
                  >
                    ✕
                  </button>
                )}
              </div>
            </div>

            {discountAmount > 0 && (
              <div className="flex justify-between text-gray-500 text-[11px]">
                <span>Taxable Amount</span>
                <span>{formatMoney(taxable)}</span>
              </div>
            )}

            {/* Tax Section (Add / Remove Tax & Charges) */}
            <div className="p-2 bg-gray-50 dark:bg-gray-800/60 rounded-lg border border-gray-100 dark:border-gray-700/60 space-y-1.5">
              <div className="flex items-center justify-between">
                <label className="font-bold text-gray-700 dark:text-gray-200 flex items-center gap-1.5 cursor-pointer text-[11px]">
                  <input
                    type="checkbox"
                    checked={order.taxEnabled !== false}
                    onChange={(e) => onUpdateTax && onUpdateTax(e.target.checked, order.taxRate ?? 5, !!order.serviceChargeEnabled)}
                    className="rounded text-[#017A9C] focus:ring-0 cursor-pointer"
                  />
                  <span>Apply Taxes</span>
                </label>
                <div className="flex items-center gap-1">
                  <select
                    value={order.taxRate ?? 5}
                    onChange={(e) => onUpdateTax && onUpdateTax(true, parseFloat(e.target.value) || 0, !!order.serviceChargeEnabled)}
                    className="px-1 py-0.5 rounded border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-[10px] font-semibold"
                  >
                    <option value="5">5% GST</option>
                    <option value="12">12% GST</option>
                    <option value="18">18% GST</option>
                    <option value="0">0% Exempt</option>
                  </select>
                  {order.taxEnabled !== false && (order.taxRate ?? 5) > 0 ? (
                    <button
                      type="button"
                      onClick={() => onUpdateTax && onUpdateTax(false, order.taxRate ?? 5, !!order.serviceChargeEnabled)}
                      className="px-1.5 py-0.5 rounded text-[10px] font-bold text-red-600 bg-red-50 border border-red-200 hover:bg-red-100"
                      title="Remove Tax"
                    >
                      ✕ Remove
                    </button>
                  ) : (
                    <button
                      type="button"
                      onClick={() => onUpdateTax && onUpdateTax(true, 5, !!order.serviceChargeEnabled)}
                      className="px-1.5 py-0.5 rounded text-[10px] font-bold text-emerald-600 bg-emerald-50 border border-emerald-200 hover:bg-emerald-100"
                      title="Add Tax"
                    >
                      + Add Tax
                    </button>
                  )}
                </div>
              </div>

              {/* Service Charge (5%) */}
              <div className="flex items-center justify-between pt-1 border-t border-dashed border-gray-200 dark:border-gray-700 text-[10.5px]">
                <label className="text-gray-500 dark:text-gray-400 flex items-center gap-1.5 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={!!order.serviceChargeEnabled}
                    onChange={(e) => onUpdateTax && onUpdateTax(order.taxEnabled !== false, order.taxRate ?? 5, e.target.checked)}
                    className="rounded text-[#017A9C] focus:ring-0 cursor-pointer"
                  />
                  <span>Service Charge (5%)</span>
                </label>
                <span>{formatMoney(serviceCharge)}</span>
              </div>
            </div>

            {order.taxEnabled !== false && (order.taxRate ?? 5) > 0 ? (
              <>
                <div className="flex justify-between">
                  <span>CGST ({((order.taxRate ?? 5) / 2).toFixed(1)}%)</span>
                  <span>{formatMoney(cgst)}</span>
                </div>
                <div className="flex justify-between">
                  <span>SGST ({((order.taxRate ?? 5) / 2).toFixed(1)}%)</span>
                  <span>{formatMoney(sgst)}</span>
                </div>
              </>
            ) : (
              <div className="flex justify-between text-red-600 italic">
                <span>Taxes (Removed / Exempt)</span>
                <span>₹0.00</span>
              </div>
            )}

            {order.serviceChargeEnabled && (
              <div className="flex justify-between text-gray-600 dark:text-gray-300">
                <span>Service Charge (5%)</span>
                <span>{formatMoney(serviceCharge)}</span>
              </div>
            )}

            <div className="flex justify-between text-sm font-extrabold text-gray-900 dark:text-white pt-2 border-t border-dashed border-gray-200 dark:border-gray-700">
              <span>Total Payable</span>
              <span className="text-[#017A9C]">{formatMoney(grandTotal)}</span>
            </div>
          </div>

          {/* Action Triggers */}
          <div className="p-4 border-t border-gray-100 dark:border-gray-800 space-y-2">
            <div className="flex gap-2">
              <button
                onClick={onSavePending}
                className="flex-1 py-2.5 px-3 rounded-xl border border-amber-400 bg-amber-50 hover:bg-amber-100 dark:bg-amber-950/40 dark:border-amber-600 text-amber-800 dark:text-amber-200 text-xs font-bold flex items-center justify-center gap-1.5 transition-colors shadow-sm"
                title="Save order in Pending status without sending to kitchen"
              >
                <Clock className="w-3.5 h-3.5" /> Save as Pending
              </button>
              <button
                onClick={onSendKot}
                disabled={!hasPendingKot}
                className="flex-1 py-2.5 px-3 rounded-xl bg-[#017A9C] hover:bg-[#016582] text-white font-bold text-xs flex items-center justify-center gap-1.5 shadow-sm transition-all disabled:opacity-40 disabled:cursor-not-allowed"
              >
                <Send className="w-3.5 h-3.5" />
                Send to KOT
              </button>
            </div>

            <div className="flex gap-2">
              <button
                onClick={onCancelOrderClick}
                className="py-2 px-3 rounded-lg border border-red-200 bg-red-50 hover:bg-red-100 dark:bg-red-950/30 dark:border-red-800 text-xs font-bold text-red-700 dark:text-red-300 flex items-center justify-center gap-1"
                title="Cancel this order"
              >
                <XCircle className="w-3.5 h-3.5" /> Cancel
              </button>
              <button
                onClick={onClearUnsent}
                className="py-2 px-3 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-xs font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-100"
              >
                Clear
              </button>
              <button
                onClick={onGenerateInvoice}
                disabled={!canInvoice}
                className="flex-1 py-2 px-3 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold shadow-sm transition-all disabled:opacity-40 disabled:cursor-not-allowed flex items-center justify-center gap-1.5"
              >
                <Receipt className="w-3.5 h-3.5" /> Generate Invoice
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
