import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/auth/context/auth-context';

export function SsoHandler() {
  const navigate = useNavigate();
  const { loginWithToken } = useAuth();

useEffect(() => {
  const params = new URLSearchParams(window.location.search);
  const incomingToken = params.get("t");

  console.log("Incoming Token:", incomingToken);

  if (!incomingToken) {
    navigate("/auth/signin", { replace: true });
    return;
  }

  localStorage.setItem("userToken", incomingToken);

  loginWithToken()
    .then((res) => {
      console.log("SSO SUCCESS", res);

      const roleId = Number(res.userDetails?.userBasicDetails?.role?.id);

      if (roleId === 1 || res.userDetails?.clientId === 1) {
        navigate("/super-dashboard", { replace: true });
      } else {
        navigate("/", { replace: true });
      }
    })
    .catch((err) => {
      console.log("SSO FAILED", err);
      navigate("/auth/signin", { replace: true });
    });
}, []);

  return (
  <div className="flex h-screen items-center justify-center">
    <div className="text-center">
      <div className="animate-spin h-10 w-10 rounded-full border-4 border-blue-500 border-t-transparent mx-auto"></div>
      <p className="mt-4 text-gray-600">
        Signing you in...
      </p>
    </div>
  </div>
);
}