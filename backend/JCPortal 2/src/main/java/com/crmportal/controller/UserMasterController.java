package com.crmportal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.UpdateUserMemberRequestDto;
import com.crmportal.response.ApiResponse;
import com.crmportal.response.dto.ParentUserResponseDto;
import com.crmportal.response.dto.PlanUserSummaryResponseDto;
import com.crmportal.response.dto.ReportingManagerResponseDTO;
import com.crmportal.response.dto.UserAdminResponseDto;
import com.crmportal.response.dto.UserLogsResponseDto;
import com.crmportal.response.dto.UserMasterResponseDto;
import com.crmportal.response.dto.UserResponseDTO;
import com.crmportal.service.UserMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "v1/api/user" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class UserMasterController {

	@Autowired
	UserMasterService userMasterService;

	@GetMapping("/getallbyuserid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllUserMaster(@RequestParam("userId") Long userId,
			@RequestParam(value = "userName", required = false) String userName) {
		Map<String, Object> response = new HashMap<>();
		try {
			Map<String, Object> responseDtos = userMasterService.getAllUserMaster(userId, userName);
			if (responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.USER_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> userResp = new HashMap<>();
				userResp.put("userDetails", responseDtos);
				response.put("data", userResp);
				response.put("msg", ConstantsPoc.USER_FOUND_SUCCESS);
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

	@GetMapping("/getallbyroleid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllAdminUserMaster(@RequestParam("roleId") Long roleId,
			@RequestParam(value = "userName", required = false) String userName,
			@RequestParam(value = "type", required = false) String type) {
		Map<String, Object> response = new HashMap<>();
		try {
			PlanUserSummaryResponseDto responseDtos = userMasterService.getAllAdminUserMaster(roleId, userName, type);

			if (responseDtos == null) {
				response.put("msg", ConstantsPoc.USER_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> userResp = new HashMap<>();
				userResp.put("User Details", responseDtos);
				response.put("data", userResp);
				response.put("msg", ConstantsPoc.USER_FOUND_SUCCESS);
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

	@GetMapping("/getbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getUserMasterById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			UserMasterResponseDto responseDto = userMasterService.getUserMasterById(id);
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.USER_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> userResp = new HashMap<>();
				List<UserMasterResponseDto> responseDtos = new ArrayList<>();
				responseDtos.add(responseDto);
				userResp.put("User Details", responseDtos);
				response.put("data", userResp);
				response.put("msg", ConstantsPoc.USER_FOUND_SUCCESS);
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

	@GetMapping("/getmanagerandadminusersbyclient")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getManagerAndAdminUsersByClient(
			@RequestParam("clientUserId") Long clientUserId) {

		Map<String, Object> response = new HashMap<>();
		try {
			List<UserMasterResponseDto> responseDtos = userMasterService
					.getManagerAndAdminUsersByClientUserId(clientUserId);

			if (responseDtos.isEmpty()) {
				response.put("msg", "No Manager or Admin users found for this client");
				response.put("success", false);
			} else {
				Map<String, Object> userResp = new HashMap<>();
				userResp.put("userDetails", responseDtos);
				response.put("data", userResp);
				response.put("msg", "Manager and Admin users fetched successfully");
				response.put("success", true);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updatemember")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateMember(@ModelAttribute @Valid UpdateUserMemberRequestDto request,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {

			Boolean responseDto = userMasterService.updateMember(request, userId);
			if (!responseDto) {
				response.put("msg", ConstantsPoc.USER_UPDATE_FAIL);
				response.put("success", false);
			} else {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.USER_UPDATE_SUCCESS);
				response.put("success", true);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/isChildUserExist")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> isChildUserExist(@RequestParam(value = "userId") Long userId,
			HttpServletRequest re) {
		Map<String, Object> response = new HashMap<>();
		try {
			boolean isChildUser = userMasterService.isChildUserExist(userId);
			response.put("data", isChildUser);
			response.put("msg", isChildUser ? "Child user found" : "Child user not found");
			response.put("success", true);
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

	@GetMapping("/getmemberbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getMemberById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			UserAdminResponseDto responseDto = userMasterService.getMemberById(id);
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.USER_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> userResp = new HashMap<>();
				List<UserAdminResponseDto> responseDtos = new ArrayList<>();
				responseDtos.add(responseDto);
				userResp.put("User Details", responseDtos);
				response.put("data", userResp);
				response.put("msg", ConstantsPoc.USER_FOUND_SUCCESS);
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

	@DeleteMapping("/deleteuserdownpaymentbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteUserDownPaymentById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = userMasterService.deleteUserDownPaymentById(id);
			if (isSuccess) {
				response.put("msg", "User Down Payment Deleted Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "User Down Payment Deleted Failed");
				response.put("success", isSuccess);
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

	@DeleteMapping("/deleteuserdocumentbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteUserDocumentById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = userMasterService.deleteUserDocumentById(id);
			if (isSuccess) {
				response.put("msg", "User Document Deleted Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "User Document Deleted Failed");
				response.put("success", isSuccess);
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

	@DeleteMapping("/deleteuseramcbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteUserAmcById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = userMasterService.deleteUserAmcById(id);
			if (isSuccess) {
				response.put("msg", "User AMC Deleted Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "User AMC Deleted Failed");
				response.put("success", isSuccess);
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

	@DeleteMapping("/deleteuserrefundbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteUserRefundById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = userMasterService.deleteUserRefundById(id);
			if (isSuccess) {
				response.put("msg", "User Refund Deleted Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "User Refund Deleted Failed");
				response.put("success", isSuccess);
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

	@DeleteMapping("/deletebasicfilebyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteBasicFileById(@RequestParam("id") Long id,
			@RequestParam("fileType") String fileType) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = userMasterService.deleteBasicFileById(id, fileType);
			if (isSuccess) {
				response.put("msg", "User CallFile Deleted Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "User CallFile Deleted Failed");
				response.put("success", isSuccess);
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

	@GetMapping("/getAllManagers")
	public ResponseEntity<?> getAllManagers() {
		Map<String, Object> response = new HashMap<>();
		try {
			List<UserResponseDTO> responseDto = userMasterService.getAllManagers();
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.USER_NOT_FOUND);
				response.put("success", false);
			} else {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.USER_FOUND_SUCCESS);
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

	@GetMapping("/getAllRolesExcludesAdminManager")
	public ResponseEntity<?> getAllRolesExcludesAdminManager() {
		Map<String, Object> response = new HashMap<>();
		try {
			List<UserResponseDTO> responseDto = userMasterService.getAllRolesExcludeManager();
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.USER_NOT_FOUND);
				response.put("success", false);
			} else {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.USER_FOUND_SUCCESS);
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

	@DeleteMapping("/deleteuserofferbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteUserOfferById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = userMasterService.deleteUserOfferById(id);
			if (isSuccess) {
				response.put("msg", "User Offer Deleted Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "User Offer Deleted Failed");
				response.put("success", isSuccess);
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

	@GetMapping("/getallclientbyreportingmanager")
	public ResponseEntity<?> getAllClientByReportingManager(@RequestParam("managerId") Long managerId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<ReportingManagerResponseDTO> responseDto = userMasterService.getAllClientByReportingManager(managerId);

			if (responseDto.isEmpty()) {
				response.put("msg", ConstantsPoc.USER_NOT_FOUND);
				response.put("success", false);
			} else {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.USER_FOUND_SUCCESS);
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

	@DeleteMapping("/deleteuserbyid")
	public ResponseEntity<Map<String, Object>> deleteUserById(@RequestParam("userId") Long userId,
			@RequestParam(value = "otp", required = false) String otp, @RequestParam("isAdmin") Boolean isAdmin) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = userMasterService.deleteUserById(userId, otp, isAdmin);
			if (isSuccess) {
				response.put("msg", "User Deleted Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "User Deleted Failed");
				response.put("success", isSuccess);
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

	@PutMapping("/userblock")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> userBlock(@RequestParam("userId") Long userId,
			@RequestParam("otp") String otp) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = userMasterService.userBlock(userId, otp);
			if (isSuccess) {
				response.put("msg", "User Block Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "User Block Failed");
				response.put("success", isSuccess);
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

	@PutMapping("/convertUserType")
	public ResponseEntity<?> convertUserType(@RequestParam("userId") Long userId, @RequestParam("type") String type,
			@RequestParam(value = "otp", required = false) String otp) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = userMasterService.convertUserType(userId, type, otp);

			if (isSuccess) {
				response.put("msg", "User Type change successfully.");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "User Type change failed.");
				response.put("success", isSuccess);
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

	@GetMapping("/getalllogsuser")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllLogsUser(
			@RequestParam(value = "isActive", required = false) Boolean IsActive) {
		Map<String, Object> response = new HashMap<>();
		try {
			Map<String, Object> datas = userMasterService.getAllLogsUser(IsActive);
			if (datas.isEmpty()) {
				response.put("msg", "Data Not Found");
				response.put("success", false);
			} else {
				response.put("data", datas);
				response.put("msg", "Data Found Successfully");
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

	@GetMapping("/{userId}/isvisible")
	public ResponseEntity<ApiResponse<Boolean>> isVisibleByUserId(@PathVariable("userId") Long userId) {
		Boolean isVisible = userMasterService.isVisibleByUserId(userId);
		return new ResponseEntity<>(ApiResponse.success("Data Fetched Successfully", isVisible, 200), HttpStatus.OK);
	}

	@PutMapping("/isVisible")
	public ResponseEntity<ApiResponse<Boolean>> updateIsVisible(@RequestParam("userId") Long userId,
			@RequestParam("isVisible") Boolean isVisible) {
		Boolean updated = userMasterService.updateIsVisible(userId, isVisible);
		return new ResponseEntity<>(ApiResponse.success("Visibility updated Successfully", updated, 200),
				HttpStatus.OK);
	}

	@GetMapping("/parentuser")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> parentUser(@RequestParam("roleId") Long roleId,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<ParentUserResponseDto> parentUsers = userMasterService.getParentUser(roleId, userId);
			if (parentUsers.isEmpty()) {
				response.put("msg", "Parent User Not Found");
				response.put("success", false);
			} else {
				response.put("data", parentUsers);
				response.put("msg", "Parent User Found Successfully");
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
	
	@PutMapping("/isinquiryvisible")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> isInquiryVisible(@RequestParam("userId") Long userId,@RequestParam("isVisible") Boolean isVisible){
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = userMasterService.isInquiryVisible(userId,isVisible);

			if (isSuccess) {
			    response.put("msg", "Inquiry Visible " + (isVisible ? "ON" : "OFF") + " successfully.");
			    response.put("success", isSuccess);
			} else {
			    response.put("msg", "Inquiry Visible " + (isVisible ? "ON" : "OFF") + " Failed.");
			    response.put("success", isSuccess);
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
