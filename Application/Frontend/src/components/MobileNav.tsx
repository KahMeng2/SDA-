import { Package2, Menu } from 'lucide-react';
import { Link } from 'react-router-dom';
import { Sheet, SheetContent, SheetTrigger } from '@/components/ui/sheet';
import { Button } from '@/components/ui/button';

interface NavigationProps {
  isDarkMode: boolean;
}
function MobileNav({}: NavigationProps) {
  return (
    <Sheet>
      <SheetTrigger asChild>
        <Button variant="outline" size="icon" className="shrink-0 md:hidden">
          <Menu className="h-5 w-5" />
          <span className="sr-only">Toggle navigation menu</span>
        </Button>
      </SheetTrigger>
      <SheetContent side="left">
        <nav className="grid gap-6 text-lg font-medium">
          <Link to="/" className="flex items-center gap-2 text-lg font-semibold">
            <Package2 className="h-6 w-6" />
            <span className="sr-only">Home</span>
          </Link>
          <Link to="/clubs" className="hover:text-foreground">
            clubs
          </Link>
          <Link to="/getClubs" className="text-muted-foreground hover:text-foreground">
            Register Clubs
          </Link>
        </nav>
      </SheetContent>
    </Sheet>
  );
}

export default MobileNav;
