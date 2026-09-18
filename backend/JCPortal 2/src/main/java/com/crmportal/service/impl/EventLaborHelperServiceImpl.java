package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.EventLaborHelperEntity;
import com.crmportal.entity.LaborHelperEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.repository.EventLaborHelperRepository;
import com.crmportal.repository.LaborHelperRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.request.dto.EventLaborHelperRequestDto;
import com.crmportal.response.dto.EventLaborHelperResponseDto;
import com.crmportal.service.EventLaborHelperService;

@Service
public class EventLaborHelperServiceImpl implements EventLaborHelperService {

    @Autowired
    private EventLaborHelperRepository eventLaborHelperRepository;

    @Autowired
    private PartyMasterRepository partyMasterRepository;

    @Autowired
    private LaborHelperRepository laborHelperRepository;

    @Override
    @Transactional
    public List<EventLaborHelperResponseDto> saveEventLaborHelpers(EventLaborHelperRequestDto request) {
        // Step 1: Delete existing entries for (eventId, eventFunctionId, partyId)
        eventLaborHelperRepository.deleteByEventIdAndEventFunctionIdAndPartyId(
                request.getEventId(),
                request.getEventFunctionId(),
                request.getPartyId()
        );

        // Fetch Party Master Entity
        PartyMasterEntity party = partyMasterRepository.findById(request.getPartyId())
                .orElseThrow(() -> new RuntimeException("Party Master not found for ID: " + request.getPartyId()));

        List<EventLaborHelperEntity> entitiesToSave = new ArrayList<>();

        // Step 2: Iterate over each labor_helper_id and prepare individual rows
        if (request.getLaborHelperIds() != null && !request.getLaborHelperIds().isEmpty()) {
            for (Long helperId : request.getLaborHelperIds()) {
                LaborHelperEntity laborHelper = laborHelperRepository.findByIdAndIsDeleteFalse(helperId)
                        .orElseThrow(() -> new RuntimeException("Labor Helper record not found for ID: " + helperId));

                EventLaborHelperEntity entity = new EventLaborHelperEntity();
                entity.setEvent_id(request.getEventId());
                entity.setEvent_function_id(request.getEventFunctionId());
                entity.setContact(party);
                entity.setLaborhelper(laborHelper);

                entitiesToSave.add(entity);
            }
        }

        // Step 3: Save all rows directly into DB
        List<EventLaborHelperEntity> savedEntities = eventLaborHelperRepository.saveAll(entitiesToSave);

        return savedEntities.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<EventLaborHelperResponseDto> getByEventId(Long eventId) {
        List<EventLaborHelperEntity> entities = eventLaborHelperRepository.findByEvent_id(eventId);
        return entities.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<EventLaborHelperResponseDto> getByEventIdAndFunctionIdAndPartyId(
            Long eventId, Long eventFunctionId, Long partyId) {
        List<EventLaborHelperEntity> entities = eventLaborHelperRepository
                .findByEvent_idAndEvent_function_idAndContact_Id(eventId, eventFunctionId, partyId);
        return entities.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteByEventIdAndFunctionIdAndPartyId(Long eventId, Long eventFunctionId, Long partyId) {
        eventLaborHelperRepository.deleteByEventIdAndEventFunctionIdAndPartyId(eventId, eventFunctionId, partyId);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteByEventId(Long eventId) {
        eventLaborHelperRepository.deleteByEventId(eventId);
        return true;
    }

    private EventLaborHelperResponseDto mapToResponseDto(EventLaborHelperEntity entity) {
        EventLaborHelperResponseDto dto = new EventLaborHelperResponseDto();
        dto.setId(entity.getId());
        dto.setEventId(entity.getEvent_id());
        dto.setEventFunctionId(entity.getEvent_function_id());
        dto.setCreatedAt(entity.getCreatedAt());

        // Map Party / Contact details
        if (entity.getContact() != null) {
            dto.setPartyId(entity.getContact().getId());
        }

        // Map all fields from LaborHelperEntity
        if (entity.getLaborhelper() != null) {
            LaborHelperEntity helper = entity.getLaborhelper();
            dto.setLaborHelperId(helper.getId());
            dto.setName(helper.getName());
            dto.setPhonenumber(helper.getPhonenumber());
            dto.setAadharcard(helper.getAadharcard());
            dto.setPancard(helper.getPancard());
            dto.setAadharcarddocpathfront(helper.getAadharcarddocpathfront());
            dto.setAadharcarddocpathback(helper.getAadharcarddocpathback());
            dto.setPancarddocpath(helper.getPancarddocpath());
            dto.setPhoto(helper.getPhoto());
            dto.setDrivinglicense(helper.getDrivinglicense());

            if (helper.getContactCategory() != null) {
                dto.setContactCategoryId(helper.getContactCategory().getId());
            }
        }

        return dto;
    }
}