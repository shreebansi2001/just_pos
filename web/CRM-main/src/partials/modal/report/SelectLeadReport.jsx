import { useEffect, useState } from "react";
import { CustomModal } from "@/components/custom-modal/CustomModal";
import { toAbsoluteUrl } from '@/lib/helpers';
import { FileText } from "lucide-react";
import {
  GettemplatebyuserId,
  GetAllCustomThemeByUserIdAndModuleId,
  AddExclusiveReport,
} from "@/services/apiServices";
import Swal from "sweetalert2";

export default function SelectLeadReport({
  open,
  onClose,
  leadAssignId = 0,
  leadSourceId = 0,
  leadStatusId = 0,
  leadPriority = "",
  fromDate = "",
  endDate = "",
}) {
  const [tabs, setTabs] = useState([]);
  const [activeTab, setActiveTab] = useState(null);
  const [templates, setTemplates] = useState([]);
  const [templatesLoading, setTemplatesLoading] = useState(false);
  const [generatingId, setGeneratingId] = useState(null); 

  const userId = localStorage.getItem("userId");
  const activeLang = localStorage.getItem("lang") || "en";

  const getLangValue = (obj, baseKey, lang) => {
    if (!obj) return "";
    switch (lang) {
      case "hi":
        return obj[`${baseKey}Hindi`] || obj[`${baseKey}English`] || "";
      case "gu":
        return obj[`${baseKey}Gujarati`] || obj[`${baseKey}English`] || "";
      default:
        return obj[`${baseKey}English`] || "";
    }
  };

  useEffect(() => {
    if (!open) return;

    setTemplates([]);
    setActiveTab(null);

    const fetchTemplateModules = async () => {
      try {
        const res = await GettemplatebyuserId();
        if (res?.data?.success && res?.data?.data) {
          const modules = res.data.data.filter(
            (m) =>
              m.isActive &&
              !m.isDelete &&
              ["Lead Module"].includes(m.nameEnglish),
          );

          const formatted = modules.map((m) => ({
            key: m.id,
            label: getLangValue(m, "name", activeLang),
            moduleId: m.id,
            img: "/media/icons/simple.png",
            nameEnglish: m.nameEnglish,
          }));

          setTabs(formatted);
          if (formatted.length > 0) setActiveTab(formatted[0].key);
        }
      } catch (err) {
        console.error("Error fetching template modules:", err);
      }
    };

    fetchTemplateModules();
  }, [open]);

  useEffect(() => {
    if (!activeTab) return;

    const fetchTemplates = async () => {
      setTemplates([]);
      try {
        setTemplatesLoading(true);
        const res = await GetAllCustomThemeByUserIdAndModuleId(userId, activeTab);
        let list = [];
        if (res?.data?.success && Array.isArray(res?.data?.data)) {
          list = res.data.data.map((item) => ({
            id: item.id,
            templateMasterId: item.templateMaster.id,
            name: item.templateMaster.name || "",
            description: item.templateMaster.description || "",
            frontPage: item.templateMaster.frontPage,
            mappingId: item.templateMappingResponseDto?.id || item.id,
            catFontId: item.catFontId,
            catFontSize: item.catFontSize,
            itemFontId: item.itemFontId,
            itemFontSize: item.itemFontSize,
            sloganFontId: item.sloganFontId,
            sloganFontSize: item.sloganFontSize,
          }));
        }
        setTemplates(list);
      } catch (err) {
        console.error("Error fetching templates:", err);
        setTemplates([]);
      } finally {
        setTemplatesLoading(false);
      }
    };

    fetchTemplates();
  }, [activeTab, userId]);

  const handleGenerateReport = async (template) => {
    const adminTemplateModuleId = template.id ?? template.mappingId ?? 0;

    if (!adminTemplateModuleId) {
      Swal.fire({ icon: "warning", title: "Missing required data" });
      return;
    }

    const payload = {
      eventId: 0,
      partyId: -1,
      eventFunctionId: -1,
      eventFunctionIds: [],
      adminTemplateModuleId,
      type: null,
      userId,
      lang: activeLang === "hi" ? 1 : activeLang === "gu" ? 2 : 0,

      isCategoryImage: false,
      isCategoryInstruction: false,
      isCategorySlogan: false,
      isItemImage: false,
      isCombo: false,
      isItemInstruction: false,
      isItemSlogan: false,
      isCompanyDetails: false,
      isCompanyLogo: false,
      isPartyDetails: false,
      isWithQty: false,
      isDoc: false,
      pageSize: "",
      isWithPrice: false,
      isExtraCharges: false,
      isTermsCond: false,
      isOnePage: false,
      isHalfPax: false,
      is3Column: false,
      isFunctionNextPage: false,
      isAddDecoration: false,
      isShowEventRemarks: false,

      agencyId: [],
      managerIds: [],
      itemId: [],
      rawMaterialCatIds: [],
      catFontId: template.catFontId ?? -1,
      itemFontId: template.itemFontId ?? -1,
      sloganFontId: template.sloganFontId ?? -1,
      catFontSize: template.catFontSize ?? -1,
      itemFontSize: template.itemFontSize ?? -1,
      sloganFontSize: template.sloganFontSize ?? -1,
      startDate: fromDate || "",
      endDate: endDate || "",
      eventStatus: [],

      leadAssignId: leadAssignId ? leadAssignId : "",
      sourceId: leadSourceId ? leadSourceId : 0,
      statusId: leadStatusId ? leadStatusId : 0,
      priority: leadPriority ? leadPriority : "All",
      advancePaymentId: 0,
      customPackageId: 0,
    };

    const formData = new FormData();
    Object.entries(payload).forEach(([key, value]) => {
      if (Array.isArray(value)) {
        value.forEach((v) => formData.append(`${key}[]`, v));
      } else {
        formData.append(key, value === true ? "1" : value === false ? "0" : (value ?? ""));
      }
    });

    const newTab = window.open("", "_blank");

    setGeneratingId(template.id);
    try {
      const { data } = await AddExclusiveReport(formData);
      if (data?.success && data?.report_path) {
        if (newTab) {
          newTab.location.href = data.report_path;
        } else {
          window.open(data.report_path, "_blank");
        }
        Swal.fire({ icon: "success", title: data?.msg || "Report generated", timer: 1500, showConfirmButton: false });
        onClose();
      } else {
        newTab?.close();
        Swal.fire({ icon: "error", title: data?.msg || "Failed to generate report" });
      }
    } catch (err) {
      newTab?.close();
      Swal.fire({ icon: "error", title: err?.response?.data?.msg || "Something went wrong" });
    } finally {
      setGeneratingId(null);
    }
  };

  return (
    <CustomModal
      open={open}
      onClose={onClose}
      footer={null}
      title={
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-gradient-to-br from-primary to-blue-600 rounded-lg flex items-center justify-center">
            <i className="ki-filled ki-document text-white text-xl"></i>
          </div>
          <span className="text-xl font-bold text-gray-800">Select Report Type</span>
        </div>
      }
      width={1100}
    >
      <div className="p-6">
        {/* Tabs */}
        <div className="flex gap-10 border-b border-gray-200 overflow-x-auto px-2 mb-6">
          {tabs.map((tab) => {
            const isActive = activeTab === tab.key;
            return (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key)}
                className="group relative pb-4 flex items-center gap-3 font-semibold text-lg transition-all flex-shrink-0 whitespace-nowrap"
                style={isActive ? { color: "#005BA8" } : {}}
              >
                <span>{tab.label}</span>
                <span
                  className="absolute left-0 -bottom-[1px] h-[4px] w-full transition-all duration-300"
                  style={{
                    backgroundColor: "#005BA8",
                    opacity: isActive ? 1 : 0,
                  }}
                />
              </button>
            );
          })}
        </div>

        {/* Template cards */}
        {templatesLoading ? (
          <div className="flex justify-center items-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          </div>
        ) : templates.length > 0 ? (
          <div className="space-y-4">
            {templates.map((template) => {
              const isGenerating = generatingId === template.id;
              return (
                <div
                  key={template.id}
                  className="flex items-center gap-6 bg-white rounded-xl border-2 border-gray-200 shadow-sm hover:shadow-md transition-all duration-300"
                >
                  <div className="w-24 h-24 flex-shrink-0 rounded-lg flex items-center justify-center overflow-hidden">
                    <img
                      src={toAbsoluteUrl("/media/icon/Exclusive-Reports.png")}
                      alt={template.name || "Template"}
                      className="w-9 h-9 object-contain"
                    />
                  </div>
                  <div className="flex-1">
                    <h3 className="text-base font-bold text-gray-800">{template.name}</h3>
                    <p className="text-sm text-gray-500 mt-1">{template.description}</p>
                  </div>
                  <div className="flex-shrink-0 pe-3">
                    <button
                      className={`inline-flex items-center gap-2 px-6 h-[50px] w-[200px] text-sm font-medium rounded-full justify-center text-white transition-opacity ${
                        isGenerating ? "bg-gray-400 cursor-not-allowed" : "bg-primary hover:opacity-90"
                      }`}
                      disabled={isGenerating}
                      onClick={(e) => {
                        e.stopPropagation();
                        handleGenerateReport(template);
                      }}
                    >
                      <FileText className="w-4 h-4" />
                      {isGenerating ? "Generating..." : "Generate Report"}
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        ) : (
          <div className="text-center py-12 text-gray-500">
            No templates available for this module
          </div>
        )}
      </div>
    </CustomModal>
  );
}