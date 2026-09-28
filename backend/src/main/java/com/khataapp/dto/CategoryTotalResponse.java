package com.khataapp.dto;

import java.math.BigDecimal;

public record CategoryTotalResponse(String category, BigDecimal total, long transactionCount) {
}
