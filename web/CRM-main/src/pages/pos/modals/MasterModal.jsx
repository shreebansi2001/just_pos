import React, { useState, useEffect } from 'react';
import { X, Plus, Trash2 } from 'lucide-react';

export function MasterModal({ isOpen, onClose, masterType, itemData, categories, floors, tables, onSave }) {
  const [formData, setFormData] = useState({});
  const [hasVariants, setHasVariants] = useState(false);
  const [variants, setVariants] = useState([['Half', ''], ['Full', '']]);

  useEffect(() => {
    if (itemData) {
      setFormData({ ...itemData });
      if (itemData.variants && itemData.variants.length > 0) {
        setHasVariants(true);
        setVariants([...itemData.variants]);
      } else {
        setHasVariants(false);
        setVariants([['Half', ''], ['Full', '']]);
      }
    } else {
      if (masterType === 'categories') {
        setFormData({ name: '', sortOrder: (categories.length || 0) + 1, active: true });
      } else if (masterType === 'items') {
        setFormData({
          name: '',
          categoryId: categories[0]?.id || '',
          station: 'Kitchen',
          veg: true,
          price: '',
          tag: '',
          active: true
        });
        setHasVariants(false);
        setVariants([['Half', ''], ['Full', '']]);
      } else if (masterType === 'floors') {
        setFormData({ name: '', shortcode: '', sortOrder: (floors.length || 0) + 1, active: true });
      } else if (masterType === 'tables') {
        setFormData({ name: '', shortcode: '', capacity: 4, floorId: floors[0]?.id || '', active: true });
      } else if (masterType === 'taxes') {
        setFormData({ taxName: '', percentage: 5.0, active: true });
      }
    }
  }, [itemData, masterType, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    let finalPayload = { ...formData };
    if (masterType === 'items') {
      if (hasVariants) {
        const validVars = variants
          .filter((v) => v[0] && v[1] !== '')
          .map((v) => [v[0], parseFloat(v[1]) || 0]);
        if (validVars.length === 0) {
          alert('Please specify at least one valid portion size and price.');
          return;
        }
        finalPayload.variants = validVars;
        delete finalPayload.price;
      } else {
        finalPayload.price = parseFloat(finalPayload.price) || 0;
        delete finalPayload.variants;
      }
    }
    onSave(masterType, finalPayload);
    onClose();
  };

  const title = (itemData ? 'Edit ' : 'Add ') + masterType.slice(0, -1);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-xs p-4">
      <div className="w-full max-w-lg bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-2xl shadow-xl overflow-hidden animate-in fade-in zoom-in-95">
        <div className="p-4 border-b border-gray-100 dark:border-gray-800 flex items-center justify-between">
          <h3 className="font-serif font-bold text-base capitalize text-gray-900 dark:text-white">{title}</h3>
          <button
            onClick={onClose}
            className="w-7 h-7 rounded-full bg-gray-100 dark:bg-gray-800 flex items-center justify-center text-gray-400 hover:text-gray-700 dark:hover:text-gray-200"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-5 max-h-[75vh] overflow-y-auto space-y-4 text-xs">
          {/* Category Fields */}
          {masterType === 'categories' && (
            <>
              <div>
                <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Category Name *</label>
                <input
                  type="text"
                  required
                  value={formData.name || ''}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="e.g. Starters"
                  className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Sort Order</label>
                  <input
                    type="number"
                    value={formData.sortOrder || 1}
                    onChange={(e) => setFormData({ ...formData, sortOrder: parseInt(e.target.value) || 1 })}
                    className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
                  />
                </div>
                <div>
                  <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Status</label>
                  <label className="flex items-center gap-2 mt-2 cursor-pointer font-bold">
                    <input
                      type="checkbox"
                      checked={formData.active ?? true}
                      onChange={(e) => setFormData({ ...formData, active: e.target.checked })}
                      className="accent-[#017A9C]"
                    />
                    <span>Active in POS</span>
                  </label>
                </div>
              </div>
            </>
          )}

          {/* Item Fields */}
          {masterType === 'items' && (
            <>
              <div>
                <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Dish Name *</label>
                <input
                  type="text"
                  required
                  value={formData.name || ''}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="e.g. Paneer Tikka"
                  className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Category *</label>
                  <select
                    value={formData.categoryId || ''}
                    onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
                    className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
                  >
                    {categories.map((c) => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Kitchen Station</label>
                  <select
                    value={formData.station || 'Kitchen'}
                    onChange={(e) => setFormData({ ...formData, station: e.target.value })}
                    className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
                  >
                    <option>Kitchen</option>
                    <option>Bar</option>
                    <option>Live Counter</option>
                    <option>Dessert Counter</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3 items-center">
                <div>
                  <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Dietary Type</label>
                  <div className="flex gap-4 mt-1">
                    <label className="flex items-center gap-1.5 cursor-pointer font-semibold">
                      <input
                        type="radio"
                        name="veg"
                        checked={formData.veg === true}
                        onChange={() => setFormData({ ...formData, veg: true })}
                        className="accent-emerald-600"
                      />
                      🟢 Veg
                    </label>
                    <label className="flex items-center gap-1.5 cursor-pointer font-semibold">
                      <input
                        type="radio"
                        name="veg"
                        checked={formData.veg === false}
                        onChange={() => setFormData({ ...formData, veg: false })}
                        className="accent-red-600"
                      />
                      🔴 Non-veg
                    </label>
                  </div>
                </div>

                <div>
                  <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Status</label>
                  <label className="flex items-center gap-2 mt-1 cursor-pointer font-bold">
                    <input
                      type="checkbox"
                      checked={formData.active ?? true}
                      onChange={(e) => setFormData({ ...formData, active: e.target.checked })}
                      className="accent-[#017A9C]"
                    />
                    <span>Active in POS</span>
                  </label>
                </div>
              </div>

              <div>
                <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Short Description</label>
                <input
                  type="text"
                  value={formData.tag || ''}
                  onChange={(e) => setFormData({ ...formData, tag: e.target.value })}
                  placeholder="e.g. Smoky, char-grilled cottage cheese"
                  className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
                />
              </div>

              <div className="pt-2 border-t border-gray-100 dark:border-gray-800">
                <label className="flex items-center gap-2 cursor-pointer font-bold">
                  <input
                    type="checkbox"
                    checked={hasVariants}
                    onChange={(e) => setHasVariants(e.target.checked)}
                    className="accent-[#017A9C]"
                  />
                  <span>This item has size variants (e.g. Half / Full)</span>
                </label>
              </div>

              {!hasVariants ? (
                <div>
                  <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Price (₹) *</label>
                  <input
                    type="number"
                    value={formData.price ?? ''}
                    onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                    placeholder="220"
                    className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
                  />
                </div>
              ) : (
                <div className="space-y-2">
                  <label className="block font-bold uppercase tracking-wider text-gray-500">Sizes &amp; Rates</label>
                  {variants.map((v, i) => (
                    <div key={i} className="flex gap-2 items-center">
                      <input
                        type="text"
                        placeholder="Size (e.g. Half)"
                        value={v[0]}
                        onChange={(e) => {
                          const copy = [...variants];
                          copy[i][0] = e.target.value;
                          setVariants(copy);
                        }}
                        className="flex-1 px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none"
                      />
                      <input
                        type="number"
                        placeholder="Price"
                        value={v[1]}
                        onChange={(e) => {
                          const copy = [...variants];
                          copy[i][1] = e.target.value;
                          setVariants(copy);
                        }}
                        className="w-28 px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none"
                      />
                      <button
                        type="button"
                        onClick={() => {
                          if (variants.length > 1) {
                            setVariants(variants.filter((_, idx) => idx !== i));
                          }
                        }}
                        className="p-2 text-red-500 hover:bg-red-50 rounded"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  ))}
                  <button
                    type="button"
                    onClick={() => setVariants([...variants, ['', '']])}
                    className="mt-1 px-3 py-1.5 rounded-lg border border-dashed border-gray-300 text-xs font-bold text-gray-600 hover:border-[#017A9C] flex items-center gap-1"
                  >
                    <Plus className="w-3 h-3" /> Add Size Option
                  </button>
                </div>
              )}
            </>
          )}

          {/* Floor Fields */}
          {masterType === 'floors' && (
            <>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Floor Name *</label>
                  <input
                    type="text"
                    required
                    value={formData.name || ''}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    placeholder="e.g. Rooftop Lounge"
                    className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
                  />
                </div>
                <div>
                  <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Shortcode *</label>
                  <input
                    type="text"
                    required
                    maxLength="6"
                    value={formData.shortcode || ''}
                    onChange={(e) => setFormData({ ...formData, shortcode: e.target.value.toUpperCase() })}
                    placeholder="e.g. RT"
                    className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm font-mono focus:outline-none focus:border-[#017A9C]"
                  />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Sort Order</label>
                  <input
                    type="number"
                    value={formData.sortOrder || 1}
                    onChange={(e) => setFormData({ ...formData, sortOrder: parseInt(e.target.value) || 1 })}
                    className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
                  />
                </div>
                <div>
                  <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Status</label>
                  <label className="flex items-center gap-2 mt-2 cursor-pointer font-bold">
                    <input
                      type="checkbox"
                      checked={formData.active ?? true}
                      onChange={(e) => setFormData({ ...formData, active: e.target.checked })}
                      className="accent-[#017A9C]"
                    />
                    <span>Active in POS</span>
                  </label>
                </div>
              </div>
            </>
          )}

          {/* Table Fields */}
          {masterType === 'tables' && (
            <>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Table Name *</label>
                  <input
                    type="text"
                    required
                    value={formData.name || ''}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    placeholder="e.g. Table 14"
                    className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
                  />
                </div>
                <div>
                  <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Shortcode *</label>
                  <input
                    type="text"
                    required
                    maxLength="8"
                    value={formData.shortcode || ''}
                    onChange={(e) => setFormData({ ...formData, shortcode: e.target.value.toUpperCase() })}
                    placeholder="e.g. GF-8"
                    className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm font-mono focus:outline-none focus:border-[#017A9C]"
                  />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Seating Capacity</label>
                  <input
                    type="number"
                    min="1"
                    value={formData.capacity || 4}
                    onChange={(e) => setFormData({ ...formData, capacity: parseInt(e.target.value) || 4 })}
                    className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
                  />
                </div>
                <div>
                  <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Floor *</label>
                  <select
                    value={formData.floorId || ''}
                    onChange={(e) => setFormData({ ...formData, floorId: e.target.value })}
                    className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
                  >
                    {floors.map((f) => (
                      <option key={f.id} value={f.id}>{f.name}</option>
                    ))}
                  </select>
                </div>
              </div>
              <div>
                <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Status</label>
                <label className="flex items-center gap-2 mt-1 cursor-pointer font-bold">
                  <input
                    type="checkbox"
                    checked={formData.active ?? true}
                    onChange={(e) => setFormData({ ...formData, active: e.target.checked })}
                    className="accent-[#017A9C]"
                  />
                  <span>Active in POS</span>
                </label>
              </div>
            </>
          )}

          {/* Tax Master Fields */}
          {masterType === 'taxes' && (
            <>
              <div>
                <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Tax Name *</label>
                <input
                  type="text"
                  required
                  value={formData.taxName || formData.name || ''}
                  onChange={(e) => setFormData({ ...formData, taxName: e.target.value, name: e.target.value })}
                  placeholder="e.g. CGST, SGST, IGST"
                  className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
                />
              </div>
              <div>
                <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Tax Rate (%) *</label>
                <input
                  type="number"
                  step="0.1"
                  min="0"
                  max="100"
                  required
                  value={formData.percentage ?? ''}
                  onChange={(e) => setFormData({ ...formData, percentage: parseFloat(e.target.value) || 0 })}
                  placeholder="e.g. 2.5"
                  className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
                />
              </div>
              <div>
                <label className="flex items-center gap-2 cursor-pointer font-bold text-gray-700 dark:text-gray-200">
                  <input
                    type="checkbox"
                    checked={formData.active ?? true}
                    onChange={(e) => setFormData({ ...formData, active: e.target.checked })}
                    className="accent-[#017A9C]"
                  />
                  <span>Active in Tax Calculations</span>
                </label>
              </div>
            </>
          )}

          <div className="pt-4 border-t border-gray-100 dark:border-gray-800 flex gap-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-2 rounded-xl border border-gray-200 dark:border-gray-700 text-xs font-bold text-gray-700 dark:text-gray-300 hover:bg-gray-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="flex-1 py-2 rounded-xl bg-[#017A9C] hover:bg-[#016582] text-white font-bold text-xs shadow-sm transition-all"
            >
              Save Changes
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
