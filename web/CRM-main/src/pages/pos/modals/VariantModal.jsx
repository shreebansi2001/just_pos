import React, { useState } from 'react';
import { X } from 'lucide-react';

export function VariantModal({ isOpen, onClose, item, onConfirm }) {
  const [selectedIdx, setSelectedIdx] = useState(0);

  if (!isOpen || !item || !item.variants) return null;

  const formatMoney = (n) => '₹' + Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 });

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-xs p-4">
      <div className="w-full max-w-sm bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-2xl shadow-xl overflow-hidden animate-in fade-in zoom-in-95">
        <div className="p-4 border-b border-gray-100 dark:border-gray-800 flex items-center justify-between">
          <h3 className="font-serif font-bold text-base text-gray-900 dark:text-white">
            {item.name}
          </h3>
          <button
            onClick={onClose}
            className="w-7 h-7 rounded-full bg-gray-100 dark:bg-gray-800 flex items-center justify-center text-gray-400 hover:text-gray-700 dark:hover:text-gray-200"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <div className="p-4 space-y-2">
          {item.variants.map((v, idx) => (
            <label
              key={idx}
              className={`flex items-center justify-between p-3 rounded-xl border cursor-pointer transition-all ${
                selectedIdx === idx
                  ? 'border-[#017A9C] bg-[#017A9C]/5 dark:bg-[#017A9C]/10 text-gray-900 dark:text-white font-bold'
                  : 'border-gray-200 dark:border-gray-800 text-gray-600 dark:text-gray-300 hover:bg-gray-50'
              }`}
            >
              <div className="flex items-center gap-3">
                <input
                  type="radio"
                  name="variant"
                  checked={selectedIdx === idx}
                  onChange={() => setSelectedIdx(idx)}
                  className="accent-[#017A9C]"
                />
                <span className="text-sm">{v[0]}</span>
              </div>
              <span className="text-sm">{formatMoney(v[1])}</span>
            </label>
          ))}
        </div>

        <div className="p-4 border-t border-gray-100 dark:border-gray-800">
          <button
            onClick={() => {
              const v = item.variants[selectedIdx];
              onConfirm(item, v[0], v[1]);
              onClose();
            }}
            className="w-full py-2.5 rounded-xl bg-[#017A9C] hover:bg-[#016582] text-white text-xs font-bold shadow-sm transition-all"
          >
            Add to Order
          </button>
        </div>
      </div>
    </div>
  );
}
