import React from 'react';
import { ArrowLeft, ArrowRight, Clock, CheckCircle2 } from 'lucide-react';

export function KotKanbanView({ kots, onMoveKotStatus }) {
  const KOT_COLUMNS = [
    { id: 'new', label: 'New', color: 'bg-[#017A9C]', border: 'border-[#017A9C]' },
    { id: 'preparing', label: 'Preparing', color: 'bg-orange-500', border: 'border-orange-500' },
    { id: 'ready', label: 'Ready', color: 'bg-emerald-500', border: 'border-emerald-500' },
    { id: 'served', label: 'Served', color: 'bg-blue-500', border: 'border-blue-500' },
  ];

  const getElapsed = (ts) => {
    const mins = Math.floor((Date.now() - (ts || Date.now())) / 60000);
    if (mins < 1) return 'just now';
    return `${mins} min`;
  };

  const handleDragStart = (e, kotId) => {
    e.dataTransfer.setData('text/plain', kotId);
    e.dataTransfer.effectAllowed = 'move';
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
  };

  const handleDrop = (e, targetStatus) => {
    e.preventDefault();
    const kotId = e.dataTransfer.getData('text/plain');
    if (kotId) {
      onMoveKotStatus(kotId, targetStatus);
    }
  };

  return (
    <div className="space-y-4">
      <div className="text-xs text-gray-500 dark:text-gray-400">
        Drag a ticket to the next column, or use the step buttons on each ticket card.
      </div>

      <div className="flex gap-4 items-start overflow-x-auto pb-4 scrollbar-hide">
        {KOT_COLUMNS.map((col, idx) => {
          const ticketsInCol = Object.values(kots)
            .filter((k) => k.status === col.id)
            .sort((a, b) => b.createdAt - a.createdAt);

          return (
            <div
              key={col.id}
              onDragOver={handleDragOver}
              onDrop={(e) => handleDrop(e, col.id)}
              className="w-[280px] flex-shrink-0 bg-gray-100 dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-xl flex flex-col max-h-[calc(100vh-190px)]"
            >
              {/* Column Header */}
              <div className="p-3.5 flex items-center justify-between border-b border-gray-200 dark:border-gray-800/80">
                <div className="flex items-center gap-2">
                  <span className={`w-2.5 h-2.5 rounded-full ${col.color}`}></span>
                  <span className="text-xs font-extrabold text-gray-900 dark:text-white uppercase tracking-wider">
                    {col.label}
                  </span>
                </div>
                <span className="text-xs font-bold text-gray-500 bg-white dark:bg-gray-800 px-2 py-0.5 rounded-full border border-gray-200 dark:border-gray-700">
                  {ticketsInCol.length}
                </span>
              </div>

              {/* Tickets List */}
              <div className="p-3 space-y-3 overflow-y-auto flex-1 min-h-[100px]">
                {ticketsInCol.length === 0 ? (
                  <div className="text-center py-8 border-2 border-dashed border-gray-200 dark:border-gray-800 rounded-lg text-xs text-gray-400">
                    No tickets
                  </div>
                ) : (
                  ticketsInCol.map((ticket) => {
                    const nextStatus = idx < KOT_COLUMNS.length - 1 ? KOT_COLUMNS[idx + 1].id : null;
                    const prevStatus = idx > 0 ? KOT_COLUMNS[idx - 1].id : null;

                    return (
                      <div
                        key={ticket.id}
                        draggable
                        onDragStart={(e) => handleDragStart(e, ticket.id)}
                        className={`bg-white dark:bg-[#1A2332] border border-gray-200 dark:border-gray-700/80 rounded-xl shadow-sm overflow-hidden border-l-4 ${col.border} cursor-grab active:cursor-grabbing hover:shadow-md transition-all`}
                      >
                        {/* Ticket Header */}
                        <div className="p-3 border-b border-dashed border-gray-100 dark:border-gray-800 flex items-start justify-between">
                          <div>
                            <div className="font-serif font-bold text-sm text-gray-900 dark:text-white">
                              {ticket.id}
                            </div>
                            <div className="text-[11px] text-gray-500 dark:text-gray-400">
                              {ticket.tableLabel} · {ticket.type}
                            </div>
                          </div>
                          <span className="inline-flex items-center gap-1 text-[10px] font-bold text-orange-600 bg-orange-50 dark:bg-orange-950/40 px-2 py-0.5 rounded-md">
                            <Clock className="w-3 h-3" />
                            {getElapsed(ticket.createdAt)}
                          </span>
                        </div>

                        {/* Items List */}
                        <div className="p-3 space-y-1 text-xs">
                          {ticket.items?.map((it, i) => (
                            <div key={i} className="flex justify-between items-center">
                              <span className="font-medium text-gray-800 dark:text-gray-200">
                                <b className="text-[#017A9C] mr-1.5">{it.qty}×</b>
                                {it.name}
                              </span>
                            </div>
                          ))}
                        </div>

                        {/* Stepper Footer Actions */}
                        <div className="p-2.5 bg-gray-50 dark:bg-gray-800/50 border-t border-gray-100 dark:border-gray-800 flex gap-1.5">
                          <button
                            onClick={() => prevStatus && onMoveKotStatus(ticket.id, prevStatus)}
                            disabled={!prevStatus}
                            className="flex-1 py-1.5 px-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-xs font-bold text-gray-600 dark:text-gray-300 disabled:opacity-30 disabled:cursor-not-allowed flex items-center justify-center gap-1 hover:bg-gray-50"
                          >
                            <ArrowLeft className="w-3 h-3" />
                          </button>
                          <button
                            onClick={() => nextStatus && onMoveKotStatus(ticket.id, nextStatus)}
                            disabled={!nextStatus}
                            className="flex-2 py-1.5 px-3 rounded-lg bg-[#017A9C]/10 text-[#017A9C] hover:bg-[#017A9C] hover:text-white dark:bg-[#017A9C]/20 text-xs font-bold disabled:opacity-30 disabled:cursor-not-allowed flex items-center justify-center gap-1 transition-colors"
                          >
                            {idx === KOT_COLUMNS.length - 2 ? (
                              <>
                                <CheckCircle2 className="w-3 h-3" /> Serve
                              </>
                            ) : (
                              <>
                                Next <ArrowRight className="w-3 h-3" />
                              </>
                            )}
                          </button>
                        </div>
                      </div>
                    );
                  })
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
