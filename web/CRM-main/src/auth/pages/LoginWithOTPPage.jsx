import { useState } from 'react';
import { LoaderCircleIcon, Smartphone } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import {
  Form, FormControl, FormField, FormItem, FormLabel, FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { LoginWithOtp } from '@/services/apiServices';

const otpSchema = z.object({
  phone: z
    .string()
    .min(10, 'Enter a valid phone number')
    .regex(/^\+?[0-9\s\-()]+$/, 'Enter a valid phone number'),

  uniqueCode: z
    .string()
    .min(1, 'Unique Code is required'),
});

export function LoginWithOTPPage() {
  const navigate = useNavigate();
  const [isProcessing, setIsProcessing] = useState(false);
  const [error, setError] = useState(null);

 const form = useForm({
  resolver: zodResolver(otpSchema),
  defaultValues: {
    phone: '',
    uniqueCode: '',
  },
});

  async function onSubmit(values) {
    try {
      setIsProcessing(true);
      setError(null);

      const res = await LoginWithOtp(values.phone);
      const data = res?.data;

      if (data?.status === false) {
        setError(data?.message || 'Failed to send OTP.');
        return;
      }

      // Pass phone + uniqueCode (if API returns it) to verify screen
      navigate('/auth/verify-otp', {
        state: {
          phone: values.phone,
          uniqueCode: data?.data?.uniqueCode || '',
        },
      });
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to send OTP. Please try again.');
    } finally {
      setIsProcessing(false);
    }
  }

  return (
    <div className="w-full max-w-md mx-auto px-2">
      <Form {...form}>
        <form  onSubmit={form.handleSubmit(onSubmit)} className="block w-full space-y-5">
          <div className="space-y-1 pb-1">
            <h1 className="text-2xl font-bold text-gray-900">Login with OTP instead</h1>
            <p className="text-sm text-primary">
              Enter your phone number to receive an OTP code for account verification.
            </p>
          </div>

          {error && (
            <p className="text-sm text-red-500 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
              {error}
            </p>
          )}

          <FormField
            control={form.control}
            name="phone"
            render={({ field }) => (
              <FormItem>
                <FormLabel className="text-sm font-semibold text-gray-800">Phone Number</FormLabel>
                <FormControl>
                  <div className="relative">
                    <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                      <Smartphone className="w-4 h-4" />
                    </span>
                    <Input
                      placeholder="Enter phone number"
                      type="tel"
                      className="pl-9 h-12 rounded-xl border-gray-200 focus:border-primary focus:ring-primary"
                      {...field}
                    />
                  </div>
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
<FormField
  control={form.control}
  name="uniqueCode"
  render={({ field }) => (
    <FormItem>
      <FormLabel className="text-sm font-semibold text-gray-800">
        Unique Code
      </FormLabel>
      <FormControl>
        <Input
          placeholder="Enter unique code"
          className="h-12 rounded-xl border-gray-200 focus:border-primary focus:ring-primary"
          {...field}
        />
      </FormControl>
      <FormMessage />
    </FormItem>
  )}
/>
          <Link to="/auth/signin" className="block text-sm text-primary font-medium hover:underline">
            Login with Email instead
          </Link>

          <Button
            type="submit"
            disabled={isProcessing}
            className="w-full h-12 rounded-xl bg-primary  text-white text-base font-semibold transition-colors"
          >
            {isProcessing ? (
              <span className="flex items-center gap-2">
                <LoaderCircleIcon className="h-4 w-4 animate-spin" />
                Sending OTP...
              </span>
            ) : 'Send your OTP'}
          </Button>

         
        </form>
      </Form>
    </div>
  );
}