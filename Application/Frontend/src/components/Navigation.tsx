import { Package2 } from 'lucide-react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '@/hooks/AuthProvider';
interface NavigationProps {
  isDarkMode: boolean;
}
let active = 'flex items-center gap-2 text-lg font-semibold md:text-base text-gray-900 ';
let notActive = 'flex items-center gap-2 text-lg font-semibold md:text-base text-gray-500';
function Navigation({}: NavigationProps) {
  const { user } = useAuth();
  return (
    <nav className="hidden flex-col gap-6 text-lg font-medium md:flex md:flex-row md:items-center md:gap-5 md:text-sm lg:gap-6 ">
      <NavLink to="/">
        <Package2 className="h-6 w-6" />
        <span className="sr-only">Home</span>
      </NavLink>
      <NavLink to="/events" className={({ isActive }) => (isActive ? active : notActive)}>
        Events
      </NavLink>
      <NavLink to="/clubs" className={({ isActive }) => (isActive ? active : notActive)}>
        Clubs
      </NavLink>
      {user?.role === 'FACULTY' && (
        <NavLink to="/faculty" className={({ isActive }) => (isActive ? active : notActive)}>
          Faculty
        </NavLink>
      )}
    </nav>
  );
}

export default Navigation;
