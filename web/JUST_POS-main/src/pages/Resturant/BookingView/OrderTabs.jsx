import { useNavigate, useLocation } from "react-router-dom";
import { LayoutGrid, ListOrdered } from "lucide-react";

export default function OrderTabs() {
  const navigate = useNavigate();
  const location = useLocation();
  const isBookOrder = location.pathname === "/order-booking" || location.pathname === "/ordertaking";
  const isLiveOrder = location.pathname === "/order/live-order";

  return (
    <div className="flex w-full items-center gap-8 border-b border-slate-200 mb-4">
      {/* <button
        onClick={() => navigate("/order-booking")}
        className={`flex items-center gap-2 pb-3 text-lg font-semibold border-b-[3px] transition cursor-pointer ${
          isBookOrder
            ? "text-primary border-primary"
            : "text-slate-500 border-transparent hover:text-slate-700"
        }`}
      >
        <LayoutGrid className="w-5 h-5" />
        Book Order
      </button> */}

      <button
        onClick={() => navigate("/order/live-order")}
        className={`flex items-center gap-2 pb-3 text-lg font-semibold border-b-[3px] transition cursor-pointer ${isLiveOrder
            ? "text-primary border-primary"
            : "text-slate-500 border-transparent hover:text-slate-700"
          }`}
      >
        <ListOrdered className="w-5 h-5" />
        Live Order
      </button>
    </div>
  );
}