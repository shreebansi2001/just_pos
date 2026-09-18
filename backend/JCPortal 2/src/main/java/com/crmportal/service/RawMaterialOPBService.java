package com.crmportal.service;

import java.util.Map;

import com.crmportal.request.dto.RawMaterialOPBRequestDto;

public interface RawMaterialOPBService {

    Map<String, Object> getByCategory(Long categoryId, Long userId, String search, Integer pageNo, Integer pageSize);

    void saveOPB(RawMaterialOPBRequestDto request);

}