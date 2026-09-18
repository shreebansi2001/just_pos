// Static floor-plan layout: which tables exist, how they're grouped, and how
// many seats each has. This never changes at runtime — live status/cart data
// lives in tableStore.js instead. Swap this for an API call if your floor
// plan is configurable per branch.

export const SECTIONS = [];

/*
export const SECTIONS = [
  {
    id: "main-dining",
    title: "Main Dining (A/C)",
    tables: [
      { id: "T1", seats: 4 },
      { id: "T2", seats: 4 },
      { id: "T3", seats: 2 },
      { id: "T4", seats: 4 },
      { id: "T5", seats: 6 },
      { id: "T6", seats: 6 },
      { id: "T7", seats: 4 },
      { id: "T8", seats: 4 },
      { id: "T9", seats: 4 },
      { id: "T10", seats: 4 },
      { id: "T11", seats: 4 },
    ],
  },
  {
    id: "terrace",
    title: "Terrace (Non A/C)",
    tables: [
      { id: "T12", seats: 4 },
      { id: "T13", seats: 4 },
      { id: "T14", seats: 4 },
      { id: "T15", seats: 4 },
    ],
  },
  {
    id: "lounge-bar",
    title: "The Lounge & Bar",
    tables: [
      { id: "B1", seats: 1, stool: true },
      { id: "B2", seats: 1, stool: true },
      { id: "B3", seats: 1, stool: true },
      { id: "B4", seats: 1, stool: true },
    ],
  },
];
*/