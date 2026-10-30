package com.example.finance_tracker.account.repository;

import com.example.finance_tracker.account.Account;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class InMemoryAccountRepository {

    private final Map<Long, Account> table = new ConcurrentSkipListMap<>();
    private final AtomicLong idSequence = new AtomicLong();

    public Account save(Account account) {
        if (account.getId() == null) {
            account.setId(idSequence.incrementAndGet());
        }
        table.put(account.getId(), copy(account));
        return copy(account);
    }

    public  Optional<Account> findById(Long id) {
        Account row = table.get(id);
        if (row == null) {
            return Optional.empty();
        }
        return Optional.of(copy(row));
    }

    public  List<Account> findAll() {
        List<Account> result = new ArrayList<>();
        for (Account row : table.values()) {
            result.add(copy(row));
        }
        return result;
    }

    public  boolean existsById(Long id) {
        return table.containsKey(id);
    }

    public  void deleteById(Long id) {
        table.remove(id);
    }

    private static Account copy(Account account) {
        return new Account(
                account.getId(),
                account.getName(),
                account.getBalance(),
                account.getCreatedAt());
    }
}