package com.crmportal.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.CatBgSelectionEntity;
import com.crmportal.repository.CatBgSelectionRepository;
import com.crmportal.request.dto.CatBgSelectionRequestDto;
import com.crmportal.response.dto.CatBgSelectionResponseDto;
import com.crmportal.service.CatBgSelectionService;
import com.crmportal.service.UserFileService;

@Service
public class CatBgSelectionServiceImpl implements CatBgSelectionService {

    @Autowired
    private CatBgSelectionRepository repository;

    @Autowired
    private Environment environment;

    @Autowired
    private UserFileService userFileService;

    // ── Add or Update ─────────────────────────────────────────────────────────
    @Override
    public CatBgSelectionResponseDto addOrUpdate(CatBgSelectionRequestDto request,
            MultipartFile image, Long userId) {

        CatBgSelectionEntity entity;

        if (request.getId() == null || request.getId() == 0 || request.getId() == -1) {
            entity = new CatBgSelectionEntity();
        } else {
            entity = repository.findByIdAndIsDeleteFalse(request.getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Record not found with id: " + request.getId()));
        }

        entity.setCategoryName(request.getCategoryName());
        entity.setIsCatImg(request.getIsCatImg());
        entity.setUserId(userId);
        entity.setIsDelete(false);

        entity = repository.save(entity);

        if (image != null && !image.isEmpty()) {

            Map<String, Object> uploadResponse;
            try {
                uploadResponse = userFileService.storeFile(
                        userId,
                        "CATEGORY_BG",  
                        entity.getId(),
                        "IMAGE",
                        image
                );
            } catch (Exception e) {
                throw new RuntimeException("File upload failed: " + e.getMessage());
            }

            if (!(Boolean) uploadResponse.get("success")) {
                throw new RuntimeException("File upload failed");
            }

            // Extract DB path
            String fullPath = (String) uploadResponse.get("fullPath");
            String baseUrl = environment.getProperty("app.image.url", "");

            String dbPath = fullPath.replace(baseUrl, "");

            entity.setImagePath(dbPath);

            //  STEP 3: Save again with image path
            entity = repository.save(entity);
        }

        return toResponse(entity);
    }

    // ── Get All ───────────────────────────────────────────────────────────────
    @Override
    public List<CatBgSelectionResponseDto> getAll(Long userId, Boolean isCatImg) {

        if (isCatImg != null) {
            return repository.findByUserIdAndIsCatImgAndIsDeleteFalse(userId, isCatImg)
                    .stream().map(this::toResponse).collect(Collectors.toList());
        }

        return repository.findByUserIdAndIsDeleteFalse(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Get By Type ───────────────────────────────────────────────────────────
    @Override
    public List<CatBgSelectionResponseDto> getByType(Long userId, Boolean isCatImg) {

        if (isCatImg == null) {
            return repository.findByUserIdAndIsDeleteFalse(userId)
                    .stream().map(this::toResponse).collect(Collectors.toList());
        }

        return repository.findByUserIdAndIsCatImgAndIsDeleteFalse(userId, isCatImg)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Get By ID ─────────────────────────────────────────────────────────────
    @Override
    public CatBgSelectionResponseDto getById(Long id) {

        CatBgSelectionEntity entity = repository.findByIdAndIsDeleteFalse(id)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));

        return toResponse(entity);
    }

    // ── Delete (Soft Delete) ──────────────────────────────────────────────────
    @Override
    public Boolean delete(Long id) {

        CatBgSelectionEntity entity = repository.findByIdAndIsDeleteFalse(id)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));

        entity.setIsDelete(true);
        repository.save(entity);

        return true;
    }

    // ── Convert entity → response DTO ─────────────────────────────────────────
    private CatBgSelectionResponseDto toResponse(CatBgSelectionEntity entity) {

        CatBgSelectionResponseDto dto = new CatBgSelectionResponseDto();

        dto.setId(entity.getId());
        dto.setCategoryName(entity.getCategoryName());
        dto.setImagePath(entity.getImagePath());
        dto.setIsCatImg(entity.getIsCatImg());

        dto.setCreatedAt(entity.getCreatedAt() != null
                ? entity.getCreatedAt().toString()
                : null);

        // Full URL for frontend
        if (entity.getImagePath() != null) {
            dto.setImageUrl(
                    environment.getProperty("app.image.url", "") + entity.getImagePath()
            );
        }

        return dto;
    }
}