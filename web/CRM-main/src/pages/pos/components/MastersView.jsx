import React, { useState } from 'react';
import { Plus, Edit2, Trash2, Power } from 'lucide-react';

export function MastersView({
  categories,
  items,
  floors,
  tables,
  taxes = [],
  roles = [],
  staffUsers = [],
  currentUserId,
  onSwitchUser,
  onOpenAddModal,
  onOpenEditModal,
  onToggleActive,
  onDeleteMaster
}) {
  const [activeTab, setActiveTab] = useState('categories');

  const TABS = [
    { id: 'categories', label: 'Categories', hint: 'Menu categories shown as tabs on the Order desk.' },
    { id: 'items', label: 'Items', hint: 'Every dish, its price (or sizes), category and kitchen station.' },
    { id: 'floors', label: 'Floors', hint: 'Dining sections — grouped tabs on the Tables screen.' },
    { id: 'tables', label: 'Tables', hint: 'Physical dining tables, each mapped to a floor.' },
    { id: 'taxes', label: 'Tax Master', hint: 'Tax Master: CGST, SGST, IGST percentages applied to order totals.' },
    { id: 'roles', label: 'Roles & Rights', hint: 'Personalize roles and assign granular screen & action rights for staff.' },
    { id: 'users', label: 'Staff Master', hint: 'Register staff members and assign their dynamic roles & PINs.' },
  ];

  const formatMoney = (n) => '₹' + Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 });
  const getCatName = (id) => categories.find((c) => c.id === id)?.name || '—';
  const getFloorName = (id) => floors.find((f) => f.id === id)?.name || '—';

  return (
    <div className="space-y-4">
      {/* Tab Navigation */}
      <div className="flex gap-2 border-b border-gray-200 dark:border-gray-800 pb-2">
        {TABS.map((t) => (
          <button
            key={t.id}
            onClick={() => setActiveTab(t.id)}
            className={`px-4 py-2 rounded-lg text-xs font-bold transition-all ${
              activeTab === t.id
                ? 'bg-[#017A9C] text-white shadow-sm'
                : 'bg-white dark:bg-[#151D28] text-gray-600 dark:text-gray-300 border border-gray-200 dark:border-gray-800 hover:bg-gray-50'
            }`}
          >
            {t.label}
          </button>
        ))}
      </div>

      {/* Toolbar */}
      <div className="flex justify-between items-center gap-4 bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-xl p-3.5 shadow-sm">
        <div className="text-xs text-gray-500 dark:text-gray-400">
          {TABS.find((t) => t.id === activeTab)?.hint}
        </div>
        <button
          onClick={() => onOpenAddModal(activeTab)}
          className="px-3.5 py-2 rounded-xl bg-[#017A9C] hover:bg-[#016582] text-white font-bold text-xs flex items-center gap-1.5 shadow-sm transition-all whitespace-nowrap"
        >
          <Plus className="w-4 h-4" /> Add New
        </button>
      </div>

      {/* Table Container */}
      <div className="bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-xl shadow-sm overflow-x-auto">
        {/* Categories Table */}
        {activeTab === 'categories' && (
          <table className="w-full text-left border-collapse text-xs">
            <thead>
              <tr className="border-b border-gray-200 dark:border-gray-800 bg-gray-50 dark:bg-gray-800/50 uppercase tracking-wider text-gray-400">
                <th className="py-3 px-4 w-16">Sort</th>
                <th className="py-3 px-4">Name</th>
                <th className="py-3 px-4">Dishes Count</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 dark:divide-gray-800">
              {categories.map((c) => {
                const count = items.filter((i) => i.categoryId === c.id).length;
                return (
                  <tr key={c.id} className="hover:bg-gray-50 dark:hover:bg-gray-800/40">
                    <td className="py-3 px-4 font-mono font-semibold">{c.sortOrder}</td>
                    <td className="py-3 px-4 font-bold text-gray-900 dark:text-white">{c.name}</td>
                    <td className="py-3 px-4 text-gray-500">{count} item{count !== 1 ? 's' : ''}</td>
                    <td className="py-3 px-4">
                      <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${c.active ? 'bg-emerald-50 text-emerald-600' : 'bg-gray-100 text-gray-500'}`}>
                        {c.active ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-right space-x-1">
                      <button
                        onClick={() => onOpenEditModal('categories', c)}
                        className="px-2.5 py-1 rounded border border-gray-200 dark:border-gray-700 text-[11px] font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-100"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => onToggleActive('categories', c.id)}
                        className="px-2.5 py-1 rounded border border-gray-200 dark:border-gray-700 text-[11px] font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-100"
                      >
                        {c.active ? 'Deactivate' : 'Activate'}
                      </button>
                      <button
                        onClick={() => onDeleteMaster('categories', c.id)}
                        className="px-2.5 py-1 rounded text-[11px] font-bold text-red-600 hover:bg-red-50 dark:hover:bg-red-950/40"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}

        {/* Items Table */}
        {activeTab === 'items' && (
          <table className="w-full text-left border-collapse text-xs">
            <thead>
              <tr className="border-b border-gray-200 dark:border-gray-800 bg-gray-50 dark:bg-gray-800/50 uppercase tracking-wider text-gray-400">
                <th className="py-3 px-4">Name</th>
                <th className="py-3 px-4">Category</th>
                <th className="py-3 px-4">Dietary</th>
                <th className="py-3 px-4">Price / Sizes</th>
                <th className="py-3 px-4">Station</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 dark:divide-gray-800">
              {items.map((i) => {
                let priceTxt = i.variants
                  ? i.variants.map((v) => `${v[0]} ${formatMoney(v[1])}`).join(' / ')
                  : formatMoney(i.price);
                if (i.pricingType === 'kg') {
                  priceTxt = `${formatMoney(i.pricePerKg || i.price)} / KG (By Weight)`;
                } else if (i.pricingType === 'both') {
                  priceTxt = `${formatMoney(i.price)} / Pl · ${formatMoney(i.pricePerKg)} / KG (Dual)`;
                }

                return (
                  <tr key={i.id} className="hover:bg-gray-50 dark:hover:bg-gray-800/40">
                    <td className="py-3 px-4">
                      <div className="font-bold text-gray-900 dark:text-white">{i.name}</div>
                      {i.tag && <div className="text-[11px] text-gray-400">{i.tag}</div>}
                    </td>
                    <td className="py-3 px-4 text-gray-600 dark:text-gray-300">{getCatName(i.categoryId)}</td>
                    <td className="py-3 px-4">
                      <span className={`inline-flex items-center gap-1 font-semibold ${i.veg ? 'text-emerald-600' : 'text-red-600'}`}>
                        {i.veg ? '🟢 Veg' : '🔴 Non-veg'}
                      </span>
                    </td>
                    <td className="py-3 px-4 font-semibold text-gray-900 dark:text-white">{priceTxt}</td>
                    <td className="py-3 px-4 text-gray-500">{i.station || 'Kitchen'}</td>
                    <td className="py-3 px-4">
                      <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${i.active ? 'bg-emerald-50 text-emerald-600' : 'bg-gray-100 text-gray-500'}`}>
                        {i.active ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-right space-x-1">
                      <button
                        onClick={() => onOpenEditModal('items', i)}
                        className="px-2.5 py-1 rounded border border-gray-200 dark:border-gray-700 text-[11px] font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-100"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => onToggleActive('items', i.id)}
                        className="px-2.5 py-1 rounded border border-gray-200 dark:border-gray-700 text-[11px] font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-100"
                      >
                        {i.active ? 'Deactivate' : 'Activate'}
                      </button>
                      <button
                        onClick={() => onDeleteMaster('items', i.id)}
                        className="px-2.5 py-1 rounded text-[11px] font-bold text-red-600 hover:bg-red-50 dark:hover:bg-red-950/40"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}

        {/* Floors Table */}
        {activeTab === 'floors' && (
          <table className="w-full text-left border-collapse text-xs">
            <thead>
              <tr className="border-b border-gray-200 dark:border-gray-800 bg-gray-50 dark:bg-gray-800/50 uppercase tracking-wider text-gray-400">
                <th className="py-3 px-4 w-16">Sort</th>
                <th className="py-3 px-4">Floor Name</th>
                <th className="py-3 px-4">Shortcode</th>
                <th className="py-3 px-4">Tables</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 dark:divide-gray-800">
              {floors.map((f) => {
                const count = tables.filter((t) => t.floorId === f.id).length;
                return (
                  <tr key={f.id} className="hover:bg-gray-50 dark:hover:bg-gray-800/40">
                    <td className="py-3 px-4 font-mono font-semibold">{f.sortOrder}</td>
                    <td className="py-3 px-4 font-bold text-gray-900 dark:text-white">{f.name}</td>
                    <td className="py-3 px-4 font-mono font-bold text-[#017A9C]">{f.shortcode}</td>
                    <td className="py-3 px-4 text-gray-500">{count} tables</td>
                    <td className="py-3 px-4">
                      <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${f.active ? 'bg-emerald-50 text-emerald-600' : 'bg-gray-100 text-gray-500'}`}>
                        {f.active ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-right space-x-1">
                      <button
                        onClick={() => onOpenEditModal('floors', f)}
                        className="px-2.5 py-1 rounded border border-gray-200 dark:border-gray-700 text-[11px] font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-100"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => onToggleActive('floors', f.id)}
                        className="px-2.5 py-1 rounded border border-gray-200 dark:border-gray-700 text-[11px] font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-100"
                      >
                        {f.active ? 'Deactivate' : 'Activate'}
                      </button>
                      <button
                        onClick={() => onDeleteMaster('floors', f.id)}
                        className="px-2.5 py-1 rounded text-[11px] font-bold text-red-600 hover:bg-red-50 dark:hover:bg-red-950/40"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}

        {/* Tables Table */}
        {activeTab === 'tables' && (
          <table className="w-full text-left border-collapse text-xs">
            <thead>
              <tr className="border-b border-gray-200 dark:border-gray-800 bg-gray-50 dark:bg-gray-800/50 uppercase tracking-wider text-gray-400">
                <th className="py-3 px-4">Name</th>
                <th className="py-3 px-4">Shortcode</th>
                <th className="py-3 px-4">Capacity</th>
                <th className="py-3 px-4">Floor</th>
                <th className="py-3 px-4">Live Status</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 dark:divide-gray-800">
              {tables.map((t) => (
                <tr key={t.id} className="hover:bg-gray-50 dark:hover:bg-gray-800/40">
                  <td className="py-3 px-4 font-bold text-gray-900 dark:text-white">{t.name}</td>
                  <td className="py-3 px-4 font-mono font-bold text-[#017A9C]">{t.shortcode}</td>
                  <td className="py-3 px-4 text-gray-600 dark:text-gray-300">Seats {t.capacity}</td>
                  <td className="py-3 px-4 text-gray-500">{getFloorName(t.floorId)}</td>
                  <td className="py-3 px-4">
                    <span className="capitalize text-[10.5px] font-bold text-gray-700 dark:text-gray-300">
                      {t.status}
                    </span>
                  </td>
                  <td className="py-3 px-4">
                    <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${t.active ? 'bg-emerald-50 text-emerald-600' : 'bg-gray-100 text-gray-500'}`}>
                      {t.active ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-right space-x-1">
                    <button
                      onClick={() => onOpenEditModal('tables', t)}
                      className="px-2.5 py-1 rounded border border-gray-200 dark:border-gray-700 text-[11px] font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-100"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => onToggleActive('tables', t.id)}
                      className="px-2.5 py-1 rounded border border-gray-200 dark:border-gray-700 text-[11px] font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-100"
                    >
                      {t.active ? 'Deactivate' : 'Activate'}
                    </button>
                    <button
                      onClick={() => onDeleteMaster('tables', t.id)}
                      className="px-2.5 py-1 rounded text-[11px] font-bold text-red-600 hover:bg-red-50 dark:hover:bg-red-950/40"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {/* Taxes Table (Tax Master) */}
        {activeTab === 'taxes' && (
          <div className="space-y-4">
            {/* Global Tax & Invoice Billing Rules Card */}
            <div className="p-4 bg-gray-50 dark:bg-gray-800/50 rounded-xl border border-gray-200 dark:border-gray-700/60 space-y-3">
              <div className="flex justify-between items-center flex-wrap gap-2">
                <div>
                  <h4 className="font-bold text-sm text-gray-900 dark:text-white flex items-center gap-1.5">
                    🏛️ Global Tax &amp; Invoice Billing Rules
                  </h4>
                  <p className="text-xs text-gray-500 dark:text-gray-400">
                    Configure default GST rates, invoice inclusion rules, and cashier overrides for all POS terminals.
                  </p>
                </div>
                <span className="px-2.5 py-1 rounded-full text-[11px] font-extrabold bg-emerald-50 text-emerald-600 border border-emerald-200">
                  Live Master Active
                </span>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs pt-1">
                <div className="bg-white dark:bg-gray-800 p-3 rounded-lg border border-gray-200 dark:border-gray-700">
                  <span className="font-bold text-gray-700 dark:text-gray-200 block mb-1">Default POS GST Slab</span>
                  <span className="text-gray-500 block text-[11px] mb-2">Applied to fresh orders</span>
                  <select className="w-full px-2 py-1 rounded border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-900 font-semibold">
                    <option value="5">5% GST (2.5% CGST + 2.5% SGST)</option>
                    <option value="12">12% GST (6.0% CGST + 6.0% SGST)</option>
                    <option value="18">18% GST (9.0% CGST + 9.0% SGST)</option>
                    <option value="0">0% (Tax Exempt Default)</option>
                  </select>
                </div>

                <div className="bg-white dark:bg-gray-800 p-3 rounded-lg border border-gray-200 dark:border-gray-700">
                  <span className="font-bold text-gray-700 dark:text-gray-200 block mb-1">Service Charge Levy</span>
                  <span className="text-gray-500 block text-[11px] mb-2">Optional banquet/dine-in charge</span>
                  <div className="flex items-center gap-2">
                    <input type="checkbox" id="rc_scEnable" className="rounded text-[#017A9C]" />
                    <label htmlFor="rc_scEnable" className="font-semibold text-gray-700 dark:text-gray-300 text-[11px]">Enable 5% Charge</label>
                  </div>
                </div>

                <div className="bg-white dark:bg-gray-800 p-3 rounded-lg border border-gray-200 dark:border-gray-700">
                  <span className="font-bold text-gray-700 dark:text-gray-200 block mb-1">POS Cashier Permissions</span>
                  <span className="text-gray-500 block text-[11px] mb-2">Invoice breakdown &amp; overrides</span>
                  <div className="space-y-1 text-[11px] font-semibold text-gray-700 dark:text-gray-300">
                    <div className="flex items-center gap-1.5">
                      <input type="checkbox" defaultChecked className="rounded text-[#017A9C]" />
                      <span>Print CGST/SGST on Bills</span>
                    </div>
                    <div className="flex items-center gap-1.5">
                      <input type="checkbox" defaultChecked className="rounded text-[#017A9C]" />
                      <span>Allow 1-Click Tax Exemption</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <table className="w-full text-left border-collapse text-xs">
              <thead>
                <tr className="border-b border-gray-200 dark:border-gray-800 bg-gray-50 dark:bg-gray-800/50 uppercase tracking-wider text-gray-400">
                  <th className="py-3 px-4">Tax Name</th>
                  <th className="py-3 px-4">Percentage</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
            <tbody className="divide-y divide-gray-100 dark:divide-gray-800">
              {taxes.map((tx) => (
                <tr key={tx.id} className="hover:bg-gray-50 dark:hover:bg-gray-800/40">
                  <td className="py-3 px-4 font-bold text-gray-900 dark:text-white">{tx.taxName || tx.name}</td>
                  <td className="py-3 px-4">
                    <span className="px-2.5 py-1 rounded-full text-xs font-extrabold bg-[#017A9C]/10 text-[#017A9C]">
                      {tx.percentage}%
                    </span>
                  </td>
                  <td className="py-3 px-4">
                    <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${tx.active ? 'bg-emerald-50 text-emerald-600' : 'bg-gray-100 text-gray-500'}`}>
                      {tx.active ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-right space-x-1">
                    <button
                      onClick={() => onOpenEditModal('taxes', tx)}
                      className="px-2.5 py-1 rounded border border-gray-200 dark:border-gray-700 text-[11px] font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-100"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => onToggleActive('taxes', tx.id)}
                      className="px-2.5 py-1 rounded border border-gray-200 dark:border-gray-700 text-[11px] font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-100"
                    >
                      {tx.active ? 'Deactivate' : 'Activate'}
                    </button>
                    <button
                      onClick={() => onDeleteMaster('taxes', tx.id)}
                      className="px-2.5 py-1 rounded text-[11px] font-bold text-red-600 hover:bg-red-50 dark:hover:bg-red-950/40"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          </div>
        )}

        {/* Roles & Rights Table */}
        {activeTab === 'roles' && (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse text-xs">
              <thead>
                <tr className="border-b border-gray-200 dark:border-gray-800 bg-gray-50 dark:bg-gray-800/50 uppercase tracking-wider text-gray-400">
                  <th className="py-3 px-4">Role &amp; Code</th>
                  <th className="py-3 px-4">Description</th>
                  <th className="py-3 px-4">Permissions Active</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100 dark:divide-gray-800">
                {roles.map((r) => {
                  const isKitchen = r.roleCode === 'KITCHEN';
                  const isAdmin = r.roleCode === 'ADMIN';
                  return (
                    <tr key={r.id} className="hover:bg-gray-50 dark:hover:bg-gray-800/40">
                      <td className="py-3 px-4">
                        <div className="flex items-center gap-2">
                          <span className="font-bold text-gray-900 dark:text-white">{r.roleName}</span>
                          <span className="px-1.5 py-0.5 rounded text-[10px] font-extrabold bg-[#017A9C]/10 text-[#017A9C]">
                            {r.roleCode}
                          </span>
                        </div>
                      </td>
                      <td className="py-3 px-4 text-gray-500 max-w-xs">{r.description || '—'}</td>
                      <td className="py-3 px-4">
                        {isKitchen ? (
                          <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-50 text-amber-700 dark:bg-amber-950/40 dark:text-amber-400">
                            🍳 KOT Live Only (Restricted)
                          </span>
                        ) : isAdmin ? (
                          <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-sky-50 text-sky-700 dark:bg-sky-950/40 dark:text-sky-400">
                            👑 Full System Access
                          </span>
                        ) : (
                          <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-300">
                            {(r.permissions || []).length} Rights Active
                          </span>
                        )}
                      </td>
                      <td className="py-3 px-4">
                        <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${r.active ? 'bg-emerald-50 text-emerald-600' : 'bg-gray-100 text-gray-500'}`}>
                          {r.active ? 'Active' : 'Inactive'}
                        </span>
                      </td>
                      <td className="py-3 px-4 text-right space-x-1">
                        <button
                          onClick={() => onOpenEditModal('roles', r)}
                          className="px-2.5 py-1 rounded border border-gray-200 dark:border-gray-700 text-[11px] font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-100"
                        >
                          Edit
                        </button>
                        {!r.isSystem && (
                          <>
                            <button
                              onClick={() => onToggleActive('roles', r.id)}
                              className="px-2.5 py-1 rounded border border-gray-200 dark:border-gray-700 text-[11px] font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-100"
                            >
                              {r.active ? 'Deactivate' : 'Activate'}
                            </button>
                            <button
                              onClick={() => onDeleteMaster('roles', r.id)}
                              className="px-2.5 py-1 rounded text-[11px] font-bold text-red-600 hover:bg-red-50"
                            >
                              Delete
                            </button>
                          </>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

        {/* Staff Master Table */}
        {activeTab === 'users' && (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse text-xs">
              <thead>
                <tr className="border-b border-gray-200 dark:border-gray-800 bg-gray-50 dark:bg-gray-800/50 uppercase tracking-wider text-gray-400">
                  <th className="py-3 px-4">Staff Member</th>
                  <th className="py-3 px-4">Employee Code</th>
                  <th className="py-3 px-4">Assigned Role</th>
                  <th className="py-3 px-4">Contact</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100 dark:divide-gray-800">
                {staffUsers.map((u) => {
                  const role = roles.find((r) => r.id === u.roleId) || { roleName: 'Unassigned', roleCode: 'USER' };
                  const isCurrent = u.id === currentUserId;
                  return (
                    <tr key={u.id} className={`hover:bg-gray-50 dark:hover:bg-gray-800/40 ${isCurrent ? 'bg-[#017A9C]/5' : ''}`}>
                      <td className="py-3 px-4">
                        <div className="flex items-center gap-2">
                          <span className="font-bold text-gray-900 dark:text-white">{u.name}</span>
                          {isCurrent && (
                            <span className="px-1.5 py-0.5 rounded text-[9px] font-extrabold bg-[#017A9C] text-white">
                              CURRENT
                            </span>
                          )}
                        </div>
                      </td>
                      <td className="py-3 px-4 font-mono font-semibold text-gray-600 dark:text-gray-300">{u.userCode}</td>
                      <td className="py-3 px-4">
                        <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-[#017A9C]/10 text-[#017A9C]">
                          {role.roleName}
                        </span>
                      </td>
                      <td className="py-3 px-4 text-gray-500">
                        <div>{u.email}</div>
                        <div className="text-[10px] text-gray-400">{u.phone}</div>
                      </td>
                      <td className="py-3 px-4">
                        <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${u.active ? 'bg-emerald-50 text-emerald-600' : 'bg-gray-100 text-gray-500'}`}>
                          {u.active ? 'Active' : 'Inactive'}
                        </span>
                      </td>
                      <td className="py-3 px-4 text-right space-x-1">
                        {onSwitchUser && (
                          <button
                            onClick={() => onSwitchUser(u.id)}
                            className={`px-2.5 py-1 rounded text-[11px] font-bold ${
                              isCurrent
                                ? 'bg-[#017A9C] text-white'
                                : 'border border-gray-200 dark:border-gray-700 text-gray-700 dark:text-gray-200 hover:bg-gray-100'
                            }`}
                          >
                            {isCurrent ? 'Active' : 'Switch 👤'}
                          </button>
                        )}
                        <button
                          onClick={() => onOpenEditModal('users', u)}
                          className="px-2.5 py-1 rounded border border-gray-200 dark:border-gray-700 text-[11px] font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-100"
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => onToggleActive('users', u.id)}
                          className="px-2.5 py-1 rounded border border-gray-200 dark:border-gray-700 text-[11px] font-bold text-gray-700 dark:text-gray-200 hover:bg-gray-100"
                        >
                          {u.active ? 'Deactivate' : 'Activate'}
                        </button>
                        {staffUsers.length > 1 && (
                          <button
                            onClick={() => onDeleteMaster('users', u.id)}
                            className="px-2.5 py-1 rounded text-[11px] font-bold text-red-600 hover:bg-red-50"
                          >
                            Delete
                          </button>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
