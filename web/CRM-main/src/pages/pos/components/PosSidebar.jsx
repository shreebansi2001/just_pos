import React from 'react';
import { LayoutGrid, ShoppingBag, Clock, Receipt, CalendarDays, Settings2, Moon, Sun } from 'lucide-react';

export function PosSidebar({ activeView, onViewChange, kotBadgeCount, resBadgeCount, isDark, onToggleTheme }) {
  const navItems = [
    { id: 'tables', label: 'Tables', icon: LayoutGrid },
    { id: 'pos', label: 'New Order', icon: ShoppingBag },
    { id: 'kot', label: 'KOT', icon: Clock, badge: kotBadgeCount },
    { id: 'billing', label: 'Billing', icon: Receipt },
    { id: 'reservations', label: 'Reserve', icon: CalendarDays, badge: resBadgeCount },
    { id: 'masters', label: 'Masters', icon: Settings2 },
  ];

  return (
    <aside className="w-[74px] bg-white dark:bg-[#121820] border-r border-gray-200 dark:border-gray-800 flex flex-col items-center py-4 flex-shrink-0 sticky top-0 h-screen overflow-y-auto z-20">
      {/* Brand Icon */}
      <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-[#017A9C] to-[#015870] flex items-center justify-center text-white font-serif font-bold text-xl mb-4 shadow-md shadow-[#017A9C]/20 flex-shrink-0">
        S
      </div>

      {/* Navigation Buttons */}
      <div className="flex flex-col gap-1 w-full px-2.5">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeView === item.id;
          return (
            <button
              key={item.id}
              onClick={() => onViewChange(item.id)}
              className={`w-full h-[52px] rounded-xl flex flex-col items-center justify-center gap-1 relative transition-all ${
                isActive
                  ? 'bg-[#017A9C]/10 text-[#017A9C] font-bold dark:bg-[#017A9C]/20'
                  : 'text-gray-500 hover:text-gray-900 hover:bg-gray-100 dark:text-gray-400 dark:hover:text-white dark:hover:bg-gray-800/60 font-medium'
              }`}
            >
              <Icon className="w-5 h-5" />
              <span className="text-[10px] leading-none tracking-tight">{item.label}</span>
              {item.badge > 0 && (
                <span className="absolute top-1.5 right-1.5 min-w-[17px] h-[17px] px-1 rounded-full bg-red-500 text-white text-[9.5px] font-bold flex items-center justify-center">
                  {item.badge}
                </span>
              )}
            </button>
          );
        })}
      </div>

      {/* Bottom Theme Toggle */}
      <div className="mt-auto pt-4 flex flex-col items-center gap-2">
        <button
          onClick={onToggleTheme}
          title="Toggle Theme"
          className="w-10 h-10 rounded-xl text-gray-500 hover:text-gray-900 hover:bg-gray-100 dark:text-gray-400 dark:hover:text-white dark:hover:bg-gray-800 flex items-center justify-center transition-colors"
        >
          {isDark ? <Sun className="w-5 h-5 text-amber-400" /> : <Moon className="w-5 h-5" />}
        </button>
      </div>
    </aside>
  );
}
