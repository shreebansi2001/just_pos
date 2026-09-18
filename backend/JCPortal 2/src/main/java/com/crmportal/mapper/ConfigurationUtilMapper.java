package com.crmportal.mapper;

import com.crmportal.entity.ConfigurationUtilEntity;
import com.crmportal.request.dto.ConfigurationUtilDto;

public class ConfigurationUtilMapper {

    public static ConfigurationUtilEntity toEntity(ConfigurationUtilDto dto) {

        if (dto == null) {
            return null;
        }

        ConfigurationUtilEntity entity = new ConfigurationUtilEntity();

        entity.setUser(dto.getUser());
        entity.setCounterNamePlate(dto.getCounterNamePlate());
        entity.setDateFormat(dto.getDateFormat());          
        entity.setTimeZone(dto.getTimeZone());              
        entity.setTimeFormat(dto.getTimeFormat());          
        entity.setPageSize(dto.getPageSize());
        entity.setTwoLanguageDefault(dto.getTwoLanguageDefault());
        entity.setTwoLanguagePreferred(dto.getTwoLanguagePreferred());
        entity.setChoiceOfMenu(dto.getChoiceOfMenu());
        entity.setDirectShare(dto.getDirectShare());
        entity.setSacNumber(dto.getSacNumber());

        entity.setDisplayMaxPerson(dto.getDisplayMaxPerson());
        entity.setDisplayAutoTime(dto.getDisplayAutoTime());
        entity.setTotalRawMaterialReport(dto.getTotalRawMaterialReport());
        entity.setEditRawmaterialQuantityBeforeGenReport(dto.getEditRawmaterialQuantityBeforeGenReport());

        entity.setFontColor(dto.getFontColor());
        entity.setBgColor(dto.getBgColor());
        entity.setCombineReportConfiguration(dto.getCombineReportConfiguration());
        entity.setCatFontId(dto.getCatFontId());
        entity.setItemFontId(dto.getItemFontId());
        entity.setSloganFontId(dto.getSloganFontId());
        entity.setCatFontSize(dto.getCatFontSize());
        entity.setItemFontSize(dto.getItemFontSize());
        entity.setSloganFontSize(dto.getSloganFontSize());
        return entity;
    }
}