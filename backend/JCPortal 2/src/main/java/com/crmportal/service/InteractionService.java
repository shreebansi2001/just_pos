package com.crmportal.service;

import java.util.List;

import com.crmportal.request.dto.InteractionRequestDTO;
import com.crmportal.response.dto.InteractionResponseDTO;


public interface InteractionService {

    InteractionResponseDTO addOrUpdate(InteractionRequestDTO dto);

    InteractionResponseDTO getById(Long id);

    List<InteractionResponseDTO> getAll();
}