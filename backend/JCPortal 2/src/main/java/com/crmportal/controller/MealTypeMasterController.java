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

import com.crmportal.request.dto.MealTypeMasterRequestDto;
import com.crmportal.response.dto.MealTypeMasterResponseDto;
import com.crmportal.service.MealTypeMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({"/v1/api/mealtype"})
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class MealTypeMasterController {

	
	@Autowired
	MealTypeMasterService mealTypeMasterService;
	
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addMealType(@Valid @RequestBody MealTypeMasterRequestDto request){
		Map<String, Object>  response = new HashMap<>();
		try {
			MealTypeMasterResponseDto responseDto = mealTypeMasterService.addOrUpdateMealType(request,Long.parseLong("-1"));
			if(responseDto != null) {
				response.put("msg", ConstantsPoc.MEAL_TYPE_CREATE_SUCCESS);
				response.put("success" , true);
			}else {
				response.put("msg", ConstantsPoc.MEAL_TYPE_CREATE_FAIL);
				response.put("success" , true);				
			}
			return new ResponseEntity<>(response , HttpStatus.OK);
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
	public ResponseEntity<Map<String, Object>> upadteMealType(@Valid @RequestBody MealTypeMasterRequestDto request, @RequestParam("id") Long id){
		Map<String, Object>  response = new HashMap<>();
		try {
			MealTypeMasterResponseDto responseDto = mealTypeMasterService.addOrUpdateMealType(request,id);
			if(responseDto != null) {
				response.put("msg", ConstantsPoc.MEAL_TYPE_UPDATE_SUCCESS);
				response.put("success" , true);
			}else {
				response.put("msg", ConstantsPoc.MEAL_TYPE_UPDATE_FAIL);
				response.put("success" , true);				
			}
			return new ResponseEntity<>(response , HttpStatus.OK);
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
    public ResponseEntity<Map<String, Object>> getAllMealTypesByUserId(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "mealTypeName", required = false) String mealTypeName) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<MealTypeMasterResponseDto> responseDtos =
                    mealTypeMasterService.getAllMealTypesByUserId(userId, mealTypeName);

            if (responseDtos.isEmpty()) {
                response.put("msg", ConstantsPoc.MEAL_TYPE_NOT_FOUND);
                response.put("success", false);
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("MealType Details", responseDtos);
                response.put("data", data);
                response.put("msg", ConstantsPoc.MEAL_TYPE_FOUND_SUCCESS);
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
    public ResponseEntity<Map<String, Object>> getMealTypeById(@RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            MealTypeMasterResponseDto responseDto = mealTypeMasterService.getMealTypeById(id);

            if (responseDto == null) {
                response.put("msg", ConstantsPoc.MEAL_TYPE_NOT_FOUND);
                response.put("success", false);
            } else {
                Map<String, Object> data = new HashMap<>();
                List<MealTypeMasterResponseDto> list = new ArrayList<>();
                list.add(responseDto);
                data.put("MealType Details", list);
                response.put("data", data);
                response.put("msg", ConstantsPoc.MEAL_TYPE_FOUND_SUCCESS);
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
    public ResponseEntity<Map<String, Object>> deleteMealTypeById(@RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Boolean isSuccess = mealTypeMasterService.deleteMealTypeById(id);
            if (isSuccess) {
                response.put("msg", ConstantsPoc.MEAL_TYPE_DELETE_SUCCESS);
                response.put("success", true);
            } else {
                response.put("msg", ConstantsPoc.MEAL_TYPE_DELETE_FAIL);
                response.put("success", false);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
