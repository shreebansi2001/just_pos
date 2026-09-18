import { ChevronFirst } from 'lucide-react';
import { Link } from 'react-router-dom';
import { toAbsoluteUrl } from '@/lib/helpers';
import { cn } from '@/lib/utils';
import { useSettings } from '@/providers/settings-provider';
import { Button } from '@/components/ui/button';

// ─── CRM Logo Set ─────────────────────────────────────────────────
const LOGOS = {
  default: '/media/icon/CRMLOGO1.png',
  monogram: '/media/icon/CRMlogo.png',
  defaultDark: '/media/app/default-logo-dark.svg',
  monogramDark: '/media/app/mini-logo.svg',
};

export function SidebarHeader() {
  const { settings, storeOption } = useSettings();
  const isCollapsed = settings.layouts.demo1.sidebarCollapse;

  const handleToggleClick = () => {
    storeOption('layouts.demo1.sidebarCollapse', !isCollapsed);
  };

  return (
    <div className="sidebar-header hidden lg:flex items-center relative justify-between px-4 lg:px-6 py-4 shrink-0">
      <Link to="/" className="shrink-0 flex items-center">
        {/* Light mode */}
        <div className="dark:hidden flex items-center">
          <img
            src={toAbsoluteUrl(LOGOS.default)}
            className="default-logo h-[34px] max-w-none"
            alt="Default Logo"
          />
          <img
            src={toAbsoluteUrl(LOGOS.monogram)}
            className="small-logo h-[34px] max-w-none"
            alt="Mini Logo"
          />
        </div>

        {/* Dark mode */}
        <div className="hidden dark:block">
          <img
            src={toAbsoluteUrl(LOGOS.defaultDark)}
            className="default-logo h-[22px] max-w-none"
            alt="Default Logo Dark"
          />
          <img
            src={toAbsoluteUrl(LOGOS.monogramDark)}
            className="small-logo h-[22px] max-w-none"
            alt="Mini Logo Dark"
          />
        </div>
      </Link>

      <Button
        onClick={handleToggleClick}
        size="sm"
        mode="icon"
        variant="outline"
        className={cn(
          'size-7 absolute start-full top-1/2 rtl:translate-x-2/4 -translate-x-2/4 -translate-y-1/2',
          isCollapsed ? 'ltr:rotate-180' : 'rtl:rotate-180',
        )}
      >
        <ChevronFirst className="size-4!" />
      </Button>
    </div>
  );
}