import { useState } from "react";
import { SelectDropdown } from "@/components/form-components/SelectDropdown";

const FunctionTypeDropdown = ({ value, onChange, ...rest }) => {
  const [selectedCompanies, setSelectedCompanies] = useState(value || []);

  const handleChange = (event) => {
    setSelectedCompanies(event.target.value);
    onChange(event);
  };

  return (
    <SelectDropdown
      value={selectedCompanies}
      onChange={handleChange}
      staticOptions={[
        { label: "Wedding", value: "Wedding" },
        { label: "Pool Party", value: "PoolParty" },
        { label: "Sangeet", value: "Sangeet" },
      ]}
      mode="multiple"
      placeholder={"Please select"}
      {...rest}
    />
  );
};

export default FunctionTypeDropdown;
