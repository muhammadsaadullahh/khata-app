package com.khataapp.controller;

import com.khataapp.dto.CreateTransactionRequest;
import com.khataapp.dto.SummaryResponse;
import com.khataapp.dto.TransactionResponse;
import com.khataapp.dto.AnalyticsResponse;
import com.khataapp.service.KhataService;
import com.khataapp.service.ExportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/v1/khata")
public class KhataController {
    private final KhataService service;
    private final ExportService exportService;

    public KhataController(KhataService service, ExportService exportService) {
        this.service = service;
        this.exportService = exportService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody CreateTransactionRequest request,
                                      Authentication authentication) {
        requireAuthenticated(authentication);
        return service.create(authentication.getName(), request);
    }

    @GetMapping("/{userId}")
    public List<TransactionResponse> findAll(@PathVariable String userId, Authentication authentication) {
        requireOwner(authentication, userId);
        return service.findAll(userId);
    }

    @GetMapping("/{userId}/summary")
    public SummaryResponse summary(@PathVariable String userId, Authentication authentication) {
        requireOwner(authentication, userId);
        return service.summary(userId);
    }

    @GetMapping("/{userId}/range")
    public List<TransactionResponse> findByRange(@PathVariable String userId,
                                                  @RequestParam(required = false) String from,
                                                  @RequestParam(required = false) String to,
                                                  @RequestParam(defaultValue = "month") String period,
                                                  Authentication authentication) {
        requireOwner(authentication, userId);
        LocalDate end = to == null ? LocalDate.now() : parseDate(to);
        LocalDate start = from == null ? periodStart(period, end) : parseDate(from);
        return service.findByRange(userId, start, end);
    }

    @GetMapping("/{userId}/analytics")
    public AnalyticsResponse analytics(@PathVariable String userId,
                                       @RequestParam(required = false) String from,
                                       @RequestParam(required = false) String to,
                                       @RequestParam(defaultValue = "month") String period,
                                       Authentication authentication) {
        requireOwner(authentication, userId);
        LocalDate end = to == null ? LocalDate.now() : parseDate(to);
        LocalDate start = from == null ? periodStart(period, end) : parseDate(from);
        return service.analytics(userId, start, end);
    }

    @GetMapping("/{userId}/export/{format}")
    public ResponseEntity<byte[]> export(@PathVariable String userId,
                                         @PathVariable String format,
                                         @RequestParam(defaultValue = "month") String period,
                                         Authentication authentication) {
        requireOwner(authentication, userId);
        LocalDate end = LocalDate.now();
        List<TransactionResponse> transactions = service.findByRange(userId, periodStart(period, end), end);
        boolean pdf = "pdf".equalsIgnoreCase(format);
        if (!pdf && !"excel".equalsIgnoreCase(format) && !"xlsx".equalsIgnoreCase(format)) {
            throw new IllegalArgumentException("Export format must be pdf or excel");
        }
        byte[] content = pdf ? exportService.pdf(transactions) : exportService.excel(transactions);
        String filename = "khata-transactions." + (pdf ? "pdf" : "xlsx");
        MediaType mediaType = pdf ? MediaType.APPLICATION_PDF
                : MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(mediaType).body(content);
    }

    @DeleteMapping("/{userId}/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String userId,
                       @PathVariable String transactionId,
                       Authentication authentication) {
        requireOwner(authentication, userId);
        service.delete(userId, transactionId);
    }

    private void requireOwner(Authentication authentication, String userId) {
        if (authentication == null || !authentication.isAuthenticated() || !userId.equals(authentication.getName())) {
            throw new org.springframework.security.access.AccessDeniedException("User does not own this khata");
        }
    }

    private void requireAuthenticated(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException("Authentication is required");
        }
    }

    private LocalDate parseDate(String value) {
            try {
                return LocalDate.parse(value);
            } catch (RuntimeException ex) {
                throw new IllegalArgumentException("Dates must use yyyy-MM-dd format");
            }
        }

    private LocalDate periodStart(String period, LocalDate end) {
            return switch (period.toLowerCase()) {
                case "month", "monthly" -> end.withDayOfMonth(1);
                case "3-month", "quarter" -> end.minusMonths(2).withDayOfMonth(1);
                case "6-month", "half-year" -> end.minusMonths(5).withDayOfMonth(1);
                case "year", "yearly" -> end.with(TemporalAdjusters.firstDayOfYear());
                default -> throw new IllegalArgumentException("Unsupported period: " + period);
            };
    }
}
