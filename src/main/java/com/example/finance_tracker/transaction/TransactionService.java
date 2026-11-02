package com.example.finance_tracker.transaction;

import com.example.finance_tracker.account.Account;
import com.example.finance_tracker.account.repository.InMemoryAccountRepository;
import com.example.finance_tracker.category.repository.InMemoryCategoryRepository;
import com.example.finance_tracker.common.exception.InsufficientFundsException;
import com.example.finance_tracker.common.exception.NotFoundException;
import com.example.finance_tracker.common.exception.ValidationException;
import com.example.finance_tracker.transaction.dto.TransactionRequest;
import com.example.finance_tracker.transaction.repository.InMemoryTransactionRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final InMemoryTransactionRepository transactionRepository;
    private final InMemoryAccountRepository accountRepository;
    private final InMemoryCategoryRepository categoryRepository;
    private final Clock clock;

    public TransactionService(InMemoryTransactionRepository transactionRepository,
                              InMemoryAccountRepository accountRepository,
                              InMemoryCategoryRepository categoryRepository,
                              Clock clock) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.clock = clock;
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Transaction findById(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isEmpty()) {
            throw new NotFoundException("Transaction", id);
        }
        return transaction.get();
    }

    public Transaction create(TransactionRequest request) {
        Account account = getAccount(request.accountId());
        checkCategory(request);

        LocalDate date = request.date();
        if (date == null) {
            date = LocalDate.now(clock);
        }

        Transaction transaction = new Transaction(null, request.accountId(), request.categoryId(),
                request.amount(), request.type(), date, request.description());


        addToBalance(account, transaction);
        requireEnoughMoney(account);

        Transaction saved = transactionRepository.save(transaction);
        accountRepository.save(account);
        return saved;
    }


    public Transaction update(Long id, TransactionRequest request) {
        Transaction transaction = findById(id);


        if (!request.accountId().equals(transaction.getAccountId())) {
            throw new ValidationException("A transaction cannot be moved to another account");
        }

        Account account = getAccount(transaction.getAccountId());
        checkCategory(request);

        removeFromBalance(account, transaction);

        transaction.setCategoryId(request.categoryId());
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
        Account account = getAccount(transaction.getAccountId());

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

    private Account getAccount(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            throw new NotFoundException("Account", id);
        }
        return account.get();
    }

    //category required for expense not for income
    private void checkCategory(TransactionRequest request) {
        if (request.categoryId() == null) {
            if (request.type() == TransactionType.EXPENSE) {
                throw new ValidationException("categoryId is required for an expense");
            }
            return;
        }
        if (!categoryRepository.existsById(request.categoryId())) {
            throw new NotFoundException("Category", request.categoryId());
        }
    }
}