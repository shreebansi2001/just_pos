import { useEffect } from "react";
import { createPortal } from "react-dom";
import { X } from "lucide-react";

export const CustomModal = ({
  open,
  onClose,
  children,
  title,
  footer,
  width = 600,
}) => {
  useEffect(() => {
    if (open) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }
    return () => {
      document.body.style.overflow = "";
    };
  }, [open]);

  if (!open) return null;

  return createPortal(
    <div className="fixed inset-0 flex items-center justify-center" style={{ zIndex: 999999 }}>
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-black/50 backdrop-blur-sm"
        onClick={onClose}
      />

      {/* Modal */}
      <div
        className="relative bg-white rounded-2xl shadow-2xl max-h-[90vh] overflow-y-auto"
        style={{ width, zIndex: 999999 }}
        onClick={(e) => e.stopPropagation()}
      >
        {title && (
          <div className="flex items-center justify-between p-5 border-b">
            <h2 className="text-lg font-semibold">{title}</h2>
            <button onClick={onClose} className="p-1 rounded-md hover:bg-gray-100">
              <X size={18} />
            </button>
          </div>
        )}

        <div className="p-6">{children}</div>

        {footer && (
          <div className="border-t p-4 flex justify-end">
            {footer}
          </div>
        )}
      </div>
    </div>,
    document.body
  );
};