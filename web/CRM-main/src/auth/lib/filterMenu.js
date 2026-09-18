export function filterMenuByPermission(menu = [], rights = {}, isSuperUser = false) {
  return menu.reduce((acc, item) => {
    if (item.heading || item.separator) {
      acc.push(item);
      return acc;
    }

    if (isSuperUser) {
      acc.push(item);
      return acc;
    }

    if (item.children) {
      // For parent items, recurse children first
      // Parent's own pageName is a grouping label (e.g. 'CrmLeads') — not in API rights
      // So visibility is determined purely by whether any children are visible
      const visibleChildren = filterMenuByPermission(item.children, rights, isSuperUser);
      if (visibleChildren.length > 0) {
        acc.push({ ...item, children: visibleChildren });
      }
      return acc;
    }

    // Leaf item: if no pageName defined, show by default; else check view right
    const canView = item.pageName ? !!rights[item.pageName]?.view : true;
    if (canView) acc.push(item);
    return acc;
  }, []);
}