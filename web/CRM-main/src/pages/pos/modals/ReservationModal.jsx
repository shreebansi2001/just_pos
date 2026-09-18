import React, { useState, useEffect } from 'react';
import { X, Calendar } from 'lucide-react';

export function ReservationModal({ isOpen, onClose, reservation, floors, tables, onSave }) {
  const [formData, setFormData] = useState({
    guestName: '',
    phone: '',
    pax: 2,
    duration: 90,
    date: new Date().toISOString().split('T')[0],
    time: '19:30',
    floorId: '',
    tableId: '',
    notes: '',
  });

  useEffect(() => {
    if (reservation) {
      setFormData({ ...reservation });
    } else {
      setFormData({
        guestName: '',
        phone: '',
        pax: 2,
        duration: 90,
        date: new Date().toISOString().split('T')[0],
        time: '19:30',
        floorId: floors[0]?.id || '',
        tableId: '',
        notes: '',
      });
    }
  }, [reservation, floors, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!formData.guestName.trim() || !formData.phone.trim()) {
      alert('Guest name and phone number are required.');
      return;
    }
    onSave({
      ...formData,
      pax: parseInt(formData.pax) || 2,
      duration: parseInt(formData.duration) || 90,
      status: reservation ? reservation.status : 'upcoming',
    });
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-xs p-4">
      <div className="w-full max-w-lg bg-white dark:bg-[#151D28] border border-gray-200 dark:border-gray-800 rounded-2xl shadow-xl overflow-hidden animate-in fade-in zoom-in-95">
        <div className="p-4 border-b border-gray-100 dark:border-gray-800 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Calendar className="w-4 h-4 text-[#017A9C]" />
            <h3 className="font-serif font-bold text-base text-gray-900 dark:text-white">
              {reservation ? 'Edit Reservation' : 'New Table Reservation'}
            </h3>
          </div>
          <button
            onClick={onClose}
            className="w-7 h-7 rounded-full bg-gray-100 dark:bg-gray-800 flex items-center justify-center text-gray-400 hover:text-gray-700 dark:hover:text-gray-200"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-5 max-h-[75vh] overflow-y-auto space-y-4 text-xs">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Guest Name *</label>
              <input
                type="text"
                required
                placeholder="e.g. Verma Family"
                value={formData.guestName}
                onChange={(e) => setFormData({ ...formData, guestName: e.target.value })}
                className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
              />
            </div>
            <div>
              <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Phone Number *</label>
              <input
                type="tel"
                required
                placeholder="98200 xxxxx"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Party Size (Pax) *</label>
              <input
                type="number"
                min="1"
                required
                value={formData.pax}
                onChange={(e) => setFormData({ ...formData, pax: e.target.value })}
                className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
              />
            </div>
            <div>
              <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Duration (Mins)</label>
              <input
                type="number"
                min="15"
                step="15"
                value={formData.duration}
                onChange={(e) => setFormData({ ...formData, duration: e.target.value })}
                className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Booking Date *</label>
              <input
                type="date"
                required
                value={formData.date}
                onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
              />
            </div>
            <div>
              <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Time *</label>
              <input
                type="time"
                required
                value={formData.time}
                onChange={(e) => setFormData({ ...formData, time: e.target.value })}
                className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Floor Preference</label>
              <select
                value={formData.floorId || ''}
                onChange={(e) => setFormData({ ...formData, floorId: e.target.value })}
                className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
              >
                <option value="">Any Floor</option>
                {floors.map((f) => (
                  <option key={f.id} value={f.id}>{f.name}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Assigned Table</label>
              <select
                value={formData.tableId || ''}
                onChange={(e) => setFormData({ ...formData, tableId: e.target.value })}
                className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
              >
                <option value="">Auto-assign at seating</option>
                {tables.map((t) => (
                  <option key={t.id} value={t.id}>
                    {t.shortcode} — {t.name} (Seats {t.capacity})
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div>
            <label className="block font-bold uppercase tracking-wider text-gray-500 mb-1">Special Notes / Occasion</label>
            <textarea
              rows="2"
              placeholder="e.g. Birthday celebration, window seat preference, cake requested"
              value={formData.notes || ''}
              onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
              className="w-full px-3 py-2 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C]"
            />
          </div>

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
              Save Reservation
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
