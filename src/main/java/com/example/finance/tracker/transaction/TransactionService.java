package com.example.finance.tracker.transaction;

import com.example.finance.tracker.account.Account;
import com.example.finance.tracker.account.repository.AccountRepository;
import com.example.finance.tracker.category.Category;
import com.example.finance.tracker.category.repository.CategoryRepository;
import com.example.finance.tracker.common.exception.InsufficientFundsException;
import com.example.finance.tracker.common.exception.NotFoundException;
import com.example.finance.tracker.common.exception.ValidationException;
import com.example.finance.tracker.transaction.dto.TransactionRequest;
import com.example.finance.tracker.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.example.finance.tracker.transaction.dto.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.finance.tracker.security.CurrentUser;

import java.time.Clock;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUser currentUser;
    private final Clock clock;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CategoryRepository categoryRepository,
                              CurrentUser currentUser,
                              Clock clock) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.currentUser = currentUser;
        this.clock = clock;
    }

    @Transactional
    public Page<TransactionResponse> search(Long accountId, Long categoryId,
                                            LocalDate from, LocalDate to, Pageable pageable) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ValidationException("'from' must be on or before 'to'");
        }
        return transactionRepository.search(currentUser.id(), accountId, categoryId, from, to, pageable)
                .map(transaction -> TransactionResponse.from(transaction));
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Transaction findById(Long id) {
        return transactionRepository.findByIdAndAccountOwnerId(id, currentUser.id())
                .orElseThrow(() -> new NotFoundException("Transaction", id));
    }

    @Transactional
    public Transaction create(TransactionRequest request) {
        Account account = getAccount(request.accountId());
        Category category = getCategory(request);

        LocalDate date = request.date();
        if (date == null) {
            date = LocalDate.now(clock);
        }

        Transaction transaction = new Transaction(null, account, category,
                request.amount(), request.type(), date, request.description());


        addToBalance(account, transaction);
        requireEnoughMoney(account);

        accountRepository.save(account);
        return transactionRepository.save(transaction);

    }

    @Transactional
    public Transaction update(Long id, TransactionRequest request) {
        Transaction transaction = findById(id);
        requireNotTransfer(transaction);


        if (!request.accountId().equals(transaction.getAccount().getId())) {
            throw new ValidationException("A transaction cannot be moved to another account");
        }

        Account account = transaction.getAccount();
        Category category = getCategory(request);

        removeFromBalance(account, transaction);

        transaction.setCategory(category);
        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        if (request.date() != null) {
            transaction.setDate(request.date());
        }
        transaction.setDescription(request.description());

        addToBalance(account, transaction);
        requireEnoughMoney(account);

        accountRepository.save(account);
        return transactionRepository.save(transaction);
    }

    public void delete(Long id) {
        Transaction transaction = findById(id);
        requireNotTransfer(transaction);
        Account account = transaction.getAccount();

        removeFromBalance(account, transaction);
        requireEnoughMoney(account);

        accountRepository.save(account);
        transactionRepository.deleteById(id);
    }


    private static void addToBalance(Account account, Transaction transaction) {
        if (transaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
        } else {
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        }
    }


    private static void removeFromBalance(Account account, Transaction transaction) {
        if (transaction.getType() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
        } else {
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        }
    }

    //409
    private static void requireEnoughMoney(Account account) {
        if (account.getBalance().signum() < 0) {
            throw new InsufficientFundsException(account.getId());
        }
    }

    private static void requireNotTransfer(Transaction transaction) {
        if (transaction.getTransferId() != null) {
            throw new ValidationException("Transaction " + transaction.getId()
                    + " is part of a transfer and cannot be changed on its own");
        }
    }

    //404
    private Account getAccount(Long id) {
        return accountRepository.findByIdAndOwnerId(id, currentUser.id())
                .orElseThrow(() -> new NotFoundException("Account", id));
    }

    private Category getCategory(TransactionRequest request) {
        if (request.categoryId() == null) {
            if (request.type() == TransactionType.EXPENSE) {
                throw new ValidationException("categoryId is required for an expense");
            }
            return null;
        }
        return categoryRepository.findVisibleById(request.categoryId(), currentUser.id())
                .orElseThrow(() -> new NotFoundException("Category", request.categoryId()));
    }
}