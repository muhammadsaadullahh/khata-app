package com.khataapp.dto;

import java.time.LocalDate;

public record DateRange(LocalDate from, LocalDate to) {
    public DateRange {
        if (from == null || to == null || from.isAfter(to)) {
            throw new IllegalArgumentException("The date range is invalid");
        }
    }
}
