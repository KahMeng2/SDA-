import { useState, useRef, useLayoutEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { useAuth } from '@/hooks/AuthProvider';
import { useToast } from '@/hooks/use-toast';
import {
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { CalendarIcon } from 'lucide-react';
import { format } from 'date-fns';
import { Calendar } from '@/components/ui/calendar';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import axiosInstance from '@/api/apiConfig';

const loginSchema = z.object({
  username: z.string().min(1, 'Username is required'),
  password: z.string().min(1, 'Password is required'),
});

const signupSchema = z.object({
  email: z.string().email('Invalid email address'),
  username: z.string().min(1, 'Username is required'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
  firstName: z.string().min(1, 'First name is required'),
  middleName: z.string().optional(),
  lastName: z.string().min(1, 'Last name is required'),
  dob: z.date({
    required_error: 'Date of birth is required',
  }),
});

type LoginFormData = z.infer<typeof loginSchema>;
type SignupFormData = z.infer<typeof signupSchema>;

export function AuthForm() {
  const [activeTab, setActiveTab] = useState<'login' | 'signup'>('login');
  const { login } = useAuth();
  const { toast } = useToast();
  const loginFormRef = useRef<HTMLDivElement>(null);
  const signupFormRef = useRef<HTMLDivElement>(null);
  const [formHeight, setFormHeight] = useState<number>(0);

  const loginForm = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      username: '',
      password: '',
    },
  });

  const signupForm = useForm<SignupFormData>({
    resolver: zodResolver(signupSchema),
    defaultValues: {
      email: '',
      username: '',
      password: '',
      firstName: '',
      middleName: '',
      lastName: '',
      dob: undefined,
    },
  });

  const onLoginSubmit = async (data: LoginFormData) => {
    try {
      await login(data.username, data.password);
      toast({
        title: 'Login Successful',
        description: 'You have been successfully logged in.',
      });
    } catch (error) {
      toast({
        title: 'Login Failed',
        description: 'Invalid username or password. Please try again.',
        variant: 'destructive',
      });
    }
  };

  const onSignupSubmit = async (data: SignupFormData) => {
    try {
      await axiosInstance.post('/students/signup', {
        ...data,
        dob: format(data.dob, 'yyyy-MM-dd'),
      });
      toast({
        title: 'Signup Successful',
        description: 'Your account has been created successfully. You can now log in.',
      });
      setActiveTab('login');
    } catch (error) {
      toast({
        title: 'Signup Failed',
        description: 'There was an error creating your account. Please try again.',
        variant: 'destructive',
      });
    }
  };
  useLayoutEffect(() => {
    const updateHeight = () => {
      const activeFormRef = activeTab === 'login' ? loginFormRef : signupFormRef;
      if (activeFormRef.current) {
        setFormHeight(activeFormRef.current.scrollHeight);
      }
    };

    updateHeight();
    window.addEventListener('resize', updateHeight);
    return () => window.removeEventListener('resize', updateHeight);
  }, [activeTab]);

  return (
    <Tabs
      value={activeTab}
      onValueChange={(value) => setActiveTab(value as 'login' | 'signup')}
      className="w-full"
    >
      <TabsList className="grid w-full grid-cols-2">
        <TabsTrigger value="login">Login</TabsTrigger>
        <TabsTrigger value="signup">Signup</TabsTrigger>
      </TabsList>
      <div
        className="relative mt-4 overflow-hidden transition-all duration-300 ease-in-out"
        style={{ height: `${formHeight}px` }}
      >
        <TabsContent
          value="login"
          className="absolute top-0 left-0 w-full transition-all duration-300 ease-in-out"
          style={{
            transform: `translateX(${activeTab === 'login' ? '0%' : '-100%'})`,
            opacity: activeTab === 'login' ? 1 : 0,
          }}
        >
          <div ref={loginFormRef}>
            <Form {...loginForm}>
              <form onSubmit={loginForm.handleSubmit(onLoginSubmit)}>
                {/* ... (login form content remains the same) */}
              </form>
            </Form>
          </div>
        </TabsContent>
        <TabsContent
          value="signup"
          className="absolute top-0 left-0 w-full transition-all duration-300 ease-in-out"
          style={{
            transform: `translateX(${activeTab === 'signup' ? '0%' : '100%'})`,
            opacity: activeTab === 'signup' ? 1 : 0,
          }}
        >
          <div ref={signupFormRef}>
            <Form {...signupForm}>
              <form onSubmit={signupForm.handleSubmit(onSignupSubmit)}>
                <CardHeader>
                  <CardTitle className="text-2xl">Signup</CardTitle>
                  <CardDescription>Create a new account to get started.</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <FormField
                    control={signupForm.control}
                    name="email"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Email</FormLabel>
                        <FormControl>
                          <Input type="email" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={signupForm.control}
                    name="username"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Username</FormLabel>
                        <FormControl>
                          <Input {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={signupForm.control}
                    name="password"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Password</FormLabel>
                        <FormControl>
                          <Input type="password" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={signupForm.control}
                    name="firstName"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>First Name</FormLabel>
                        <FormControl>
                          <Input {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={signupForm.control}
                    name="middleName"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Middle Name</FormLabel>
                        <FormControl>
                          <Input {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={signupForm.control}
                    name="lastName"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Last Name</FormLabel>
                        <FormControl>
                          <Input {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={signupForm.control}
                    name="dob"
                    render={({ field }) => (
                      <FormItem className="flex flex-col">
                        <FormLabel>Date of birth</FormLabel>
                        <Popover>
                          <PopoverTrigger asChild>
                            <FormControl>
                              <Button
                                variant={'outline'}
                                className={`w-full pl-3 text-left font-normal ${!field.value && 'text-muted-foreground'}`}
                              >
                                {field.value ? (
                                  format(field.value, 'PPP')
                                ) : (
                                  <span>Pick a date</span>
                                )}
                                <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                              </Button>
                            </FormControl>
                          </PopoverTrigger>
                          <PopoverContent className="w-auto p-0" align="start">
                            <Calendar
                              mode="single"
                              selected={field.value}
                              onSelect={field.onChange}
                              disabled={(date) =>
                                date > new Date() || date < new Date('1900-01-01')
                              }
                              initialFocus
                            />
                          </PopoverContent>
                        </Popover>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </CardContent>
                <CardFooter>
                  <Button type="submit" className="w-full">
                    Sign up
                  </Button>
                </CardFooter>
              </form>
            </Form>
          </div>
        </TabsContent>
      </div>
    </Tabs>
  );
}
