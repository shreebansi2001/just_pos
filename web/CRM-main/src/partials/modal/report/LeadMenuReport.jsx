import { useEffect, useState, useRef } from "react";
import { createPortal } from "react-dom";
import { CustomModal } from "../../../components/custom-modal/CustomModal";
import { AddExclusiveReport, GetReportConfiguration } from "@/services/apiServices";
import Swal from "sweetalert2";


const WhatsAppModal = ({ isOpen, onClose, onSend }) => {
  const [name, setName] = useState("");
  const [mobile, setMobile] = useState("");
  const [error, setError] = useState("");

  const handleSend = () => {
    const cleaned = mobile.replace(/\D/g, "");
    if (!cleaned || cleaned.length < 10) {
      setError("Please enter a valid mobile number (min 10 digits).");
      return;
    }
    onSend(`+91${cleaned}`, name.trim());
    setName("");
    setMobile("");
  };

  const handleClose = () => {
    setName("");
    setMobile("");
    setError("");
    onClose();
  };

  if (!isOpen) return null;

  return createPortal(
    <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/50">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm mx-4 overflow-hidden">
        <div className="bg-green-600 px-6 py-4 flex items-center gap-3">
          <h2 className="text-white font-semibold text-lg">Share via WhatsApp</h2>
        </div>
        <div className="px-6 py-5 space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1 uppercase tracking-wide">Name</label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleSend()}
              placeholder="e.g. Rahul"
              className="w-full border-2 border-gray-200 rounded-lg px-3 py-2.5 text-sm outline-none focus:border-green-500 transition"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-gray-700 mb-1 uppercase tracking-wide">Mobile Number</label>
            <div className="flex items-center border-2 border-gray-200 rounded-lg overflow-hidden focus-within:border-green-500 transition">
              <span className="px-3 py-2.5 bg-gray-50 text-gray-500 text-sm border-r border-gray-200">+91</span>
              <input
                type="tel"
                value={mobile}
                maxLength={10}
                onChange={(e) => {
                  setMobile(e.target.value.replace(/\D/g, "").slice(0, 10));
                  setError("");
                }}
                onKeyDown={(e) => e.key === "Enter" && handleSend()}
                placeholder="9876543210"
                className="flex-1 px-3 py-2.5 text-sm outline-none bg-white"
                autoFocus
              />
            </div>
            {error && <p className="text-red-500 text-xs mt-1">{error}</p>}
          </div>
        </div>
        <div className="px-6 pb-5 flex gap-3 justify-end">
          <button onClick={handleClose} className="px-4 py-2 text-sm bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition font-medium">
            Cancel
          </button>
          <button onClick={handleSend} className="px-5 py-2 text-sm bg-green-600 text-white rounded-lg hover:bg-green-700 transition font-medium">
            Send
          </button>
        </div>
      </div>
    </div>,
    document.body,
  );
};

const Toggle = ({ checked, onChange }) => (
  <button
    type="button"
    onClick={onChange}
    className={`relative inline-flex h-6 w-11 items-center rounded-full transition ${checked ? "bg-blue-600" : "bg-gray-300"}`}
  >
    <span className={`inline-block h-5 w-5 transform rounded-full bg-white transition ${checked ? "translate-x-5" : "translate-x-1"}`} />
  </button>
);

const LeadMenuReport = ({
  isModalOpen,
  setIsModalOpen,
  mappingId,
  selectedTemplateId,
  selectedTemplateName,
  moduleId,
  leadAssignId = 0,
  leadSourceId = 0,
  leadStatusId = 0,
  leadPriority = "",
}) => {
  const userId = localStorage.getItem("userId");

  const [reportType, setReportType] = useState(null);
  const [selectedLanguage, setSelectedLanguage] = useState("english");
  const [loading, setLoading] = useState(false);
  const [pdfUrl, setPdfUrl] = useState(null);
  const [options, setOptions] = useState({});
  const [visibleOptions, setVisibleOptions] = useState([]);
  const [showWhatsAppModal, setShowWhatsAppModal] = useState(false);
  const isPrintingRef = useRef(false);

  const optionDisplayLabels = {
    isExtraCharges: "Is Extra Charges",
    isWithPrice: "Is With Price",
    isWithQty: "With Quantity",
    isTermsCond: "Terms & Condition",
  };

  // ── Fetch report config (toggles only) ──────────────────────────────────
  useEffect(() => {
    if (!isModalOpen || !mappingId) return;

    const fetchConfig = async () => {
      try {
        const res = await GetReportConfiguration(mappingId, moduleId);
        const config = res?.data?.data?.[0];
        if (!config) return;

        setReportType(config.type);

        setOptions({
          categorySlogan: config.isCategorySlogan === 0,
          categoryInstruction: config.isCategoryInstruction === 1,
          categoryImage: config.isCategoryImage === 0,
          itemSlogan: config.isItemSlogan === 0,
          itemInstruction: config.isItemInstruction === 1,
          CompanyInfo: config.isCompanyDetails === 0,
          companyLogo: config.isCompanyLogo === 1,
          itemImage: config.isItemImage === 0,
          isCombo: config.isCombo === 0,
          partyDetails: config.isPartyDetails === 1,
          isWithQty: config.isWithQty === 1,
          isExtraCharges: config.isExtraCharges === 1,
          isDoc: config.isDoc === 0,
          isTermsCond: config.isTermsCond === 0,
          isWithPrice: config.isWithPrice === 0,
          isOnePage: config.isOnePage === 0,
        });

        setVisibleOptions(
          Object.entries({
            CompanyInfo: config.isCompanyDetails,
            categorySlogan: config.isCategorySlogan,
            categoryInstruction: config.isCategoryInstruction,
            categoryImage: config.isCategoryImage,
            itemSlogan: config.isItemSlogan,
            itemInstruction: config.isItemInstruction,
            companyLogo: config.isCompanyLogo,
            itemImage: config.isItemImage,
            isCombo: config.isCombo,
            partyDetails: config.isPartyDetails,
            isWithQty: config.isWithQty,
            isWithPrice: config.isWithPrice,
            isExtraCharges: config.isExtraCharges,
            isTermsCond: config.isTermsCond,
            isDoc: config.isDoc,
            isOnePage: config.isOnePage,
          })
            .filter(([_, value]) => value)
            .map(([key]) => key),
        );
      } catch (err) {
        console.error("Config fetch error", err);
      }
    };
    fetchConfig();
  }, [isModalOpen, mappingId, moduleId]);

  // ── Print interception (kept since it's used by the PDF viewer) ────────
  useEffect(() => {
    if (!pdfUrl) return;
    const originalPrint = window.print.bind(window);

    const interceptedPrint = async () => {
      if (isPrintingRef.current) return;
      isPrintingRef.current = true;
      try {
        const response = await fetch(pdfUrl);
        if (!response.ok) throw new Error("Failed to fetch PDF");
        const blob = await response.blob();
        const blobUrl = URL.createObjectURL(blob);

        const iframe = document.createElement("iframe");
        iframe.style.cssText = "position:fixed;top:-9999px;left:-9999px;width:1px;height:1px;opacity:0;pointer-events:none;";
        iframe.src = blobUrl;
        document.body.appendChild(iframe);

        iframe.onload = () => {
          setTimeout(() => {
            try {
              iframe.contentWindow.focus();
              iframe.contentWindow.print();
            } catch {
              window.open(blobUrl, "_blank");
            }
          }, 800);

          const onAfterPrint = () => {
            setTimeout(() => {
              isPrintingRef.current = false;
              document.body.removeChild(iframe);
              URL.revokeObjectURL(blobUrl);
            }, 500);
            window.removeEventListener("afterprint", onAfterPrint);
          };
          window.addEventListener("afterprint", onAfterPrint);
        };

        iframe.onerror = () => {
          window.open(pdfUrl, "_blank");
          isPrintingRef.current = false;
        };
      } catch {
        window.open(pdfUrl, "_blank");
        isPrintingRef.current = false;
      }
    };

    window.print = interceptedPrint;
    return () => { window.print = originalPrint; };
  }, [pdfUrl]);

  const toggleAll = (checked) => {
    setOptions((prev) => {
      const updated = { ...prev };
      visibleOptions.forEach((key) => { updated[key] = checked; });
      return updated;
    });
  };

  const toggleOne = (key) => {
    setOptions((prev) => ({ ...prev, [key]: !prev[key] }));
  };

  const isCheckAll = visibleOptions.length > 0 && visibleOptions.every((key) => options[key]);

 const handleReport = async () => {
 const payload = {
  eventId: 0,
  partyId: -1,
  eventFunctionId: -1,
  eventFunctionIds: [],
  adminTemplateModuleId: selectedTemplateId ?? mappingId ?? 0,
  type: reportType || null,
  userId,
  lang: selectedLanguage === "english" ? 0 : selectedLanguage === "hindi" ? 1 : 2,

  isCategoryImage: options.categoryImage ?? false,
  isCategoryInstruction: options.categoryInstruction ?? false,
  isCategorySlogan: options.categorySlogan ?? false,
  isItemImage: options.itemImage ?? false,
  isCombo: options.isCombo ?? false,
  isItemInstruction: options.itemInstruction ?? false,
  isItemSlogan: options.itemSlogan ?? false,
  isCompanyDetails: options.CompanyInfo ?? false,
  isCompanyLogo: options.companyLogo ?? false,
  isPartyDetails: options.partyDetails ?? false,
  isWithQty: options.isWithQty ?? false,
  isDoc: visibleOptions.includes("isDoc") ? (options.isDoc ?? false) : false,
  pageSize: "",
  isWithPrice: options.isWithPrice ?? false,
  isExtraCharges: options.isExtraCharges ?? false,
  isTermsCond: options.isTermsCond ?? false,
  isOnePage: options.isOnePage ?? false,
  isHalfPax: false,
  is3Column: false,
  isFunctionNextPage: false,
  isAddDecoration: false,
  isShowEventRemarks: false,

  agencyId: [],
  managerIds: [],
  itemId: [],
  rawMaterialCatIds: [],
  catFontId: -1,
  itemFontId: -1,
  sloganFontId: -1,
  catFontSize: -1,
  itemFontSize: -1,
  sloganFontSize: -1,
  startDate: "",
  endDate: "",
  eventStatus: [],

  leadAssignId: "",
  sourceId: 0,
  statusId: 0,
  priority: "All",
  advancePaymentId: 0,
  customPackageId: 0,
};

  if (!payload.adminTemplateModuleId) {
    Swal.fire({ icon: "warning", title: "Missing required data" });
    return;
  }

  const formData = new FormData();
  Object.entries(payload).forEach(([key, value]) => {
    if (Array.isArray(value)) {
      value.forEach((v) => formData.append(`${key}[]`, v));
    } else {
      formData.append(key, value === true ? "1" : value === false ? "0" : (value ?? ""));
    }
  });

  // Open the tab synchronously on click to avoid popup-blocker issues
  const newTab = window.open("", "_blank");

  setLoading(true);
  try {
    const { data } = await AddExclusiveReport(formData);
    if (data?.success && data?.report_path) {
      if (newTab) {
        newTab.location.href = data.report_path;
      } else {
        window.open(data.report_path, "_blank");
      }
      Swal.fire({ icon: "success", title: data?.msg || "Report generated", timer: 1500, showConfirmButton: false });
      setIsModalOpen(false);
    } else {
      newTab?.close();
      Swal.fire({ icon: "error", title: data?.msg || "Failed to generate report" });
    }
  } catch (err) {
    newTab?.close();
    Swal.fire({ icon: "error", title: err?.response?.data?.msg || "Something went wrong" });
  } finally {
    setLoading(false);
  }
};

  const handleClose = () => {
    if (isPrintingRef.current || document.hidden) return;
    setPdfUrl(null);
    setIsModalOpen(false);
  };

  const handleWhatsAppSend = (mobile, recipientName) => {
    const greeting = recipientName || "there";
    const message = `Hi ${greeting},\nPlease find the attached PDF.\n\n${pdfUrl}`;
    window.open(`https://web.whatsapp.com/send?phone=${mobile}&text=${encodeURIComponent(message)}`, "_blank");
    setShowWhatsAppModal(false);
  };

  return (
    <>
      <WhatsAppModal isOpen={showWhatsAppModal} onClose={() => setShowWhatsAppModal(false)} onSend={handleWhatsAppSend} />
  <CustomModal
    open={isModalOpen}
    title={selectedTemplateName || "Report"}
    onClose={() => setIsModalOpen(false)}
    width={900}
    maskClosable={false}
    keyboard={false}
    footer={
      <button
        onClick={handleReport}
        disabled={loading}
        className={`px-6 py-2 text-white rounded transition ${loading ? "bg-gray-400 cursor-not-allowed" : "bg-[#005BA8] hover:bg-[#004a8d]"}`}
      >
        {loading ? "Generating..." : "Generate Report"}
      </button>
    }
  >
    <div className="space-y-6">
      <div>
        <label className="block font-medium mb-2 text-gray-700">Select Language</label>
        <div className="flex border rounded-lg overflow-hidden shadow-sm">
          {["english", "hindi"].map((lang) => (
            <button
              key={lang}
              onClick={() => setSelectedLanguage(lang)}
              className={`flex-1 py-2.5 font-medium transition ${selectedLanguage === lang ? "bg-[#005BA8] text-white" : "bg-white text-gray-700 hover:bg-gray-50"}`}
            >
              {lang.charAt(0).toUpperCase() + lang.slice(1)}
            </button>
          ))}
        </div>
      </div>

      {/* <div className="flex justify-between items-center border-b pb-3 mb-3">
        <span className="font-semibold text-gray-700">Check All Options</span>
        <Toggle checked={isCheckAll} onChange={() => toggleAll(!isCheckAll)} />
      </div> */}
      <div className="space-y-2 max-h-64 overflow-y-auto pr-2">
        {visibleOptions.map((key) => (
          <div key={key} className="flex justify-between items-center py-2 px-3 rounded-lg hover:bg-gray-50 transition">
            <span className="capitalize text-gray-700">
              {optionDisplayLabels[key] || key.replace(/([A-Z])/g, " $1")}
            </span>
            <Toggle checked={options[key]} onChange={() => toggleOne(key)} />
          </div>
        ))}
      </div>
    </div>
  </CustomModal>
);
    </>
  );
};

export default LeadMenuReport;