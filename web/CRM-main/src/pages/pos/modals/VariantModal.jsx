import React, { useState, useEffect } from 'react';
import { X, Scale, Utensils } from 'lucide-react';

export function VariantModal({ isOpen, onClose, item, onConfirm }) {
  const [mode, setMode] = useState('portion'); // 'portion' | 'kg'
  const [selectedIdx, setSelectedIdx] = useState(0);
  const [weightKg, setWeightKg] = useState(1.0);

  const hasVariants = Boolean(item && item.variants && item.variants.length > 0);
  const hasKgPricing = Boolean(item && (item.pricingType === 'kg' || item.pricingType === 'both' || item.pricePerKg));
  const isDualMode = hasVariants && hasKgPricing;
  const isKgOnly = hasKgPricing && (!hasVariants || item.pricingType === 'kg');

  useEffect(() => {
    if (isKgOnly) {
      setMode('kg');
    } else {
      setMode('portion');
    }
    setSelectedIdx(0);
    setWeightKg(1.0);
  }, [item, isOpen, isKgOnly]);

  if (!isOpen || !item) return null;

  const formatMoney = (n) => '₹' + Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 });
  const ratePerKg = Number(item.pricePerKg || item.price || 0);
  const weightTotalPrice = Math.round(weightKg * ratePerKg);

  const kgPresets = [
    { label: '250g', val: 0.25 },
    { label: '500g', val: 0.5 },
    { label: '1.0 Kg', val: 1.0 },
    { label: '1.5 Kg', val: 1.5 },
    { label: '2.0 Kg', val: 2.0 },
    { label: '5.0 Kg', val: 5.0 }
  ];

  const handleConfirm = () => {
    if (mode === 'kg' || isKgOnly) {
      const label = `⚖️ ${weightKg} Kg (@ ${formatMoney(ratePerKg)}/Kg)`;
      onConfirm(item, label, weightTotalPrice);
    } else if (hasVariants) {
      const v = item.variants[selectedIdx];
      onConfirm(item, v[0], v[1]);
    } else {
      onConfirm(item, '', item.price);
    }
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-xs p-4">
      <div className="w-full max-w-md bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-2xl shadow-xl overflow-hidden animate-in fade-in zoom-in-95">
        <div className="p-4 border-b border-gray-100 dark:border-gray-800 flex items-center justify-between">
          <div>
            <h3 className="font-serif font-bold text-base text-gray-900 dark:text-white flex items-center gap-2">
              {item.name}
              {hasKgPricing && (
                <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-amber-100 text-amber-800 dark:bg-amber-900/40 dark:text-amber-300">
                  ⚖️ KG Available
                </span>
              )}
            </h3>
            <p className="text-[11px] text-gray-500 mt-0.5">
              {mode === 'kg' ? `Rate: ${formatMoney(ratePerKg)} / KG` : (item.price ? `Base: ${formatMoney(item.price)}` : 'Choose option')}
            </p>
          </div>
          <button
            onClick={onClose}
            className="w-7 h-7 rounded-full bg-gray-100 dark:bg-gray-800 flex items-center justify-center text-gray-400 hover:text-gray-700 dark:hover:text-gray-200"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Dual Mode Switcher if item supports both plate portions and KG weight */}
        {isDualMode && (
          <div className="p-3 bg-gray-50 dark:bg-gray-800/60 border-b border-gray-100 dark:border-gray-800">
            <div className="grid grid-cols-2 gap-2 bg-gray-200 dark:bg-gray-900 p-1 rounded-xl text-xs font-bold">
              <button
                type="button"
                onClick={() => setMode('portion')}
                className={`py-2 rounded-lg flex items-center justify-center gap-1.5 transition-all ${
                  mode === 'portion'
                    ? 'bg-white dark:bg-gray-800 text-[#017A9C] shadow-xs'
                    : 'text-gray-600 dark:text-gray-400'
                }`}
              >
                <Utensils className="w-3.5 h-3.5" />
                <span>Plate / Portion</span>
              </button>
              <button
                type="button"
                onClick={() => setMode('kg')}
                className={`py-2 rounded-lg flex items-center justify-center gap-1.5 transition-all ${
                  mode === 'kg'
                    ? 'bg-white dark:bg-gray-800 text-emerald-600 shadow-xs'
                    : 'text-gray-600 dark:text-gray-400'
                }`}
              >
                <Scale className="w-3.5 h-3.5" />
                <span>By Weight (KG)</span>
              </button>
            </div>
          </div>
        )}

        {/* KG Weight Mode Selector */}
        {mode === 'kg' ? (
          <div className="p-4 space-y-3">
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold text-gray-700 dark:text-gray-300">Quick Weight Presets</span>
              <span className="text-xs font-extrabold text-emerald-600 dark:text-emerald-400">
                Rate: {formatMoney(ratePerKg)}/Kg
              </span>
            </div>

            <div className="grid grid-cols-3 gap-2">
              {kgPresets.map((p) => (
                <button
                  key={p.val}
                  type="button"
                  onClick={() => setWeightKg(p.val)}
                  className={`py-2 px-3 rounded-xl border text-xs font-bold transition-all ${
                    weightKg === p.val
                      ? 'border-emerald-500 bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 shadow-xs'
                      : 'border-gray-200 dark:border-gray-800 text-gray-700 dark:text-gray-300 hover:bg-gray-50'
                  }`}
                >
                  {p.label}
                </button>
              ))}
            </div>

            <div className="flex items-center justify-between p-3 rounded-xl bg-gray-50 dark:bg-gray-800/80 border border-gray-200 dark:border-gray-700">
              <span className="text-xs font-bold text-gray-700 dark:text-gray-300">Custom Weight:</span>
              <div className="flex items-center gap-2">
                <button
                  type="button"
                  onClick={() => setWeightKg((prev) => Math.max(0.1, Math.round((prev - 0.25) * 100) / 100))}
                  className="w-7 h-7 rounded-lg bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 font-bold"
                >
                  −
                </button>
                <div className="flex items-center gap-1">
                  <input
                    type="number"
                    step="0.05"
                    min="0.05"
                    value={weightKg}
                    onChange={(e) => setWeightKg(parseFloat(e.target.value) || 0.1)}
                    className="w-16 text-center font-bold text-sm bg-white dark:bg-gray-900 border border-gray-300 dark:border-gray-700 rounded-md py-1"
                  />
                  <span className="text-xs font-bold text-gray-500">KG</span>
                </div>
                <button
                  type="button"
                  onClick={() => setWeightKg((prev) => Math.round((prev + 0.25) * 100) / 100)}
                  className="w-7 h-7 rounded-lg bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 font-bold"
                >
                  +
                </button>
              </div>
            </div>

            <div className="flex items-center justify-between p-2.5 rounded-xl bg-emerald-50/70 dark:bg-emerald-950/30 border border-emerald-200 dark:border-emerald-800/50">
              <span className="text-xs text-emerald-800 dark:text-emerald-300">
                Calculation: {weightKg} Kg × {formatMoney(ratePerKg)}/Kg
              </span>
              <span className="text-sm font-extrabold text-emerald-700 dark:text-emerald-400">
                {formatMoney(weightTotalPrice)}
              </span>
            </div>
          </div>
        ) : (
          /* Portion Variant Mode (Half / Full) */
          <div className="p-4 space-y-2">
            {hasVariants &&
              item.variants.map((v, idx) => (
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
        )}

        <div className="p-4 border-t border-gray-100 dark:border-gray-800 flex items-center justify-between">
          <div className="text-xs text-gray-500">
            Total: <b className="text-sm text-gray-900 dark:text-white font-bold">{formatMoney(mode === 'kg' ? weightTotalPrice : (hasVariants ? item.variants[selectedIdx]?.[1] : item.price))}</b>
          </div>
          <button
            onClick={handleConfirm}
            className="px-5 py-2.5 rounded-xl bg-[#017A9C] hover:bg-[#016582] text-white text-xs font-bold shadow-sm transition-all"
          >
            Add to Order
          </button>
        </div>
      </div>
    </div>
  );
}
