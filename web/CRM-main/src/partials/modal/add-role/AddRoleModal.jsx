import { useState } from "react";
import { FormattedMessage, useIntl } from "react-intl";
import { Tag, X, Loader2, Save } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { CustomModal } from "@/components/custom-modal/CustomModal";
import { Addrole } from "@/services/apiServices";
import Swal from "sweetalert2";

const FieldLabel = ({ children, required }) => (
  <label className="block text-sm font-medium text-gray-700 mb-1.5">
    {children}
    {required && <span className="text-rose-500 ml-0.5">*</span>}
  </label>
);

const AddRoleModal = ({ isModalOpen, setIsModalOpen, selectedEvent, onRoleAdded }) => {
  const intl = useIntl();

  const initialFormState = {
    nameEnglish: "",
  };

  const [formData, setFormData] = useState(initialFormState);
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const validate = () => {
    const newErrors = {};
    if (!formData.nameEnglish.trim()) {
      newErrors.nameEnglish = "Name is required";
    }
    return newErrors;
  };

  const handleModalClose = () => {
    setFormData(initialFormState);
    setErrors({});
    setIsModalOpen(false);
  };

  const handleSubmit = async () => {
    const formErrors = validate();
    if (Object.keys(formErrors).length > 0) {
      setErrors(formErrors);
      return;
    }

    try {
      setSaving(true);
      const userid = localStorage.getItem("userId");

      const payload = {
        name: formData.nameEnglish,
        userId: userid,
      };

      const res = await Addrole(payload);

      if (res?.data?.success === true) {
        Swal.fire({
          icon: "success",
          title: "Role added successfully!",
          timer: 1500,
          showConfirmButton: false,
        });

        handleModalClose();
        onRoleAdded?.();
      } else {
        Swal.fire({
          icon: "error",
          title: "Failed to add role",
          text: res?.data?.message || "Something went wrong",
        });
      }
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: err?.response?.data?.message || "Something went wrong",
      });
    } finally {
      setSaving(false);
    }
  };

  return (
    <CustomModal
      open={isModalOpen}
      onClose={handleModalClose}
      width={620}
      footer={
        <div className="flex items-center justify-end gap-2">
          <Button variant="outline" size="md" onClick={handleModalClose} disabled={saving}>
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
            {selectedEvent ? (
              <FormattedMessage id="COMMON.UPDATE" defaultMessage="Update" />
            ) : (
              <FormattedMessage id="COMMON.SAVE" defaultMessage="Save" />
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
              {selectedEvent ? (
                <FormattedMessage id="USER.MASTER.EDITROLE" defaultMessage="Edit Role" />
              ) : (
                <FormattedMessage
                  id="USER.MASTER.ADDROLE"
                  defaultMessage="Create New Department"
                />
              )}
            </h2>
            <p className="text-sm text-black">
              {selectedEvent
                ? "Update this department's name"
                : "Add a new department for your organization"}
            </p>
          </div>
        </div>
        <button
          type="button"
          onClick={handleModalClose}
          className="w-8 h-8 rounded-full bg-black flex items-center justify-center text-white transition-colors shrink-0"
        >
          <X className="h-4 w-4" />
        </button>
      </div>

      {/* Body */}
      <div className="flex flex-col gap-5">
        <div>
          <FieldLabel required>
            <FormattedMessage id="COMMON.NAME_ENGLISH" defaultMessage="Name (English)" />
          </FieldLabel>
          <div className="relative flex items-center">
            <Tag className="absolute left-3 h-4 w-4 text-primary" />
            <Input
              name="nameEnglish"
              value={formData.nameEnglish}
              onChange={handleChange}
              placeholder={intl.formatMessage({
                id: "USER.MASTER.ENTER_NAME",
                defaultMessage: "Enter department name",
              })}
              className="h-11 pl-9 text-sm border-gray-300"
            />
          </div>
          {errors.nameEnglish && (
            <p className="text-rose-500 text-sm mt-1.5">{errors.nameEnglish}</p>
          )}
          <p className="text-xs text-muted-foreground mt-1.5">
            Enter a clear and concise name for this department.
          </p>
        </div>
      </div>
    </CustomModal>
  );
};

export default AddRoleModal;