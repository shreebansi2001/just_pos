import { Outlet } from "react-router-dom";
import { Card, CardContent } from "@/components/ui/card";

export function AuthLayout() {
  return (
    <div className="min-h-screen  flex items-center justify-center ">
      <div className="w-full max-w-7xl flex flex-col lg:flex-row gap-20 items-center">

        {/* Left Side */}
        <div className="hidden lg:flex flex-1 flex-col">
          <h1 className="text-5xl font-bold text-gray-900">
            Welcome to{" "}
            <span className="text-primary">
              CRM
            </span>
          </h1>

          <p className="text-gray-500 mt-3 text-lg">
            One platform. Total control. Complete automation.
          </p>

          <div className="my-10">
           <img
  src="/media/icon/imageforsignupcrm.png"
  alt="Just Catering Pro"
  className="w-full max-w-3xl"
/>
          </div>

          <p className="text-center text-gray-500 max-w-2xl">
            Streamline your kitchen operations, manage orders efficiently,
            and grow your hospitality business with our{" "}
            <span className="text-primary font-semibold">
              all-in-one digital solution.
            </span>
          </p>
        </div>

        {/* Right Side */}
        <div className="w-full lg:w-[480px]">
          <Card className="shadow-xl border rounded-2xl">
            <CardContent className="p-8">
              <Outlet />
            </CardContent>
          </Card>
        </div>

      </div>
    </div>
  );
}