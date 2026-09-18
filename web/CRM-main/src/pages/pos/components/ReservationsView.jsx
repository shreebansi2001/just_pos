import React from 'react';
import { Calendar, Plus, Users, Phone, Clock } from 'lucide-react';

export function ReservationsView({
  reservations,
  tables,
  floors,
  filterDate,
  filterStatus,
  onDateChange,
  onStatusChange,
  onAddReservationClick,
  onEditReservationClick,
  onSeatReservationClick,
  onStatusUpdate,
  onViewOrder
}) {
  const STATUS_TABS = ['all', 'upcoming', 'seated', 'completed', 'cancelled', 'no-show'];

  const filtered = Object.values(reservations)
    .filter((r) => {
      if (filterDate && r.date !== filterDate) return false;
      if (filterStatus !== 'all' && r.status !== filterStatus) return false;
      return true;
    })
    .sort((a, b) => (a.time || '').localeCompare(b.time || ''));

  return (
    <div className="space-y-4">
      {/* Top Filter Bar */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-xl p-3.5 shadow-sm">
        <div className="flex items-center gap-2.5 flex-wrap">
          <div className="relative">
            <input
              type="date"
              value={filterDate}
              onChange={(e) => onDateChange(e.target.value)}
              className="px-3 py-1.5 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-xs font-semibold focus:outline-none focus:border-[#017A9C]"
            />
          </div>

          <div className="flex gap-1.5 overflow-x-auto scrollbar-hide">
            {STATUS_TABS.map((s) => (
              <button
                key={s}
                onClick={() => onStatusChange(s)}
                className={`px-3 py-1.5 rounded-lg text-xs font-bold capitalize transition-colors ${
                  filterStatus === s
                    ? 'bg-[#017A9C] text-white'
                    : 'bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-300 hover:bg-gray-200'
                }`}
              >
                {s}
              </button>
            ))}
          </div>
        </div>

        <button
          onClick={onAddReservationClick}
          className="px-3.5 py-2 rounded-xl bg-[#017A9C] hover:bg-[#016582] text-white font-bold text-xs flex items-center gap-1.5 shadow-sm transition-all whitespace-nowrap"
        >
          <Plus className="w-4 h-4" /> New Reservation
        </button>
      </div>

      {/* Reservations List */}
      {filtered.length === 0 ? (
        <div className="text-center py-16 bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-xl">
          <Calendar className="w-10 h-10 mx-auto text-gray-400 mb-2 opacity-60" />
          <p className="text-sm font-semibold text-gray-600 dark:text-gray-300">No reservations match this date/filter.</p>
        </div>
      ) : (
        <div className="space-y-2.5">
          {filtered.map((r) => {
            const table = r.tableId ? tables.find((t) => t.id === r.tableId) : null;
            const floor = r.floorId ? floors.find((f) => f.id === r.floorId) : null;

            return (
              <div
                key={r.id}
                className="bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-xl p-4 flex items-center justify-between gap-4 shadow-sm hover:border-gray-300 transition-all flex-wrap"
              >
                <div className="w-20 font-serif font-bold text-base text-gray-900 dark:text-white flex items-center gap-1.5">
                  <Clock className="w-3.5 h-3.5 text-[#017A9C]" />
                  {r.time}
                </div>

                <div className="flex-1 min-w-[200px]">
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-sm text-gray-900 dark:text-white">{r.guestName}</span>
                    <span className="text-xs text-gray-500 font-semibold flex items-center gap-0.5">
                      <Users className="w-3 h-3" /> {r.pax} pax
                    </span>
                  </div>
                  <div className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">
                    {r.phone} · {table ? `${table.shortcode} (${table.name})` : floor ? `${floor.name} (unassigned)` : 'Any table'}
                    {r.notes ? ` · ${r.notes}` : ''}
                  </div>
                </div>

                <div>
                  <span
                    className={`inline-block text-[11px] font-bold px-2.5 py-1 rounded-full uppercase tracking-wider ${
                      r.status === 'upcoming'
                        ? 'bg-[#017A9C]/10 text-[#017A9C]'
                        : r.status === 'seated'
                        ? 'bg-blue-100 text-blue-700 dark:bg-blue-950/40 dark:text-blue-400'
                        : r.status === 'completed'
                        ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-950/40 dark:text-emerald-400'
                        : 'bg-red-100 text-red-700 dark:bg-red-950/40 dark:text-red-400'
                    }`}
                  >
                    {r.status}
                  </span>
                </div>

                <div className="flex items-center gap-1.5">
                  {r.status === 'upcoming' && (
                    <>
                      <button
                        onClick={() => onSeatReservationClick(r.id)}
                        className="px-3 py-1.5 rounded-lg bg-[#017A9C] text-white text-xs font-bold shadow-sm"
                      >
                        Seat now
                      </button>
                      <button
                        onClick={() => onEditReservationClick(r.id)}
                        className="px-2.5 py-1.5 rounded-lg border border-gray-200 dark:border-gray-700 text-xs font-bold text-gray-600 dark:text-gray-300 hover:bg-gray-100"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => onStatusUpdate(r.id, 'no-show')}
                        className="px-2.5 py-1.5 rounded-lg border border-gray-200 dark:border-gray-700 text-xs font-bold text-gray-600 dark:text-gray-300 hover:bg-gray-100"
                      >
                        No-show
                      </button>
                      <button
                        onClick={() => onStatusUpdate(r.id, 'cancelled')}
                        className="px-2.5 py-1.5 rounded-lg text-xs font-bold text-red-600 hover:bg-red-50 dark:hover:bg-red-950/40"
                      >
                        Cancel
                      </button>
                    </>
                  )}

                  {r.status === 'seated' && r.orderId && (
                    <button
                      onClick={() => onViewOrder(r.orderId)}
                      className="px-3 py-1.5 rounded-lg bg-gray-100 dark:bg-gray-800 text-xs font-bold text-gray-800 dark:text-gray-200 hover:bg-gray-200"
                    >
                      View Order
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
