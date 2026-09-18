import { ShieldOff } from "lucide-react";

const NoAccess = () => {
  return (
    <div className="flex flex-col items-center justify-center h-[80vh] text-center px-4">
      <ShieldOff className="w-14 h-14 text-gray-300 mb-4" />
      <h2 className="text-lg font-semibold text-gray-700">No Access Assigned</h2>
      <p className="text-sm text-gray-400 mt-1 max-w-sm">
        Your account doesn't have any pages assigned yet. Please contact your
        administrator to get access.
      </p>
    </div>
  );
};

export default NoAccess;