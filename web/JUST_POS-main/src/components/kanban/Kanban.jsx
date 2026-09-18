import { useState } from "react";

// Default neutral scale — override via the `gray` prop to match a
// different page's palette without touching this file.
const DEFAULT_GRAY = {
  100: "#F9F9F9",
  200: "#F1F1F4",
  300: "#DBDFE9",
  400: "#C4CADA",
  500: "#99A1B7",
  600: "#78829D",
  700: "#4B5675",
  800: "#252F4A",
  900: "#071437",
};

/**
 * Generic drag-and-drop Kanban board.
 *
 * columns: [{ key, label, note?, accent, accentText }]
 *   - accent/accentText style the column's top border and count pill.
 * items: array of records, each identified by getId() and staged by getStatus().
 * renderCard(item, { onDragStart }): return the JSX for one card. Spread
 *   onDragStart onto the card's root element to make it draggable.
 * onStatusChange(id, newStatus): called on drop.
 */
export function KanbanBoard({
  columns,
  items,
  getStatus = (item) => item.status,
  getId = (item) => item.id,
  onStatusChange,
  renderCard,
  emptyLabel = "Nothing here",
  gray = DEFAULT_GRAY,
  columnsClassName = "grid-cols-1 sm:grid-cols-2 lg:grid-cols-4",
}) {
  const [dragOverCol, setDragOverCol] = useState(null);

  const handleDragStart = (e, id) => {
    e.dataTransfer.setData("text/plain", String(id));
  };

  const handleDrop = (e, colKey) => {
    e.preventDefault();
    const id = e.dataTransfer.getData("text/plain");
    if (id) onStatusChange?.(id, colKey);
    setDragOverCol(null);
  };

  return (
    <div className={`grid ${columnsClassName} gap-5`}>
      {columns.map((col) => {
        const colItems = items.filter((item) => getStatus(item) === col.key);
        return (
          <div
            key={col.key}
            onDragOver={(e) => {
              e.preventDefault();
              setDragOverCol(col.key);
            }}
            onDragLeave={() => setDragOverCol(null)}
            onDrop={(e) => handleDrop(e, col.key)}
            className="rounded-xl flex flex-col transition-colors"
            style={{
              backgroundColor: dragOverCol === col.key ? "#FFFFFF" : gray[100],
              border: `1px solid ${gray[200]}`,
              borderTop: `3px solid ${col.accent}`,
            }}
          >
            <div className="px-3 pt-3 pb-2">
              <div className="flex items-center justify-between">
                <h2 className="text-sm font-semibold" style={{ color: gray[800] }}>
                  {col.label}
                </h2>
                <span
                  className="text-xs font-medium min-w-[20px] text-center px-1.5 py-0.5 rounded"
                  style={{ backgroundColor: col.accent, color: col.accentText }}
                >
                  {colItems.length}
                </span>
              </div>
              {col.note && (
                <p className="text-xs mt-0.5" style={{ color: gray[500] }}>
                  {col.note}
                </p>
              )}
            </div>

            <div className="px-3 pb-3 space-y-2 flex-1 min-h-[140px]">
              {colItems.map((item) => (
                <div key={getId(item)}>
                  {renderCard(item, { onDragStart: (e) => handleDragStart(e, getId(item)) })}
                </div>
              ))}
              {colItems.length === 0 && (
                <div className="rounded-lg py-6 text-center" style={{ border: `1px dashed ${gray[300]}` }}>
                  <p className="text-xs" style={{ color: gray[400] }}>
                    {emptyLabel}
                  </p>
                </div>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
}

/**
 * Inline count-per-column strip. Reuses the same `columns`/`items` shape as
 * KanbanBoard so a page can drive both from one source of truth. Give a
 * column a `statColor` if you want its number to read differently than its
 * badge accent (e.g. a light badge fill but a darker readable number).
 */
export function KanbanStats({
  columns,
  items,
  getStatus = (item) => item.status,
  gray = DEFAULT_GRAY,
}) {
  const counts = columns.reduce((acc, col) => {
    acc[col.key] = items.filter((item) => getStatus(item) === col.key).length;
    return acc;
  }, {});

  return (
    <div className="flex items-center gap-6 flex-wrap">
      {columns.map((col, i) => (
        <div key={col.key} className="flex items-center gap-6">
          <div className="flex items-baseline gap-1.5">
            <span className="text-xl font-semibold tabular-nums" style={{ color: col.statColor || col.accent }}>
              {counts[col.key]}
            </span>
            <span className="text-xs" style={{ color: gray[500] }}>
              {col.label.toLowerCase()}
            </span>
          </div>
          {i < columns.length - 1 && <span className="w-px h-4" style={{ backgroundColor: gray[300] }} />}
        </div>
      ))}
    </div>
  );
}