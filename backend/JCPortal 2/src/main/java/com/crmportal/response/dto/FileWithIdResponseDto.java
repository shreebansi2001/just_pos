package com.crmportal.response.dto;

import org.springframework.web.multipart.MultipartFile;

import com.crmportal.enums.FileType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileWithIdResponseDto {

	private Long fileId;
    private String file;
    private String fileType;
}
