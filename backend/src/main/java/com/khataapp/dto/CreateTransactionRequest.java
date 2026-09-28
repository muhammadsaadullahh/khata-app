package com.khataapp.dto;

import com.khataapp.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record CreateTransactionRequest(
        @NotNull TransactionType type,
        @NotNull @DecimalMin(value = "0.01") @jakarta.validation.constraints.Digits(integer = 12, fraction = 2) BigDecimal amount,
        @NotBlank @Size(max = 80) String category,
        @Size(max = 500) String note,
        @NotBlank @Size(max = 200) String partyName,
        @Pattern(regexp = "[A-Za-z]{3}", message = "Currency must be a 3-letter ISO code")
        String currency) {
}
