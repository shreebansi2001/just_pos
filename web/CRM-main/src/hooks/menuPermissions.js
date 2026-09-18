import { useAuthStore } from '@/store/useAuthStore';

export const useFilteredMenu = (menuItems) => {
  const rights = useAuthStore((state) => state.rights);
  const user = useAuthStore((state) => state.user);

  const roleId = Number(user?.roleId || 0);
  const isSuperUser = roleId === 1 || roleId === 2;

  const canView = (pageName) => {
    if (!pageName) return true; 
    if (isSuperUser) return true;
    return rights?.[pageName]?.view === true;
  };

  const filterItems = (items) =>
    items
      .map((item) => {
        if (item.children) {
          const filteredChildren = filterItems(item.children);

          if (filteredChildren.length === 0 && !item.path) return null;
          return { ...item, children: filteredChildren };
        }
        return canView(item.pageName) ? item : null;
      })
      .filter(Boolean);

  return filterItems(menuItems);
};