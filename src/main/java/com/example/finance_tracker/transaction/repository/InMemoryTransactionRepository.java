package com.example.finance_tracker.transaction.repository;

import com.example.finance_tracker.category.Category;
import com.example.finance_tracker.transaction.Transaction;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class InMemoryTransactionRepository {

    private final Map<Long, Transaction> table =  new ConcurrentSkipListMap<>();
    private final AtomicLong idSequence = new AtomicLong();

    public Transaction save(Transaction transaction) {
        if (transaction.getId() == null) {
            transaction.setId(idSequence.incrementAndGet());
        }
        table.put(transaction.getId(), copy(transaction));
        return copy(transaction);
    }

    public Optional<Transaction> findById(Long id) {
        Transaction row = table.get(id);
        if (row == null) {
            return Optional.empty();
        }
        return Optional.of(copy(row));
    }

    public List<Transaction> findAll() {
        List<Transaction> result = new ArrayList<>();
        for (Transaction row : table.values()) {
            result.add(copy(row));
        }
        return result;
    }

    public boolean existsById(Long id) {
        return table.containsKey(id);
    }

    public void deleteById(Long id) {
        table.remove(id);
    }


    public boolean existsByAccountId(Long accountId) {
        for (Transaction row : table.values()) {
            if (row.getAccountId().equals(accountId)) {
                return true;
            }
        }
        return false;
    }


    public boolean existsByCategoryId(Long categoryId) {
        for (Transaction row : table.values()) {
            if (row.getCategoryId().equals(categoryId)) {
                return true;
            }
        }
        return false;
    }

    private static Transaction copy(Transaction transaction) {
        return new Transaction(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getCategoryId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDate(),
                transaction.getDescription());
    }
}