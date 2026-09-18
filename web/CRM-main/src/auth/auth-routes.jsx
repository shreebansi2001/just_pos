import { AuthLayout } from "./layouts/AuthLayout";

// import { ChangePasswordPage } from "./pages/change-password-page";
import { CheckEmail } from "./pages/extended/check-email";
import { ResetPasswordChanged } from "./pages/extended/reset-password-changed";
import { ResetPasswordCheckEmail } from "./pages/extended/reset-password-check-email";
import { TwoFactorAuth } from "./pages/extended/tfa";
import { ResetPasswordPage } from "./pages/reset-password-page";
import { SignInPage } from "./pages/signin-page";
import { SignUpPage } from "./pages/signup-page";
import { LoginWithOTPPage } from "./pages/LoginWithOTPPage";

export const authRoutes = [
  {
    path: "",
    element: <AuthLayout />,
    children: [
      { path: "login-otp", element: <LoginWithOTPPage /> },
      { path: "signin", element: <SignInPage /> },
      { path: "signup", element: <SignUpPage /> },
      // { path: "change-password", element: <ChangePasswordPage /> },
      { path: "reset-password", element: <ResetPasswordPage /> },
      { path: "2fa", element: <TwoFactorAuth /> },
      { path: "check-email", element: <CheckEmail /> },
      { path: "reset-password/check-email", element: <ResetPasswordCheckEmail /> },
      { path: "reset-password/changed", element: <ResetPasswordChanged /> },
    ],
  },
 
];