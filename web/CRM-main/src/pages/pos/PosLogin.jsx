import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Lock,
  User,
  Cpu,
  Eye,
  EyeOff,
  ArrowRight,
  ShieldCheck,
  Zap,
  LayoutGrid,
  Receipt,
  ClipboardList,
  Sparkles
} from 'lucide-react';

export function PosLogin() {
  const navigate = useNavigate();
  const [activeRole, setActiveRole] = useState('cashier');
  const [userCode, setUserCode] = useState('JC-POS-01');
  const [username, setUsername] = useState('cashier@justcatering.in');
  const [password, setPassword] = useState('password123');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleRoleSelect = (role) => {
    setActiveRole(role);
    if (role === 'cashier') {
      setUserCode('JC-POS-01');
      setUsername('cashier@justcatering.in');
      setPassword('password123');
    } else if (role === 'manager') {
      setUserCode('JC-MGR-01');
      setUsername('manager@justcatering.in');
      setPassword('mgr@2026');
    } else if (role === 'waiter') {
      setUserCode('JC-SRV-01');
      setUsername('captain@justcatering.in');
      setPassword('serve123');
    }
    setShowPassword(false);
  };

  const handleLogin = (e) => {
    e?.preventDefault();
    setError('');
    setIsLoading(true);

    setTimeout(() => {
      setIsLoading(false);
      localStorage.setItem('pos_user', JSON.stringify({
        userCode,
        username,
        role: activeRole.toUpperCase(),
        loginTime: new Date().toISOString()
      }));
      navigate('/pos');
    }, 550);
  };

  return (
    <div className="h-screen w-screen overflow-hidden bg-white dark:bg-[#0B111A] text-gray-900 dark:text-gray-100 grid grid-cols-1 lg:grid-cols-12">
      
      {/* Left Visual & Hero Showcase Panel (Full Height) */}
      <div className="hidden lg:flex lg:col-span-7 h-full flex-col justify-between p-10 lg:p-14 bg-gradient-to-br from-[#e0f2fe]/40 via-white to-[#f0fdfa]/40 dark:from-[#0e2938]/40 dark:via-[#0B111A] dark:to-[#071724]/60 border-r border-gray-200 dark:border-gray-800 relative overflow-hidden">
        
        {/* Ambient Glow Orbs */}
        <div className="absolute -top-24 -left-20 w-80 h-80 rounded-full bg-[#017A9C]/15 blur-3xl pointer-events-none animate-pulse" />
        <div className="absolute -bottom-20 right-10 w-72 h-72 rounded-full bg-emerald-500/10 blur-3xl pointer-events-none" />
        <div className="absolute top-1/2 left-1/3 w-64 h-64 rounded-full bg-orange-500/10 blur-3xl pointer-events-none" />

        {/* Top Header & Branding */}
        <div className="relative z-10">
          <div className="flex items-center gap-3 mb-5">
            <span className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 dark:bg-emerald-950/50 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-800">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-ping" />
              POS Engine Online · Terminal #01
            </span>
            <span className="px-3 py-1 rounded-full text-xs font-semibold bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-300 border border-gray-200 dark:border-gray-700">
              Enterprise v3.4
            </span>
          </div>

          <div className="flex items-center gap-3 mb-4">
            <div className="w-11 h-11 rounded-xl bg-gradient-to-br from-[#017A9C] to-[#015870] flex items-center justify-center text-white font-serif font-extrabold text-2xl shadow-lg shadow-[#017A9C]/25 flex-shrink-0">
              S
            </div>
            <div>
              <span className="text-xs font-extrabold uppercase tracking-widest text-[#017A9C] block">
                Saffron POS Desk
              </span>
              <span className="text-xs text-gray-500 dark:text-gray-400 font-medium">
                Hospitality, Bar &amp; Catering Engine
              </span>
            </div>
          </div>

          <h1 className="text-3xl xl:text-4xl font-extrabold tracking-tight leading-snug text-gray-900 dark:text-white mb-3">
            Precision Floor Desk, <span className="text-[#017A9C]">Kitchen KOT</span> &amp; Instant Billing
          </h1>

          <p className="text-sm text-gray-600 dark:text-gray-400 max-w-xl leading-relaxed mb-6">
            Complete dining room workflow: visual tables layout, instant kitchen routing, modifier customizations, hold/pending orders, and split-tender GST invoicing.
          </p>

          {/* 4 Feature Highlights Grid */}
          <div className="grid grid-cols-2 gap-3 max-w-xl mb-6">
            <div className="p-3.5 rounded-xl bg-white/80 dark:bg-gray-800/70 border border-gray-200/80 dark:border-gray-700/60 shadow-xs hover:border-[#017A9C]/50 transition-all group">
              <div className="w-7 h-7 rounded-lg bg-[#017A9C]/10 text-[#017A9C] flex items-center justify-center mb-2 group-hover:scale-110 transition-transform">
                <Zap className="w-4 h-4" />
              </div>
              <div className="font-bold text-xs text-gray-900 dark:text-white mb-1">Ultra-Fast Ordering &amp; KOT</div>
              <div className="text-[11px] text-gray-500 dark:text-gray-400 leading-snug">Single/multi item modifiers, save as pending, and multi-printer routing.</div>
            </div>

            <div className="p-3.5 rounded-xl bg-white/80 dark:bg-gray-800/70 border border-gray-200/80 dark:border-gray-700/60 shadow-xs hover:border-[#017A9C]/50 transition-all group">
              <div className="w-7 h-7 rounded-lg bg-blue-50 dark:bg-blue-950/50 text-blue-600 dark:text-blue-400 flex items-center justify-center mb-2 group-hover:scale-110 transition-transform">
                <LayoutGrid className="w-4 h-4" />
              </div>
              <div className="font-bold text-xs text-gray-900 dark:text-white mb-1">Interactive Floor Plan</div>
              <div className="text-[11px] text-gray-500 dark:text-gray-400 leading-snug">Live table occupancy timers, seamless table transfers, and bookings.</div>
            </div>

            <div className="p-3.5 rounded-xl bg-white/80 dark:bg-gray-800/70 border border-gray-200/80 dark:border-gray-700/60 shadow-xs hover:border-[#017A9C]/50 transition-all group">
              <div className="w-7 h-7 rounded-lg bg-emerald-50 dark:bg-emerald-950/50 text-emerald-600 dark:text-emerald-400 flex items-center justify-center mb-2 group-hover:scale-110 transition-transform">
                <Receipt className="w-4 h-4" />
              </div>
              <div className="font-bold text-xs text-gray-900 dark:text-white mb-1">Instant GST Settlements</div>
              <div className="text-[11px] text-gray-500 dark:text-gray-400 leading-snug">Multi-tender payments (Cash, UPI, Card), thermal slips &amp; WhatsApp bills.</div>
            </div>

            <div className="p-3.5 rounded-xl bg-white/80 dark:bg-gray-800/70 border border-gray-200/80 dark:border-gray-700/60 shadow-xs hover:border-[#017A9C]/50 transition-all group">
              <div className="w-7 h-7 rounded-lg bg-amber-50 dark:bg-amber-950/50 text-amber-600 dark:text-amber-400 flex items-center justify-center mb-2 group-hover:scale-110 transition-transform">
                <ClipboardList className="w-4 h-4" />
              </div>
              <div className="font-bold text-xs text-gray-900 dark:text-white mb-1">All Orders &amp; Void Ledger</div>
              <div className="text-[11px] text-gray-500 dark:text-gray-400 leading-snug">Unified orders desk with full audit reasons for cancellations &amp; refunds.</div>
            </div>
          </div>

          {/* Live Simulated KOT Widget */}
          <div className="p-3.5 rounded-xl bg-white/70 dark:bg-gray-800/60 border border-gray-200 dark:border-gray-700/80 backdrop-blur-md max-w-xl shadow-xs">
            <div className="flex items-center justify-between mb-2">
              <div className="flex items-center gap-2">
                <span className="w-2 h-2 rounded-full bg-[#017A9C] animate-ping" />
                <span className="text-xs font-bold text-[#017A9C] bg-[#017A9C]/10 px-2 py-0.5 rounded">
                  LIVE KOT #042 · Table GF-02 (Dine-In)
                </span>
              </div>
              <span className="text-[11px] font-semibold text-gray-400">⏱️ 3m 45s</span>
            </div>
            <div className="flex items-center gap-2 flex-wrap text-xs">
              <span className="bg-gray-100 dark:bg-gray-700/60 px-2 py-1 rounded-md text-gray-700 dark:text-gray-200 font-medium">
                🍛 Paneer Butter Masala × 2 <b className="text-emerald-600 font-bold ml-1">(Ready)</b>
              </span>
              <span className="bg-gray-100 dark:bg-gray-700/60 px-2 py-1 rounded-md text-gray-700 dark:text-gray-200 font-medium">
                🫓 Garlic Naan (Crispy) × 4 <b className="text-amber-600 font-bold ml-1">(Cooking)</b>
              </span>
              <span className="bg-gray-100 dark:bg-gray-700/60 px-2 py-1 rounded-md text-gray-700 dark:text-gray-200 font-medium">
                🥣 Dal Makhani × 1 <b className="text-amber-600 font-bold ml-1">(Cooking)</b>
              </span>
            </div>
          </div>
        </div>

        {/* Bottom Specifications Bar */}
        <div className="relative z-10 pt-4 border-t border-gray-200 dark:border-gray-800 flex items-center gap-4 text-xs text-gray-500 dark:text-gray-400">
          <span className="flex items-center gap-1.5"><ShieldCheck className="w-3.5 h-3.5 text-[#017A9C]" /> 256-Bit Encrypted Session</span>
          <span>•</span>
          <span>⚡ Zero-Latency Local Cache</span>
          <span>•</span>
          <span>🖨️ ESC/POS Thermal Ready</span>
        </div>
      </div>

      {/* Right Form Card (Full Height Console) */}
      <div className="lg:col-span-5 h-full flex flex-col justify-center p-8 lg:p-14 overflow-y-auto bg-white dark:bg-[#0B111A]">
        <div className="max-w-md w-full mx-auto">
          
          <div className="mb-6 text-center">
            <div className="inline-flex w-12 h-12 rounded-xl bg-[#017A9C]/10 text-[#017A9C] items-center justify-center mb-3 shadow-inner">
              <Lock className="w-6 h-6" />
            </div>
            <h2 className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">Operator Sign In</h2>
            <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">Select operator profile or enter credentials to open POS floor desk</p>
          </div>

          {/* Role Switcher Pills */}
          <div className="grid grid-cols-3 gap-1.5 p-1 bg-gray-100 dark:bg-gray-800/80 rounded-xl mb-6 border border-gray-200 dark:border-gray-700">
            <button
              type="button"
              onClick={() => handleRoleSelect('cashier')}
              className={`py-2 px-2 rounded-lg text-xs font-bold transition-all flex items-center justify-center gap-1 ${
                activeRole === 'cashier'
                  ? 'bg-white dark:bg-gray-700 text-[#017A9C] dark:text-white shadow-xs'
                  : 'text-gray-500 hover:text-gray-900 dark:text-gray-400'
              }`}
            >
              👤 Cashier
            </button>
            <button
              type="button"
              onClick={() => handleRoleSelect('manager')}
              className={`py-2 px-2 rounded-lg text-xs font-bold transition-all flex items-center justify-center gap-1 ${
                activeRole === 'manager'
                  ? 'bg-white dark:bg-gray-700 text-[#017A9C] dark:text-white shadow-xs'
                  : 'text-gray-500 hover:text-gray-900 dark:text-gray-400'
              }`}
            >
              👔 Manager
            </button>
            <button
              type="button"
              onClick={() => handleRoleSelect('waiter')}
              className={`py-2 px-2 rounded-lg text-xs font-bold transition-all flex items-center justify-center gap-1 ${
                activeRole === 'waiter'
                  ? 'bg-white dark:bg-gray-700 text-[#017A9C] dark:text-white shadow-xs'
                  : 'text-gray-500 hover:text-gray-900 dark:text-gray-400'
              }`}
            >
              🧑‍🍳 Captain
            </button>
          </div>

          {error && (
            <div className="mb-4 p-3 rounded-lg bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-900/50 text-red-600 dark:text-red-400 text-xs font-semibold">
              {error}
            </div>
          )}

          <form onSubmit={handleLogin} className="space-y-4">
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-gray-600 dark:text-gray-400 mb-1.5">
                Station Code
              </label>
              <div className="relative">
                <Cpu className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400" />
                <input
                  type="text"
                  required
                  value={userCode}
                  onChange={(e) => setUserCode(e.target.value)}
                  placeholder="e.g. JC-POS-01"
                  className="w-full pl-10 pr-3 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm font-semibold focus:outline-none focus:border-[#017A9C] dark:focus:border-[#017A9C] focus:bg-white dark:focus:bg-gray-900 transition-all"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-gray-600 dark:text-gray-400 mb-1.5">
                Cashier / Waiter Email
              </label>
              <div className="relative">
                <User className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400" />
                <input
                  type="text"
                  required
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="operator@justcatering.in"
                  className="w-full pl-10 pr-3 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm font-semibold focus:outline-none focus:border-[#017A9C] dark:focus:border-[#017A9C] focus:bg-white dark:focus:bg-gray-900 transition-all"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-gray-600 dark:text-gray-400 mb-1.5">
                PIN / Password
              </label>
              <div className="relative">
                <Lock className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="w-full pl-10 pr-10 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-sm font-semibold focus:outline-none focus:border-[#017A9C] dark:focus:border-[#017A9C] focus:bg-white dark:focus:bg-gray-900 transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  title={showPassword ? 'Password is visible · Click to obscure' : 'Password is obscured · Click to show'}
                  className="absolute right-3 top-1/2 -translate-y-1/2 p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 transition-colors"
                >
                  {/* Corrected obscure logic: When visible (showPassword === true) render Eye; when obscured (showPassword === false) render EyeOff */}
                  {showPassword ? <Eye className="w-4 h-4" /> : <EyeOff className="w-4 h-4" />}
                </button>
              </div>
            </div>

            <div className="flex items-center justify-between pt-1">
              <button
                type="button"
                onClick={() => handleRoleSelect('cashier')}
                className="text-xs font-bold text-[#017A9C] hover:underline flex items-center gap-1"
              >
                <Sparkles className="w-3.5 h-3.5" /> Fill Demo Credentials
              </button>
              <span className="text-xs text-gray-400">Shift: Morning #1</span>
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full mt-2 py-3 px-4 rounded-xl bg-[#017A9C] hover:bg-[#016582] text-white font-extrabold text-sm shadow-md shadow-[#017A9C]/20 transition-all flex items-center justify-center gap-2 disabled:opacity-50 hover:shadow-lg active:scale-[0.99]"
            >
              {isLoading ? (
                <span className="inline-flex items-center gap-2">
                  <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  Authenticating Terminal...
                </span>
              ) : (
                <>
                  <span>Enter POS Floor Desk</span>
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>
          </form>

          <div className="mt-8 text-center text-xs text-gray-400 border-t border-gray-100 dark:border-gray-800 pt-4">
            🔒 Restricted to authorized restaurant staff · Station ID: <b>{userCode}</b>
          </div>
        </div>
      </div>

    </div>
  );
}
