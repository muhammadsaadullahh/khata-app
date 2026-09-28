package com.khataapp.dto;

import java.math.BigDecimal;

public record SummaryResponse(BigDecimal totalCashIn, BigDecimal totalCashOut, BigDecimal netBalance) {
}
