package com.crmportal.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.entity.LeadMasterEntity;
import com.crmportal.repository.LeadMasterRepository;
import com.crmportal.request.dto.AssignDbRequest;
import com.crmportal.request.dto.LoginRequestDto;
import com.crmportal.request.dto.UserMasterRequestDto;
import com.crmportal.response.dto.PartyMasterResponseDto;
import com.crmportal.response.dto.UserMasterResponseDto;
import com.crmportal.service.AdminTemplateModuleService;
import com.crmportal.service.CommonService;
import com.crmportal.service.DatabasePlanningService;
import com.crmportal.service.IpAddressService;
import com.crmportal.service.UserMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "v1/api/auth" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class AuthController {

	@Autowired
	UserMasterService userMasterService;

	@Autowired
	IpAddressService ipAddressService;

	@Autowired
	CommonService commonService;

	@Autowired
	JavaMailSender javaMailSender;

	@Autowired
	AdminTemplateModuleService adminTemplateModuleService;

	@Autowired
	LeadMasterRepository leadMasterRepository;

	@Autowired
	DatabasePlanningService databasePlanningService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addUserMaster(
	        @Valid @RequestBody UserMasterRequestDto request) {

	    Map<String, Object> response = new HashMap<>();

	    try {

	        UserMasterResponseDto responseDto =
	                userMasterService.addOrUpdateUserMaster(request, -1L);

	        if (responseDto == null) {
	            response.put("msg", ConstantsPoc.USER_CREATE_FAIL);
	            response.put("success", false);
	            return new ResponseEntity<>(response, HttpStatus.OK);
	        }

	        // allocate themes
	        int allocatedThemes =
	                adminTemplateModuleService.allocateThemes(responseDto.getId());

	        boolean themeAllocated = allocatedThemes > 0;

	        // send whatsapp + mail
	        String fullName = responseDto.getFirstName() + " " + responseDto.getLastName();
	        List<String> dataList = new ArrayList<>();
	        dataList.add(fullName);

	        commonService.sendWhatsappMsg(
	                "jc_user_register_success_msg",
	                responseDto.getContactNo(),
	                dataList,
	                "",
	                false,-1l
	        );

//	        commonService.sendVerificationMail(
//	                "jc_user_register_verification_mail",
//	                responseDto,
//	                dataList,
//	                "",
//	                false
//	        );

	        // update lead status
	        if (request.getLeadId() != null) {
	            LeadMasterEntity leadMasterEntity =
	                    leadMasterRepository.findByIdAndIsDeleteFalse(request.getLeadId())
	                            .orElseThrow(() -> new RuntimeException("Lead not found"));

	           // leadMasterEntity.setLeadStatus("Confirmed");
	            leadMasterRepository.save(leadMasterEntity);
	        }

	        // final response
	        if (themeAllocated) {
	            response.put("msg", ConstantsPoc.USER_CREATE_SUCCESS + " And "
	                    + ConstantsPoc.ADMIN_TEMPLATE_MODULE_CREATE_SUCCESS);
	        } else {
	            response.put("msg", ConstantsPoc.USER_CREATE_SUCCESS + " And "
	                    + ConstantsPoc.ADMIN_TEMPLATE_MODULE_CREATE_FAIL);
	        }

	        response.put("success", true);
	        response.put("data", responseDto);

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

//	@PostMapping("/add")
//	@ResponseBody
//	public ResponseEntity<Map<String, Object>> addUserMaster(@Valid @RequestBody UserMasterRequestDto request) {
//
//		Map<String, Object> response = new HashMap<>();
//
//		try {
//
//			UserMasterResponseDto responseDto = userMasterService.addOrUpdateUserMaster(request, -1L);
//
//			if (responseDto == null) {
//				response.put("msg", ConstantsPoc.USER_CREATE_FAIL);
//				response.put("success", false);
//				return new ResponseEntity<>(response, HttpStatus.OK);
//			}
//
////			AssignDbRequest re = new AssignDbRequest();
////			re.setDbPlanningId("325");
////			re.setUserId(responseDto.getId().toString());
////			databasePlanningService.assignDbToUser(re);
//
//			// allocate themes
//			int allocatedThemes = adminTemplateModuleService.allocateThemes(responseDto.getId());
//
//			boolean themeAllocated = allocatedThemes > 0;
//
//			// send whatsapp + mail
//			String fullName = responseDto.getFirstName() + " " + responseDto.getLastName();
////			List<String> dataList = new ArrayList<>();
////			dataList.add(fullName);
////			dataList.add(responseDto.getUniqueCode());
//			String[] dataArr = { responseDto.getUniqueCode(), "Login", "8866889580" };
////	        commonService.sendWhatsappMsg(
////	                "jc_user_register_success_msg",
////	                responseDto.getContactNo(),
////	                dataList,
////	                "",
////	                false
////	        );
//
//			commonService.sendOtpWhatsappAuthType("user_unique_code", responseDto.getContactNo(), dataArr);
////	        commonService.sendVerificationMail(
////	                "jc_user_register_verification_mail",
////	                responseDto,
////	                dataList,
////	                "",
////	                false
////	        );
//
////			commonService.sendClientCredentials(responseDto, request.getEmail(), request.getPassword(),
////					responseDto.getUniqueCode());
//
//			// update lead status
//			if (request.getLeadId() != null) {
//				LeadMasterEntity leadMasterEntity = leadMasterRepository.findByIdAndIsDeleteFalse(request.getLeadId())
//						.orElseThrow(() -> new RuntimeException("Lead not found"));
//
//				// leadMasterEntity.setLeadStatus("Confirmed");
//				leadMasterRepository.save(leadMasterEntity);
//			}
//
//			// final response
//			if (themeAllocated) {
//				response.put("msg",
//						ConstantsPoc.USER_CREATE_SUCCESS + " And " + ConstantsPoc.ADMIN_TEMPLATE_MODULE_CREATE_SUCCESS);
//			} else {
//				response.put("msg",
//						ConstantsPoc.USER_CREATE_SUCCESS + " And " + ConstantsPoc.ADMIN_TEMPLATE_MODULE_CREATE_FAIL);
//			}
//
//			response.put("success", true);
//			response.put("data", responseDto);
//
//			return new ResponseEntity<>(response, HttpStatus.OK);
//
//		} catch (RuntimeException e) {
//			response.put("success", false);
//			response.put("msg", e.getMessage());
//			return new ResponseEntity<>(response, HttpStatus.OK);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			response.put("success", false);
//			response.put("msg", e.getMessage());
//			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}

	@PutMapping("/update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateUserMaster(@Valid @RequestBody UserMasterRequestDto request,
			@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			UserMasterResponseDto responseDto = userMasterService.addOrUpdateUserMaster(request, id);
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.USER_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.USER_UPDATE_FAIL);
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

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequestDto request,
			HttpServletRequest re) {
		Map<String, Object> response = new HashMap<>();
		try {
			UserMasterResponseDto responseDto = userMasterService.loginWithPassword(request.getEmail(),
					request.getPassword(), request.getOtp(), re, request.getUniqueCode(), request.getSoftType());
			if (responseDto != null) {
				Map<String, Object> userResp = new HashMap<>();
				List<UserMasterResponseDto> responseDtos = new ArrayList<>();
				responseDtos.add(responseDto);
				userResp.put("User Details", responseDtos);
				response.put("data", userResp);
				response.put("msg", "User Login Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "User Login Failed");
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

	@GetMapping("/local")
	public Map<String, String> getLocalIp(HttpServletRequest request) {
		Map<String, String> response = new HashMap<>();
		response.put("clientIp", ipAddressService.getClientIp(request));
		response.put("timestamp", new Date().toString());
		return response;
	}

	@PostMapping("/changepassword")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> changepassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, @RequestParam("conPassword") String conPassword,
			@RequestParam("userId") Long userId) {

		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = userMasterService.changePassword(oldPassword, newPassword, conPassword, userId);
			if (isSuccess) {
				response.put("msg", "Password changed successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Password change failed");
				response.put("success", false);
			}
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/forgotpassword")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> forgotPassword(@RequestParam("email") String email) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSent = userMasterService.forgotPassword(email);
			response.put("success", isSent ? true : false);
			response.put("msg", isSent ? "OTP sent to your registered email & mobileNo" : "Failed to send OTP");
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

	@PostMapping("/verifyotp")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> verifyOtp(@RequestParam("email") String email,
			@RequestParam("otp") String otp) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isValid = userMasterService.verifyOtp(email, otp);
			response.put("success", isValid ? true : false);
			response.put("message", isValid ? "OTP verified" : "Invalid OTP");
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

	@PostMapping("/resetpassword")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> resetPassword(@RequestParam("newPassword") String newPassword,
			@RequestParam("conPassword") String conPassword, @RequestParam("emailId") String emailId) {

		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = userMasterService.resetPassword(newPassword, conPassword, emailId);
			if (isSuccess) {
				response.put("msg", "Password reset successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Password reset failed");
				response.put("success", false);
			}
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/isapproved")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> isApproved(@RequestParam("isApprove") Boolean isApprove,
			@RequestParam("userId") Long userId, @RequestParam("otp") String otp) {
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {

			isSuccess = userMasterService.isApproved(isApprove, userId, otp);

			if (isSuccess) {
				response.put("msg", ConstantsPoc.USER_MASTER_ISAPPROVED_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.USER_MASTER_ISAPPROVED_FAIL);
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

	@PostMapping("/loginwithotp")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> loginWithOtp(@RequestParam("mobileNo") String mobileNo,
			HttpServletRequest re) {
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isSuccess = userMasterService.loginWithOtp(mobileNo, re);
			response.put("success", isSuccess ? true : false);
			response.put("msg", isSuccess ? "OTP sent to Whatsapp" : "Failed to send OTP");
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/loginwithcode")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> loginWithcode(@RequestParam("code") String code) {
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isSuccess = userMasterService.loginWithCode(code);
			response.put("success", isSuccess ? true : false);
			response.put("msg", isSuccess ? "OTP sent to Whatsapp" : "Failed to send OTP");
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/verifyotpformobile")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> verifyOtpForMobile(@RequestParam("mobileNo") String mobileNo,
			@RequestParam("otp") String otp, @RequestParam("uniqueCode") String uniqueCode,
			@RequestParam(value = "softType", defaultValue = "jcx") String softType) {
		Map<String, Object> response = new HashMap<>();
		try {
			UserMasterResponseDto responseDto = userMasterService.verifyOtpForMobile(mobileNo, otp, uniqueCode,
					softType);
			if (responseDto != null) {
				Map<String, Object> userResp = new HashMap<>();
				List<UserMasterResponseDto> responseDtos = new ArrayList<>();
				responseDtos.add(responseDto);
				userResp.put("User Details", responseDtos);
				response.put("data", userResp);
				response.put("msg", "User Login Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "User Login Failed");
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

	@PostMapping("/verifyotpforcode")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> verifyOtpForCode(@RequestParam("code") String code,
			@RequestParam("otp") String otp) {
		Map<String, Object> response = new HashMap<>();
		try {
			PartyMasterResponseDto responseDto = userMasterService.verifyOtpForCode(code, otp);
			if (responseDto != null) {
				Map<String, Object> userResp = new HashMap<>();
				List<PartyMasterResponseDto> responseDtos = new ArrayList<>();
				responseDtos.add(responseDto);
				userResp.put("Party Details", responseDtos);
				response.put("data", userResp);
				response.put("msg", "User Login Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "User Login Failed");
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

	@PostMapping("/refresh-token")
	public ResponseEntity<Object> refreshToken(@RequestHeader("Authorization") String authHeader) {

		if (authHeader == null || authHeader.trim().isEmpty()) {
			throw new RuntimeException("Authorization header missing");
		}

		authHeader = authHeader.trim();
		System.out.println(authHeader);
		if (!authHeader.toLowerCase().startsWith("bearer ")) {
			throw new RuntimeException("Invalid Authorization header");
		}

		String oldToken = authHeader.substring(7).trim();

		return ResponseEntity.ok(userMasterService.refreshToken(oldToken));
	}

}