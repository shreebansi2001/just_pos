package com.crmportal.request.dto;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EventLaborHelperRequestDto {

    @NotNull(message = "Event ID cannot be null")
    private Long eventId;

    @NotNull(message = "Event Function ID cannot be null")
    private Long eventFunctionId;

    @NotNull(message = "Party ID cannot be null")
    private Long partyId;

    @NotNull(message = "Labor Helper IDs list cannot be null")
    private List<Long> laborHelperIds;
}
