import { useAuthStore } from '@/store/useAuthStore';

export const usePermission = (pageName) => {
  const rights = useAuthStore((state) => state.rights);
  const user = useAuthStore((state) => state.user);

  const roleId = Number(user?.roleId || 0);

  const isSuperUser = roleId === 1 || roleId === 2;

  if (isSuperUser) {
    return { view: true, add: true, edit: true, delete: true };
  }

  return rights?.[pageName] || { view: false, add: false, edit: false, delete: false };
};