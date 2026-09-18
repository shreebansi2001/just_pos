package com.crmportal.request.dto;

import org.springframework.web.multipart.MultipartFile;

import com.crmportal.enums.FileType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileWithIdRequestDto {

	private Long fileId;
    private MultipartFile file;
    private String fileType;
}
