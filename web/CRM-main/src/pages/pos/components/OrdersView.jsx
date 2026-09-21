import React, { useState } from 'react';
import {
  Search,
  Plus,
  Edit3,
  CreditCard,
  XCircle,
  Printer,
  Clock,
  CheckCircle2,
  AlertCircle,
  UtensilsCrossed,
  ShoppingBag,
  Receipt
} from 'lucide-react';

export function OrdersView({
  orders,
  tables,
  floors,
  invoices,
  cancelledOrders = [],
  onEditOrder,
  onPayOrder,
  onCancelOrder,
  onPrintOrder,
  onCreateNewOrder
}) {
  const [activeFilter, setActiveFilter] = useState('all');
  const [searchQuery, setSearchQuery] = useState('');

  const formatMoney = (n) => '₹' + Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 });

  // 1. Gather dine-in & takeaway active orders
  const liveOrdersList = Object.values(orders || {}).map((ord) => {
    const tbl = ord.tableId ? tables.find((t) => t.id === ord.tableId) : null;
    const flr = tbl ? floors.find((f) => f.id === tbl.floorId) : null;
    const itemList = Object.values(ord.items || {});
    const total = itemList.reduce((acc, item) => acc + (item.price || 0) * (item.qty || 0), 0);
    const isPending = ord.status === 'pending';

    const readyItems = itemList.filter((i) => i.status === 'ready').length;
    const cookingItems = itemList.filter((i) => i.status === 'cooking' || !i.status).length;

    let kotSummary = 'Draft order';
    if (isPending) {
      kotSummary = `⏸️ Pending / On Hold (${itemList.length} items)`;
    } else if (readyItems > 0 && cookingItems > 0) {
      kotSummary = `🍳 ${readyItems} ready · ${cookingItems} cooking`;
    } else if (readyItems > 0 && cookingItems === 0) {
      kotSummary = `✅ All ${readyItems} dishes served`;
    } else if (itemList.length > 0) {
      kotSummary = `🍳 ${itemList.length} in kitchen`;
    }

    return {
      id: ord.id,
      type: ord.type || (ord.tableId ? 'dine-in' : 'pickup'),
      typeLabel: ord.tableId ? `Table ${tbl ? tbl.shortcode || tbl.name : ord.tableId}` : 'Takeaway / Counter',
      floorLabel: flr ? flr.name : (ord.tableId ? 'Dine-In' : 'Counter'),
      tableId: ord.tableId,
      customerName: ord.customerName || (ord.tableId ? 'Dine-in Guest' : 'Counter Guest'),
      customerPhone: ord.customerPhone || '—',
      items: itemList,
      total: total,
      status: isPending ? 'pending' : (ord.status || 'open'),
      statusLabel: kotSummary,
      time: ord.createdAt ? new Date(ord.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : 'Active',
      rawOrder: ord
    };
  });

  // 2. Gather settled invoices
  const settledList = Object.values(invoices || {}).map((inv) => ({
    id: inv.orderId || inv.id,
    type: 'settled',
    typeLabel: inv.tableLabel ? `Table ${inv.tableLabel}` : 'Counter Bill',
    floorLabel: 'Settled & Invoiced',
    tableId: null,
    customerName: inv.customerName || 'Walk-in Guest',
    customerPhone: inv.customerPhone || '—',
    items: inv.items ? (Array.isArray(inv.items) ? inv.items : Object.values(inv.items)) : [],
    total: inv.grandTotal || inv.total || 0,
    status: 'settled',
    statusLabel: `✅ Paid (${inv.paymentMethod || 'Cash'})`,
    time: inv.createdAt ? new Date(inv.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : 'Today',
    rawInvoice: inv
  }));

  // 3. Gather cancelled orders
  const cancelledList = (cancelledOrders || []).map((ord) => ({
    id: ord.id || ord.orderId,
    type: 'cancelled',
    typeLabel: ord.tableLabel || (ord.tableId ? `Table ${ord.tableId}` : 'Takeaway'),
    floorLabel: 'Cancelled & Voided',
    tableId: null,
    customerName: ord.customerName || 'Guest',
    customerPhone: ord.customerPhone || '—',
    items: ord.items ? (Array.isArray(ord.items) ? ord.items : Object.values(ord.items)) : [],
    total: ord.total || 0,
    status: 'cancelled',
    statusLabel: `🚫 ${ord.cancelReason || 'Cancelled'}`,
    time: ord.cancelledAt || 'Cancelled',
    rawOrder: ord
  }));

  // Statistics calculation
  const dineInCount = liveOrdersList.filter((o) => o.type === 'dine-in' && o.status !== 'pending').length;
  const pickupCount = liveOrdersList.filter((o) => o.type !== 'dine-in' && o.status !== 'pending').length;
  const pendingOrders = liveOrdersList.filter((o) => o.status === 'pending');
  const activeOrders = liveOrdersList.filter((o) => o.status !== 'pending');
  const runningTotalVal = activeOrders.reduce((acc, o) => acc + o.total, 0);

  // Filters
  let displayed = [...liveOrdersList, ...settledList, ...cancelledList];
  if (activeFilter === 'dineIn') displayed = liveOrdersList.filter((o) => o.type === 'dine-in');
  else if (activeFilter === 'pickup') displayed = liveOrdersList.filter((o) => o.type !== 'dine-in');
  else if (activeFilter === 'pending') displayed = pendingOrders;
  else if (activeFilter === 'settled') displayed = settledList;
  else if (activeFilter === 'cancelled') displayed = cancelledList;

  if (searchQuery.trim()) {
    const q = searchQuery.toLowerCase().trim();
    displayed = displayed.filter((o) => {
      return (
        (o.id && o.id.toLowerCase().includes(q)) ||
        (o.typeLabel && o.typeLabel.toLowerCase().includes(q)) ||
        (o.customerName && o.customerName.toLowerCase().includes(q)) ||
        (o.customerPhone && o.customerPhone.toLowerCase().includes(q)) ||
        o.items.some((it) => it.name && it.name.toLowerCase().includes(q))
      );
    });
  }

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-5">
      {/* Header & Filter Tabs */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="flex flex-wrap items-center gap-2">
          <button
            onClick={() => setActiveFilter('all')}
            className={`px-3.5 py-1.5 rounded-lg text-xs font-bold transition-all ${
              activeFilter === 'all'
                ? 'bg-[#017A9C] text-white shadow-sm'
                : 'bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-200 border border-gray-200 dark:border-gray-700 hover:bg-gray-50'
            }`}
          >
            All Orders ({liveOrdersList.length + settledList.length + cancelledList.length})
          </button>
          <button
            onClick={() => setActiveFilter('dineIn')}
            className={`px-3.5 py-1.5 rounded-lg text-xs font-bold transition-all flex items-center gap-1.5 ${
              activeFilter === 'dineIn'
                ? 'bg-[#017A9C] text-white shadow-sm'
                : 'bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-200 border border-gray-200 dark:border-gray-700 hover:bg-gray-50'
            }`}
          >
            <UtensilsCrossed className="w-3.5 h-3.5" /> Dine-in ({dineInCount})
          </button>
          <button
            onClick={() => setActiveFilter('pickup')}
            className={`px-3.5 py-1.5 rounded-lg text-xs font-bold transition-all flex items-center gap-1.5 ${
              activeFilter === 'pickup'
                ? 'bg-[#017A9C] text-white shadow-sm'
                : 'bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-200 border border-gray-200 dark:border-gray-700 hover:bg-gray-50'
            }`}
          >
            <ShoppingBag className="w-3.5 h-3.5" /> Takeaway ({pickupCount})
          </button>
          <button
            onClick={() => setActiveFilter('pending')}
            className={`px-3.5 py-1.5 rounded-lg text-xs font-bold transition-all flex items-center gap-1.5 ${
              activeFilter === 'pending'
                ? 'bg-amber-600 text-white shadow-sm'
                : 'bg-amber-50 dark:bg-amber-950/40 text-amber-700 dark:text-amber-300 border border-amber-300 dark:border-amber-700 hover:bg-amber-100'
            }`}
          >
            <Clock className="w-3.5 h-3.5" /> Pending ({pendingOrders.length})
          </button>
          <button
            onClick={() => setActiveFilter('settled')}
            className={`px-3.5 py-1.5 rounded-lg text-xs font-bold transition-all flex items-center gap-1.5 ${
              activeFilter === 'settled'
                ? 'bg-emerald-600 text-white shadow-sm'
                : 'bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-200 border border-gray-200 dark:border-gray-700 hover:bg-gray-50'
            }`}
          >
            <Receipt className="w-3.5 h-3.5" /> Paid / Settled ({settledList.length})
          </button>
          <button
            onClick={() => setActiveFilter('cancelled')}
            className={`px-3.5 py-1.5 rounded-lg text-xs font-bold transition-all flex items-center gap-1.5 ${
              activeFilter === 'cancelled'
                ? 'bg-red-600 text-white shadow-sm'
                : 'bg-red-50 dark:bg-red-950/40 text-red-700 dark:text-red-300 border border-red-200 dark:border-red-800 hover:bg-red-100'
            }`}
          >
            <XCircle className="w-3.5 h-3.5" /> Cancelled ({cancelledList.length})
          </button>
        </div>

        <button
          onClick={onCreateNewOrder}
          className="px-4 py-2 bg-[#017A9C] hover:bg-[#016582] text-white rounded-xl text-xs font-bold flex items-center gap-2 shadow-sm transition-all flex-shrink-0"
        >
          <Plus className="w-4 h-4" /> Create New Order
        </button>
      </div>

      {/* KPI Stats Grid */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
        <div className="p-3.5 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl shadow-xs">
          <div className="text-[11px] font-semibold text-gray-500 dark:text-gray-400">Active Live Orders</div>
          <div className="text-xl font-bold text-gray-900 dark:text-white mt-0.5">{activeOrders.length}</div>
          <div className="text-[10px] text-gray-400 dark:text-gray-500">Currently open orders</div>
        </div>
        <div className="p-3.5 bg-white dark:bg-gray-800 border border-amber-200 dark:border-amber-800/60 rounded-xl shadow-xs">
          <div className="text-[11px] font-semibold text-amber-600 dark:text-amber-400">Pending / On Hold</div>
          <div className="text-xl font-bold text-amber-700 dark:text-amber-300 mt-0.5">{pendingOrders.length}</div>
          <div className="text-[10px] text-amber-600/70 dark:text-amber-400/70">Saved drafts awaiting action</div>
        </div>
        <div className="p-3.5 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl shadow-xs">
          <div className="text-[11px] font-semibold text-gray-500 dark:text-gray-400">Running Dine-in Tables</div>
          <div className="text-xl font-bold text-gray-900 dark:text-white mt-0.5">{dineInCount}</div>
          <div className="text-[10px] text-gray-400 dark:text-gray-500">Occupied dining tables</div>
        </div>
        <div className="p-3.5 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl shadow-xs">
          <div className="text-[11px] font-semibold text-gray-500 dark:text-gray-400">Live Running Value</div>
          <div className="text-xl font-bold text-[#017A9C] mt-0.5">{formatMoney(runningTotalVal)}</div>
          <div className="text-[10px] text-gray-400 dark:text-gray-500">Running unbilled volume</div>
        </div>
      </div>

      {/* Search Input */}
      <div className="relative">
        <Search className="w-4 h-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Search orders by #ID, table, customer name, phone, or dish..."
          className="w-full pl-9 pr-4 py-2.5 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl text-xs text-gray-900 dark:text-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[#017A9C]/40"
        />
      </div>

      {/* Orders Cards Grid */}
      {displayed.length === 0 ? (
        <div className="text-center py-16 bg-white dark:bg-gray-800/50 border border-dashed border-gray-200 dark:border-gray-700 rounded-2xl">
          <AlertCircle className="w-10 h-10 text-gray-400 mx-auto mb-2 opacity-60" />
          <h4 className="text-sm font-bold text-gray-700 dark:text-gray-200">No Orders Found</h4>
          <p className="text-xs text-gray-400 mt-1">Try selecting another filter or clear your search term.</p>
          <button
            onClick={onCreateNewOrder}
            className="mt-4 px-4 py-2 bg-[#017A9C] text-white rounded-lg text-xs font-bold inline-flex items-center gap-1.5"
          >
            <Plus className="w-3.5 h-3.5" /> Start New Order
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {displayed.map((o) => {
            const isPending = o.status === 'pending';
            const isCancelled = o.status === 'cancelled';
            const isSettled = o.status === 'settled';

            return (
              <div
                key={o.id}
                className={`bg-white dark:bg-gray-800 border rounded-2xl shadow-sm flex flex-col justify-between overflow-hidden transition-all ${
                  isPending
                    ? 'border-amber-300 dark:border-amber-700/80 bg-amber-50/20 dark:bg-amber-950/10'
                    : isCancelled
                    ? 'border-red-200 dark:border-red-900/60 opacity-80'
                    : 'border-gray-200 dark:border-gray-700 hover:border-gray-300'
                }`}
              >
                {/* Order Head */}
                <div className="p-3.5 border-b border-gray-100 dark:border-gray-700/70 flex items-center justify-between bg-gray-50/50 dark:bg-gray-800/80">
                  <div className="flex items-center gap-2">
                    <span className="font-mono font-bold text-xs text-[#017A9C] bg-[#017A9C]/10 px-2 py-0.5 rounded">
                      {o.id}
                    </span>
                    <span className="text-[11px] font-bold px-2 py-0.5 rounded-full bg-blue-50 dark:bg-blue-950/40 text-blue-700 dark:text-blue-300 border border-blue-200 dark:border-blue-800">
                      {o.typeLabel}
                    </span>
                  </div>
                  <span className="text-[11px] font-semibold text-gray-400">{o.time}</span>
                </div>

                {/* Order Body */}
                <div className="p-3.5 space-y-3 flex-1">
                  <div className="flex items-start justify-between gap-2">
                    <div>
                      <div className="text-xs font-bold text-gray-900 dark:text-white">
                        👤 {o.customerName}
                      </div>
                      <div className="text-[11px] text-gray-500 dark:text-gray-400 mt-0.5">
                        📞 {o.customerPhone} · <span className="text-[#017A9C] font-semibold">{o.floorLabel}</span>
                      </div>
                    </div>

                    {isPending ? (
                      <span className="text-[10.5px] font-bold px-2 py-0.5 rounded-full bg-amber-100 dark:bg-amber-900/50 text-amber-800 dark:text-amber-200 border border-amber-300 dark:border-amber-700 flex items-center gap-1">
                        <Clock className="w-3 h-3" /> Pending
                      </span>
                    ) : isCancelled ? (
                      <span className="text-[10.5px] font-bold px-2 py-0.5 rounded-full bg-red-100 dark:bg-red-900/50 text-red-700 dark:text-red-200 border border-red-300 dark:border-red-700 flex items-center gap-1">
                        <XCircle className="w-3 h-3" /> Cancelled
                      </span>
                    ) : isSettled ? (
                      <span className="text-[10.5px] font-bold px-2 py-0.5 rounded-full bg-emerald-100 dark:bg-emerald-900/50 text-emerald-700 dark:text-emerald-200 border border-emerald-300 dark:border-emerald-700 flex items-center gap-1">
                        <CheckCircle2 className="w-3 h-3" /> Settled
                      </span>
                    ) : (
                      <span className="text-[10.5px] font-bold px-2 py-0.5 rounded-full bg-blue-100 dark:bg-blue-900/50 text-blue-700 dark:text-blue-200 border border-blue-200">
                        {o.statusLabel}
                      </span>
                    )}
                  </div>

                  {/* Items List */}
                  <div className="bg-gray-50 dark:bg-gray-900/60 rounded-xl p-2.5 max-h-32 overflow-y-auto space-y-1.5 border border-gray-100 dark:border-gray-800">
                    {o.items && o.items.length > 0 ? (
                      o.items.map((it, idx) => (
                        <div key={idx} className="flex justify-between items-center text-[11.5px]">
                          <span className="text-gray-700 dark:text-gray-300 truncate max-w-[180px]">
                            {it.name} <b className="text-[#017A9C]">×{it.qty}</b>
                          </span>
                          <span className="font-mono font-semibold text-gray-900 dark:text-gray-200">
                            {formatMoney((it.price || 0) * (it.qty || 0))}
                          </span>
                        </div>
                      ))
                    ) : (
                      <div className="text-[11px] text-gray-400 italic">No dishes added yet</div>
                    )}
                  </div>

                  {isCancelled && o.rawOrder?.cancelReason && (
                    <div className="text-[11px] text-red-600 bg-red-50 dark:bg-red-950/30 p-2 rounded-lg border border-red-200 dark:border-red-900">
                      <b>Reason:</b> {o.rawOrder.cancelReason}
                    </div>
                  )}
                </div>

                {/* Order Foot & Actions */}
                <div className="p-3.5 border-t border-gray-100 dark:border-gray-700/70 bg-gray-50/50 dark:bg-gray-800/80 flex items-center justify-between gap-2">
                  <div>
                    <div className="text-[10px] uppercase font-bold text-gray-400 tracking-wider">Total</div>
                    <div className="text-base font-extrabold font-mono text-gray-900 dark:text-white">
                      {formatMoney(o.total)}
                    </div>
                  </div>

                  <div className="flex items-center gap-1.5 flex-wrap">
                    {!isCancelled && !isSettled ? (
                      <>
                        <button
                          onClick={() => onEditOrder(o.id)}
                          className="px-2.5 py-1.5 bg-[#017A9C] hover:bg-[#016582] text-white rounded-lg text-xs font-bold flex items-center gap-1 shadow-xs transition-colors"
                          title="Edit order in POS desk"
                        >
                          <Edit3 className="w-3.5 h-3.5" /> Edit
                        </button>
                        <button
                          onClick={() => onPayOrder(o.id)}
                          className="px-2.5 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg text-xs font-bold flex items-center gap-1 shadow-xs transition-colors"
                          title="Settle bill & Payment"
                        >
                          <CreditCard className="w-3.5 h-3.5" /> Payment
                        </button>
                        <button
                          onClick={() => onCancelOrder(o.id)}
                          className="px-2 py-1.5 bg-red-50 hover:bg-red-100 text-red-700 dark:bg-red-950/30 dark:text-red-300 dark:hover:bg-red-900/40 rounded-lg text-xs font-bold flex items-center gap-1 border border-red-200 dark:border-red-800 transition-colors"
                          title="Cancel order and free table"
                        >
                          <XCircle className="w-3.5 h-3.5" /> Cancel
                        </button>
                        <button
                          onClick={() => onPrintOrder(o.id)}
                          className="p-1.5 bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600 text-gray-700 dark:text-gray-200 rounded-lg transition-colors"
                          title="Print thermal slip"
                        >
                          <Printer className="w-3.5 h-3.5" />
                        </button>
                      </>
                    ) : isSettled ? (
                      <>
                        <span className="text-[11px] font-bold text-emerald-600 bg-emerald-50 dark:bg-emerald-950/30 px-2 py-1 rounded-md border border-emerald-200 dark:border-emerald-800">
                          Paid
                        </span>
                        <button
                          onClick={() => onPrintOrder(o.id)}
                          className="px-2.5 py-1.5 bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-200 rounded-lg text-xs font-bold flex items-center gap-1"
                          title="Print receipt"
                        >
                          <Printer className="w-3.5 h-3.5" /> Print
                        </button>
                      </>
                    ) : (
                      <>
                        <span className="text-[11px] font-bold text-red-600 bg-red-50 dark:bg-red-950/30 px-2 py-1 rounded-md border border-red-200 dark:border-red-800">
                          Voided
                        </span>
                        <button
                          onClick={() => onPrintOrder(o.id)}
                          className="p-1.5 bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-200 rounded-lg"
                          title="Print void slip"
                        >
                          <Printer className="w-3.5 h-3.5" />
                        </button>
                      </>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
