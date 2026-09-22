import React, { useState, useRef, useEffect } from 'react';
import { User, ChevronDown, Check } from 'lucide-react';

export function PosTopbar({ title, subtitle, stats, currentUser, currentRole, staffUsers = [], roles = [], onSwitchUser }) {
  const formatMoney = (n) => '₹' + Number(n || 0).toLocaleString('en-IN', { maximumFractionDigits: 0 });
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    function handleClickOutside(e) {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setDropdownOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

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

        {/* Operator Profile Chip & Quick Switcher */}
        {currentUser && (
          <div className="relative" ref={dropdownRef}>
            <button
              onClick={() => setDropdownOpen(!dropdownOpen)}
              className="flex items-center gap-2 bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-700 rounded-lg px-3 py-1.5 text-xs font-semibold text-gray-700 dark:text-gray-200 hover:border-[#017A9C] shadow-sm transition-all"
            >
              <User className="w-3.5 h-3.5 text-[#017A9C]" />
              <b>{currentUser.name?.split(' ')[0]}</b>
              {currentRole && (
                <span className="px-1.5 py-0.5 rounded text-[9.5px] font-extrabold bg-[#017A9C] text-white">
                  {currentRole.roleCode || currentRole.roleName}
                </span>
              )}
              <ChevronDown className="w-3.5 h-3.5 opacity-60" />
            </button>

            {dropdownOpen && (
              <div className="absolute right-0 mt-2 w-64 bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-700 rounded-xl shadow-xl z-50 overflow-hidden text-xs">
                <div className="p-2.5 bg-gray-50 dark:bg-gray-800/60 border-b border-gray-200 dark:border-gray-700 font-bold text-gray-500 uppercase tracking-wider text-[10px]">
                  Switch Operator Profile
                </div>
                <div className="max-h-56 overflow-y-auto p-1 divide-y divide-gray-100 dark:divide-gray-800">
                  {staffUsers.map((u) => {
                    const r = roles.find((ro) => ro.id === u.roleId) || { roleName: 'Staff', roleCode: 'USER' };
                    const isSelected = u.id === currentUser.id;
                    return (
                      <button
                        key={u.id}
                        onClick={() => {
                          onSwitchUser(u.id);
                          setDropdownOpen(false);
                        }}
                        className={`w-full text-left px-3 py-2 rounded-lg flex items-center justify-between gap-2 hover:bg-[#017A9C]/10 transition-colors ${
                          isSelected ? 'bg-[#017A9C]/10 font-bold' : ''
                        }`}
                      >
                        <div>
                          <div className="text-gray-900 dark:text-white font-semibold">{u.name}</div>
                          <div className="text-[10.5px] text-gray-400">{r.roleName}</div>
                        </div>
                        <div className="flex items-center gap-1.5">
                          <span className="px-1.5 py-0.5 rounded text-[9px] font-extrabold bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-200">
                            {r.roleCode}
                          </span>
                          {isSelected && <Check className="w-3.5 h-3.5 text-[#017A9C]" />}
                        </div>
                      </button>
                    );
                  })}
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </header>
  );
}
