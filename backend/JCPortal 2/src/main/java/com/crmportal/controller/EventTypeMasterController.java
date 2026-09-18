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

import com.crmportal.request.dto.EventTypeMasterRequestDto;
import com.crmportal.response.dto.EventTypeMasterResponseDto;
import com.crmportal.service.EventTypeMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/eventtype" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventTypeMasterController {

	@Autowired
	EventTypeMasterService eventTypeMasterService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addEventType(@Valid @RequestBody EventTypeMasterRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventTypeMasterResponseDto responseDto = eventTypeMasterService.addOrUpdateEventType(request,
					Long.parseLong("-1"));
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.EVENT_TYPE_MASTER_CREATE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.EVENT_TYPE_MASTER_CREATE_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> updateEventType(
	        @RequestParam("id") Long id,
	        @Valid @RequestBody EventTypeMasterRequestDto request) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        EventTypeMasterResponseDto responseDto = eventTypeMasterService.addOrUpdateEventType(request, id);

	        if (responseDto == null) {
	            response.put("msg", ConstantsPoc.EVENT_TYPE_MASTER_UPDATE_FAIL);
	            response.put("success", false);
	        } else {
	            response.put("msg", ConstantsPoc.EVENT_TYPE_MASTER_UPDATE_SUCCESS);
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
	
//	  @GetMapping("/getall")
//	    @ResponseBody
//	    public ResponseEntity<Map<String, Object>> getAllEventTypes() {
//	        Map<String, Object> response = new HashMap<>();
//	        try {
//	            List<EventTypeMasterResponseDto> responseDtos = eventTypeMasterService.getAllEventTypes();
//
//	            if (responseDtos.isEmpty()) {
//	                response.put("msg", ConstantsPoc.EVENT_TYPE_MASTER_NOT_FOUND);
//	                response.put("success", false);
//	            } else {
//	                Map<String, Object> data = new HashMap<>();
//	                data.put("EventTypes Details", responseDtos);
//	                response.put("data", data);
//	                response.put("msg", ConstantsPoc.EVENT_TYPE_MASTER_FOUND_SUCCESS);
//	                response.put("success", true);
//	            }
//	            return new ResponseEntity<>(response, HttpStatus.OK);
//	        } catch (Exception e) {
//	            response.put("success", false);
//	            response.put("msg", e.getMessage());
//	            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//	        }
//	    }

	  @GetMapping("/getallbyuserid")
	  @ResponseBody
	  public ResponseEntity<Map<String, Object>> getAllEventTypesByUserId(
	          @RequestParam("userId") Long userId,
	          @RequestParam(value = "eventTypeName", required = false) String eventTypeName) {

	      Map<String, Object> response = new HashMap<>();
	      try {
	          List<EventTypeMasterResponseDto> responseDtos = eventTypeMasterService.getAllEventTypesByUserId(userId, eventTypeName);

	          if (responseDtos.isEmpty()) {
	              response.put("msg", ConstantsPoc.EVENT_TYPE_MASTER_NOT_FOUND);
	              response.put("success", false);
	          } else {
	              Map<String, Object> data = new HashMap<>();
	              data.put("EventTypes Details", responseDtos);
	              response.put("data", data);
	              response.put("msg", ConstantsPoc.EVENT_TYPE_MASTER_FOUND_SUCCESS);
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
	    public ResponseEntity<Map<String, Object>> getEventTypeById(@RequestParam("id") Long id) {
	        Map<String, Object> response = new HashMap<>();
	        try {
	            EventTypeMasterResponseDto responseDto = eventTypeMasterService.getEventTypeById(id);

	            if (responseDto == null) {
	                response.put("msg", ConstantsPoc.EVENT_TYPE_MASTER_NOT_FOUND);
	                response.put("success", false);
	            } else {
	            	Map<String, Object> data = new HashMap<>();
	            	List<EventTypeMasterResponseDto> responseDtos = new ArrayList<>();
	            	responseDtos.add(responseDto);
		            data.put("EventTypes Details", responseDtos);
		            response.put("data", data);
	                response.put("msg", ConstantsPoc.EVENT_TYPE_MASTER_FOUND_SUCCESS);
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
	    public ResponseEntity<Map<String, Object>> deleteEventTypeById(@RequestParam("id") Long id) {
	        Map<String, Object> response = new HashMap<>();
	        try {
	            Boolean deleted = eventTypeMasterService.deleteEventTypeById(id);

	            if (!deleted) {
	                response.put("msg", ConstantsPoc.EVENT_TYPE_MASTER_DELETE_FAIL);
	                response.put("success", false);
	            } else {
	                response.put("msg", ConstantsPoc.EVENT_TYPE_MASTER_DELETE_SUCCESS);
	                response.put("success", true);
	            }
	            return new ResponseEntity<>(response, HttpStatus.OK);
	        } catch (Exception e) {
	            response.put("success", false);
	            response.put("msg", e.getMessage());
	            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }

//	    @GetMapping("/geteventtypewithsearch")
//	    @ResponseBody
//	    public ResponseEntity<Map<String, Object>> getEventTypeWithSearch(@RequestParam("eventTypeName") String eventTypeName,@RequestParam("userId") Long userId) {
//	        Map<String, Object> response = new HashMap<>();
//	        try {
//	            List<EventTypeMasterResponseDto> eventTypeResponseDtos =
//	                    eventTypeMasterService.getEventTypeWithSearch(eventTypeName,userId);
//
//	            if (eventTypeResponseDtos.isEmpty()) {
//	                response.put("msg", ConstantsPoc.EVENT_TYPE_MASTER_NOT_FOUND);
//	                response.put("success", false);
//	            } else {
//	                Map<String, Object> eventResp = new HashMap<>();
//	                eventResp.put("Event Type Details", eventTypeResponseDtos);
//	                response.put("data", eventResp);
//	                response.put("msg", ConstantsPoc.EVENT_TYPE_MASTER_FOUND_SUCCESS);
//	                response.put("success", true);
//	            }
//	            return new ResponseEntity<>(response, HttpStatus.OK);
//	        } catch (RuntimeException e) {
//	            response.put("success", false);
//	            response.put("msg", e.getMessage());
//	            return new ResponseEntity<>(response, HttpStatus.OK);
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	            response.put("success", false);
//	            response.put("msg", e.getMessage());
//	            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//	        }
//	    }


}
