package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuAllocationItemRawMaterialResponseDto {

	private Long id;
    private Long menuItemId;
    private String menuItemName;
    private Long rawMaterialId;
    private String rawMaterialName;
    private BigDecimal weight;
    private BigDecimal rate;
    private BigDecimal rawmaterial_rate;
    private BigDecimal rawmaterial_weight;
    private String dateTime;
    private Long unitId;
    private String unitName;
    private Long partyId;
    private String partyName;
    private String place;
    private Long eventId;
    private Long eventFunctionId;
    private UnitMasterResponseDto units;
    private UnitHierarchyDto unitHierarchy;
    private Boolean isNewRaw;
    private BigDecimal supRate;
    private Long masterRawUnitId;
    private Boolean isCaptainReceipe;
}
