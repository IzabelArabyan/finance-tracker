package com.example.finance_tracker.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;
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
    public List<Account> findAll() {
        return List.copyOf(accounts.values());
    }

    public Optional<Account> findById(Long id) {
        return Optional.ofNullable(accounts.get(id));
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
    public Optional<Account> rename(Long id, String newName) {
        requireName(newName);
        Account existing = accounts.get(id);
        if (existing == null) {
            return Optional.empty();
        }
        Account updated = existing.withName(newName);
        accounts.put(id, updated);
        log.info("Renamed account id={} to name={}", id, updated.getName());
        return Optional.of(updated);
    }

    public Optional<Account> deposit(Long id, BigDecimal amount) {
        requirePositive(amount);
        Account existing = accounts.get(id);
        if (existing == null) {
            return Optional.empty();
        }
        BigDecimal newBalance = existing.getBalance().add(amount);
        Account updated = existing.withBalance(newBalance);
        accounts.put(id, updated);
        log.info("Deposited {} to account id={}, new balance={}", amount, id, newBalance);
        return Optional.of(updated);
    }

    public Optional<Account> withdraw(Long id, BigDecimal amount) {
        requirePositive(amount);
        Account existing = accounts.get(id);
        if (existing == null) {
            return Optional.empty();
        }
        if (existing.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(id, existing.getBalance(), amount);
        }
        BigDecimal newBalance = existing.getBalance().subtract(amount);
        Account updated = existing.withBalance(newBalance);
        accounts.put(id, updated);
        log.info("Withdrew {} from account id={}, new balance={}", amount, id, newBalance);
        return Optional.of(updated);
    }
    public  Optional<Account> delete(Long id) {
        Account removed = accounts.remove(id);
        if (removed == null) {
            return Optional.empty();
        }
        log.info("Deleted account id={} name={}", id, removed.getName());
        return Optional.of(removed);
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
