package com.khataapp.dto;

import com.khataapp.model.TransactionType;
import java.math.BigDecimal;

public record TransactionResponse(String transactionId, String userId, TransactionType type,
                                  BigDecimal amount, String category, String note,
                                  String partyName, String createdAt, String currency) {
}
