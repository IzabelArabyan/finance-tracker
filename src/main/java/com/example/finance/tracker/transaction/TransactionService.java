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

import java.time.Clock;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final Clock clock;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CategoryRepository categoryRepository,
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
        return transactionRepository.findById(id)
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
    //404
    private Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account", id));
    }

    private Category getCategory(TransactionRequest request) {
        if (request.categoryId() == null) {
            if (request.type() == TransactionType.EXPENSE) {
                throw new ValidationException("categoryId is required for an expense");
            }
            return null;
        }
        return categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new NotFoundException("Category", request.categoryId()));
    }
}