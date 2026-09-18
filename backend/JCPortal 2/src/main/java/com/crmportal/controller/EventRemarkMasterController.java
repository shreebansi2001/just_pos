package com.crmportal.controller;

import java.util.ArrayList;
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

import com.crmportal.request.dto.EventRemarkMasterRequestDto;
import com.crmportal.response.dto.EventRemarkMasterResponseDto;
import com.crmportal.service.EventRemarkMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/eventremark" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventRemarkMasterController {

	@Autowired
	EventRemarkMasterService eventRemarkMasterService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addEventRemark(@Valid @RequestBody EventRemarkMasterRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventRemarkMasterResponseDto responseDto = eventRemarkMasterService.addOrUpdateEventRemark(request,
					Long.parseLong("-1"));
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.EVENT_REMARK_MASTER_CREATE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.EVENT_REMARK_MASTER_CREATE_SUCCESS);
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

	@PutMapping("/update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateEventRemark(
	        @RequestParam("id") Long id,
	        @Valid @RequestBody EventRemarkMasterRequestDto request) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        EventRemarkMasterResponseDto responseDto = eventRemarkMasterService.addOrUpdateEventRemark(request, id);

	        if (responseDto == null) {
	            response.put("msg", ConstantsPoc.EVENT_REMARK_MASTER_UPDATE_FAIL);
	            response.put("success", false);
	        } else {
	            response.put("msg", ConstantsPoc.EVENT_REMARK_MASTER_UPDATE_SUCCESS);
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
	  public ResponseEntity<Map<String, Object>> getAllEventRemarksByUserId(
	          @RequestParam("userId") Long userId,
	          @RequestParam(value = "remarkTypeName", required = false) String remarkTypeName) {

	      Map<String, Object> response = new HashMap<>();
	      try {
	          List<EventRemarkMasterResponseDto> responseDtos = eventRemarkMasterService.getAllEventRemarks(userId,remarkTypeName);

	          if (responseDtos.isEmpty()) {
	              response.put("msg", ConstantsPoc.EVENT_REMARK_MASTER_NOT_FOUND);
	              response.put("success", false);
	          } else {
	              Map<String, Object> data = new HashMap<>();
	              data.put("EventRemarks Details", responseDtos);
	              response.put("data", data);
	              response.put("msg", ConstantsPoc.EVENT_REMARK_MASTER_FOUND_SUCCESS);
	              response.put("success", true);
	          }
	          return new ResponseEntity<>(response, HttpStatus.OK);
	      } catch (Exception e) {
	          response.put("success", false);
	          response.put("msg", e.getMessage());
	          return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	      }
	  }


	    @GetMapping("/getbyid")
	    @ResponseBody
	    public ResponseEntity<Map<String, Object>> getEventRemarkById(@RequestParam("id") Long id) {
	        Map<String, Object> response = new HashMap<>();
	        try {
	            EventRemarkMasterResponseDto responseDto = eventRemarkMasterService.getEventRemarkById(id);

	            if (responseDto == null) {
	                response.put("msg", ConstantsPoc.EVENT_REMARK_MASTER_NOT_FOUND);
	                response.put("success", false);
	            } else {
	            	Map<String, Object> data = new HashMap<>();
	            	List<EventRemarkMasterResponseDto> responseDtos = new ArrayList<>();
	            	responseDtos.add(responseDto);
		            data.put("EventTypes Details", responseDtos);
		            response.put("data", data);
	                response.put("msg", ConstantsPoc.EVENT_REMARK_MASTER_FOUND_SUCCESS);
	                response.put("success", true);
	            }
	            return new ResponseEntity<>(response, HttpStatus.OK);
	        } catch (Exception e) {
	            response.put("success", false);
	            response.put("msg", e.getMessage());
	            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }

	    @DeleteMapping("/deletebyid")
	    @ResponseBody
	    public ResponseEntity<Map<String, Object>> deleteEventRemarkById(@RequestParam("id") Long id) {
	        Map<String, Object> response = new HashMap<>();
	        try {
	            Boolean deleted = eventRemarkMasterService.deleteEventRemarkById(id);

	            if (!deleted) {
	                response.put("msg", ConstantsPoc.EVENT_REMARK_MASTER_DELETE_FAIL);
	                response.put("success", false);
	            } else {
	                response.put("msg", ConstantsPoc.EVENT_REMARK_MASTER_DELETE_SUCCESS);
	                response.put("success", true);
	            }
	            return new ResponseEntity<>(response, HttpStatus.OK);
	        } catch (Exception e) {
	            response.put("success", false);
	            response.put("msg", e.getMessage());
	            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }



}
