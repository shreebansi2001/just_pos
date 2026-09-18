package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.crmportal.request.dto.PipelineRequestDto;
import com.crmportal.request.dto.SalesInvoiceRequestDto;
import com.crmportal.response.dto.PipelineResponseDto;
import com.crmportal.response.dto.SalesInvoiceResponseDto;
import com.crmportal.service.PipelineService;

@RestController
@RequestMapping({ "/v1/api/pipeline" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class PipelineController {

	@Autowired
	PipelineService pipelineService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> createUpdatePipeLine(@RequestBody PipelineRequestDto request) {
		Map<String, Object> response = new HashMap<>();

		try {
			PipelineResponseDto responseDto = pipelineService.createUpdatePipeLine(request);

			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", "Pipeline added successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Pipeline is not added.");
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

	@GetMapping("/getall")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllPipeLine(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();

		try {
			List<PipelineResponseDto> responseDtos = pipelineService.getAllPipeLine(userId);

			if (responseDtos != null) {
				response.put("data", responseDtos);
				response.put("msg", "Pipeline Found successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Pipeline Not Found.");
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

	@GetMapping("/getstagebypipelineid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getStageByPipelineId(@RequestParam Long pipelineId,
			@RequestParam Long userId) {
		Map<String, Object> response = new HashMap<>();

		try {
			Map<String, Object> responseDtos = pipelineService.getStageByPipelineId(pipelineId, userId);

			if (responseDtos != null) {
				response.put("data", responseDtos);
				response.put("msg", "Pipeline get successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Pipeline is not get.");
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

	@GetMapping("/getstageleaddatabypipelineid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getStageLeadDataByPipelineId(@RequestParam Long pipelineId,
			@RequestParam Long memberId, @RequestParam Long userId) {
		Map<String, Object> response = new HashMap<>();

		try {
			Map<String, Object> responseDtos = pipelineService.getStageLeadDataByPipelineId(pipelineId, memberId,
					userId);

			if (responseDtos != null) {
				response.put("data", responseDtos);
				response.put("msg", "Pipeline get successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Pipeline is not get.");
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

	@GetMapping("/getstageleaddatabypipelineidandstage")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getStageLeadDataByPipelineIdAndStage(@RequestParam Long pipelineId,
			@RequestParam Long userId, @RequestParam String stage, @RequestParam Long memberId) {
		Map<String, Object> response = new HashMap<>();

		try {
			Map<String, Object> responseDtos = pipelineService.getStageLeadDataByPipelineIdAndStage(pipelineId, userId,
					stage, memberId);

			if (responseDtos != null) {
				response.put("data", responseDtos);
				response.put("msg", "Pipeline get successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Pipeline is not get.");
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

	@GetMapping("/getperformance")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getPerformance(@RequestParam Long userId,
			@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
			@RequestParam Long memberId) {
		Map<String, Object> response = new HashMap<>();

		try {
			Map<String, Object> responseDtos = pipelineService.getPerformance(userId, startDate, endDate,memberId);

			if (responseDtos != null) {
				response.put("data", responseDtos);
				response.put("msg", "Performance get successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Performance is not get.");
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

	@GetMapping("/getEmployeePerformance")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getEmployeePerformance(@RequestParam Long userId,
			@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
			@RequestParam(required = false) Long pipelineId,@RequestParam Long memberId) {
		Map<String, Object> response = new HashMap<>();

		try {
			Map<String, Object> responseDtos = pipelineService.getEmployeePerformance(userId, startDate, endDate,
					pipelineId,memberId);

			if (responseDtos != null) {
				response.put("data", responseDtos);
				response.put("msg", "Performance get successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Performance is not get.");
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

	@DeleteMapping("/delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deletePipeLine(@RequestParam Long pipelineId) {
		Map<String, Object> response = new HashMap<>();

		try {
			Boolean isDeleted = pipelineService.deletePipeLine(pipelineId);

			if (isDeleted != null) {
				response.put("msg", "Pipeline deleted successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Pipeline is not deleted.");
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
	
	
	@DeleteMapping("/deletestage")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteStage(@RequestParam Long stageId,@RequestParam String stageType) {
		Map<String, Object> response = new HashMap<>();

		try {
			Boolean isDeleted = pipelineService.deleteStage(stageId,stageType);

			if (isDeleted != null) {
				response.put("msg", "Stage deleted successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Stage is not deleted.");
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
}
