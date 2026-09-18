import React from 'react';
import { X } from 'lucide-react';

export function PickerModal({ isOpen, onClose, title, tables, floors, filterFn, onSelectTable }) {
  if (!isOpen) return null;

  const candidates = tables.filter((t) => t.active && t.status === 'available' && (!filterFn || filterFn(t)));

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-xs p-4">
      <div className="w-full max-w-sm bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-2xl shadow-xl overflow-hidden animate-in fade-in zoom-in-95">
        <div className="p-4 border-b border-gray-100 dark:border-gray-800 flex items-center justify-between">
          <h3 className="font-serif font-bold text-base text-gray-900 dark:text-white">{title}</h3>
          <button
            onClick={onClose}
            className="w-7 h-7 rounded-full bg-gray-100 dark:bg-gray-800 flex items-center justify-center text-gray-400 hover:text-gray-700 dark:hover:text-gray-200"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="p-4 max-h-80 overflow-y-auto space-y-2">
          {candidates.length === 0 ? (
            <div className="text-center py-6 text-xs text-gray-400">
              No matching free tables available right now.
            </div>
          ) : (
            candidates.map((t) => {
              const floor = floors.find((f) => f.id === t.floorId);
              return (
                <button
                  key={t.id}
                  onClick={() => {
                    onSelectTable(t.id);
                    onClose();
                  }}
                  className="w-full flex items-center justify-between p-3 rounded-xl border border-gray-200 dark:border-gray-800 hover:border-[#017A9C] hover:bg-gray-50 dark:hover:bg-gray-800 text-left transition-all"
                >
                  <div>
                    <div className="text-sm font-bold text-gray-900 dark:text-white">{t.shortcode} — {t.name}</div>
                    <div className="text-xs text-gray-400">{floor ? floor.name : 'Floor'}</div>
                  </div>
                  <span className="text-xs font-semibold text-gray-500 bg-gray-100 dark:bg-gray-800 px-2.5 py-1 rounded-md">
                    Seats {t.capacity}
                  </span>
                </button>
              );
            })
          )}
        </div>
      </div>
    </div>
  );
}
