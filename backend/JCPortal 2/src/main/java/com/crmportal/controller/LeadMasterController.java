package com.crmportal.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.entity.LeadMasterEntity;
import com.crmportal.mapper.LeadMasterMapper;
import com.crmportal.request.dto.FollowUpDetailsRequestDto;
import com.crmportal.request.dto.LeadMasterRequestDto;
import com.crmportal.request.dto.LeadMasterResponseDto;
import com.crmportal.response.dto.AssignMemberResponseDTO;
import com.crmportal.response.dto.FollowUpDetailsResponseDto;
import com.crmportal.response.dto.FollowUpResponseDto;
import com.crmportal.service.LeadMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/leadmaster" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class LeadMasterController {

	@Autowired
	LeadMasterService leadMasterService;

	@GetMapping("/generateLeadCode")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getLeadCode(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();

		try {
			String leadCode = leadMasterService.generateNewLeadCode(userId);

			if (leadCode != null) {
				response.put("data", leadCode);
				response.put("msg", "Lead code generated successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Lead code generation failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addLeadMaster(@Valid @RequestBody LeadMasterRequestDto request) {
		Map<String, Object> response = new HashMap<>();

		try {
			LeadMasterResponseDto leadMasterResponseDto = leadMasterService.addOrUpdateLeadMaster(request,
					Long.valueOf(-1));

			if (leadMasterResponseDto != null) {
				response.put("data", leadMasterResponseDto);
				response.put("msg", ConstantsPoc.LEAD_MASTER_CREATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.LEAD_MASTER_CREATE_FAIL);
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addLeadMaster(@Valid @RequestBody LeadMasterRequestDto request,
			@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			LeadMasterResponseDto leadMasterResponseDto = leadMasterService.addOrUpdateLeadMaster(request, id);

			if (leadMasterResponseDto != null) {
				response.put("msg", ConstantsPoc.LEAD_MASTER_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.LEAD_MASTER_UPDATE_FAIL);
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/changeleadstage")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> changeLeadStage(@RequestParam Long leadId,
			@RequestParam String stageType, @RequestParam(required = false) Long assignId, @RequestParam Long stageId,
			@RequestParam(name = "remark", required = false) String remark,
			@RequestBody(required = false) FollowUpDetailsRequestDto requestDto) {
		Map<String, Object> response = new HashMap<>();

		try {
			Boolean isChanges = leadMasterService.changeLeadStage(leadId, stageType, stageId, remark, requestDto,
					assignId);

			if (isChanges != null) {
				response.put("msg", "Lead stage changed successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Lead stage is not changed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getById")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getLeadById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			LeadMasterResponseDto leadMasterResponseDto = leadMasterService.getLeadById(id);
			List<LeadMasterResponseDto> leadMasterResponseDtos = new ArrayList<>();
			if (leadMasterResponseDto == null) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.LEAD_MASTER_NOT_FOUND);
			} else {
				Map<String, Object> responseDto = new HashMap<>();
				leadMasterResponseDtos.add(leadMasterResponseDto);
				responseDto.put("Lead Details", leadMasterResponseDtos);
				response.put("data", leadMasterResponseDtos);
				response.put("success", true);
				response.put("msg", ConstantsPoc.LEAD_MASTER_FOUND_SUCCESS);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/deleteById")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteLeadById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = leadMasterService.deleteLeadById(id);
			if (isSuccess) {
				response.put("success", true);
				response.put("msg", ConstantsPoc.LEAD_MASTER_DELETE_SUCCESS);
			} else {
				response.put("success", false);
				response.put("msg", ConstantsPoc.LEAD_MASTER_DELETE_FAIL);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAll")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllLeads(@RequestParam("AssignId") Long AssignId,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Map<String, Object> responseDtos = leadMasterService.getAllLeades(AssignId, userId);
			if (responseDtos != null) {
				response.put("data", responseDtos);
				response.put("success", true);
				response.put("msg", ConstantsPoc.LEAD_MASTER_FOUND_SUCCESS);
			} else {
				response.put("success", false);
				response.put("msg", ConstantsPoc.LEAD_MASTER_NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getbyleadtype")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getLeadsByLeadType(@RequestParam("leadType") String leadType,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<LeadMasterResponseDto> responseDtos = leadMasterService.getLeadesByLeadType(leadType, userId);
			if (responseDtos != null) {
				response.put("data", responseDtos);
				response.put("success", true);
				response.put("msg", ConstantsPoc.LEAD_MASTER_FOUND_SUCCESS);
			} else {
				response.put("success", false);
				response.put("msg", ConstantsPoc.LEAD_MASTER_NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getbyleadstatus")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getLeadsByLeadStatus(@RequestParam("leadStatus") String leadStatus,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<LeadMasterResponseDto> responseDtos = leadMasterService.getLeadesByLeadStatus(leadStatus, userId);
			if (responseDtos != null) {
				response.put("data", responseDtos);
				response.put("success", true);
				response.put("msg", ConstantsPoc.LEAD_MASTER_FOUND_SUCCESS);
			} else {
				response.put("success", false);
				response.put("msg", ConstantsPoc.LEAD_MASTER_NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getleadbyleadassigned")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getLeadsByLeadAssigned(
			@RequestParam("leadAssignedId") Long leadAssignedId, @RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<LeadMasterResponseDto> responseDtos = leadMasterService.getLeadesByLeadAssignedId(leadAssignedId,
					userId);
			if (responseDtos != null) {
				response.put("data", responseDtos);
				response.put("success", true);
				response.put("msg", ConstantsPoc.LEAD_MASTER_FOUND_SUCCESS);
			} else {
				response.put("success", false);
				response.put("msg", ConstantsPoc.LEAD_MASTER_NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/deletefollowupbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteFollowUpDetails(@RequestParam Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSUccess = false;
		try {
			isSUccess = leadMasterService.deleteFollowUpDetailsById(id);

			if (isSUccess) {
				response.put("msg", "Followup deleted successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Followup is not deleted.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getFolloupDetails")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getFilteredFollowUpDetails(@RequestParam String startDate,
			@RequestParam String endDate, @RequestParam Long leadId, @RequestParam Boolean isCreated) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<FollowUpDetailsResponseDto> responseDtos = leadMasterService.getFilteredFolloUpDetails(startDate,
					endDate, leadId, isCreated);
			if (responseDtos != null) {
				response.put("data", responseDtos);
				response.put("success", true);
				response.put("msg", ConstantsPoc.LEAD_MASTER_FOUND_SUCCESS);
			} else {
				response.put("data", Collections.emptyList());
				response.put("success", false);
				response.put("msg", ConstantsPoc.LEAD_MASTER_NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/assignMultipleLeadToMember")
	public ResponseEntity<?> assignMultipleLeadToMember(@RequestParam("leadId") List<Long> leadIds,
			@RequestParam("memberId") Long memberId, @RequestParam(name = "description", required = false) String description,
			@RequestParam(name = "closeDate", required = false) String closeDate) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<LeadMasterResponseDto> responseDtos = leadMasterService.assignMultipleLeadToMember(leadIds, memberId,
					description, closeDate);

			if (responseDtos != null) {
				response.put("data", responseDtos);
				response.put("success", true);
				response.put("msg", ConstantsPoc.LEAD_MASTER_ASSIGN_SUCCESS);
			} else {
				response.put("data", Collections.emptyList());
				response.put("success", false);
				response.put("msg", ConstantsPoc.LEAD_MASTER_ASSIGN_FAIL);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getCountLeadByLeadType")
	public ResponseEntity<?> getCountLeadByLeadType(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Map<String, Long> responseDtos = leadMasterService.getCountLeadByLeadType(userId);

			if (responseDtos != null) {
				response.put("data", responseDtos);
				response.put("success", true);
				response.put("msg", ConstantsPoc.LEAD_MASTER_FOUND_SUCCESS);
			} else {
				response.put("data", Collections.emptyList());
				response.put("success", false);
				response.put("msg", ConstantsPoc.LEAD_MASTER_NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getCountLeadByLeadStatus")
	public ResponseEntity<?> getCountLeadByLeadStatus(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Map<String, Long> responseDtos = leadMasterService.getCountLeadByLeadStatus(userId);

			if (responseDtos != null) {
				response.put("data", responseDtos);
				response.put("success", true);
				response.put("msg", ConstantsPoc.LEAD_MASTER_FOUND_SUCCESS);
			} else {
				response.put("data", Collections.emptyList());
				response.put("success", false);
				response.put("msg", ConstantsPoc.LEAD_MASTER_NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllFollowUpByMemberId")
	public ResponseEntity<?> getAllFollowUpByMemberId(@RequestParam("memberId") Long memberId,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			AssignMemberResponseDTO responseDtos = leadMasterService.getAllFollowUpByMemberId(memberId, userId);

			if (responseDtos != null) {
				response.put("data", responseDtos);
				response.put("success", true);
				response.put("msg", ConstantsPoc.LEAD_MASTER_FOUND_SUCCESS);
			} else {
				response.put("data", Collections.emptyList());
				response.put("success", false);
				response.put("msg", ConstantsPoc.LEAD_MASTER_NOT_FOUND);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/addorupdatefollowup")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateFollowUp(
			@RequestBody List<FollowUpDetailsRequestDto> followUpDetails) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = leadMasterService.addOrUpdateFollowUp(followUpDetails);
			if (isSuccess) {
				response.put("msg", "FollowUp Added/Updated Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "FollowUp Added/Updated Failed");
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/changeLeadStatus")
	@ResponseBody
	public ResponseEntity<?> changeLeadStatus(
			@RequestParam List<Long> leadIds,@RequestParam Long leadStatusId) {

	    return ResponseEntity.ok(leadMasterService.changeLeadStatus(leadIds,leadStatusId));
	}
	
	@GetMapping("/search")
	public ResponseEntity<?> searchLeads(
	        @RequestParam(required = false) Long statusId,
	        @RequestParam(required = false) String priority,
	        @RequestParam(required = false) Long sourceId,
	        @RequestParam(required = false) Long leadAssignId) {

	    return ResponseEntity.ok(
	            leadMasterService.searchLeads(statusId, priority, sourceId, leadAssignId));
	}
}