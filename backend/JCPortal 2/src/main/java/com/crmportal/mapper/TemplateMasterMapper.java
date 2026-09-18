package com.crmportal.mapper;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.crmportal.entity.TemplateMasterEntity;
import com.crmportal.response.dto.TemplateMasterResponseDto;

@Mapper(
		   componentModel = "spring",
		   nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
		   nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
		)
public interface TemplateMasterMapper {

	@Mapping(target = "createdAt", dateFormat = "dd-MM-yyyy")
    @Mapping(target = "templateModuleMaster.createdAt", dateFormat = "dd-MM-yyyy")
    TemplateMasterResponseDto entityToResponse(
            TemplateMasterEntity entity,
            @Context String baseUrl
    );

    List<TemplateMasterResponseDto> entityToResponse(
            List<TemplateMasterEntity> entities,
            @Context String baseUrl
    );

    @AfterMapping
    default void applyBaseUrl(
            TemplateMasterEntity entity,
            @MappingTarget TemplateMasterResponseDto dto,
            @Context String baseUrl
    ) {
        dto.setDummyPdf(prefix(baseUrl, entity.getDummyPdf()));
        dto.setFrontPage(prefix(baseUrl, entity.getFrontPage()));
        dto.setSecondFrontPage(prefix(baseUrl, entity.getSecondFrontPage()));
        dto.setWatermark(prefix(baseUrl, entity.getWatermark()));
        dto.setLastMainPage(prefix(baseUrl, entity.getLastMainPage()));
        dto.setCatBgPage(prefix(baseUrl, entity.getCatBgPage()));
        dto.setExtraPage(prefix(baseUrl, entity.getExtraPage()));
        dto.setNamePlateBg(prefix(baseUrl, entity.getNamePlateBg()));
        dto.setNamePlateCoverBg(prefix(baseUrl, entity.getNamePlateCoverBg()));
    }

    default String prefix(String baseUrl, String value) {
        return value == null ? null : value;
    }
}
