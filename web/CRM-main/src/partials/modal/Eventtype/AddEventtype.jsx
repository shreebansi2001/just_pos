import { useState, useEffect } from "react";
import { EditEventType, Addeventtype, Translateapi } from "@/services/apiServices";
import Swal from "sweetalert2";
import { FormattedMessage } from "react-intl";
import { CustomModal } from "../../../components/custom-modal/CustomModal";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Loader2, Save, Tag, X } from "lucide-react";

const AddEventtype = ({
  isModalOpen,
  setIsModalOpen,
  refreshData = () => {},
  selectedEvent,
}) => {
  const [formData, setFormData] = useState({
    nameEnglish: "",
    nameGujarati: "",
    nameHindi: "",
  });
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);
  const [debounceTimer, setDebounceTimer] = useState(null);

  const isEditing = Boolean(selectedEvent);

  useEffect(() => {
    if (!isModalOpen) return;
    if (selectedEvent) {
      setFormData({
        nameEnglish: selectedEvent.nameEnglish || selectedEvent.event_type || "",
        nameGujarati: selectedEvent.nameGujarati || "",
        nameHindi: selectedEvent.nameHindi || "",
      });
    } else {
      setFormData({ nameEnglish: "", nameGujarati: "", nameHindi: "" });
    }
    setErrors({});
  }, [isModalOpen, selectedEvent]);

  const triggerTranslate = (text) => {
    if (!text?.trim()) return;
    if (debounceTimer) clearTimeout(debounceTimer);
    const timer = setTimeout(() => {
      Translateapi(text)
        .then((res) => {
    
          const data = res.data;
          setFormData((prev) => ({
            ...prev,
            nameGujarati: data?.regional || data?.gujarati || "",
            nameHindi: data?.hindi || "",
          }));
        })
        .catch((err) => console.error("Translation error:", err));
    }, 500);
    setDebounceTimer(timer);
  };

  const handleEnglishChange = (e) => {
    const val = e.target.value;
    setFormData((prev) => ({ ...prev, nameEnglish: val }));
    triggerTranslate(val);
  };

  const handleSubmit = async () => {
    if (!formData.nameEnglish.trim()) {
      setErrors({ nameEnglish: "Name is required" });
      return;
    }
    setErrors({});

    const Id = localStorage.getItem("mainId");
    if (!Id) {
      Swal.fire("Error", "User data not found", "error");
      return;
    }

    const payload = { ...formData, userId: Id };

    try {
      setSaving(true);
      if (isEditing) {
        const res = await EditEventType(selectedEvent.eventid, payload);
        if (res?.data.success === false) {
          Swal.fire("Error", res.data.msg || "Something went wrong", "error");
          return;
        }
        Swal.fire({ icon: "success", title: "Event updated successfully!", timer: 1500, showConfirmButton: false });
        setIsModalOpen(false);
        refreshData(false);
      } else {
        const res = await Addeventtype(payload);
        if (res?.data.success === false) {
          Swal.fire("Error", res.data.msg || "Something went wrong", "error");
          return;
        }
        Swal.fire({ icon: "success", title: "Event added successfully!", timer: 1500, showConfirmButton: false });
        setIsModalOpen(false);
        refreshData(true);
      }
    } catch (err) {
      console.error("Submit Error:", err);
      Swal.fire("Error", "Something went wrong", "error");
    } finally {
      setSaving(false);
    }
  };

  const FieldLabel = ({ children, required }) => (
    <label className="block text-sm font-medium text-gray-700 mb-1.5">
      {children}
      {required && <span className="text-rose-500 ml-0.5">*</span>}
    </label>
  );

  return (
    <CustomModal
      open={isModalOpen}
      onClose={() => setIsModalOpen(false)}
      width={620}
      footer={
        <div className="flex items-center justify-end gap-2">
          <Button variant="outline" size="md" onClick={() => setIsModalOpen(false)} disabled={saving}>
            <FormattedMessage id="COMMON.CANCEL" defaultMessage="Cancel" />
          </Button>
          <Button
            size="md"
            className="bg-primary hover:bg-primary/90"
            onClick={handleSubmit}
            disabled={saving}
          >
            {saving ? (
              <Loader2 className="w-4 h-4 mr-2 animate-spin" />
            ) : (
              <Save className="w-4 h-4 mr-2" />
            )}
            {isEditing ? (
              <FormattedMessage id="COMMON.UPDATE" defaultMessage="Update Event" />
            ) : (
              <FormattedMessage id="COMMON.SAVE" defaultMessage="Save Event" />
            )}
          </Button>
        </div>
      }
    >
      {/* Header */}
      <div className="-mx-6 -mt-6 mb-5 rounded-t-2xl px-6 py-5 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-11 h-11 rounded-xl bg-primary flex items-center justify-center shrink-0">
            <Tag className="h-5 w-5 text-white" />
          </div>
          <div>
            <h2 className="text-lg font-semibold text-black leading-tight">
              {isEditing ? (
                <FormattedMessage id="USER.MASTER.EDIT_EVENT_TYPE" defaultMessage="Edit Event" />
              ) : (
                <FormattedMessage id="USER.MASTER.ADD_EVENT_TYPE" defaultMessage="Add Event Type" />
              )}
            </h2>
            <p className="text-sm text-black">
              {isEditing ? "Update this event type" : "Create a new event type"}
            </p>
          </div>
        </div>
        <button
          type="button"
          onClick={() => setIsModalOpen(false)}
          className="w-8 h-8 rounded-full bg-black flex items-center justify-center text-white shrink-0"
        >
          <X className="h-4 w-4" />
        </button>
      </div>

      {/* Body */}
      <div className="flex flex-col gap-5">
        {/* English */}
        <div>
          <FieldLabel required>
            <FormattedMessage id="COMMON.NAME" defaultMessage="Name" /> (English)
          </FieldLabel>
          <div className="relative flex items-center">
            <Tag className="absolute left-3 h-4 w-4 text-primary" />
            <Input
              value={formData.nameEnglish}
              onChange={handleEnglishChange}
              placeholder="Enter event type in English"
              className="h-11 pl-9 text-sm border-gray-300"
            />
          </div>
          {errors.nameEnglish && (
            <p className="text-rose-500 text-xs mt-1">{errors.nameEnglish}</p>
          )}
          <p className="text-xs text-muted-foreground mt-1.5">
            Gujarati and Hindi will be auto-translated.
          </p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {/* Gujarati */}
          <div>
            <FieldLabel>
              <FormattedMessage id="COMMON.NAME" defaultMessage="Name" /> (Gujarati)
            </FieldLabel>
            <Input
              value={formData.nameGujarati}
              onChange={(e) =>
                setFormData((prev) => ({ ...prev, nameGujarati: e.target.value }))
              }
              placeholder="ગુજરાતીમાં નામ"
              className="h-11 text-sm border-gray-300"
            />
          </div>

          {/* Hindi */}
          <div>
            <FieldLabel>
              <FormattedMessage id="COMMON.NAME" defaultMessage="Name" /> (Hindi)
            </FieldLabel>
            <Input
              value={formData.nameHindi}
              onChange={(e) =>
                setFormData((prev) => ({ ...prev, nameHindi: e.target.value }))
              }
              placeholder="हिंदी में नाम"
              className="h-11 text-sm border-gray-300"
            />
          </div>
        </div>
      </div>
    </CustomModal>
  );
};

export default AddEventtype;