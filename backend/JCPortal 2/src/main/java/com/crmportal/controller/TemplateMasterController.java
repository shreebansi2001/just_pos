package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.NamePlateImagesRequestDto;
import com.crmportal.request.dto.TemplateMasterRequestDto;
import com.crmportal.response.dto.NamePlateImagesResponseDto;
import com.crmportal.response.dto.NamePlateResponseDto;
import com.crmportal.response.dto.TemplateMasterResponseDto;
import com.crmportal.service.NamePlateImagesService;
import com.crmportal.service.TemplateMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/templatemaster" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class TemplateMasterController {

	@Autowired
	TemplateMasterService templateMasterService;

	@Autowired
	NamePlateImagesService namePlateImagesService;
	
	@PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addTemplateMaster(@Valid @ModelAttribute TemplateMasterRequestDto request,
			@RequestParam(value = "menuReportPages", required = false) List<MultipartFile> menuReportPages,
			@RequestParam(value = "dummyPdf", required = false) MultipartFile dummyPdf,
			@RequestParam(value = "namePlateBg", required = false) List<MultipartFile> namePlateBg) {
		Map<String, Object> response = new HashMap<>();
		try {
			TemplateMasterResponseDto responseDto = templateMasterService.addOrUpdateTemplateMaster(request,
					menuReportPages, dummyPdf, namePlateBg, Long.parseLong("-1"));

			if (responseDto == null) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.TEMPLATE_CREATE_FAIL);
			} else {
				response.put("success", true);
				response.put("msg", ConstantsPoc.TEMPLATE_CREATE_SUCCESS);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException re) {
			response.put("success", false);
			response.put("msg", re.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateTemplateMaster(@Valid @ModelAttribute TemplateMasterRequestDto request,
			@RequestParam(value = "menuReportPages", required = false) List<MultipartFile> menuReportPages,
			@RequestParam(value = "dummyPdf", required = false) MultipartFile dummyPdf,
			@RequestParam(value = "namePlateBg", required = false) List<MultipartFile> namePlateBg,
			@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			TemplateMasterResponseDto responseDto = templateMasterService.addOrUpdateTemplateMaster(request,
					menuReportPages, dummyPdf, namePlateBg, id);
			if (responseDto == null) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.TEMPLATE_UPDATE_FAIL);
			} else {
//				response.put("data", responseDto);
				response.put("success", true);
				response.put("msg", ConstantsPoc.TEMPLATE_UPDATE_SUCCESS);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException re) {
			response.put("success", false);
			response.put("msg", re.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getall")
	@ResponseBody
	public ResponseEntity<?> getAllTemplateMaster() {
		Map<String, Object> response = new HashMap<>();
		try {
			List<TemplateMasterResponseDto> responseDto = templateMasterService.getAllTemplateMaster();
			response.put("data", responseDto);
			response.put("msg", ConstantsPoc.TEMPLATE_FOUND_SUCCESS);
			response.put("success", true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException re) {
			response.put("success", false);
			response.put("msg", re.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getbyid")
	@ResponseBody
	public ResponseEntity<?> getTemplateMasterById(Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			TemplateMasterResponseDto responseDto = templateMasterService.getTemplateMasterById(id);

			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.TEMPLATE_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.TEMPLATE_FOUND_FAIL);
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException re) {
			response.put("success", false);
			response.put("msg", re.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/deletebyid")
	@ResponseBody
	public ResponseEntity<?> deleteTemplateMasterById(@RequestParam("id") Long id) {
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isSuccess = templateMasterService.deleteTemplateById(id);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.TEMPLATE_DELETE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.TEMPLATE_DELETE_FAIL);
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updatestatus")
	@ResponseBody
	public ResponseEntity<?> updateTemplateMasterStatusById(@RequestParam("id") Long id,
			@RequestParam("status") Boolean status) {
		Boolean isUpdated = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isUpdated = templateMasterService.updateTemplateMasterStatusById(id, status);
			if (isUpdated) {
				response.put("msg", ConstantsPoc.TEMPLATE_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.TEMPLATE_UPDATE_FAIL);
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getallbymoduleid")
	@ResponseBody
	public ResponseEntity<?> getAllTemplateMasterByModuleId(@RequestParam(value = "moduleId", required = false) Long id,
			@RequestParam(value = "isNameplate", required = false) Boolean isNameplate,@RequestParam(value = "userId", required = false) Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<TemplateMasterResponseDto> responseDto = templateMasterService.getAllTemplateMasterByModuleId(id,
					isNameplate,userId);

			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.TEMPLATE_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.TEMPLATE_FOUND_FAIL);
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getallbytemplatemappingid")
	@ResponseBody
	public ResponseEntity<?> getAllTemplateMasterByTemplateMappingId(@RequestParam("moduleId") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<TemplateMasterResponseDto> responseDto = templateMasterService
					.getAllTemplateMasterByTemplateMappingId(id);

			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.TEMPLATE_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.TEMPLATE_FOUND_FAIL);
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getallbymoduleidanduserid")
	@ResponseBody
	public ResponseEntity<?> getAllTemplateMasterByModuleIdAndUserId(@RequestParam("moduleId") Long moduleId,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<TemplateMasterResponseDto> responseDto = templateMasterService
					.getAllTemplateMasterByModuleIdAndUserId(moduleId, userId);

			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.TEMPLATE_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.TEMPLATE_FOUND_FAIL);
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/add-update-nameplate-images")
	public ResponseEntity<?> addUpdateNamePlateImages(@ModelAttribute NamePlateImagesRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {

			NamePlateImagesResponseDto responseDto = namePlateImagesService.addUpdateNamePlateImages(request);
			
			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.TEMPLATE_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.TEMPLATE_FOUND_FAIL);
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
