package com.example.finance.tracker.account;

import com.example.finance.tracker.account.dto.CreateAccountRequest;
import com.example.finance.tracker.account.dto.UpdateAccountRequest;
import com.example.finance.tracker.account.repository.AccountRepository;
import com.example.finance.tracker.security.CurrentUser;
import com.example.finance.tracker.transaction.repository.TransactionRepository;
import com.example.finance.tracker.common.exception.NotFoundException;
import com.example.finance.tracker.common.exception.ValidationException;
import com.example.finance.tracker.user.User;
import com.example.finance.tracker.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;

import java.time.Instant;


@Service
public class AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;
    private final Clock clock;

    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository,
                          UserRepository userRepository,
                          CurrentUser currentUser,
                          Clock clock) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
        this.clock = clock;
    }

    public List<Account> findAll() {
        return (accountRepository.findAllByOwnerIdOrderByIdAsc(currentUser.id()));
    }

    public Account findById(Long id) {
        return accountRepository.findByIdAndOwnerId(id, currentUser.id())
                .orElseThrow(() -> new NotFoundException("Account", id));
    }


    public Account create(CreateAccountRequest request) {

        BigDecimal balance = request.initialBalance();
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        User owner = userRepository.getReferenceById(currentUser.id());
        Account account = new Account(null, request.name().trim(), balance, Instant.now(clock), owner);
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
