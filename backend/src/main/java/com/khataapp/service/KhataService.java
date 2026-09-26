package com.khataapp.service;

import com.khataapp.dto.CreateTransactionRequest;
import com.khataapp.dto.SummaryResponse;
import com.khataapp.dto.TransactionResponse;
import com.khataapp.model.ItemType;
import com.khataapp.model.KhataItem;
import com.khataapp.model.TransactionType;
import com.khataapp.repository.KhataRepository;
import com.khataapp.exception.NotFoundException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
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
        item.setCategory(request.category());
        item.setNote(request.note());
        item.setCreatedAt(createdAt);
        return toResponse(repository.save(item));
    }

    public List<TransactionResponse> findAll(String userId) {
        return repository.findTransactionsByUserId(userId).stream().map(this::toResponse).toList();
    }

    public SummaryResponse summary(String userId) {
        BigDecimal cashIn = BigDecimal.ZERO;
        BigDecimal cashOut = BigDecimal.ZERO;
        for (KhataItem item : repository.findTransactionsByUserId(userId)) {
            if (TransactionType.GOT.name().equals(item.getTransactionType())) cashIn = cashIn.add(item.getAmount());
            else cashOut = cashOut.add(item.getAmount());
        }
        return new SummaryResponse(cashIn, cashOut, cashIn.subtract(cashOut));
    }

    public void delete(String userId, String transactionId) {
        KhataItem item = repository.findTransactionById(userId, transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction was not found"));
        repository.delete(item);
    }

    private TransactionResponse toResponse(KhataItem item) {
        return new TransactionResponse(item.getTransactionId(), item.getUserId(),
                TransactionType.valueOf(item.getTransactionType()), item.getAmount(),
                item.getCategory(), item.getNote(), item.getPartyName(), item.getCreatedAt());
    }
}
