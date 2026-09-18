package com.crmportal.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.crmportal.request.dto.BanquetHallRequestDto;
import com.crmportal.response.dto.BanquetHallResponseDto;

public interface BanquetHallMasterService {

    BanquetHallResponseDto addOrUpdate(BanquetHallRequestDto request,
                                       List<MultipartFile> images);

    List<BanquetHallResponseDto> getAll(Long userId);

    BanquetHallResponseDto getById(Long id, Long userId);

    Boolean delete(Long id, Long userId);

    Boolean deleteImage(Long imageId);

    // Verify password for a hall
    Boolean verifyPassword(String hallName, String rawPassword);
    
    BanquetHallResponseDto toggleStatus(Long id);
}