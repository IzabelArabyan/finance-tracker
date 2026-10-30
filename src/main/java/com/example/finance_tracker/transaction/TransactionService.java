package com.example.finance_tracker.transaction;

import com.example.finance_tracker.account.Account;
import com.example.finance_tracker.account.repository.InMemoryAccountRepository;
import com.example.finance_tracker.category.repository.InMemoryCategoryRepository;
import com.example.finance_tracker.transaction.dto.TransactionRequest;
import com.example.finance_tracker.transaction.repository.InMemoryTransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final InMemoryTransactionRepository transactionRepository;
    private final InMemoryAccountRepository accountRepository;
    private final InMemoryCategoryRepository categoryRepository;

    public TransactionService(InMemoryTransactionRepository transactionRepository,
                              InMemoryAccountRepository accountRepository,
                              InMemoryCategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Transaction findById(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction " + id + " not found");
        }
        return transaction.get();
    }

    public Transaction create(TransactionRequest request) {
        checkAccountAndCategoryExist(request);

        LocalDate date = request.date();
        if (date == null) {
            date = LocalDate.now();
        }

        Transaction transaction = new Transaction(null, request.accountId(), request.categoryId(),
                request.amount(), request.type(), date, request.description());

        Transaction saved = transactionRepository.save(transaction);
        addToBalance(saved);
        return saved;
    }

    public Transaction update(Long id, TransactionRequest request) {
        Transaction transaction = findById(id);
        checkAccountAndCategoryExist(request);

        removeFromBalance(transaction);

        transaction.setAccountId(request.accountId());
        transaction.setCategoryId(request.categoryId());
        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        if (request.date() != null) {
            transaction.setDate(request.date());
        }
        transaction.setDescription(request.description());

        Transaction saved = transactionRepository.save(transaction);
        addToBalance(saved);              // apply the new version
        return saved;
    }

    public void delete(Long id) {
        Transaction transaction = findById(id);
        removeFromBalance(transaction);
        transactionRepository.deleteById(id);
    }

    //helper methods
    private void addToBalance(Transaction transaction) {
        Account account = getAccount(transaction.getAccountId());
        if (transaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
        } else {
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        }
        accountRepository.save(account);
    }

    private void removeFromBalance(Transaction transaction) {
        Account account = getAccount(transaction.getAccountId());
        if (transaction.getType() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
        } else {
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        }
        accountRepository.save(account);
    }

    private Account getAccount(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account " + id + " not found");
        }
        return account.get();
    }

    private void checkAccountAndCategoryExist(TransactionRequest request) {
        getAccount(request.accountId());
        if (!categoryRepository.existsById(request.categoryId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category " + request.categoryId() + " not found");
        }
    }


}
