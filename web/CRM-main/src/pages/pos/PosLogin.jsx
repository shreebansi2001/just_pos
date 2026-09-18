import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Lock, User, KeyRound, Eye, EyeOff, UtensilsCrossed, ArrowRight, ShieldCheck } from 'lucide-react';

export function PosLogin() {
  const navigate = useNavigate();
  const [userCode, setUserCode] = useState('JC-POS-01');
  const [username, setUsername] = useState('cashier@justcatering.in');
  const [password, setPassword] = useState('password123');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleLogin = (e) => {
    e?.preventDefault();
    setError('');
    setIsLoading(true);

    setTimeout(() => {
      setIsLoading(false);
      // Store session token and operator info
      localStorage.setItem('pos_user', JSON.stringify({
        userCode,
        username,
        role: 'CASHIER_OPERATOR',
        loginTime: new Date().toISOString()
      }));
      navigate('/pos');
    }, 600);
  };

  const handleQuickDemo = () => {
    setUserCode('JC-POS-01');
    setUsername('head_cashier@justcatering.in');
    setPassword('pos@2026');
  };

  return (
    <div className="min-h-screen bg-[#F8FAFC] dark:bg-[#0B111A] text-gray-900 dark:text-gray-100 flex items-center justify-center p-4">
      <div className="w-full max-w-6xl grid grid-cols-1 lg:grid-cols-12 gap-8 items-center">
        
        {/* Left Visual Branding Panel */}
        <div className="lg:col-span-7 flex flex-col justify-center px-4 lg:px-8 py-6">
          <div className="flex items-center gap-3 mb-6">
            <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-[#017A9C] to-[#015870] flex items-center justify-center text-white shadow-lg shadow-[#017A9C]/25">
              <UtensilsCrossed className="w-6 h-6" />
            </div>
            <div>
              <span className="text-xs font-bold uppercase tracking-widest text-[#017A9C]">Hospitality Suite</span>
              <h2 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">Saffron POS Desk</h2>
            </div>
          </div>

          <h1 className="text-4xl lg:text-5xl font-extrabold tracking-tight leading-tight text-gray-900 dark:text-white mb-4">
            Next-Gen Floor, KOT &amp; <span className="text-[#017A9C]">Order Terminal</span>
          </h1>

          <p className="text-lg text-gray-600 dark:text-gray-400 mb-8 max-w-xl">
            Streamline dine-in tables, kitchen tickets, rapid GST invoicing, and reservations in one ultra-fast, synchronized interface.
          </p>

          <div className="grid grid-cols-2 gap-4 max-w-lg mb-8">
            <div className="p-4 rounded-xl bg-white dark:bg-gray-800/80 border border-gray-200/80 dark:border-gray-700/60 shadow-sm">
              <div className="text-[#017A9C] font-bold text-lg mb-1">Live Tables</div>
              <div className="text-xs text-gray-500 dark:text-gray-400">4 operational states, floor layouts, and takeaway launchers.</div>
            </div>
            <div className="p-4 rounded-xl bg-white dark:bg-gray-800/80 border border-gray-200/80 dark:border-gray-700/60 shadow-sm">
              <div className="text-[#017A9C] font-bold text-lg mb-1">Kitchen Kanban</div>
              <div className="text-xs text-gray-500 dark:text-gray-400">Real-time KOT tracking from New to Served with timer alerts.</div>
            </div>
            <div className="p-4 rounded-xl bg-white dark:bg-gray-800/80 border border-gray-200/80 dark:border-gray-700/60 shadow-sm">
              <div className="text-[#017A9C] font-bold text-lg mb-1">Instant GST Bill</div>
              <div className="text-xs text-gray-500 dark:text-gray-400">Automated CGST/SGST, discounts, and Cash/Card/UPI settlement.</div>
            </div>
            <div className="p-4 rounded-xl bg-white dark:bg-gray-800/80 border border-gray-200/80 dark:border-gray-700/60 shadow-sm">
              <div className="text-[#017A9C] font-bold text-lg mb-1">Floor Reserve</div>
              <div className="text-xs text-gray-500 dark:text-gray-400">Direct booking schedule with one-click table capacity seating.</div>
            </div>
          </div>

          <div className="flex items-center gap-2 text-xs text-gray-500 dark:text-gray-400">
            <ShieldCheck className="w-4 h-4 text-[#017A9C]" />
            Enterprise encrypted operator terminal · Station ID: POS-TERMINAL-01
          </div>
        </div>

        {/* Right Form Card */}
        <div className="lg:col-span-5">
          <div className="bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 rounded-2xl shadow-xl p-8">
            <div className="mb-6 text-center">
              <div className="inline-flex w-12 h-12 rounded-xl bg-[#017A9C]/10 text-[#017A9C] items-center justify-center mb-3">
                <Lock className="w-6 h-6" />
              </div>
              <h3 className="text-2xl font-bold text-gray-900 dark:text-white">Terminal Sign In</h3>
              <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">Authenticate to access floor ordering and cash drawer</p>
            </div>

            {error && (
              <div className="mb-4 p-3 rounded-lg bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-900/50 text-red-600 dark:text-red-400 text-xs font-semibold">
                {error}
              </div>
            )}

            <form onSubmit={handleLogin} className="space-y-4">
              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-gray-600 dark:text-gray-400 mb-1">
                  Tenant / Station Code
                </label>
                <div className="relative">
                  <KeyRound className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                  <input
                    type="text"
                    required
                    value={userCode}
                    onChange={(e) => setUserCode(e.target.value)}
                    placeholder="e.g. JC-POS-01"
                    className="w-full pl-9 pr-3 py-2.5 rounded-lg border border-gray-300 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C] dark:focus:border-[#017A9C]"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-gray-600 dark:text-gray-400 mb-1">
                  Operator Username or Email
                </label>
                <div className="relative">
                  <User className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                  <input
                    type="text"
                    required
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="operator@justcatering.in"
                    className="w-full pl-9 pr-3 py-2.5 rounded-lg border border-gray-300 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C] dark:focus:border-[#017A9C]"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-gray-600 dark:text-gray-400 mb-1">
                  PIN or Password
                </label>
                <div className="relative">
                  <Lock className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                  <input
                    type={showPassword ? 'text' : 'password'}
                    required
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="••••••••"
                    className="w-full pl-9 pr-10 py-2.5 rounded-lg border border-gray-300 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:border-[#017A9C] dark:focus:border-[#017A9C]"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-200"
                  >
                    {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                  </button>
                </div>
              </div>

              <div className="flex items-center justify-between pt-1">
                <button
                  type="button"
                  onClick={handleQuickDemo}
                  className="text-xs font-semibold text-[#017A9C] hover:underline"
                >
                  Fill Demo Credentials
                </button>
                <span className="text-xs text-gray-400">Station #01</span>
              </div>

              <button
                type="submit"
                disabled={isLoading}
                className="w-full mt-2 py-3 px-4 rounded-xl bg-[#017A9C] hover:bg-[#016582] text-white font-bold text-sm shadow-md transition-all flex items-center justify-center gap-2 disabled:opacity-50"
              >
                {isLoading ? (
                  <span>Authenticating Station...</span>
                ) : (
                  <>
                    <span>Open POS Terminal</span>
                    <ArrowRight className="w-4 h-4" />
                  </>
                )}
              </button>
            </form>

            <div className="mt-6 pt-6 border-t border-gray-100 dark:border-gray-800 text-center">
              <a
                href="/auth/login"
                className="text-xs text-gray-500 dark:text-gray-400 hover:text-[#017A9C] transition-colors inline-flex items-center gap-1 font-medium"
              >
                ← Back to JC Portal CRM
              </a>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
}
