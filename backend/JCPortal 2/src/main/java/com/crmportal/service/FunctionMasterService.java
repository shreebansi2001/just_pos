package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.FunctionMasterRequestDto;
import com.crmportal.response.dto.FunctionMasterResponseDto;

@Service
public interface FunctionMasterService {

	FunctionMasterResponseDto addOrUpdateFunctionMaster(@Valid FunctionMasterRequestDto request, long id);

	List<FunctionMasterResponseDto> getAllFunctionsByUserId(Long userId, String functionName);

	FunctionMasterResponseDto getFunctionsById(Long id);

	Boolean deleteFunctionById(Long id);

}
