package com.crmportal.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.InteractionEntity;
import com.crmportal.repository.InteractionRepository;
import com.crmportal.request.dto.InteractionRequestDTO;
import com.crmportal.response.dto.InteractionResponseDTO;
import com.crmportal.service.InteractionService;

@Service
public class InteractionServiceImpl implements InteractionService {

    @Autowired
    private InteractionRepository interactionRepository;

    @Override
    public InteractionResponseDTO addOrUpdate(InteractionRequestDTO dto) {
        InteractionEntity entity;

        if (dto.getId() != null) {
            entity = interactionRepository.findById(dto.getId()).orElse(new InteractionEntity());
        } else {
            entity = new InteractionEntity();
        }

        entity.setInteractionname(dto.getInteractionname());
        entity.setInteractiontype(dto.getInteractiontype());
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        InteractionEntity saved = interactionRepository.save(entity);

        return convertToResponse(saved);
    }

    @Override
    public InteractionResponseDTO getById(Long id) {
        InteractionEntity entity = interactionRepository.findById(id).orElse(null);
        return entity != null ? convertToResponse(entity) : null;
    }

    @Override
    public List<InteractionResponseDTO> getAll() {
        return interactionRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private InteractionResponseDTO convertToResponse(InteractionEntity entity) {
        InteractionResponseDTO dto = new InteractionResponseDTO();
        dto.setId(entity.getId());
        dto.setInteractionname(entity.getInteractionname());
        dto.setInteractiontype(entity.getInteractiontype());
        dto.setIsActive(entity.getIsActive());
        dto.setIsDelete(entity.getIsDelete());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}