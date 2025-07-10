import React from 'react';
import Navigation from './Navigation';
import MobileNav from './MobileNav';
import AppRoutes from '../routes/AppRoutes';
import { CircleUser } from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Button } from './ui/button';
import LoginDialog from './LoginDialog';
import { useAuth } from '@/hooks/AuthProvider';

const Layout: React.FC = () => {
  const { user, logout, isAuthenticated } = useAuth();

  return (
    <div className="flex flex-col min-h-screen w-full">
      <header className="sticky top-0 flex h-16 items-center gap-4 border-b bg-background px-4 md:px-6 bg-white/30 backdrop-blur-md z-50">
        <Navigation isDarkMode={false} />
        <MobileNav isDarkMode={false} />

        <div className="flex justify-end w-full items-center gap-4 md:ml-auto md:gap-2 lg:gap-4">
          {isAuthenticated ? (
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="secondary" size="icon" className="rounded-full">
                  <CircleUser className="h-5 w-5" />
                  <span className="sr-only">Toggle user menu</span>
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuLabel>{user?.username}</DropdownMenuLabel>
                <DropdownMenuLabel>ID: {user?.id}</DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem>Settings</DropdownMenuItem>
                <DropdownMenuItem>Support</DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={logout}>Logout</DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          ) : (
            <LoginDialog>
              <Button>Log in / Sign Up</Button>
            </LoginDialog>
          )}
        </div>
      </header>
      <main className="flex-grow w-full bg-neutral-50">
        <div className="container mx-auto px-4 md:px-6">
          <AppRoutes />
        </div>
      </main>
      <footer className="w-full border-t bg-background">
        <div className="container mx-auto px-4 md:px-6 py-4 text-center">
          <p>© 2024 Brogrammer Brigade. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
};

export default Layout;
