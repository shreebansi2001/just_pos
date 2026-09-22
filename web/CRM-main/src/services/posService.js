import axiosInstance, { GET, POST, PUT, DELETE } from './axiosInstance';

// Initial Seed Data mirroring the reference demo for instant offline and online operability
export const initialPosSeed = {
  categories: [
    { id: 'CAT01', name: 'Starters', sortOrder: 1, active: true },
    { id: 'CAT02', name: 'Main Course', sortOrder: 2, active: true },
    { id: 'CAT03', name: 'Rice & Biryani', sortOrder: 3, active: true },
    { id: 'CAT04', name: 'Breads', sortOrder: 4, active: true },
    { id: 'CAT05', name: 'Live Counter', sortOrder: 5, active: true },
    { id: 'CAT06', name: 'Desserts', sortOrder: 6, active: true },
    { id: 'CAT07', name: 'Beverages', sortOrder: 7, active: true },
  ],
  items: [
    { id: 'ITM01', name: 'Paneer Tikka', categoryId: 'CAT01', veg: true, price: 220, tag: 'Smoky, char-grilled', station: 'Kitchen', active: true },
    { id: 'ITM02', name: 'Hara Bhara Kebab', categoryId: 'CAT01', veg: true, price: 190, tag: 'Spinach & peas', station: 'Kitchen', active: true },
    { id: 'ITM03', name: 'Chicken Seekh Kebab', categoryId: 'CAT01', veg: false, price: 260, tag: 'Minced, skewered', station: 'Kitchen', active: true },
    { id: 'ITM04', name: 'Fish Amritsari', categoryId: 'CAT01', veg: false, price: 280, tag: 'Crisp fried', station: 'Kitchen', active: true },
    { id: 'ITM05', name: 'Corn & Cheese Balls', categoryId: 'CAT01', veg: true, price: 170, tag: 'Deep fried', station: 'Kitchen', active: true },
    { id: 'ITM06', name: 'Chilli Mushroom', categoryId: 'CAT01', veg: true, price: 200, tag: 'Indo-Chinese', station: 'Kitchen', active: true },
    { id: 'ITM07', name: 'Paneer Butter Masala', categoryId: 'CAT02', veg: true, tag: 'Rich tomato gravy', station: 'Kitchen', active: true, variants: [['Half', 180], ['Full', 260]] },
    { id: 'ITM08', name: 'Dal Makhani', categoryId: 'CAT02', veg: true, price: 210, tag: 'Slow-cooked, buttery', station: 'Kitchen', active: true },
    { id: 'ITM09', name: 'Butter Chicken', categoryId: 'CAT02', veg: false, tag: 'House special', station: 'Kitchen', active: true, variants: [['Half', 210], ['Full', 300]] },
    { id: 'ITM10', name: 'Mutton Rogan Josh', categoryId: 'CAT02', veg: false, price: 360, tag: 'Kashmiri style', station: 'Kitchen', active: true },
    { id: 'ITM11', name: 'Veg Kolhapuri', categoryId: 'CAT02', veg: true, price: 230, tag: 'Spicy mixed veg', station: 'Kitchen', active: true },
    { id: 'ITM12', name: 'Malai Kofta', categoryId: 'CAT02', veg: true, price: 240, tag: 'Creamy, mild', station: 'Kitchen', active: true },
    { id: 'ITM13', name: 'Hyderabadi Chicken Biryani', categoryId: 'CAT03', veg: false, tag: 'Dum-cooked', station: 'Kitchen', active: true, variants: [['Half', 180], ['Full', 280]] },
    { id: 'ITM14', name: 'Veg Dum Biryani', categoryId: 'CAT03', veg: true, price: 220, tag: 'Fragrant basmati', station: 'Kitchen', active: true },
    { id: 'ITM15', name: 'Jeera Rice', categoryId: 'CAT03', veg: true, price: 150, tag: 'Cumin tempered', station: 'Kitchen', active: true },
    { id: 'ITM16', name: 'Curd Rice', categoryId: 'CAT03', veg: true, price: 140, tag: 'South Indian comfort', station: 'Kitchen', active: true },
    { id: 'ITM17', name: 'Butter Naan', categoryId: 'CAT04', veg: true, price: 55, tag: 'Tandoor fresh', station: 'Kitchen', active: true },
    { id: 'ITM18', name: 'Garlic Naan', categoryId: 'CAT04', veg: true, price: 65, tag: 'Tandoor fresh', station: 'Kitchen', active: true },
    { id: 'ITM19', name: 'Tandoori Roti', categoryId: 'CAT04', veg: true, price: 35, tag: 'Whole wheat', station: 'Kitchen', active: true },
    { id: 'ITM20', name: 'Laccha Paratha', categoryId: 'CAT04', veg: true, price: 60, tag: 'Layered, flaky', station: 'Kitchen', active: true },
    { id: 'ITM21', name: 'Live Chaat Counter', categoryId: 'CAT05', veg: true, price: 180, tag: 'Per plate, made fresh', station: 'Live Counter', active: true },
    { id: 'ITM22', name: 'Live Dosa Counter', categoryId: 'CAT05', veg: true, price: 150, tag: 'Per plate, made fresh', station: 'Live Counter', active: true },
    { id: 'ITM23', name: 'Live Pasta Counter', categoryId: 'CAT05', veg: true, price: 200, tag: 'Per plate, made fresh', station: 'Live Counter', active: true },
    { id: 'ITM24', name: 'Gulab Jamun (2 pc)', categoryId: 'CAT06', veg: true, price: 90, tag: 'Warm, syrup soaked', station: 'Dessert Counter', active: true },
    { id: 'ITM25', name: 'Rasmalai (2 pc)', categoryId: 'CAT06', veg: true, price: 110, tag: 'Chilled, saffron', station: 'Dessert Counter', active: true },
    { id: 'ITM26', name: 'Chocolate Brownie', categoryId: 'CAT06', veg: true, price: 140, tag: 'With vanilla scoop', station: 'Dessert Counter', active: true },
    { id: 'ITM27', name: 'Masala Chaas', categoryId: 'CAT07', veg: true, price: 60, tag: 'Spiced buttermilk', station: 'Bar', active: true },
    { id: 'ITM28', name: 'Virgin Mojito', categoryId: 'CAT07', veg: true, price: 130, tag: 'Mint, lime, soda', station: 'Bar', active: true },
    { id: 'ITM29', name: 'Fresh Lime Soda', categoryId: 'CAT07', veg: true, price: 90, tag: 'Sweet / salted', station: 'Bar', active: true },
    { id: 'ITM30', name: 'Filter Coffee', categoryId: 'CAT07', veg: true, price: 70, tag: 'South Indian brew', station: 'Bar', active: false },
  ],
  floors: [
    { id: 'FLR01', name: 'Ground Floor', shortcode: 'GF', sortOrder: 1, active: true },
    { id: 'FLR02', name: 'Rooftop', shortcode: 'RT', sortOrder: 2, active: true },
    { id: 'FLR03', name: 'Private Hall', shortcode: 'PH', sortOrder: 3, active: true },
  ],
  tables: [
    { id: 'TBL01', name: 'Table 1', shortcode: 'GF-1', capacity: 2, floorId: 'FLR01', active: true, status: 'available', orderId: null },
    { id: 'TBL02', name: 'Table 2', shortcode: 'GF-2', capacity: 2, floorId: 'FLR01', active: true, status: 'available', orderId: null },
    { id: 'TBL03', name: 'Table 3', shortcode: 'GF-3', capacity: 4, floorId: 'FLR01', active: true, status: 'available', orderId: null },
    { id: 'TBL04', name: 'Table 4', shortcode: 'GF-4', capacity: 4, floorId: 'FLR01', active: true, status: 'available', orderId: null },
    { id: 'TBL05', name: 'Table 5', shortcode: 'GF-5', capacity: 4, floorId: 'FLR01', active: true, status: 'available', orderId: null },
    { id: 'TBL06', name: 'Table 6', shortcode: 'GF-6', capacity: 6, floorId: 'FLR01', active: true, status: 'available', orderId: null },
    { id: 'TBL07', name: 'Table 7', shortcode: 'RT-1', capacity: 6, floorId: 'FLR02', active: true, status: 'available', orderId: null },
    { id: 'TBL08', name: 'Table 8', shortcode: 'RT-2', capacity: 4, floorId: 'FLR02', active: true, status: 'available', orderId: null },
    { id: 'TBL09', name: 'Table 9', shortcode: 'RT-3', capacity: 2, floorId: 'FLR02', active: true, status: 'available', orderId: null },
    { id: 'TBL10', name: 'Table 10', shortcode: 'RT-4', capacity: 8, floorId: 'FLR02', active: true, status: 'available', orderId: null },
    { id: 'TBL11', name: 'Table 11', shortcode: 'PH-1', capacity: 4, floorId: 'FLR03', active: true, status: 'available', orderId: null },
    { id: 'TBL12', name: 'Table 12', shortcode: 'PH-2', capacity: 6, floorId: 'FLR03', active: true, status: 'available', orderId: null },
  ],
  taxes: [
    { id: 'TAX01', taxName: 'CGST', percentage: 2.5, active: true },
    { id: 'TAX02', taxName: 'SGST', percentage: 2.5, active: true },
    { id: 'TAX03', taxName: 'IGST', percentage: 5.0, active: true },
    { id: 'TAX04', taxName: 'Service Tax', percentage: 5.0, active: false },
  ],
  roles: [
    {
      id: 'ROL01',
      roleName: 'Admin / Owner',
      roleCode: 'ADMIN',
      description: 'Full system access to all POS modules, financial reports, and masters configuration.',
      permissions: [
        'view_tables', 'view_orders', 'view_pos', 'view_kot', 'view_reservations', 'view_billing', 'view_masters',
        'can_apply_discount', 'can_exempt_tax', 'can_cancel_order', 'can_modify_masters'
      ],
      active: true,
      isSystem: true
    },
    {
      id: 'ROL02',
      roleName: 'Kitchen Manager / Chef',
      roleCode: 'KITCHEN',
      description: 'Kitchen display screen only (KOT Kanban). Restricted from viewing sales, billing, tables, and masters.',
      permissions: ['view_kot'],
      active: true,
      isSystem: true
    },
    {
      id: 'ROL03',
      roleName: 'Cashier / Billing Staff',
      roleCode: 'CASHIER',
      description: 'Billing settlements, POS order taking, invoices, and table views. No access to masters or admin settings.',
      permissions: ['view_tables', 'view_orders', 'view_pos', 'view_billing', 'can_apply_discount', 'can_exempt_tax'],
      active: true,
      isSystem: true
    },
    {
      id: 'ROL04',
      roleName: 'Waiter / Captain',
      roleCode: 'CAPTAIN',
      description: 'Table floor layout, order entry, and punching KOTs. Cannot settle bills or change master data.',
      permissions: ['view_tables', 'view_orders', 'view_pos'],
      active: true,
      isSystem: true
    },
    {
      id: 'ROL05',
      roleName: 'Store Manager',
      roleCode: 'MANAGER',
      description: 'Operations oversight: tables, orders, KOTs, billing, reservations, and inventory masters.',
      permissions: [
        'view_tables', 'view_orders', 'view_pos', 'view_kot', 'view_reservations', 'view_billing', 'view_masters',
        'can_apply_discount', 'can_exempt_tax', 'can_cancel_order'
      ],
      active: true,
      isSystem: true
    }
  ],
  staffUsers: [
    {
      id: 'USR01',
      name: 'Saffron Admin',
      userCode: 'JC-ADM-01',
      email: 'admin@justcatering.in',
      phone: '+91 98765 43210',
      roleId: 'ROL01',
      pin: '9999',
      active: true
    },
    {
      id: 'USR02',
      name: 'Ramesh Sharma (Head Chef)',
      userCode: 'JC-KTC-01',
      email: 'chef.ramesh@justcatering.in',
      phone: '+91 98765 11111',
      roleId: 'ROL02',
      pin: '1234',
      active: true
    },
    {
      id: 'USR03',
      name: 'Pooja Patel (Cashier)',
      userCode: 'JC-POS-01',
      email: 'cashier@justcatering.in',
      phone: '+91 98765 22222',
      roleId: 'ROL03',
      pin: '0000',
      active: true
    },
    {
      id: 'USR04',
      name: 'Vikram Singh (Captain)',
      userCode: 'JC-SRV-01',
      email: 'captain@justcatering.in',
      phone: '+91 98765 33333',
      roleId: 'ROL04',
      pin: '5555',
      active: true
    },
    {
      id: 'USR05',
      name: 'Sunil Verma (Store Mgr)',
      userCode: 'JC-MGR-01',
      email: 'manager@justcatering.in',
      phone: '+91 98765 44444',
      roleId: 'ROL05',
      pin: '8888',
      active: true
    }
  ]
};

export const ALL_POS_PERMISSIONS = [
  { id: 'view_tables', label: 'Tables Floor View', category: 'Navigation / Screen Rights', desc: 'View table map, occupancy, and seated guests' },
  { id: 'view_orders', label: 'All Orders List', category: 'Navigation / Screen Rights', desc: 'View active, completed, and takeaway orders list' },
  { id: 'view_pos', label: 'New Order / POS Desk', category: 'Navigation / Screen Rights', desc: 'Punch new orders, customize dishes, send KOT' },
  { id: 'view_kot', label: 'KOT Live Kitchen Board', category: 'Navigation / Screen Rights', desc: 'View kitchen tickets, mark items cooking/ready' },
  { id: 'view_reservations', label: 'Table Reservations', category: 'Navigation / Screen Rights', desc: 'Book, hold, and manage upcoming dining reservations' },
  { id: 'view_billing', label: 'Billing & Settlements', category: 'Navigation / Screen Rights', desc: 'Settle orders, collect payment, generate tax invoices' },
  { id: 'view_masters', label: 'System Masters', category: 'Navigation / Screen Rights', desc: 'Manage Categories, Items, Floors, Tables, Taxes, Roles & Staff' },
  { id: 'can_apply_discount', label: 'Apply Cart Discounts', category: 'Operational Rights', desc: 'Permit cashier/waiter to give % or ₹ discounts' },
  { id: 'can_exempt_tax', label: 'Tax Exemption', category: 'Operational Rights', desc: 'Permit 1-click tax exemption for duty-free/special orders' },
  { id: 'can_cancel_order', label: 'Cancel / Void Order', category: 'Operational Rights', desc: 'Permit voiding active orders or deleting billed items' },
  { id: 'can_modify_masters', label: 'Modify Master Rules', category: 'Operational Rights', desc: 'Add/edit items, tax slabs, roles, and staff records' }
];

export const posApi = {
  getStats: () => GET('/pos/stats').catch(() => null),
  getCategories: () => GET('/pos/categories').catch(() => null),
  saveCategory: (data) => POST('/pos/categories', data).catch(() => null),
  deleteCategory: (id) => DELETE(`/pos/categories/${id}`).catch(() => null),

  getFloors: () => GET('/pos/floors').catch(() => null),
  saveFloor: (data) => POST('/pos/floors', data).catch(() => null),
  deleteFloor: (id) => DELETE(`/pos/floors/${id}`).catch(() => null),

  getTables: () => GET('/pos/tables').catch(() => null),
  saveTable: (data) => POST('/pos/tables', data).catch(() => null),
  deleteTable: (id) => DELETE(`/pos/tables/${id}`).catch(() => null),
  updateTableStatus: (id, status) => PUT(`/pos/tables/${id}/status?status=${status}`).catch(() => null),

  getItems: () => GET('/pos/items').catch(() => null),
  saveItem: (data) => POST('/pos/items', data).catch(() => null),
  deleteItem: (id) => DELETE(`/pos/items/${id}`).catch(() => null),

  getTaxes: () => GET('/pos/taxes').catch(() => null),
  saveTax: (data) => POST('/pos/taxes', data).catch(() => null),
  deleteTax: (id) => DELETE(`/pos/taxes/${id}`).catch(() => null),

  createOrder: (data) => POST('/pos/orders', data).catch(() => null),
  getActiveOrders: () => GET('/pos/orders').catch(() => null),
  updateOrderItems: (id, items) => PUT(`/pos/orders/${id}/items`, items).catch(() => null),
  updateOrderDiscount: (id, discount) => PUT(`/pos/orders/${id}/discount`, discount).catch(() => null),
  moveTable: (id, newTableId) => POST(`/pos/orders/${id}/move`, { newTableId }).catch(() => null),
  cancelOrder: (id) => POST(`/pos/orders/${id}/cancel`).catch(() => null),

  sendKot: (id, items) => POST(`/pos/orders/${id}/kot`, items).catch(() => null),
  getKots: () => GET('/pos/kots').catch(() => null),
  updateKotStatus: (id, status) => PUT(`/pos/kots/${id}/status`, { status }).catch(() => null),

  generateInvoice: (id) => POST(`/pos/orders/${id}/invoice`).catch(() => null),
  payInvoice: (id, paymentMode) => POST(`/pos/invoices/${id}/pay`, { paymentMode }).catch(() => null),
  getInvoices: () => GET('/pos/invoices').catch(() => null),

  getReservations: (date, status) => GET(`/pos/reservations?date=${date || ''}&status=${status || ''}`).catch(() => null),
  saveReservation: (data) => POST('/pos/reservations', data).catch(() => null),
  seatReservation: (id, tableId) => POST(`/pos/reservations/${id}/seat?tableId=${tableId}`).catch(() => null),
};
