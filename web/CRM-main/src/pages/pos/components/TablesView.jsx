import React from 'react';
import { ShoppingBag, Bike, Users, Sparkles, AlertCircle } from 'lucide-react';

export function TablesView({
  floors,
  tables,
  orders,
  reservations,
  activeFloorId,
  onSelectFloor,
  onStartOrder,
  onOpenOrder,
  onFreeTable,
  onMarkTableClean,
  onSeatReservation,
  onCancelOrder,
  onOpenInvoice
}) {
  const todayStr = new Date().toISOString().split('T')[0];

  const filteredTables = tables.filter((t) => {
    if (!t.active) return false;
    if (activeFloorId !== 'all' && t.floorId !== activeFloorId) return false;
    return true;
  });

  const otherOrders = Object.values(orders).filter((o) => o.type !== 'dine-in');

  const getUpcomingReservation = (tableId) => {
    return Object.values(reservations).find(
      (r) => r.tableId === tableId && r.date === todayStr && r.status === 'upcoming'
    );
  };

  const formatMoney = (n) => '₹' + Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 });

  return (
    <div className="space-y-6">
      {/* Quick Launchers */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
        <button
          onClick={() => onStartOrder('takeaway')}
          className="bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 hover:border-[#017A9C] dark:hover:border-[#017A9C] rounded-xl p-4 flex items-center gap-3 text-left transition-all shadow-sm group hover:-translate-y-0.5"
        >
          <div className="w-10 h-10 rounded-lg bg-[#017A9C]/10 text-[#017A9C] flex items-center justify-center flex-shrink-0 group-hover:bg-[#017A9C] group-hover:text-white transition-colors">
            <ShoppingBag className="w-5 h-5" />
          </div>
          <div>
            <div className="text-sm font-bold text-gray-900 dark:text-white">Takeaway order</div>
            <div className="text-xs text-gray-500 dark:text-gray-400">Walk-in counter, no table</div>
          </div>
        </button>

        <button
          onClick={() => onStartOrder('delivery')}
          className="bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 hover:border-[#017A9C] dark:hover:border-[#017A9C] rounded-xl p-4 flex items-center gap-3 text-left transition-all shadow-sm group hover:-translate-y-0.5"
        >
          <div className="w-10 h-10 rounded-lg bg-[#017A9C]/10 text-[#017A9C] flex items-center justify-center flex-shrink-0 group-hover:bg-[#017A9C] group-hover:text-white transition-colors">
            <Bike className="w-5 h-5" />
          </div>
          <div>
            <div className="text-sm font-bold text-gray-900 dark:text-white">Delivery order</div>
            <div className="text-xs text-gray-500 dark:text-gray-400">Address &amp; phone details</div>
          </div>
        </button>

        <button
          onClick={() => onStartOrder('catering')}
          className="bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 hover:border-[#017A9C] dark:hover:border-[#017A9C] rounded-xl p-4 flex items-center gap-3 text-left transition-all shadow-sm group hover:-translate-y-0.5"
        >
          <div className="w-10 h-10 rounded-lg bg-[#017A9C]/10 text-[#017A9C] flex items-center justify-center flex-shrink-0 group-hover:bg-[#017A9C] group-hover:text-white transition-colors">
            <Users className="w-5 h-5" />
          </div>
          <div>
            <div className="text-sm font-bold text-gray-900 dark:text-white">Bulk catering order</div>
            <div className="text-xs text-gray-500 dark:text-gray-400">Banquet &amp; events (min. 20 pax)</div>
          </div>
        </button>
      </div>

      {/* Dine-in Floor Section */}
      <div>
        <div className="flex items-center justify-between mb-3">
          <div className="text-xs font-bold uppercase tracking-wider text-gray-500 dark:text-gray-400">
            Dine-in floor status
          </div>
        </div>

        {/* Floor Filter Tabs */}
        <div className="flex gap-2 overflow-x-auto pb-2 mb-4 scrollbar-hide">
          <button
            onClick={() => onSelectFloor('all')}
            className={`px-4 py-2 rounded-full text-xs font-bold transition-colors whitespace-nowrap ${
              activeFloorId === 'all'
                ? 'bg-gray-900 dark:bg-white text-white dark:text-gray-900'
                : 'bg-white dark:bg-[#151D28] text-gray-600 dark:text-gray-300 border border-gray-200 dark:border-gray-800 hover:border-gray-400'
            }`}
          >
            All floors
          </button>
          {floors.filter((f) => f.active).map((f) => (
            <button
              key={f.id}
              onClick={() => onSelectFloor(f.id)}
              className={`px-4 py-2 rounded-full text-xs font-bold transition-colors whitespace-nowrap ${
                activeFloorId === f.id
                  ? 'bg-gray-900 dark:bg-white text-white dark:text-gray-900'
                  : 'bg-white dark:bg-[#151D28] text-gray-600 dark:text-gray-300 border border-gray-200 dark:border-gray-800 hover:border-gray-400'
              }`}
            >
              {f.name}
            </button>
          ))}
        </div>

        {/* Tables Grid */}
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-3">
          {filteredTables.map((t) => {
            const floor = floors.find((f) => f.id === t.floorId);
            const order = t.orderId ? orders[t.orderId] : null;
            const upcomingRes = t.status === 'available' ? getUpcomingReservation(t.id) : null;

            return (
              <div
                key={t.id}
                onClick={() => {
                  if (t.status === 'available') onStartOrder('dine-in', t.id);
                  else if (t.status === 'occupied' && t.orderId) onOpenOrder(t.orderId);
                  else if (t.status === 'billed' && t.orderId) onOpenInvoice(t.orderId);
                  else if (t.status === 'cleaning') onMarkTableClean(t.id);
                }}
                className={`bg-white dark:bg-[#151D28] border rounded-xl p-3.5 text-left transition-all shadow-sm cursor-pointer relative hover:-translate-y-0.5 ${
                  t.status === 'occupied'
                    ? 'border-orange-300 dark:border-orange-900/60'
                    : t.status === 'billed'
                    ? 'border-blue-300 dark:border-blue-900/60'
                    : t.status === 'cleaning'
                    ? 'border-amber-300 dark:border-amber-900/60'
                    : 'border-gray-200 dark:border-gray-800 hover:border-[#017A9C]'
                }`}
              >
                <div className="font-serif font-bold text-lg text-gray-900 dark:text-white leading-none">
                  {t.shortcode}
                </div>
                <div className="text-[11px] text-gray-500 dark:text-gray-400 mt-1">
                  Seats {t.capacity} {floor ? `· ${floor.shortcode || floor.name}` : ''}
                </div>

                {/* Status Badges */}
                <div className="mt-2.5">
                  {t.status === 'available' && (
                    <span className="inline-flex items-center gap-1 text-[10.5px] font-bold text-emerald-600 dark:text-emerald-400 bg-emerald-50 dark:bg-emerald-950/40 px-2 py-0.5 rounded-full">
                      <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                      Available
                    </span>
                  )}
                  {t.status === 'occupied' && (
                    <span className="inline-flex items-center gap-1 text-[10.5px] font-bold text-orange-600 dark:text-orange-400 bg-orange-50 dark:bg-orange-950/40 px-2 py-0.5 rounded-full">
                      <span className="w-1.5 h-1.5 rounded-full bg-orange-500"></span>
                      Occupied
                    </span>
                  )}
                  {t.status === 'billed' && (
                    <span className="inline-flex items-center gap-1 text-[10.5px] font-bold text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-950/40 px-2 py-0.5 rounded-full">
                      <span className="w-1.5 h-1.5 rounded-full bg-blue-500"></span>
                      Bill Printed
                    </span>
                  )}
                  {t.status === 'cleaning' && (
                    <span className="inline-flex items-center gap-1 text-[10.5px] font-bold text-amber-600 dark:text-amber-400 bg-amber-50 dark:bg-amber-950/40 px-2 py-0.5 rounded-full">
                      <span className="w-1.5 h-1.5 rounded-full bg-amber-500"></span>
                      Needs Cleaning
                    </span>
                  )}
                </div>

                {/* Order Meta */}
                {t.status === 'occupied' && order && (
                  <div className="text-[11px] text-gray-500 dark:text-gray-400 mt-1.5 line-clamp-1">
                    {Object.values(order.items || {}).reduce((sum, i) => sum + i.qty, 0)} items in order
                  </div>
                )}

                {/* Cleaning Prompt */}
                {t.status === 'cleaning' && (
                  <div className="mt-2 text-[11px] font-semibold text-amber-600 dark:text-amber-400 flex items-center gap-1">
                    <Sparkles className="w-3 h-3" /> Tap to mark ready
                  </div>
                )}

                {/* Actions */}
                {t.status === 'occupied' && (
                  <div className="mt-2.5 pt-2 border-t border-gray-100 dark:border-gray-800/80 flex gap-1">
                    <button
                      type="button"
                      onClick={(e) => {
                        e.stopPropagation();
                        onFreeTable(t.id);
                      }}
                      className="w-full text-[10.5px] font-bold py-1 px-2 rounded bg-red-50 hover:bg-red-100 text-red-600 dark:bg-red-950/40 dark:text-red-400"
                    >
                      Free Table
                    </button>
                  </div>
                )}

                {/* Upcoming Reservation Badge */}
                {upcomingRes && (
                  <div className="mt-2 p-2 rounded-lg bg-[#017A9C]/10 border border-[#017A9C]/20 text-[10.5px] text-[#017A9C]">
                    <div className="font-bold line-clamp-1">📅 {upcomingRes.time} — {upcomingRes.guestName}</div>
                    <div className="text-gray-500 dark:text-gray-400">{upcomingRes.pax} guests</div>
                    <button
                      type="button"
                      onClick={(e) => {
                        e.stopPropagation();
                        onSeatReservation(upcomingRes.id, t.id);
                      }}
                      className="mt-1.5 w-full py-1 rounded bg-[#017A9C] text-white font-bold text-[10px]"
                    >
                      Seat now
                    </button>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>

      {/* Other Active Orders Section */}
      {otherOrders.length > 0 && (
        <div className="pt-4 border-t border-gray-200 dark:border-gray-800">
          <div className="text-xs font-bold uppercase tracking-wider text-gray-500 dark:text-gray-400 mb-3">
            Other Active Orders ({otherOrders.length})
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
            {otherOrders.map((o) => {
              const typeLabel = o.type.toUpperCase();
              const itemCount = Object.values(o.items || {}).reduce((s, i) => s + i.qty, 0);

              return (
                <div
                  key={o.id}
                  onClick={() => onOpenOrder(o.id)}
                  className="bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 hover:border-[#017A9C] rounded-xl p-4 flex items-center justify-between cursor-pointer shadow-sm transition-all"
                >
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="text-xs font-extrabold text-[#017A9C] bg-[#017A9C]/10 px-2 py-0.5 rounded">
                        {typeLabel}
                      </span>
                      <span className="text-xs font-mono font-semibold text-gray-500">{o.id}</span>
                    </div>
                    <div className="text-sm font-bold text-gray-900 dark:text-white mt-1">
                      {o.customerName || 'Walk-in Guest'}
                    </div>
                    <div className="text-xs text-gray-500 dark:text-gray-400">
                      {itemCount} items · {o.customerPhone || 'No phone'}
                    </div>
                  </div>

                  <div className="flex flex-col items-end gap-2">
                    <button
                      type="button"
                      onClick={(e) => {
                        e.stopPropagation();
                        onCancelOrder(o.id);
                      }}
                      className="text-xs text-red-500 hover:text-red-700 font-bold px-2 py-1 rounded hover:bg-red-50 dark:hover:bg-red-950/40"
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
}
