import { create } from "zustand";
import { persist } from "zustand/middleware";

export const useAuthStore = create(
  persist(
    (set) => ({
      user: null,
      token: null,
      rights: {},
      normalizedRights: {},
      upgradedModules: [],
      roleReportRights: null,
      banquetRights: [],

      setAuth: (user, token, rights, upgradedModules = [], roleReportRights = null) =>
        set({
          user,
          token,
          rights,
          normalizedRights: rights,
          upgradedModules,
          roleReportRights,
          banquetRights: user?.banquetRights || [],
        }),

      clearAuth: () =>
        set({
          user: null,
          token: null,
          rights: {},
          normalizedRights: {},
          upgradedModules: [],
          roleReportRights: null,
          banquetRights: [],
        }),
    }),
    { name: "auth-storage" }
  )
);