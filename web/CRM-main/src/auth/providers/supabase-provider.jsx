import { useEffect, useState } from 'react';
import { AuthContext } from '@/auth/context/auth-context';
import * as authHelper from '@/auth/lib/helpers';
import { LoginUser, getUserById, LoginOutUser , RefreshToken  } from '@/services/apiServices';
import { getSoftType } from '@/config/getSoftType';
import { normalizeRights } from '../lib/normalizeRights';
import { useAuthStore } from '@/store/useAuthStore';

export function AuthProvider({ children }) {
  const [loading, setLoading] = useState(true);
  const [auth, setAuth] = useState(authHelper.getAuth());
  const [currentUser, setCurrentUser] = useState();
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    setIsAdmin(currentUser?.clientId === 1 || currentUser?.roleId === 1);
  }, [currentUser]);

  const saveAuth = (auth) => {
    setAuth(auth);
    if (auth) authHelper.setAuth(auth);
    else authHelper.removeAuth();
  };

  const verify = async () => {
    const token = localStorage.getItem('userToken');
    const userId = localStorage.getItem('mainId');

   if (!token) {
  saveAuth(undefined);
  setCurrentUser(undefined);
  setLoading(false);
  return;
}

if (!userId) {
  setLoading(false);
  return;
}

    try {
      const response = await getUserById(userId);
      if (response?.data?.success) {
        const userData = response.data.data['User Details'][0];
        
        setCurrentUser(transformUser(userData));

        const normalized = normalizeRights(userData.userRights || []);
        useAuthStore.getState().setAuth(
          transformUser(userData),
          userData.token,
          normalized,
          userData.userUpgradedModule,
          userData.roleReportRights
        );
      } else {
        logout();
      }
    } catch {
      logout();
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    verify();
  }, []);
const loginWithToken = async () => {
   try {
    console.log('TOKEN BEING SENT:', localStorage.getItem('userToken'));   // ← add this line
const response = await RefreshToken();

console.log("Refresh API Response:", response.data);

const userData = response.data;

console.log("Token from API:", userData.token);

localStorage.setItem("userToken", userData.token);

console.log(
  "Stored Token:",
  localStorage.getItem("userToken")
);     
const hasCRM = (userData.userUpgradedModule || []).some(
  
  (module) => module.moduleName === "CRM" && module.isActive
);
console.log("userUpgradedModule:", userData.userUpgradedModule);
if (!hasCRM) {
  throw new Error(
    "You are not authorized to access the CRM module. Please contact the administrator."
  );
}
    const authData = {
      access_token: userData.token,
      userId: userData.id,
    };

    saveAuth(authData);
    localStorage.setItem('userToken', userData.token); 
    localStorage.setItem('lang', 'en');

    const finalUserId =
      userData.clientId === 0 || userData.clientId === -1
        ? userData.id
        : userData.clientId;
    localStorage.setItem('userId', finalUserId.toString());
    localStorage.setItem('mainId', userData.id);

    const user = transformUser(userData);
    setCurrentUser(user);

    const normalized = normalizeRights(userData.userRights || []);
    useAuthStore.getState().setAuth(
      user,
      userData.token,
      normalized,
      userData.userUpgradedModule,
      userData.roleReportRights
    );

    return { ...authData, userDetails: userData };
  } catch (error) {
    localStorage.removeItem('userToken');
    saveAuth(undefined);
    setCurrentUser(undefined);
    throw new Error(
      error.response?.data?.msg || error.message || 'Auto-login failed.'
    );
  }
};
  const transformUser = (userData) => ({
    id: userData.id,
    email: userData.email || '',
    username: userData.username || '',
    first_name: userData.firstName || '',
    last_name: userData.lastName || '',
    fullname: userData.fullName || `${userData.firstName || ''} ${userData.lastName || ''}`.trim(),
    phone: userData.mobileNo || '',
    pic: userData.profileImage || '',
    language: localStorage.getItem('lang') || 'en',
    is_admin: userData.clientId === 1,
    clientId: userData.clientId,
    roleId: userData.userBasicDetails?.role?.id,
    userPlan: userData.userPlan,
    plan: userData.plan,
    isApprove: userData.isApprove,
    userDetails: userData,
  });

  const login = async (uniqueCode, email, password) => {
    try {
      const response = await LoginUser({
        uniqueCode,
        email,
        password,
        otp: '',
        softType: getSoftType(),
      });

      if (!response.data.success) {
        throw new Error(response.data.msg || 'Login failed. Please try again.');
      }

      const userData = response.data.data['User Details'][0];
const hasCRM = (userData.userUpgradedModule || []).some(
  (module) =>
    module.upgradeModuleId === 4 &&
    module.isActive &&
    module.isPayDone
);

if (!hasCRM) {
  throw new Error(
    "You are not authorized to access the CRM module."
  );
}
      const authData = {
        access_token: userData.token,
        userId: userData.id,
      };

      saveAuth(authData);
      localStorage.setItem('userToken', userData.token);
      localStorage.setItem('lang', 'en');

      const finalUserId =
        userData.clientId === 0 || userData.clientId === -1
          ? userData.id
          : userData.clientId;
      localStorage.setItem('userId', finalUserId.toString());
      localStorage.setItem('mainId', userData.id);

      const user = transformUser(userData);
      setCurrentUser(user);

      // normalize and store permission rights
      const normalized = normalizeRights(userData.userRights || []);
      useAuthStore.getState().setAuth(
        user,
        userData.token,
        normalized,
        userData.userUpgradedModule,
        userData.roleReportRights
      );

      await LoginOutUser(userData.email, 'login').catch(() => {});
      return { ...authData, userDetails: userData };
    } catch (error) {
      saveAuth(undefined);
      setCurrentUser(undefined);
      throw new Error(
        error.response?.data?.msg || error.message || 'Login failed. Please try again.'
      );
    }
  };

  const logout = async () => {
    const email = currentUser?.email;
    if (email) {
      await LoginOutUser(email, 'logout').catch(() => {});
    }

    localStorage.removeItem('userToken');
    localStorage.removeItem('userId');
    localStorage.removeItem('mainId');
    localStorage.removeItem('lang');
    saveAuth(undefined);
    setCurrentUser(undefined);
    useAuthStore.getState().clearAuth();
  };

  const getUser = async () => {
    const userId = localStorage.getItem('mainId');
    if (!userId) return null;
    const response = await getUserById(userId);
    if (!response?.data?.success) return null;
    return transformUser(response.data.data['User Details'][0]);
  };

  return (
    <AuthContext.Provider
      value={{
        loading,
        setLoading,
        auth,
        saveAuth,
        user: currentUser,
        setUser: setCurrentUser,
        login,
        loginWithToken,  
        logout,
        verify,
        getUser,
        isAdmin,
        register: async () => { throw new Error('Not implemented'); },
        requestPasswordReset: async () => { throw new Error('Not implemented'); },
        resetPassword: async () => { throw new Error('Not implemented'); },
        resendVerificationEmail: async () => { throw new Error('Not implemented'); },
        updateProfile: async () => { throw new Error('Not implemented'); },
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}