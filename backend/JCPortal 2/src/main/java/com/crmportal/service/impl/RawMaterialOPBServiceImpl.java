package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.request.dto.RawMaterialOPBItemRequestDto;
import com.crmportal.request.dto.RawMaterialOPBRequestDto;
import com.crmportal.response.dto.RawMaterialOPBResponseDto;
import com.crmportal.service.RawMaterialOPBService;

@Service
@Transactional
public class RawMaterialOPBServiceImpl implements RawMaterialOPBService {

    @Autowired
    private RawMaterialMasterRepository rawMaterialRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Get all items by category

    @Override
    public Map<String, Object> getByCategory(
            Long categoryId,
            Long userId,
            String search,
            Integer pageNo,
            Integer pageSize) {

        Map<String, Object> response = new HashMap<>();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        Page<RawMaterialMasterEntity> page;

        search = search != null ? search.trim() : "";

        if (categoryId == 0) {

            page = rawMaterialRepository
                    .findByUserIdAndIsDeleteFalseAndNameEnglishContainingIgnoreCase(
                            userId,
                            search,
                            pageable);

        } else {

            page = rawMaterialRepository
                    .findByRawMaterialCatIdAndUserIdAndIsDeleteFalseAndNameEnglishContainingIgnoreCase(
                            categoryId,
                            userId,
                            search,
                            pageable);
        }

        List<RawMaterialOPBResponseDto> list =
                page.getContent()
                        .stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());

        response.put("success", true);
        response.put("msg", "Raw materials fetched successfully");
        response.put("data", list);
        response.put("totalRecords", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("currentPage", pageNo);
        response.put("pageSize", pageSize);

        return response;
    }

    // Save OPB for multiple items at once

    @Override
    public void saveOPB(RawMaterialOPBRequestDto request) {

        for (RawMaterialOPBItemRequestDto item : request.getItems()) {

            RawMaterialMasterEntity entity = rawMaterialRepository
                    .findById(item.getRawMaterialId())
                    .orElseThrow(() -> new RuntimeException(
                            "Raw material not found with ID: " + item.getRawMaterialId()));

            entity.setOpbStock(
                    item.getOpbStock() != null ? item.getOpbStock() : BigDecimal.ZERO);

            entity.setMinStock(
                    item.getMinStock() != null ? item.getMinStock() : BigDecimal.ZERO);

            //  save supplier rate
            entity.setSupplierRate(
                    item.getSupplierRate() != null ? item.getSupplierRate() : BigDecimal.ZERO);

            if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
                entity.setExpiryDate(LocalDate.parse(item.getExpiryDate(), formatter));
            } else {
                entity.setExpiryDate(null);
            }

            rawMaterialRepository.save(entity);
        }
    }
    //  Helper

    private RawMaterialOPBResponseDto convertToDto(RawMaterialMasterEntity entity) {

        RawMaterialOPBResponseDto dto = new RawMaterialOPBResponseDto();

        dto.setRawMaterialId(entity.getId());
        dto.setRawMaterialName(entity.getNameEnglish());

        if (entity.getRawMaterialCat() != null) {
            dto.setCategoryId(entity.getRawMaterialCat().getId());
            dto.setCategoryName(entity.getRawMaterialCat().getNameEnglish());
        }

        if (entity.getUnit() != null) {
            dto.setUnitName(entity.getUnit().getNameEnglish());
        }

        dto.setOpbStock(entity.getOpbStock() != null
                ? entity.getOpbStock() : BigDecimal.ZERO);

        dto.setMinStock(entity.getMinStock() != null
                ? entity.getMinStock() : BigDecimal.ZERO);

        dto.setExpiryDate(entity.getExpiryDate() != null
                ? entity.getExpiryDate().format(formatter) : null);
        
        dto.setCreatedAt(entity.getCreatedAt() != null
                ? entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null);

        dto.setSupplierRate(entity.getSupplierRate() != null
                ? entity.getSupplierRate() : BigDecimal.ZERO);

        return dto;
    }
}