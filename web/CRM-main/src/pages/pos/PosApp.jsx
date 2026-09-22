import React, { useState, useEffect } from 'react';
import { PosSidebar } from './components/PosSidebar';
import { PosTopbar } from './components/PosTopbar';
import { TablesView } from './components/TablesView';
import { OrderDeskView } from './components/OrderDeskView';
import { KotKanbanView } from './components/KotKanbanView';
import { BillingView } from './components/BillingView';
import { ReservationsView } from './components/ReservationsView';
import { MastersView } from './components/MastersView';
import { OrdersView } from './components/OrdersView';

import { VariantModal } from './modals/VariantModal';
import { PickerModal } from './modals/PickerModal';
import { InvoiceModal } from './modals/InvoiceModal';
import { MasterModal } from './modals/MasterModal';
import { ReservationModal } from './modals/ReservationModal';

import { initialPosSeed, posApi } from '../../services/posService';

export function PosApp() {
  const [isDark, setIsDark] = useState(false);
  const [activeView, setActiveView] = useState('tables');

  // Master and Operational States
  const [categories, setCategories] = useState(initialPosSeed.categories);
  const [items, setItems] = useState(initialPosSeed.items);
  const [floors, setFloors] = useState(initialPosSeed.floors);
  const [tables, setTables] = useState(initialPosSeed.tables);
  const [taxes, setTaxes] = useState(initialPosSeed.taxes || []);
  const [roles, setRoles] = useState(initialPosSeed.roles || []);
  const [staffUsers, setStaffUsers] = useState(initialPosSeed.staffUsers || []);
  const [currentUserId, setCurrentUserId] = useState(initialPosSeed.staffUsers?.[0]?.id || 'USR01');

  const currentUser = staffUsers.find((u) => u.id === currentUserId) || staffUsers[0];
  const currentRole = roles.find((r) => r.id === currentUser?.roleId) || roles[0];
  const userPermissions = currentRole?.permissions || [];

  const handleSwitchUser = (newUserId) => {
    setCurrentUserId(newUserId);
    const targetUser = staffUsers.find((u) => u.id === newUserId);
    const targetRole = roles.find((r) => r.id === targetUser?.roleId);
    if (targetRole) {
      const perms = targetRole.permissions || [];
      const permKey = activeView === 'pos' ? 'view_pos' : `view_${activeView}`;
      if (!perms.includes(permKey)) {
        if (targetRole.roleCode === 'KITCHEN' && perms.includes('view_kot')) {
          setActiveView('kot');
        } else {
          const firstAllowed = ['tables', 'orders', 'pos', 'kot', 'billing', 'reservations', 'masters'].find(
            (v) => perms.includes(v === 'pos' ? 'view_pos' : `view_${v}`)
          );
          if (firstAllowed) setActiveView(firstAllowed);
        }
      }
    }
  };

  const [orders, setOrders] = useState({});
  const [cancelledOrders, setCancelledOrders] = useState([]);
  const [kots, setKots] = useState({});
  const [invoices, setInvoices] = useState({});
  const [reservations, setReservations] = useState({});

  const [currentOrderId, setCurrentOrderId] = useState(null);
  const [activeFloorId, setActiveFloorId] = useState('all');

  // Modals state
  const [variantModalOpen, setVariantModalOpen] = useState(false);
  const [selectedVariantItem, setSelectedVariantItem] = useState(null);

  const [pickerModalOpen, setPickerModalOpen] = useState(false);
  const [pickerConfig, setPickerConfig] = useState({ title: '', filterFn: null, onSelect: () => {} });

  const [invoiceModalOpen, setInvoiceModalOpen] = useState(false);
  const [selectedInvoice, setSelectedInvoice] = useState(null);

  const [masterModalOpen, setMasterModalOpen] = useState(false);
  const [activeMasterType, setActiveMasterType] = useState('categories');
  const [editingMasterItem, setEditingMasterItem] = useState(null);

  const [reservationModalOpen, setReservationModalOpen] = useState(false);
  const [editingReservation, setEditingReservation] = useState(null);

  const [resFilterDate, setResFilterDate] = useState(new Date().toISOString().split('T')[0]);
  const [resFilterStatus, setResFilterStatus] = useState('all');

  // Sequence counters for demo
  const [seq, setSeq] = useState({ order: 10, kot: 10, inv: 68, res: 10 });

  // Initialize Theme and seed demo live state
  useEffect(() => {
    // Dark mode check
    if (document.documentElement.classList.contains('dark') || document.documentElement.getAttribute('data-theme') === 'dark') {
      setIsDark(true);
    }

    // Seed realistic active operational state
    const today = new Date().toISOString().split('T')[0];

    // Seed an occupied table (TBL03)
    const oid1 = 'ORD001';
    const kid1 = 'KOT001';
    const initOrders = {
      [oid1]: {
        id: oid1,
        type: 'dine-in',
        tableId: 'TBL03',
        customerName: '',
        customerPhone: '',
        discountType: 'pct',
        discountVal: 0,
        status: 'open',
        createdAt: Date.now() - 15 * 60000,
        kotIds: [kid1],
        items: {
          ITM01: { itemId: 'ITM01', name: 'Paneer Tikka', price: 220, qty: 2, sentQty: 2 },
          ITM09: { itemId: 'ITM09', name: 'Butter Chicken (Full)', price: 300, qty: 1, sentQty: 1 },
          ITM18: { itemId: 'ITM18', name: 'Garlic Naan', price: 65, qty: 4, sentQty: 4 },
          ITM24: { itemId: 'ITM24', name: 'Gulab Jamun (2 pc)', price: 90, qty: 2, sentQty: 0 },
        },
      },
    };

    const initKots = {
      [kid1]: {
        id: kid1,
        orderId: oid1,
        tableLabel: 'GF-3',
        type: 'dine-in',
        status: 'preparing',
        createdAt: Date.now() - 10 * 60000,
        items: [
          { name: 'Paneer Tikka', qty: 2 },
          { name: 'Butter Chicken (Full)', qty: 1 },
          { name: 'Garlic Naan', qty: 4 },
        ],
      },
      KOT002: {
        id: 'KOT002',
        orderId: 'ORD002',
        tableLabel: 'RT-1',
        type: 'dine-in',
        status: 'ready',
        createdAt: Date.now() - 18 * 60000,
        items: [
          { name: 'Hyderabadi Chicken Biryani (Full)', qty: 2 },
          { name: 'Virgin Mojito', qty: 2 },
        ],
      },
    };

    const initTables = initialPosSeed.tables.map((t) => {
      if (t.id === 'TBL03') return { ...t, status: 'occupied', orderId: oid1 };
      if (t.id === 'TBL07') return { ...t, status: 'occupied', orderId: 'ORD002' };
      if (t.id === 'TBL11') return { ...t, status: 'cleaning', orderId: null };
      return t;
    });

    const initInvoices = {
      'KTA-KG/00068/26-27': {
        id: 'KTA-KG/00068/26-27',
        orderId: 'ORD-0068',
        type: 'takeaway',
        tableLabel: 'Catering',
        customerName: 'Vijay Khorjuvekar',
        customerPhone: '8830768469',
        sub: 7300,
        taxableAmount: 6952.36,
        disc: 0,
        cgst: 173.82,
        sgst: 173.82,
        total: 7300,
        status: 'paid',
        paymentMode: 'Cash',
        createdAt: Date.now() - 45 * 60000,
        items: [
          { name: 'KG-Vegetable Biriyani', qty: 2.50, unitMode: 'kg', weightKg: 2.50, price: 1100, pricePerKg: 1100 },
          { name: 'KG-Veg Kolhapuri', qty: 2.00, unitMode: 'kg', weightKg: 2.00, price: 900, pricePerKg: 900 },
          { name: 'Kg-Steamed Basmati Rice', qty: 2.50, unitMode: 'kg', weightKg: 2.50, price: 900, pricePerKg: 900 },
          { name: 'Transport Services', qty: 1.00, unitMode: 'portion', price: 500 },
        ],
      },
    };

    const initReservations = {
      RES001: {
        id: 'RES001',
        guestName: 'Verma Family',
        phone: '98765 43210',
        pax: 4,
        duration: 90,
        date: today,
        time: '20:00',
        floorId: 'FLR01',
        tableId: 'TBL05',
        notes: 'Anniversary celebration',
        status: 'upcoming',
        orderId: null,
      },
      RES002: {
        id: 'RES002',
        guestName: 'Kapoor Party',
        phone: '99887 66554',
        pax: 6,
        duration: 120,
        date: today,
        time: '20:30',
        floorId: 'FLR02',
        tableId: null,
        notes: 'Window side seating',
        status: 'upcoming',
        orderId: null,
      },
    };

    setOrders(initOrders);
    setKots(initKots);
    setTables(initTables);
    setInvoices(initInvoices);
    setReservations(initReservations);
  }, []);

  const toggleTheme = () => {
    const next = !isDark;
    setIsDark(next);
    const root = document.documentElement;
    if (next) {
      root.classList.add('dark');
      root.setAttribute('data-theme', 'dark');
    } else {
      root.classList.remove('dark');
      root.setAttribute('data-theme', 'light');
    }
  };

  // Metrics computation
  const freeTablesCount = tables.filter((t) => t.active && t.status === 'available').length;
  const inUseTablesCount = tables.filter((t) => t.active && t.status !== 'available').length;
  const activeKotsCount = Object.values(kots).filter((k) => k.status !== 'served').length;
  const todaySalesTotal = Object.values(invoices)
    .filter((i) => i.status === 'paid')
    .reduce((sum, i) => sum + (i.total || 0), 0);

  const todayStr = new Date().toISOString().split('T')[0];
  const upcomingResTodayCount = Object.values(reservations).filter(
    (r) => r.date === todayStr && r.status === 'upcoming'
  ).length;

  // View Titles
  const viewMeta = {
    tables: { title: 'Tables & Floor', subtitle: 'Live floor plan — tap a table to start or resume order' },
    orders: { title: 'All Orders Desk', subtitle: 'Manage, edit, cancel, pay, and track all live, pending, and past dining orders' },
    pos: { title: 'Order Desk', subtitle: 'Build the order, add items, and fire to the kitchen' },
    kot: { title: 'Kitchen Display (KOT)', subtitle: 'Live 4-column kanban grouped by station & elapsed time' },
    billing: { title: 'Billing & Register', subtitle: 'Generated invoices, printable receipts, and payment settlements' },
    reservations: { title: 'Table Reservations', subtitle: 'Floor bookings schedule with one-click capacity seating' },
    masters: { title: 'POS Masters Setup', subtitle: 'Configure menu categories, dishes, variants, floors, and tables' },
  };

  // Order Actions
  const handleStartOrder = (type, tableId = null) => {
    const nextOrdNum = seq.order + 1;
    setSeq({ ...seq, order: nextOrdNum });
    const orderId = `ORD${String(nextOrdNum).padStart(3, '0')}`;

    const newOrder = {
      id: orderId,
      type,
      tableId,
      customerName: '',
      customerPhone: '',
      discountType: 'pct',
      discountVal: 0,
      status: 'open',
      createdAt: Date.now(),
      kotIds: [],
      items: {},
    };

    setOrders({ ...orders, [orderId]: newOrder });
    if (tableId) {
      setTables(
        tables.map((t) => (t.id === tableId ? { ...t, status: 'occupied', orderId } : t))
      );
    }
    setCurrentOrderId(orderId);
    setActiveView('pos');
  };

  const handleOpenOrder = (orderId) => {
    setCurrentOrderId(orderId);
    setActiveView('pos');
  };

  const handleChangeItemQty = (item, delta, variantKey = null) => {
    if (!currentOrderId || !orders[currentOrderId]) return;
    const ord = { ...orders[currentOrderId] };
    const key = variantKey || item.id;
    const existing = ord.items?.[key];

    if (delta > 0) {
      if (!existing) {
        ord.items = {
          ...ord.items,
          [key]: {
            itemId: item.id,
            name: item.name,
            price: item.price,
            qty: 1,
            sentQty: 0,
          },
        };
      } else {
        ord.items[key] = { ...existing, qty: existing.qty + 1 };
      }
    } else if (existing) {
      if (existing.sentQty > 0 && existing.qty <= existing.sentQty) {
        if (!confirm(`Void 1 × "${existing.name}"? It was already sent to the kitchen.`)) {
          return;
        }
        ord.items[key] = { ...existing, qty: existing.qty - 1, sentQty: Math.max(existing.sentQty - 1, 0) };
      } else {
        ord.items[key] = { ...existing, qty: existing.qty - 1 };
      }

      if (ord.items[key].qty <= 0) {
        delete ord.items[key];
      }
    }

    setOrders({ ...orders, [currentOrderId]: ord });
  };

  const handleSendKot = () => {
    if (!currentOrderId || !orders[currentOrderId]) return;
    const ord = { ...orders[currentOrderId] };
    const pendingLines = Object.entries(ord.items || {}).filter(
      ([, line]) => (line.qty || 0) > (line.sentQty || 0)
    );

    if (pendingLines.length === 0) return;

    // Check if an active KOT already exists for this order
    const existingActiveKotId = (ord.kotIds || []).find(id => kots[id] && kots[id].status !== 'served' && kots[id].status !== 'cancelled');
    if (existingActiveKotId && kots[existingActiveKotId]) {
      const activeKot = { ...kots[existingActiveKotId] };
      activeKot.round = (activeKot.round || 1) + 1;
      const newItems = pendingLines.map(([, line]) => ({
        name: line.name,
        qty: line.qty - (line.sentQty || 0),
        round: activeKot.round,
        note: line.note || ord.instructions || ""
      }));
      activeKot.items = [...activeKot.items, ...newItems];
      if (ord.instructions) {
        activeKot.chefNote = activeKot.chefNote ? `${activeKot.chefNote} • [R${activeKot.round}]: ${ord.instructions}` : ord.instructions;
      }
      if (activeKot.status === 'ready') activeKot.status = 'preparing';

      pendingLines.forEach(([k]) => {
        ord.items[k].sentQty = ord.items[k].qty;
      });

      setKots({ ...kots, [existingActiveKotId]: activeKot });
      setOrders({ ...orders, [currentOrderId]: ord });
      return;
    }

    const nextKotNum = seq.kot + 1;
    setSeq({ ...seq, kot: nextKotNum });
    const kotId = `KOT${String(nextKotNum).padStart(3, '0')}`;

    const table = tables.find((t) => t.id === ord.tableId);
    const tableLabel = ord.type === 'dine-in' ? table?.shortcode || 'Table' : ord.type.toUpperCase();

    const newKot = {
      id: kotId,
      orderId: ord.id,
      tableLabel,
      type: ord.type,
      status: 'new',
      round: 1,
      chefNote: ord.instructions || null,
      createdAt: Date.now(),
      items: pendingLines.map(([, line]) => ({
        name: line.name,
        qty: line.qty - (line.sentQty || 0),
        round: 1,
        note: line.note || ""
      })),
    };

    // Mark items as sent
    pendingLines.forEach(([k]) => {
      ord.items[k].sentQty = ord.items[k].qty;
    });
    ord.kotIds = [...(ord.kotIds || []), kotId];

    setKots({ ...kots, [kotId]: newKot });
    setOrders({ ...orders, [currentOrderId]: ord });
  };

  const handleGenerateInvoice = () => {
    if (!currentOrderId || !orders[currentOrderId]) return;
    const ord = orders[currentOrderId];
    const table = tables.find((t) => t.id === ord.tableId);
    const tableLabel = ord.type === 'dine-in' ? table?.shortcode : ord.type.toUpperCase();

    // Check if unpaid invoice already exists
    let inv = Object.values(invoices).find((i) => i.orderId === ord.id && i.status === 'unpaid');

    let sub = 0;
    Object.values(ord.items || {}).forEach((i) => (sub += i.price * i.qty));
    const total = sub;
    const taxableAmount = Math.round((total / 1.05) * 100) / 100;
    const cgst = Math.round((taxableAmount * 0.025) * 100) / 100;
    const sgst = Math.round((taxableAmount * 0.025) * 100) / 100;

    if (!inv) {
      const hasKg = Object.values(ord.items || {}).some(i => i.unitMode === 'kg');
      const nextInvNum = (seq.inv || 68) + 1;
      setSeq({ ...seq, inv: nextInvNum });
      const prefix = hasKg ? 'KTA-KG' : 'KTA';
      const invId = `${prefix}/${String(nextInvNum).padStart(5, '0')}/26-27`;

      inv = {
        id: invId,
        orderId: ord.id,
        type: ord.type,
        tableLabel,
        customerName: ord.customerName || 'Vijay Khorjuvekar',
        customerPhone: ord.customerPhone || '8830768469',
        items: Object.values(ord.items || {}).map((i) => ({ 
          name: i.name, 
          qty: i.qty, 
          price: i.price, 
          unitMode: i.unitMode, 
          weightKg: i.weightKg, 
          pricePerKg: i.pricePerKg,
          addons: i.addons 
        })),
        sub,
        taxableAmount,
        disc,
        cgst,
        sgst,
        total,
        status: 'unpaid',
        paymentMode: 'Cash',
        createdAt: Date.now(),
      };
    } else {
      inv = {
        ...inv,
        items: Object.values(ord.items || {}).map((i) => ({ 
          name: i.name, 
          qty: i.qty, 
          price: i.price, 
          unitMode: i.unitMode, 
          weightKg: i.weightKg, 
          pricePerKg: i.pricePerKg,
          addons: i.addons 
        })),
        sub,
        taxableAmount,
        disc,
        cgst,
        sgst,
        total,
      };
    }

    if (ord.tableId) {
      setTables(tables.map((t) => (t.id === ord.tableId ? { ...t, status: 'billed' } : t)));
    }

    setInvoices({ ...invoices, [inv.id]: inv });
    setSelectedInvoice(inv);
    setInvoiceModalOpen(true);
  };

  const handleSettlePayment = (invoiceId, paymentMode) => {
    const inv = invoices[invoiceId];
    if (!inv) return;

    const updatedInv = { ...inv, status: 'paid', paymentMode };
    const ord = orders[inv.orderId];

    if (ord) {
      if (ord.tableId) {
        setTables(
          tables.map((t) => (t.id === ord.tableId ? { ...t, status: 'cleaning', orderId: null } : t))
        );
      }
      if (ord.reservationId && reservations[ord.reservationId]) {
        setReservations({
          ...reservations,
          [ord.reservationId]: { ...reservations[ord.reservationId], status: 'completed' },
        });
      }
      const updatedOrders = { ...orders };
      delete updatedOrders[ord.id];
      setOrders(updatedOrders);
    }

    setInvoices({ ...invoices, [invoiceId]: updatedInv });
    setInvoiceModalOpen(false);
    if (currentOrderId === inv.orderId) {
      setCurrentOrderId(null);
      setActiveView('tables');
    }
  };

  const handleFreeTable = (tableId) => {
    const t = tables.find((x) => x.id === tableId);
    if (!t) return;
    if (confirm(`Free ${t.shortcode} and clear current order?`)) {
      if (t.orderId && orders[t.orderId]) {
        const updated = { ...orders };
        delete updated[t.orderId];
        setOrders(updated);
      }
      setTables(tables.map((tbl) => (tbl.id === tableId ? { ...tbl, status: 'available', orderId: null } : tbl)));
      if (currentOrderId === t.orderId) {
        setCurrentOrderId(null);
      }
    }
  };

  const handleMarkTableClean = (tableId) => {
    setTables(
      tables.map((t) => (t.id === tableId ? { ...t, status: 'available', orderId: null } : t))
    );
  };

  const handleCancelOrder = (orderId, reason) => {
    const o = orders[orderId];
    if (!o) return;
    const userReason = reason || prompt(`Cancel ${o.id}? Please enter reason:`, 'Customer cancelled / Voided');
    if (userReason === null) return;
    if (o.tableId) {
      setTables(tables.map((t) => (t.id === o.tableId ? { ...t, status: 'available', orderId: null, isPending: false } : t)));
    }
    const cancelledRecord = {
      ...o,
      status: 'cancelled',
      cancelReason: userReason || 'Cancelled by operator',
      cancelledAt: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };
    setCancelledOrders((prev) => [cancelledRecord, ...prev]);

    const updated = { ...orders };
    delete updated[orderId];
    setOrders(updated);
    if (currentOrderId === orderId) {
      setCurrentOrderId(null);
      setActiveView('orders');
    }
  };

  const handleSavePending = (orderId) => {
    const targetId = orderId || currentOrderId;
    const o = orders[targetId];
    if (!o) return;
    setOrders({
      ...orders,
      [targetId]: { ...o, status: 'pending' },
    });
    if (o.tableId) {
      setTables(
        tables.map((t) => (t.id === o.tableId ? { ...t, isPending: true } : t))
      );
    }
    alert(`Order ${o.id} marked as Pending / On Hold.`);
    setActiveView('orders');
  };

  const handleMoveKotStatus = (kotId, targetStatus) => {
    if (kots[kotId]) {
      setKots({
        ...kots,
        [kotId]: { ...kots[kotId], status: targetStatus },
      });
    }
  };

  // Seating a reservation
  const handleSeatReservation = (resId, preselectedTableId = null) => {
    const res = reservations[resId];
    if (!res) return;

    const doSeat = (tId) => {
      const targetTable = tables.find((t) => t.id === tId);
      if (!targetTable || targetTable.status !== 'available') {
        alert('Table is not available. Please pick another.');
        return;
      }

      const nextOrdNum = seq.order + 1;
      setSeq({ ...seq, order: nextOrdNum });
      const orderId = `ORD${String(nextOrdNum).padStart(3, '0')}`;

      const newOrder = {
        id: orderId,
        type: 'dine-in',
        tableId: tId,
        customerName: res.guestName,
        customerPhone: res.phone,
        reservationId: res.id,
        discountType: 'pct',
        discountVal: 0,
        status: 'open',
        createdAt: Date.now(),
        kotIds: [],
        items: {},
      };

      setOrders({ ...orders, [orderId]: newOrder });
      setTables(
        tables.map((t) => (t.id === tId ? { ...t, status: 'occupied', orderId } : t))
      );
      setReservations({
        ...reservations,
        [resId]: { ...res, status: 'seated', tableId: tId, orderId },
      });
      setCurrentOrderId(orderId);
      setActiveView('pos');
    };

    if (preselectedTableId) {
      doSeat(preselectedTableId);
    } else if (res.tableId) {
      const t = tables.find((x) => x.id === res.tableId);
      if (t && t.status === 'available') {
        doSeat(t.id);
      } else {
        // Open table picker
        setPickerConfig({
          title: `Seat ${res.guestName} (Seats ${res.pax})`,
          filterFn: (tbl) => tbl.capacity >= res.pax,
          onSelect: (chosenTableId) => doSeat(chosenTableId),
        });
        setPickerModalOpen(true);
      }
    } else {
      setPickerConfig({
        title: `Seat ${res.guestName} (Seats ${res.pax})`,
        filterFn: (tbl) => tbl.capacity >= res.pax,
        onSelect: (chosenTableId) => doSeat(chosenTableId),
      });
      setPickerModalOpen(true);
    }
  };

  // Move table for current order
  const handleMoveTable = () => {
    if (!currentOrderId || !orders[currentOrderId]) return;
    const ord = orders[currentOrderId];
    setPickerConfig({
      title: 'Move Order to Free Table',
      filterFn: (t) => t.id !== ord.tableId,
      onSelect: (newTableId) => {
        const oldTable = tables.find((t) => t.id === ord.tableId);
        const newTable = tables.find((t) => t.id === newTableId);
        if (!newTable) return;

        setTables(
          tables.map((t) => {
            if (t.id === oldTable?.id) return { ...t, status: 'available', orderId: null };
            if (t.id === newTableId) return { ...t, status: 'occupied', orderId: ord.id };
            return t;
          })
        );
        setOrders({
          ...orders,
          [currentOrderId]: { ...ord, tableId: newTableId },
        });
      },
    });
    setPickerModalOpen(true);
  };

  // Master CRUD saves
  const handleSaveMaster = (type, data) => {
    if (type === 'categories') {
      if (data.id) {
        setCategories(categories.map((c) => (c.id === data.id ? { ...c, ...data } : c)));
      } else {
        setCategories([...categories, { ...data, id: `CAT${Date.now()}` }]);
      }
    } else if (type === 'items') {
      if (data.id) {
        setItems(items.map((i) => (i.id === data.id ? { ...i, ...data } : i)));
      } else {
        setItems([...items, { ...data, id: `ITM${Date.now()}` }]);
      }
    } else if (type === 'floors') {
      if (data.id) {
        setFloors(floors.map((f) => (f.id === data.id ? { ...f, ...data } : f)));
      } else {
        setFloors([...floors, { ...data, id: `FLR${Date.now()}` }]);
      }
    } else if (type === 'tables') {
      if (data.id) {
        setTables(tables.map((t) => (t.id === data.id ? { ...t, ...data } : t)));
      } else {
        setTables([...tables, { ...data, id: `TBL${Date.now()}`, status: 'available', orderId: null }]);
      }
    } else if (type === 'taxes') {
      if (data.id) {
        setTaxes(taxes.map((tx) => (tx.id === data.id ? { ...tx, ...data } : tx)));
      } else {
        setTaxes([...taxes, { ...data, id: `TAX${Date.now()}` }]);
      }
    } else if (type === 'roles') {
      if (data.id) {
        setRoles(roles.map((r) => (r.id === data.id ? { ...r, ...data } : r)));
      } else {
        setRoles([...roles, { ...data, id: `ROL${Date.now()}` }]);
      }
    } else if (type === 'users') {
      if (data.id) {
        setStaffUsers(staffUsers.map((u) => (u.id === data.id ? { ...u, ...data } : u)));
      } else {
        setStaffUsers([...staffUsers, { ...data, id: `USR${Date.now()}` }]);
      }
    }
  };

  const handleToggleMasterActive = (type, id) => {
    if (type === 'categories') setCategories(categories.map((c) => (c.id === id ? { ...c, active: !c.active } : c)));
    if (type === 'items') setItems(items.map((i) => (i.id === id ? { ...i, active: !i.active } : i)));
    if (type === 'floors') setFloors(floors.map((f) => (f.id === id ? { ...f, active: !f.active } : f)));
    if (type === 'tables') setTables(tables.map((t) => (t.id === id ? { ...t, active: !t.active } : t)));
    if (type === 'taxes') setTaxes(taxes.map((tx) => (tx.id === id ? { ...tx, active: !tx.active } : tx)));
    if (type === 'roles') setRoles(roles.map((r) => (r.id === id ? { ...r, active: !r.active } : r)));
    if (type === 'users') setStaffUsers(staffUsers.map((u) => (u.id === id ? { ...u, active: !u.active } : u)));
  };

  const handleDeleteMaster = (type, id) => {
    if (confirm('Delete this entry?')) {
      if (type === 'categories') setCategories(categories.filter((c) => c.id !== id));
      if (type === 'items') setItems(items.filter((i) => i.id !== id));
      if (type === 'floors') setFloors(floors.filter((f) => f.id !== id));
      if (type === 'tables') setTables(tables.filter((t) => t.id !== id));
      if (type === 'taxes') setTaxes(taxes.filter((tx) => tx.id !== id));
      if (type === 'roles') setRoles(roles.filter((r) => r.id !== id));
      if (type === 'users') setStaffUsers(staffUsers.filter((u) => u.id !== id));
    }
  };

  return (
    <div className="flex min-h-screen bg-[#F8FAFC] dark:bg-[#0B111A] text-gray-900 dark:text-gray-100 font-sans antialiased">
      {/* Sidebar */}
      <PosSidebar
        activeView={activeView}
        onViewChange={setActiveView}
        kotBadgeCount={activeKotsCount}
        resBadgeCount={upcomingResTodayCount}
        isDark={isDark}
        onToggleTheme={toggleTheme}
        userPermissions={userPermissions}
      />

      {/* Main Content Area */}
      <div className="flex-1 min-w-0 flex flex-col">
        <PosTopbar
          title={viewMeta[activeView]?.title}
          subtitle={viewMeta[activeView]?.subtitle}
          currentUser={currentUser}
          currentRole={currentRole}
          staffUsers={staffUsers}
          roles={roles}
          onSwitchUser={handleSwitchUser}
          stats={{
            freeTables: freeTablesCount,
            inUseTables: inUseTablesCount,
            activeKots: activeKotsCount,
            todaySales: todaySalesTotal,
          }}
        />

        <main className="flex-1 p-6 overflow-y-auto">
          {activeView === 'tables' && (
            <TablesView
              floors={floors}
              tables={tables}
              orders={orders}
              reservations={reservations}
              activeFloorId={activeFloorId}
              onSelectFloor={setActiveFloorId}
              onStartOrder={handleStartOrder}
              onOpenOrder={handleOpenOrder}
              onFreeTable={handleFreeTable}
              onMarkTableClean={handleMarkTableClean}
              onSeatReservation={handleSeatReservation}
              onCancelOrder={handleCancelOrder}
              onOpenInvoice={(orderId) => {
                const inv = Object.values(invoices).find((i) => i.orderId === orderId);
                if (inv) {
                  setSelectedInvoice(inv);
                  setInvoiceModalOpen(true);
                }
              }}
            />
          )}

          {activeView === 'orders' && (
            <OrdersView
              orders={orders}
              tables={tables}
              floors={floors}
              invoices={invoices}
              cancelledOrders={cancelledOrders}
              onEditOrder={(orderId) => {
                setCurrentOrderId(orderId);
                setActiveView('pos');
              }}
              onPayOrder={(orderId) => {
                const o = orders[orderId];
                if (o) {
                  setCurrentOrderId(orderId);
                  handleGenerateInvoice();
                }
              }}
              onCancelOrder={(orderId) => handleCancelOrder(orderId)}
              onPrintOrder={() => {
                window.print();
              }}
              onCreateNewOrder={() => {
                handleStartOrder('dine-in');
              }}
            />
          )}

          {activeView === 'pos' && (
            <OrderDeskView
              order={currentOrderId ? orders[currentOrderId] : null}
              categories={categories}
              items={items}
              tables={tables}
              onBackToFloor={() => setActiveView('tables')}
              onMoveTableClick={handleMoveTable}
              onCancelOrderClick={handleCancelOrder}
              onChangeItemQty={(item, delta, variantKey) => handleChangeItemQty(item, delta, variantKey)}
              onOpenVariantModal={(item) => {
                setSelectedVariantItem(item);
                setVariantModalOpen(true);
              }}
              onSendKot={handleSendKot}
              onSavePending={() => handleSavePending(currentOrderId)}
              onGenerateInvoice={handleGenerateInvoice}
              onClearUnsent={() => {
                if (currentOrderId && orders[currentOrderId]) {
                  const ord = { ...orders[currentOrderId] };
                  const keptItems = {};
                  Object.entries(ord.items || {}).forEach(([k, i]) => {
                    if (i.sentQty > 0) {
                      keptItems[k] = { ...i, qty: i.sentQty };
                    }
                  });
                  ord.items = keptItems;
                  setOrders({ ...orders, [currentOrderId]: ord });
                }
              }}
              onUpdateDiscount={(type, val) => {
                if (currentOrderId && orders[currentOrderId]) {
                  setOrders({
                    ...orders,
                    [currentOrderId]: { ...orders[currentOrderId], discountType: type, discountVal: val },
                  });
                }
              }}
              onUpdateTax={(taxEnabled, taxRate, serviceChargeEnabled) => {
                if (currentOrderId && orders[currentOrderId]) {
                  setOrders({
                    ...orders,
                    [currentOrderId]: {
                      ...orders[currentOrderId],
                      taxEnabled,
                      taxRate,
                      serviceChargeEnabled,
                    },
                  });
                }
              }}
              onUpdateCustomer={(name, phone) => {
                if (currentOrderId && orders[currentOrderId]) {
                  setOrders({
                    ...orders,
                    [currentOrderId]: { ...orders[currentOrderId], customerName: name, customerPhone: phone },
                  });
                }
              }}
            />
          )}

          {activeView === 'kot' && (
            <KotKanbanView kots={kots} onMoveKotStatus={handleMoveKotStatus} />
          )}

          {activeView === 'billing' && (
            <BillingView
              invoices={invoices}
              onSelectInvoice={(invId) => {
                setSelectedInvoice(invoices[invId]);
                setInvoiceModalOpen(true);
              }}
            />
          )}

          {activeView === 'reservations' && (
            <ReservationsView
              reservations={reservations}
              tables={tables}
              floors={floors}
              filterDate={resFilterDate}
              filterStatus={resFilterStatus}
              onDateChange={setResFilterDate}
              onStatusChange={setResFilterStatus}
              onAddReservationClick={() => {
                setEditingReservation(null);
                setReservationModalOpen(true);
              }}
              onEditReservationClick={(resId) => {
                setEditingReservation(reservations[resId]);
                setReservationModalOpen(true);
              }}
              onSeatReservationClick={(resId) => handleSeatReservation(resId)}
              onStatusUpdate={(resId, status) => {
                setReservations({
                  ...reservations,
                  [resId]: { ...reservations[resId], status },
                });
              }}
              onViewOrder={(orderId) => {
                setCurrentOrderId(orderId);
                setActiveView('pos');
              }}
            />
          )}

          {activeView === 'masters' && (
            <MastersView
              categories={categories}
              items={items}
              floors={floors}
              tables={tables}
              taxes={taxes}
              roles={roles}
              staffUsers={staffUsers}
              currentUserId={currentUserId}
              onSwitchUser={handleSwitchUser}
              onOpenAddModal={(type) => {
                setActiveMasterType(type);
                setEditingMasterItem(null);
                setMasterModalOpen(true);
              }}
              onOpenEditModal={(type, item) => {
                setActiveMasterType(type);
                setEditingMasterItem(item);
                setMasterModalOpen(true);
              }}
              onToggleActive={handleToggleMasterActive}
              onDeleteMaster={handleDeleteMaster}
            />
          )}
        </main>
      </div>

      {/* Modals */}
      <VariantModal
        isOpen={variantModalOpen}
        onClose={() => setVariantModalOpen(false)}
        item={selectedVariantItem}
        onConfirm={(item, variantName, variantPrice) => {
          handleChangeItemQty(
            { id: item.id, name: `${item.name} (${variantName})`, price: variantPrice },
            1,
            `${item.id}::${variantName}`
          );
        }}
      />

      <PickerModal
        isOpen={pickerModalOpen}
        onClose={() => setPickerModalOpen(false)}
        title={pickerConfig.title}
        tables={tables}
        floors={floors}
        filterFn={pickerConfig.filterFn}
        onSelectTable={pickerConfig.onSelect}
      />

      <InvoiceModal
        isOpen={invoiceModalOpen}
        onClose={() => setInvoiceModalOpen(false)}
        invoice={selectedInvoice}
        onSettlePayment={handleSettlePayment}
      />

      <MasterModal
        isOpen={masterModalOpen}
        onClose={() => setMasterModalOpen(false)}
        masterType={activeMasterType}
        itemData={editingMasterItem}
        categories={categories}
        floors={floors}
        tables={tables}
        roles={roles}
        onSave={handleSaveMaster}
      />

      <ReservationModal
        isOpen={reservationModalOpen}
        onClose={() => setReservationModalOpen(false)}
        reservation={editingReservation}
        floors={floors}
        tables={tables}
        onSave={(data) => {
          if (data.id) {
            setReservations({ ...reservations, [data.id]: data });
          } else {
            const nextResNum = seq.res + 1;
            setSeq({ ...seq, res: nextResNum });
            const id = `RES${String(nextResNum).padStart(3, '0')}`;
            setReservations({ ...reservations, [id]: { ...data, id } });
          }
        }}
      />
    </div>
  );
}
