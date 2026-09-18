package com.crmportal.pos.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PosStatsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int freeTables;
    private int inUseTables;
    private int activeKots;
    private double todaySales;
}
