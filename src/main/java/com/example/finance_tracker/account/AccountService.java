package com.example.finance_tracker.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong();
    private final Clock clock;

    public AccountService(Clock clock) {
        this.clock = clock;
        create("Main", new BigDecimal("1000.00"));
    }

    public Account create(String name, BigDecimal balance) {
        requireName(name);
        if (balance == null || balance.signum() < 0) {
            throw new InvalidAccountOperationException("Initial balance must be 0 or more");
        }
        long id = idSequence.incrementAndGet();
        Account account = new Account(id, name, balance, Instant.now(clock));
        accounts.put(id, account);
        log.info("Created account id={} name={} balance= {}",
                account.getId(),account.getName(), account.getBalance());
        return account;
    }

    private static void requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidAccountOperationException("Account name must not be blank");
        }
    }

    private static void requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidAccountOperationException("Amount must be greater than zero");
        }
    }
}
