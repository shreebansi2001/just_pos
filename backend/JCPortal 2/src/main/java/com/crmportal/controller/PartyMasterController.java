package com.crmportal.controller;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.PartyMasterRequestDto;
import com.crmportal.response.ApiResponse;
import com.crmportal.response.dto.PartyMasterResponseDto;
import com.crmportal.service.PartyMasterService;
import com.crmportal.utility.ConstantsPoc;
import org.springframework.http.MediaType;

import java.io.File;
import java.nio.file.Files;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
@RestController
@RequestMapping({ "/v1/api/partymaster" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class PartyMasterController {

	@Autowired
	PartyMasterService partyMasterService;
	
	@Autowired
	Environment environment;

	@PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addPartyMaster(@Valid @ModelAttribute PartyMasterRequestDto request,
			BindingResult bindingResult,
			@RequestParam(value = "file", required = false) MultipartFile file) {

		Map<String, Object> response = new HashMap<>();
		
		// Validation errors
	    if (bindingResult.hasErrors()) {
	    	FieldError mobileError = bindingResult.getFieldError("mobileno");

	    	if (mobileError != null) {
	            response.put("success", false);
	            response.put("msg", mobileError.getDefaultMessage());

	            return ResponseEntity
	                    .status(HttpStatus.BAD_REQUEST)
	                    .body(response);
	        }
	    }
	    
		try {
			PartyMasterResponseDto responseDto = partyMasterService.addOrUpdatePartyMaster(request, -1L, file);

			if (responseDto != null) {
				response.put("msg", ConstantsPoc.PARTY_MASTER_CREATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.PARTY_MASTER_CREATE_FAIL);
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (DataIntegrityViolationException e) {

			response.put("success", false);

			if (e.getMostSpecificCause() != null
					&& e.getMostSpecificCause().getMessage().toLowerCase().contains("mobileno")) {
				response.put("msg", "Mobile number already exists");
			} else {
				response.put("msg", "Duplicate data not allowed");
			}

			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

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

	@PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addPartyMaster(@Valid @ModelAttribute PartyMasterRequestDto request,
			BindingResult bindingResult,
			@RequestParam("id") Long id, @RequestParam(value = "file", required = false) MultipartFile file) {

		Map<String, Object> response = new HashMap<>();
		try {
			// Validation errors
		    if (bindingResult.hasErrors()) {
		    	FieldError mobileError = bindingResult.getFieldError("mobileno");

		    	if (mobileError != null) {
		            response.put("success", false);
		            response.put("msg", mobileError.getDefaultMessage());

		            return ResponseEntity
		                    .status(HttpStatus.BAD_REQUEST)
		                    .body(response);
		        }
		    }

			PartyMasterResponseDto responseDto = partyMasterService.addOrUpdatePartyMaster(request, id, file);

			if (responseDto != null) {
				response.put("ModuleId", responseDto.getId());
				response.put("ModuleName", "Party");
				response.put("FileType", "Img");
				response.put("msg", ConstantsPoc.PARTY_MASTER_UPADTE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.PARTY_MASTER_UPADTE_FAIL);
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

//	@GetMapping("/getall")
//	@ResponseBody
//	public ResponseEntity<Map<String, Object>> getAllPartyMaster() {
//		Map<String, Object> response = new HashMap<>();
//		try {
//			List<PartyMasterResponseDto> partyMasterResponseDtos = partyMasterService.getAllPartyMaster();
//
//			if (partyMasterResponseDtos.isEmpty()) {
//				response.put("msg", ConstantsPoc.PARTY_MASTER_NOT_FOUND);
//				response.put("success", false);
//			} else {
//				Map<String, Object> partyResp = new HashMap<>();
//				partyResp.put("Party Details", partyMasterResponseDtos);
//				response.put("data", partyResp);
//				response.put("msg", ConstantsPoc.PARTY_MASTER_FOUND_SUCCESS);
//				response.put("success", true);
//			}
//
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		} catch (RuntimeException e) {
//			response.put("success", false);
//			response.put("msg", e.getMessage());
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			response.put("success", false);
//			response.put("msg", e.getMessage());
//			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}

	@GetMapping("/getbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getPartyMasterById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			PartyMasterResponseDto partyMasterResponseDto = partyMasterService.getPartyMasterById(id);
			List<PartyMasterResponseDto> partyMasterResponseDtos = new ArrayList<>();
			if (partyMasterResponseDto == null) {
				response.put("msg", ConstantsPoc.PARTY_MASTER_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> partyResp = new HashMap<>();
				partyMasterResponseDtos.add(partyMasterResponseDto);
				partyResp.put("Party Details", partyMasterResponseDtos);
				response.put("data", partyResp);
				response.put("msg", ConstantsPoc.PARTY_MASTER_FOUND_SUCCESS);
				response.put("success", true);
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

	@GetMapping("/getallbyuserid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllPartyMasterByUserId(@RequestParam("userId") Long userId,
			@RequestParam(value = "partyName", required = false) String partyName) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<PartyMasterResponseDto> partyMasterResponseDtos = partyMasterService.getAllPartyMasterByUserId(userId,
					partyName);

			if (partyMasterResponseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.PARTY_MASTER_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> partyResp = new HashMap<>();
				partyResp.put("Party Details", partyMasterResponseDtos);
				response.put("data", partyResp);
				response.put("msg", ConstantsPoc.PARTY_MASTER_FOUND_SUCCESS);
				response.put("success", true);
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

	@DeleteMapping("/deletebyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteById(@RequestParam("id") Long id) {
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isSuccess = partyMasterService.deleteById(id);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.PARTY_MASTER_DELETE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.PARTY_MASTER_DELETE_FAIL);
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

//	@GetMapping("/getpartynamewithsearch")
//	@ResponseBody
//	public ResponseEntity<Map<String, Object>> getPartyNameWithSearch(@RequestParam("partyName") String partyName,@RequestParam("userId") Long userId) {
//	    Map<String, Object> response = new HashMap<>();
//	    try {
//	        List<PartyMasterResponseDto> partyMasterResponseDtos = partyMasterService.getPartyNameWithSearch(partyName,userId);
//
//	        if (partyMasterResponseDtos.isEmpty()) {
//	            response.put("msg", "Party not found");
//	            response.put("success", false);
//	        } else {
//	            Map<String, Object> partyResp = new HashMap<>();
//	            partyResp.put("Party Details", partyMasterResponseDtos);
//	            response.put("data", partyResp);
//	            response.put("msg", "Party found successfully");
//	            response.put("success", true);
//	        }
//	        return new ResponseEntity<>(response, HttpStatus.OK);
//
//	    } catch (RuntimeException e) {
//	        response.put("success", false);
//	        response.put("msg", e.getMessage());
//	        return new ResponseEntity<>(response, HttpStatus.OK);
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        response.put("success", false);
//	        response.put("msg", e.getMessage());
//	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//	    }
//	}

	@GetMapping("/getallbycattypeid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllPartyMasterByCatTypeId(@RequestParam("catTypeId") Long catTypeId,
			@RequestParam("userId") Long userId,
			@RequestParam(value = "partyName", required = false) String partyName) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<PartyMasterResponseDto> responseDtos = partyMasterService.getAllPartyMasterByCatTypeId(catTypeId,
					partyName, userId);
			if (responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.PARTY_MASTER_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> partyResp = new HashMap<>();
				partyResp.put("Party Details", responseDtos);
				response.put("data", partyResp);
				response.put("msg", ConstantsPoc.PARTY_MASTER_FOUND_SUCCESS);
				response.put("success", true);
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

	@GetMapping("/getallbycontcatid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllPartyMasterByContCatId(@RequestParam("contCatId") Long contCatId,
			@RequestParam("userId") Long userId,
			@RequestParam(value = "partyName", required = false) String partyName) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<PartyMasterResponseDto> responseDtos = partyMasterService.getAllPartyMasterByContCatId(contCatId,
					partyName, userId);
			if (responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.PARTY_MASTER_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> partyResp = new HashMap<>();
				partyResp.put("Party Details", responseDtos);
				response.put("data", partyResp);
				response.put("msg", ConstantsPoc.PARTY_MASTER_FOUND_SUCCESS);
				response.put("success", true);
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
	
	@GetMapping("/report/pdf")
	public ResponseEntity<Map<String, Object>> generatePdfReport(
	        @RequestParam("type") String type,
	        @RequestParam("userId") Long userId,
	        HttpServletRequest request) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        byte[] pdf = partyMasterService.generatePartyReportPdf(type, userId);

	        String rootPath = request.getSession().getServletContext().getRealPath("/");
	        String folderName = String.valueOf(userId);

	        File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");
	        if (!dir.exists()) dir.mkdirs();

	        String fileName = "party-report-" + System.currentTimeMillis() + ".pdf";
	        File file = new File(dir, fileName);
	        Files.write(file.toPath(), pdf);

	        String fileUrl = environment.getProperty("ws_image_path")
	                + "/api/download/pdf/" + folderName + "/" + fileName;

	        response.put("success", true);
	        response.put("msg", "PDF generated successfully");
	        response.put("fileUrl", fileUrl);

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("msg", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}

	@GetMapping("/report/excel")
	public ResponseEntity<Map<String, Object>> generateExcelReport(
	        @RequestParam("type") String type,
	        @RequestParam("userId") Long userId,
	        HttpServletRequest request) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        byte[] excel = partyMasterService.generatePartyReportExcel(type, userId);

	        String rootPath = request.getSession().getServletContext().getRealPath("/");
	        String folderName = String.valueOf(userId);

	        File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");
	        if (!dir.exists()) dir.mkdirs();

	        String fileName = "party-report-" + System.currentTimeMillis() + ".xlsx";
	        File file = new File(dir, fileName);
	        Files.write(file.toPath(), excel);

	        String fileUrl = environment.getProperty("ws_image_path") + "/api/download/excel/" + folderName + "/"
					+ fileName;

	        response.put("success", true);
	        response.put("msg", "Excel generated successfully");
	        response.put("fileUrl", fileUrl);

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("msg", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
	@PutMapping("/create-or-get")
	public ResponseEntity<ApiResponse<Long>> getOrCreatePartyId(@RequestParam("leadId") Long leadId){
		Long partyId = partyMasterService.getOrCreatePartyId(leadId);
		return new ResponseEntity<>(ApiResponse.success("Party Id Fetched successfully", partyId, 200), HttpStatus.OK);
	}

}
