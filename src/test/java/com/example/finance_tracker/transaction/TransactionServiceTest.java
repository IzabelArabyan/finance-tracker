package com.example.finance_tracker.transaction;

import com.example.finance_tracker.account.Account;
import com.example.finance_tracker.account.repository.InMemoryAccountRepository;
import com.example.finance_tracker.category.repository.InMemoryCategoryRepository;
import com.example.finance_tracker.transaction.dto.TransactionRequest;
import com.example.finance_tracker.transaction.repository.InMemoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final LocalDate DATE = LocalDate.of(2026, 1, 15);

    @Mock
    private InMemoryTransactionRepository transactionRepository;

    @Mock
    private InMemoryAccountRepository accountRepository;

    @Mock
    private InMemoryCategoryRepository categoryRepository;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(transactionRepository, accountRepository, categoryRepository);
    }

    //create
    //1
    @Test
    void createIncomeAddsMoney() {
        Account account = new Account(1L, "Main", new BigDecimal("1000.00"), Instant.now());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).then(returnsFirstArg());

        transactionService.create(new TransactionRequest(1L, 1L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null));

        // 1000.00 + 100.00
        assertEquals(new BigDecimal("1100.00"), account.getBalance());
    }

    //2
    @Test
    void createExpenseTakesMoney() {
        Account account = new Account(1L, "Main", new BigDecimal("1000.00"), Instant.now());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).then(returnsFirstArg());

        transactionService.create(new TransactionRequest(1L, 1L, new BigDecimal("100.00"), TransactionType.EXPENSE, DATE, null));

        // 1000.00 - 100.00
        assertEquals(new BigDecimal("900.00"), account.getBalance());
    }

    //3
    @Test
    void createSavesTheAccount() {
        Account account = new Account(1L, "Main", new BigDecimal("1000.00"), Instant.now());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).then(returnsFirstArg());

        transactionService.create(new TransactionRequest(1L, 1L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null));

        verify(accountRepository).save(account);
    }


    //4
    @Test
    void createWithoutDateUsesToday() {
        Account account = new Account(1L, "Main", new BigDecimal("1000.00"), Instant.now());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).then(returnsFirstArg());

        Transaction result = transactionService.create(
                new TransactionRequest(1L, 1L, new BigDecimal("100.00"), TransactionType.INCOME, null, null));

        assertEquals(LocalDate.now(), result.getDate());
    }

    //5
    @Test
    void createGives404WhenAccountIsMissing() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            transactionService.create(new TransactionRequest(99L, 1L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null));
            fail("Expected a 404 error");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals("Account 99 not found", e.getReason());
        }
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).save(any(Account.class));
    }

    //6
    @Test
    void createGives404WhenCategoryIsMissing() {
        Account account = new Account(1L, "Main", new BigDecimal("1000.00"), Instant.now());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.existsById(99L)).thenReturn(false);

        try {
            transactionService.create(new TransactionRequest(1L, 99L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null));
            fail("Expected a 404 error");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertEquals("Category 99 not found", e.getReason());
        }
        assertEquals(new BigDecimal("1000.00"), account.getBalance());
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).save(any(Account.class));
    }

    //update
    //1
    @Test
    void updateIncomeAmountGoesUp() {
        // 1000.00 + income 100.00 = 1100 update incode to 150
        Account account = new Account(1L, "Main", new BigDecimal("1100.00"), Instant.now());
        Transaction old = new Transaction(5L, 1L, 1L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(old));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).then(returnsFirstArg());

        transactionService.update(5L, new TransactionRequest(1L, 1L, new BigDecimal("150.00"), TransactionType.INCOME, DATE, null));

        // 1100.00 - 100.00 + 150.00
        assertEquals(new BigDecimal("1150.00"), account.getBalance());
    }

    //2
    @Test
    void updateExpenseAmountGoesDown() {
        // 1000.00 - expense 100.00 = 900 expense =  30.00.
        Account account = new Account(1L, "Main", new BigDecimal("900.00"), Instant.now());
        Transaction old = new Transaction(5L, 1L, 1L, new BigDecimal("100.00"), TransactionType.EXPENSE, DATE, null);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(old));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).then(returnsFirstArg());

        transactionService.update(5L, new TransactionRequest(1L, 1L, new BigDecimal("30.00"), TransactionType.EXPENSE, DATE, null));

        // 900.00 + 100.00 - 30.00
        assertEquals(new BigDecimal("970.00"), account.getBalance());
    }

    //3
    @Test
    void updateIncomeToExpense() {
        Account account = new Account(1L, "Main", new BigDecimal("1100.00"), Instant.now());
        Transaction old = new Transaction(5L, 1L, 1L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(old));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).then(returnsFirstArg());

        transactionService.update(5L, new TransactionRequest(1L, 1L, new BigDecimal("100.00"), TransactionType.EXPENSE, DATE, null));

        // 1100.00 - 100.00 (undo income) - 100.00 (apply expense)
        assertEquals(new BigDecimal("900.00"), account.getBalance());
    }

    //4
    @Test
    void updateExpenseToIncome() {
        Account account = new Account(1L, "Main", new BigDecimal("900.00"), Instant.now());
        Transaction old = new Transaction(5L, 1L, 1L, new BigDecimal("100.00"), TransactionType.EXPENSE, DATE, null);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(old));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).then(returnsFirstArg());

        transactionService.update(5L, new TransactionRequest(1L, 1L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null));

        // 900.00 + 100.00 (undo expense) + 100.00 (apply income)
        assertEquals(new BigDecimal("1100.00"), account.getBalance());
    }

    //5
    @Test
    void updateMovesTransactionToAnotherAccount() {
        Account oldAccount = new Account(1L, "Main", new BigDecimal("1100.00"), Instant.now());
        Account newAccount = new Account(2L, "Savings", new BigDecimal("500.00"), Instant.now());
        Transaction old = new Transaction(5L, 1L, 1L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(old));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(oldAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(newAccount));
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).then(returnsFirstArg());

        Transaction result = transactionService.update(5L,
                new TransactionRequest(2L, 1L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null));

        // old account: 1100.00 - 100.00, new account: 500.00 + 100.00
        assertEquals(new BigDecimal("1000.00"), oldAccount.getBalance());
        assertEquals(new BigDecimal("600.00"), newAccount.getBalance());
        assertEquals(2L, result.getAccountId());
    }


    //6
    @Test
    void updateGives404WhenTransactionIsMissing() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            transactionService.update(99L, new TransactionRequest(1L, 1L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null));
            fail("Expected a 404 error");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

        }
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).save(any(Account.class));
    }

    //7
    @Test
    void updateGives404WhenAccountIsMissing() {
        Transaction old = new Transaction(5L, 1L, 1L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(old));
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            transactionService.update(5L, new TransactionRequest(99L, 1L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null));
            fail("Expected a 404 error");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

        }
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).save(any(Account.class));
    }

    //8
    @Test
    void updateGives404WhenNewCategoryIsMissing() {
        Account account = new Account(1L, "Main", new BigDecimal("1100.00"), Instant.now());
        Transaction old = new Transaction(5L, 1L, 1L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(old));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.existsById(99L)).thenReturn(false);

        try {
            transactionService.update(5L, new TransactionRequest(1L, 99L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null));
            fail("Expected a 404 error");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());

        }

        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, never()).save(any(Account.class));
    }

    //delete
    //1
    @Test
    void deleteIncomeTakesTheMoneyBack() {
        Account account = new Account(1L, "Main", new BigDecimal("1100.00"), Instant.now());
        Transaction transaction = new Transaction(5L, 1L, 1L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(transaction));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        transactionService.delete(5L);

        // 1100.00 - 100.00
        assertEquals(new BigDecimal("1000.00"), account.getBalance());
    }

    //2
    @Test
    void deleteExpenseGivesTheMoneyBack() {
        Account account = new Account(1L, "Main", new BigDecimal("900.00"), Instant.now());
        Transaction transaction = new Transaction(5L, 1L, 1L, new BigDecimal("100.00"), TransactionType.EXPENSE, DATE, null);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(transaction));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        transactionService.delete(5L);

        // 900.00 + 100.00
        assertEquals(new BigDecimal("1000.00"), account.getBalance());
    }

    //3
    @Test
    void deleteRemovesThatTransactionAndSavesTheAccount() {
        Account account = new Account(1L, "Main", new BigDecimal("1100.00"), Instant.now());
        Transaction transaction = new Transaction(5L, 1L, 1L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(transaction));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        transactionService.delete(5L);

        verify(transactionRepository).deleteById(5L);
        verify(transactionRepository, never()).deleteById(6L);
        verify(accountRepository).save(account);
    }

    //4
    @Test
    void deleteGives404WhenTransactionIsMissing() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            transactionService.delete(99L);
            fail("Expected a 404 error");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
        verify(transactionRepository, never()).deleteById(99L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    //5
    @Test
    void deleteGives404WhenAccountIsMissing() {
        Transaction transaction = new Transaction(5L, 99L, 1L, new BigDecimal("100.00"), TransactionType.INCOME, DATE, null);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(transaction));
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            transactionService.delete(5L);
            fail("Expected a 404 error");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
        verify(transactionRepository, never()).deleteById(5L);
    }
}