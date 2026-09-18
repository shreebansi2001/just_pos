import { useEffect, useState } from 'react';
import { AlertCircle, Check, Eye, EyeOff, LoaderCircleIcon, Mail, Lock , KeyRound  } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { Alert, AlertIcon, AlertTitle } from '@/components/ui/alert';
import { Button } from '@/components/ui/button';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { useAuth } from '@/auth/context/auth-context';
import { getSigninSchema } from '../forms/signin-schema';

export function SignInPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { login } = useAuth();
  const [passwordVisible, setPasswordVisible] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);

  useEffect(() => {
    const pwdReset = searchParams.get('pwd_reset');
    const errorParam = searchParams.get('error');
    const errorDescription = searchParams.get('error_description');

    if (pwdReset === 'success') {
      setSuccessMessage('Your password has been successfully reset. You can now sign in with your new password.');
    }

    if (errorParam) {
      switch (errorParam) {
        case 'auth_callback_failed':
          setError(errorDescription || 'Authentication failed. Please try again.');
          break;
        case 'auth_callback_error':
          setError(errorDescription || 'An error occurred during authentication. Please try again.');
          break;
        case 'auth_token_error':
          setError(errorDescription || 'Failed to set authentication session. Please try again.');
          break;
        default:
          setError(errorDescription || 'Authentication error. Please try again.');
          break;
      }
    }
  }, [searchParams]);

  const form = useForm({
    resolver: zodResolver(getSigninSchema()),
    defaultValues: { userCode: '', email: '', password: '' },
  });

function hasAnyAssignedPage(userDetails) {
  const rights = userDetails?.userRights || [];
  return rights.some((module) =>
    (module.userRights || []).some(
      (page) => page.view || page.add || page.edit || page.delete
    )
  );
}

  async function onSubmit(values) {
  try {
  setIsProcessing(true);
  setError(null);

  const auth = await login(values.userCode, values.email, values.password);

  const userDetails = auth?.userDetails;
  const clientId  = userDetails?.clientId;
  const roleId    = Number(userDetails?.userBasicDetails?.role?.id);
  const userPlan  = userDetails?.userPlan?.plan ?? null;
  const plan      = userDetails?.plan ?? null;
  const isApprove = userDetails?.isApprove;

 if (roleId === 1 || clientId === 1) {
  navigate('/super-dashboard', { replace: true });
} else if (roleId === 2) {
  const hasValidPlan =
    userPlan != null && userPlan !== '' && userPlan !== 'null' &&
    (typeof userPlan === 'object' ? Object.keys(userPlan).length > 0 : true);
  if (!hasValidPlan || isApprove === false) {
    navigate('/approvepending', { replace: true });
  } else {
    navigate('/', { replace: true }); // Admin always has full access
  }
} else if (roleId > 2) {
  if (plan === 'null' || plan == null) {
    setModalOpen(true);
  } else if (hasAnyAssignedPage(userDetails)) {
    navigate('/', { replace: true });
  } else {
    navigate('/no-access', { replace: true });
  }
} else {
  if (hasAnyAssignedPage(userDetails)) {
    navigate('/', { replace: true });
  } else {
    navigate('/no-access', { replace: true });
  }
}
  }
 catch (err) {
  let msg = err?.message || '';
  if (msg.includes('Network Error')) {
    msg = 'Network error. Please check your connection.';
  } else if (msg.includes('timeout')) {
    msg = 'Request timed out. Please try again.';
  }
  setError(msg || 'Login failed. Please try again.');
} finally {
  setIsProcessing(false);
}
  }
  return (
    <div className="w-full max-w-md mx-auto px-2">
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="block w-full space-y-5" autoComplete="off">

          {/* Header */}
          <div className="space-y-1 pb-1">
            <h1 className="text-3xl font-bold text-gray-900">Sign In</h1>
            <p className="text-primary font-semibold text-base">Welcome back</p>
            <p className="text-sm text-gray-500">
              Hey, Enter your details below to sign in and access your account securely and easily.
            </p>
          </div>

          {/* Alerts */}
          {error && (
            <Alert variant="destructive" appearance="light" onClose={() => setError(null)}>
              <AlertIcon><AlertCircle /></AlertIcon>
              <AlertTitle>{error}</AlertTitle>
            </Alert>
          )}
          {successMessage && (
            <Alert appearance="light" onClose={() => setSuccessMessage(null)}>
              <AlertIcon><Check /></AlertIcon>
              <AlertTitle>{successMessage}</AlertTitle>
            </Alert>
          )}

          {/* Unique Code */}
          <FormField
            control={form.control}
            name="userCode"
            render={({ field }) => (
              <FormItem>
                <FormLabel className="text-sm font-semibold text-gray-800">Unique Code</FormLabel>
                <FormControl>
                  <div className="relative">
                    <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                      <KeyRound className="w-4 h-4" />
                    </span>
                    <Input
                      placeholder="Enter your unique code"
                      autoComplete="off"
                      name="userCode"
                      className="pl-9 h-12 rounded-xl border-gray-200 focus:border-primary focus:ring-primary"
                      {...field}
                    />
                  </div>
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          {/* Email */}
          <FormField
            control={form.control}
            name="email"
            render={({ field }) => (
              <FormItem>
                <FormLabel className="text-sm font-semibold text-gray-800">Email Address</FormLabel>
                <FormControl>
                  <div className="relative">
                    <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                      <Mail className="w-4 h-4" />
                    </span>
                    <Input
                      placeholder="Enter your email"
                      autoComplete="username"
                      name="email"
                      type="email"
                      className="pl-9 h-12 rounded-xl border-gray-200 focus:border-primary focus:ring-primary"
                      {...field}
                    />
                  </div>
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          {/* Password */}
          <FormField
            control={form.control}
            name="password"
            render={({ field }) => (
              <FormItem>
                <FormLabel className="text-sm font-semibold text-gray-800">Password</FormLabel>
                <FormControl>
                  <div className="relative">
                    <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                      <Lock className="w-4 h-4" />
                    </span>
                    <Input
                      placeholder="••••••••••"
                      autoComplete="current-password"
                      name="password"
                      type={passwordVisible ? 'text' : 'password'}
                      className="pl-9 pr-10 h-12 rounded-xl border-gray-200 focus:border-primary focus:ring-primary"
                      {...field}
                    />
                    <button
                      type="button"
                      onClick={() => setPasswordVisible(!passwordVisible)}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                    >
                      {passwordVisible ? <Eye className="w-4 h-4" /> : <EyeOff className="w-4 h-4" />}
                    </button>
                  </div>
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          {/* OTP + Forgot */}
          <div className="flex items-center justify-between text-sm">
            <Link to="/auth/login-otp" className="text-primary font-medium hover:underline">
              Login with OTP instead
            </Link>
            <Link to="/auth/reset-password" className="text-primary font-medium hover:underline">
              Forgot Password?
            </Link>
          </div>

          {/* Submit */}
          <Button
            type="submit"
            disabled={isProcessing}
            className="w-full h-12 rounded-xl bg-primary hover:bg-primary/90 text-white text-base font-semibold transition-colors"
          >
            {isProcessing ? (
              <span className="flex items-center gap-2">
                <LoaderCircleIcon className="h-4 w-4 animate-spin" />
                Please wait...
              </span>
            ) : 'Login to Your Account'}
          </Button>

        </form>
      </Form>
    </div>
  );
}