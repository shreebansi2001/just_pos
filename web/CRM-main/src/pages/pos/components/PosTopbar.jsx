import React from 'react';

export function PosTopbar({ title, subtitle, stats }) {
  const formatMoney = (n) => '₹' + Number(n || 0).toLocaleString('en-IN', { maximumFractionDigits: 0 });

  return (
    <header className="flex items-center justify-between gap-4 px-6 py-4 border-b border-gray-200 dark:border-gray-800 bg-white dark:bg-[#121820] sticky top-0 z-10 flex-wrap">
      <div>
        <h1 className="font-serif font-bold text-xl text-gray-900 dark:text-white tracking-tight leading-none">
          {title}
        </h1>
        <div className="text-xs text-gray-500 dark:text-gray-400 mt-1">
          {subtitle}
        </div>
      </div>

      <div className="flex items-center gap-2.5 flex-wrap">
        <div className="flex items-center gap-1.5 bg-gray-100 dark:bg-gray-800/80 border border-gray-200 dark:border-gray-700/60 rounded-lg px-3 py-1.5 text-xs font-semibold text-gray-600 dark:text-gray-300">
          <b className="text-gray-900 dark:text-white text-sm">{stats.freeTables ?? '–'}</b> tables free
        </div>

        <div className="flex items-center gap-1.5 bg-orange-50 dark:bg-orange-950/40 border border-orange-200/60 dark:border-orange-900/50 rounded-lg px-3 py-1.5 text-xs font-semibold text-orange-600 dark:text-orange-400">
          <b className="text-sm">{stats.inUseTables ?? 0}</b> tables in use
        </div>

        <div className="flex items-center gap-1.5 bg-gray-100 dark:bg-gray-800/80 border border-gray-200 dark:border-gray-700/60 rounded-lg px-3 py-1.5 text-xs font-semibold text-gray-600 dark:text-gray-300">
          <b className="text-gray-900 dark:text-white text-sm">{stats.activeKots ?? 0}</b> active KOTs
        </div>

        <div className="flex items-center gap-1.5 bg-[#017A9C]/10 border border-[#017A9C]/20 rounded-lg px-3 py-1.5 text-xs font-semibold text-[#017A9C]">
          <b className="text-sm">{formatMoney(stats.todaySales)}</b> today's sales
        </div>
      </div>
    </header>
  );
}
