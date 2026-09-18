package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.ExtraChargesHeadingEntity;
import com.crmportal.entity.ExtraChargesRowEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.ExtraChargesHeadingRepository;
import com.crmportal.repository.ExtraChargesRowRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.ExtraChargesSaveRequestDto;
import com.crmportal.request.dto.ExtraChargesSaveRequestDto.ExtraChargesHeadingRequestDto;
import com.crmportal.request.dto.ExtraChargesSaveRequestDto.ExtraChargesRowRequestDto;
import com.crmportal.response.dto.ExtraChargesResponseDto;
import com.crmportal.response.dto.ExtraChargesResponseDto.ExtraChargesHeadingResponseDto;
import com.crmportal.response.dto.ExtraChargesResponseDto.ExtraChargesRowResponseDto;
import com.crmportal.service.ExtraChargesService;

@Service
@Transactional
public class ExtraChargesServiceImpl implements ExtraChargesService {

    @Autowired
    private ExtraChargesHeadingRepository headingRepository;

    @Autowired
    private ExtraChargesRowRepository rowRepository;

    @Autowired
    private EventMasterRepository eventMasterRepository;

    @Autowired
    private EventFunctionMasterRepository eventFunctionMasterRepository;

    @Autowired
    private UserMasterRepository userMasterRepository;

    // ─── Save or Update ────────────────────────────────────────────────────

    @Override
    public ExtraChargesResponseDto saveOrUpdateExtraCharges(ExtraChargesSaveRequestDto request) {

        EventMasterEntity event = eventMasterRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found: " + request.getEventId()));

        EventFunctionMasterEntity eventFunction = null;
        if (request.getEventFunctionId() != null && request.getEventFunctionId() != -1) {
            eventFunction = eventFunctionMasterRepository.findById(request.getEventFunctionId())
                    .orElseThrow(() -> new RuntimeException(
                            "Event Function not found: " + request.getEventFunctionId()));
        }
        // eventFunctionId = -1 means "All Functions"

        UserMasterEntity user = userMasterRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        for (ExtraChargesHeadingRequestDto headingDto : request.getHeadings()) {

            ExtraChargesHeadingEntity headingEntity;
            if (headingDto.getId() != null && headingDto.getId() > 0) {
                headingEntity = headingRepository
                        .findByIdAndIsDeleteFalse(headingDto.getId())
                        .orElseThrow(() -> new RuntimeException(
                                "Extra Charges Heading not found with id: " + headingDto.getId()));
            } else {
                headingEntity = new ExtraChargesHeadingEntity();
            }

            headingEntity.setHeadingName(headingDto.getHeadingName());
            headingEntity.setHeadingNameHindi(headingDto.getHeadingNameHindi());
            headingEntity.setHeadingNameGujarati(headingDto.getHeadingNameGujarati());
            headingEntity.setSubHeadingName(headingDto.getSubHeadingName());
            headingEntity.setSubHeadingNameHindi(headingDto.getSubHeadingNameHindi());
            headingEntity.setSubHeadingNameGujarati(headingDto.getSubHeadingNameGujarati());
            headingEntity.setEvent(event);
            headingEntity.setEventFunction(eventFunction);
            headingEntity.setUser(user);
            headingEntity.setIsDelete(false);
            headingEntity = headingRepository.save(headingEntity);

            if (headingDto.getRows() != null) {
                for (ExtraChargesRowRequestDto rowDto : headingDto.getRows()) {

                    ExtraChargesRowEntity row;
                    if (rowDto.getId() != null && rowDto.getId() > 0) {
                        row = rowRepository.findById(rowDto.getId())
                                .orElseThrow(() -> new RuntimeException(
                                        "Row not found: " + rowDto.getId()));
                    } else {
                        row = new ExtraChargesRowEntity();
                        row.setHeading(headingEntity);
                    }

                    row.setChargeDate(rowDto.getChargeDate());
                    row.setChargeStartTime(rowDto.getChargeStartTime());
                    row.setChargeEndTime(rowDto.getChargeEndTime());
                    row.setSession(rowDto.getSession());
                    row.setPersonItem(rowDto.getPersonItem());
                    row.setRate(rowDto.getRate() != null ? rowDto.getRate() : BigDecimal.ZERO);
                    row.setTotal(rowDto.getTotal() != null ? rowDto.getTotal() : BigDecimal.ZERO);
                    row.setIsDelete(false);
                    rowRepository.save(row);
                }
            }
        }

        return getExtraCharges(request.getEventId(), request.getEventFunctionId(), request.getUserId());
    }

    // ─── Get ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public ExtraChargesResponseDto getExtraCharges(Long eventId, Long eventFunctionId, Long userId) {

        List<ExtraChargesHeadingEntity> headings;
        if (eventFunctionId == null || eventFunctionId == -1) {
            headings = headingRepository.findAllByEventId(eventId);
        } else {
            headings = headingRepository.findByEventIdAndEventFunctionId(eventId, eventFunctionId);
        }

        List<ExtraChargesHeadingResponseDto> headingDtos = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (ExtraChargesHeadingEntity heading : headings) {

            ExtraChargesHeadingResponseDto headingDto = new ExtraChargesHeadingResponseDto();
            headingDto.setId(heading.getId());
            headingDto.setHeadingName(heading.getHeadingName());
            headingDto.setHeadingNameHindi(heading.getHeadingNameHindi());
            headingDto.setHeadingNameGujarati(heading.getHeadingNameGujarati());
            headingDto.setSubHeadingName(heading.getSubHeadingName());
            headingDto.setSubHeadingNameGujarati(heading.getSubHeadingNameGujarati());
            headingDto.setSubHeadingNameHindi(heading.getSubHeadingNameHindi());

            List<ExtraChargesRowEntity> rows = rowRepository.findAllByHeadingAndIsDeleteFalse(heading);
            List<ExtraChargesRowResponseDto> rowDtos = new ArrayList<>();
            BigDecimal headingTotal = BigDecimal.ZERO;

            for (ExtraChargesRowEntity row : rows) {
                ExtraChargesRowResponseDto rowDto = new ExtraChargesRowResponseDto();
                rowDto.setId(row.getId());
                rowDto.setChargeDate(row.getChargeDate());
                rowDto.setChargeStartTime(row.getChargeStartTime());
                rowDto.setChargeEndTime(row.getChargeEndTime());
                rowDto.setSession(row.getSession());
                rowDto.setPersonItem(row.getPersonItem());
                rowDto.setRate(row.getRate());
                rowDto.setTotal(row.getTotal());
                rowDtos.add(rowDto);

                if (row.getTotal() != null) {
                    headingTotal = headingTotal.add(row.getTotal());
                }
            }

            headingDto.setRows(rowDtos);
            headingDto.setHeadingTotal(headingTotal);
            headingDtos.add(headingDto);
            grandTotal = grandTotal.add(headingTotal);
        }

        ExtraChargesResponseDto response = new ExtraChargesResponseDto();
        response.setEventId(eventId);
        response.setEventFunctionId(eventFunctionId != null && eventFunctionId == -1 ? null : eventFunctionId);
        response.setHeadings(headingDtos);
        response.setGrandTotal(grandTotal);
        return response;
    }

    // ─── Delete Heading ────────────────────────────────────────────────────

    @Override
    public Boolean deleteHeading(Long headingId) {
        ExtraChargesHeadingEntity heading = headingRepository.findByIdAndIsDeleteFalse(headingId)
                .orElseThrow(() -> new RuntimeException("Heading not found: " + headingId));

        // Soft delete all rows under this heading first
        List<ExtraChargesRowEntity> rows = rowRepository.findAllByHeadingAndIsDeleteFalse(heading);
        for (ExtraChargesRowEntity row : rows) {
            row.setIsDelete(true);
            rowRepository.save(row);
        }

        heading.setIsDelete(true);
        headingRepository.save(heading);
        return true;
    }

    // ─── Delete Row ────────────────────────────────────────────────────────

    @Override
    public Boolean deleteRow(Long rowId) {
        ExtraChargesRowEntity row = rowRepository.findById(rowId)
                .orElseThrow(() -> new RuntimeException("Row not found: " + rowId));
        row.setIsDelete(true);
        rowRepository.save(row);
        return true;
    }
}