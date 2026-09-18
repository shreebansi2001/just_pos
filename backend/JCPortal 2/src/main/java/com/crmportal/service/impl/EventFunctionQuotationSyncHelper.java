package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.crmportal.entity.EventFunctionQuotationItemEntity;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;
import com.crmportal.response.dto.EventFunctionQuotationItemsResponseDto;

@Component
public class EventFunctionQuotationSyncHelper {

	public Set<Long> getMasterEventFunctionIds(List<EventFunctionMasterResponseDto> eventFunctions) {

		return eventFunctions.stream().map(EventFunctionMasterResponseDto::getId).collect(Collectors.toSet());
	}

	public Set<Long> getItemEventFunctionIds(List<EventFunctionQuotationItemsResponseDto> items) {

		return items.stream().filter(i -> Boolean.TRUE.equals(i.getIsEventFunction()))
				.map(EventFunctionQuotationItemsResponseDto::getEventFunctionId).filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}

	public void syncMissingMasterFunctions(List<EventFunctionQuotationItemsResponseDto> items,
			List<EventFunctionMasterResponseDto> eventFunctions, Set<Long> itemEventFunctionIds) {

		for (EventFunctionMasterResponseDto ef : eventFunctions) {

			if (!itemEventFunctionIds.contains(ef.getId())) {

				EventFunctionQuotationItemsResponseDto dto = new EventFunctionQuotationItemsResponseDto();

				dto.setId(null);
				dto.setEventFunctionId(ef.getId());
				dto.setFunctionName(ef.getFunction().getNameEnglish());
				dto.setFunctionDate(ef.getFunctionStartDateTime());
				dto.setPax(ef.getPax());
				dto.setExtraPax(0);
				dto.setRatePerPlate(BigDecimal.ZERO);
				dto.setAmount(BigDecimal.ZERO);
				dto.setIsEventFunction(true);

				items.add(dto);
			}
		}
	}

	public List<Long> findDeletedMasterFunctionItems(List<EventFunctionQuotationItemsResponseDto> items,
			Set<Long> masterEventFunctionIds) {

		List<Long> deletedIds = new ArrayList<>();

		for (EventFunctionQuotationItemsResponseDto item : items) {

			if (Boolean.TRUE.equals(item.getIsEventFunction()) && item.getEventFunctionId() != null
					&& !masterEventFunctionIds.contains(item.getEventFunctionId()) && item.getId() != null) {

				deletedIds.add(item.getId());
			}
		}
		return deletedIds;
	}

	public boolean shouldCopyToInvoice(EventFunctionQuotationItemEntity itemDto, Set<Long> masterEventFunctionIds) {

		return !(Boolean.TRUE.equals(itemDto.getIsEventFunction()) && itemDto.getEventFunctionId() != null
				&& !masterEventFunctionIds.contains(itemDto.getEventFunctionId()));
	}
}
