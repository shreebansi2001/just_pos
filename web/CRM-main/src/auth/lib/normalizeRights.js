export function normalizeRights(modules = []) {
  const flat = {};
  modules.forEach((mod) => {
    (mod.userRights || []).forEach((page) => {
      flat[page.pageName] = {
        view: !!page.view,
        add: !!page.add,
        edit: !!page.edit,
        delete: !!page.delete,
      };
    });
  });
  return flat;
}