package com.khataapp.service;

import com.khataapp.dto.CreateTransactionRequest;
import com.khataapp.dto.SummaryResponse;
import com.khataapp.dto.TransactionResponse;
import com.khataapp.dto.AnalyticsResponse;
import com.khataapp.dto.CategoryTotalResponse;
import com.khataapp.dto.PeriodSummaryResponse;
import com.khataapp.model.ItemType;
import com.khataapp.model.KhataItem;
import com.khataapp.model.TransactionType;
import com.khataapp.repository.KhataRepository;
import com.khataapp.exception.NotFoundException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;

@Service
public class KhataService {
    private final KhataRepository repository;

    public KhataService(KhataRepository repository) { this.repository = repository; }

    public TransactionResponse create(String userId, CreateTransactionRequest request) {
        String id = UUID.randomUUID().toString();
        String createdAt = Instant.now().toString();
        KhataItem item = new KhataItem();
        item.setPk("USER#" + userId);
        item.setSk("TXN#" + createdAt + "#" + id);
        item.setItemType(ItemType.TRANSACTION.name());
        item.setUserId(userId);
        item.setTransactionId(id);
        item.setPartyName(request.partyName());
        item.setTransactionType(request.type().name());
        item.setAmount(request.amount());
        item.setCurrency(request.currency() == null || request.currency().isBlank()
                ? "INR" : request.currency().toUpperCase());
        item.setCategory(request.category());
        item.setNote(request.note());
        item.setCreatedAt(createdAt);
        return toResponse(repository.save(item));
    }

    public List<TransactionResponse> findAll(String userId) {
        return repository.findTransactionsByUserId(userId).stream()
                .sorted(Comparator.comparing(KhataItem::getCreatedAt).reversed())
                .map(this::toResponse).toList();
    }

    public SummaryResponse summary(String userId) {
        return summarize(repository.findTransactionsByUserId(userId));
    }

    public List<TransactionResponse> findByRange(String userId, LocalDate from, LocalDate to) {
        return repository.findTransactionsByUserIdAndDateRange(userId, from, to).stream()
                .sorted(Comparator.comparing(KhataItem::getCreatedAt).reversed())
                .map(this::toResponse).toList();
    }

    public AnalyticsResponse analytics(String userId, LocalDate from, LocalDate to) {
        List<KhataItem> items = repository.findTransactionsByUserIdAndDateRange(userId, from, to);
        Map<String, List<KhataItem>> byCategory = items.stream().collect(java.util.stream.Collectors.groupingBy(
                item -> item.getCategory() == null ? "Other" : item.getCategory(),
                LinkedHashMap::new, java.util.stream.Collectors.toList()));
        List<CategoryTotalResponse> categories = byCategory.entrySet().stream()
                .map(entry -> new CategoryTotalResponse(entry.getKey(),
                        entry.getValue().stream().map(KhataItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add),
                        entry.getValue().size()))
                .sorted(Comparator.comparing(CategoryTotalResponse::total).reversed())
                .toList();
        Map<YearMonth, List<KhataItem>> byMonth = items.stream().collect(java.util.stream.Collectors.groupingBy(
                item -> YearMonth.from(parseDate(item.getCreatedAt())), java.util.TreeMap::new,
                java.util.stream.Collectors.toList()));
        List<PeriodSummaryResponse> trend = byMonth.entrySet().stream().map(entry -> {
            SummaryResponse summary = summarize(entry.getValue());
            return new PeriodSummaryResponse(entry.getKey().toString(), summary.totalCashIn(),
                    summary.totalCashOut(), summary.netBalance());
        }).toList();
        return new AnalyticsResponse(summarize(items), categories, trend, buildInsight(categories, items));
    }

    private SummaryResponse summarize(List<KhataItem> items) {
        BigDecimal cashIn = BigDecimal.ZERO;
        BigDecimal cashOut = BigDecimal.ZERO;
        for (KhataItem item : items) {
            if (TransactionType.GOT.name().equals(item.getTransactionType())) cashIn = cashIn.add(item.getAmount());
            else cashOut = cashOut.add(item.getAmount());
        }
        return new SummaryResponse(cashIn, cashOut, cashIn.subtract(cashOut));
    }

    private String buildInsight(List<CategoryTotalResponse> categories, List<KhataItem> items) {
        if (items.isEmpty()) return "Add a transaction to receive personalized spending insights.";
        if (categories.isEmpty()) return "Your spending pattern is still taking shape.";
        CategoryTotalResponse highest = categories.get(0);
        BigDecimal outcome = summarize(items).totalCashOut();
        if (outcome.signum() > 0) {
            int share = highest.total().multiply(BigDecimal.valueOf(100))
                    .divide(outcome, 0, java.math.RoundingMode.HALF_UP).intValue();
            return "Your highest spending category is " + highest.category() + " at " + share
                    + "% of total outcomes. Review it for possible savings.";
        }
        return "You have recorded income but no outcomes in this period. Keep building your savings.";
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.substring(0, 10));
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Transaction has an invalid date", ex);
        }
    }

    public void delete(String userId, String transactionId) {
        KhataItem item = repository.findTransactionById(userId, transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction was not found"));
        repository.delete(item);
    }

    private TransactionResponse toResponse(KhataItem item) {
        return new TransactionResponse(item.getTransactionId(), item.getUserId(),
                TransactionType.valueOf(item.getTransactionType()), item.getAmount(),
                item.getCategory(), item.getNote(), item.getPartyName(), item.getCreatedAt(),
                item.getCurrency() == null ? "INR" : item.getCurrency());
    }
}
