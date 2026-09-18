import { createContext, useContext } from 'react';

export const AuthContext = createContext({
  loading: false,
  setLoading: () => {},
  auth: undefined,
  saveAuth: () => {},
  user: undefined,
  setUser: () => {},
  login: async (uniqueCode, email, password) => {},
  logout: async () => {},
  verify: async () => {},
  getUser: async () => null,
  updateProfile: async () => {},
  isAdmin: false,
  register: async () => {},
  requestPasswordReset: async () => {},
  resetPassword: async () => {},
  resendVerificationEmail: async () => {},
});

export function useAuth() {
  return useContext(AuthContext);
}