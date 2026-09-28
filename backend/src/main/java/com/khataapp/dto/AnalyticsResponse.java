package com.khataapp.dto;

import java.util.List;

public record AnalyticsResponse(
        SummaryResponse summary,
        List<CategoryTotalResponse> categories,
        List<PeriodSummaryResponse> trend,
        String insight) {
}
