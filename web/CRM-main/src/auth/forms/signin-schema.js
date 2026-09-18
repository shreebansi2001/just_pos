import { z } from 'zod';

export const getSigninSchema = () => {
  return z.object({
    userCode: z.string().min(1, { message: 'Unique code is required.' }),
    email: z
      .string()
      .email({ message: 'Please enter a valid email address.' })
      .min(1, { message: 'Email is required.' }),
    password: z.string().min(1, { message: 'Password is required.' }),
    rememberMe: z.boolean().optional(),
  });
};