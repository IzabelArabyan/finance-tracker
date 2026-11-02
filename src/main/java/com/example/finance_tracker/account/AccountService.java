package com.example.finance_tracker.account;

import com.example.finance_tracker.account.dto.CreateAccountRequest;
import com.example.finance_tracker.account.dto.UpdateAccountRequest;
import com.example.finance_tracker.account.repository.InMemoryAccountRepository;
import com.example.finance_tracker.transaction.repository.InMemoryTransactionRepository;
import com.example.finance_tracker.common.exception.NotFoundException;
import com.example.finance_tracker.common.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;

import java.util.Optional;
import java.time.Instant;


@Service
public class AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final InMemoryAccountRepository accountRepository;
    private final InMemoryTransactionRepository transactionRepository;
    private final Clock clock;

    public AccountService(InMemoryAccountRepository accountRepository, InMemoryTransactionRepository transactionRepository, Clock clock) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.clock = clock;
    }

    public List<Account> findAll() {
        return (accountRepository.findAll());
    }

    public Account findById(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            throw new NotFoundException("Account", id);
        }
        return account.get();
    }

    public Account create(CreateAccountRequest request) {

        BigDecimal balance = request.initialBalance();
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }

        Account account = new Account(null, request.name().trim(), balance, Instant.now(clock));
        Account saved = accountRepository.save(account);
        log.info("Created account id={} name={} balance={}", saved.getId(), saved.getName(), saved.getBalance());
        return saved;
    }

    public Account update(Long id, UpdateAccountRequest request) {
        Account account = findById(id);
        account.setName(request.name().trim());
        Account saved = accountRepository.save(account);
        log.info("Renamed account id={} to name={}", id, saved.getName());
        return saved;
    }

    public void delete(Long id) {
        findById(id);
        if (transactionRepository.existsByAccountId(id)) {
            throw new ValidationException("Account " + id + " still has transactions, delete them first");
        }
        accountRepository.deleteById(id);
        log.info("Deleted account id={}", id);
    }



}
