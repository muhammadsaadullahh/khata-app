package com.khataapp.dto;

import java.math.BigDecimal;

public record PeriodSummaryResponse(String period, BigDecimal income, BigDecimal outcome, BigDecimal net) {
}
