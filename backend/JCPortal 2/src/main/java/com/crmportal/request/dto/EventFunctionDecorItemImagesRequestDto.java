package com.crmportal.request.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionDecorItemImagesRequestDto {

	private Long eventId;
	private Long eventFunctionId;
	private Long userId;
	private Long decorItemId;
	private List<MultipartFile> images;

}
