package com.example.finance.tracker.transaction.repository;

import com.example.finance.tracker.TestcontainersConfiguration;
import com.example.finance.tracker.account.Account;
import com.example.finance.tracker.account.repository.AccountRepository;
import com.example.finance.tracker.category.Category;
import com.example.finance.tracker.category.repository.CategoryRepository;
import com.example.finance.tracker.transaction.Transaction;
import com.example.finance.tracker.transaction.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;



@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
public class TransactionRepositoryTest {
    private static final LocalDate DATE = LocalDate.of(2026, 1, 15);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Account main;
    private Account savings;
    private Category food;

    @BeforeEach
    void setUp() {

        main = accountRepository.save(new Account(null, "Main", new BigDecimal("1000.00"), Instant.now()));
        savings = accountRepository.save(new Account(null, "Savings", new BigDecimal("500.00"), Instant.now()));
        food = categoryRepository.save(new Category(null, "Food"));
    }

   //helper
    private Transaction saveTransaction(Account account, Category category, String amount, TransactionType type) {
        return transactionRepository.save(
                new Transaction(null, account, category, new BigDecimal(amount), type, DATE, null));
    }

    //1 find account
    @Test
    void findByAccountIdReturnsOnlyThatAccountsTransactions() {
        saveTransaction(main, food, "10.00", TransactionType.EXPENSE);
        saveTransaction(main, food, "20.00", TransactionType.EXPENSE);
        saveTransaction(savings, food, "30.00", TransactionType.EXPENSE);

        List<Transaction> result = transactionRepository.findByAccountId(main.getId());

        assertEquals(2, result.size());
        for (Transaction transaction : result) {
            assertEquals(main.getId(), transaction.getAccount().getId());
        }
    }

    //2 exist account
    @Test
    void existsByAccountIdIsTrueWhenAccountHasTransactions() {
        saveTransaction(main, food, "10.00", TransactionType.EXPENSE);

        assertTrue(transactionRepository.existsByAccountId(main.getId()));
    }

    //3
    @Test
    void existsByAccountIdIsFalseWhenAccountHasNoTransactions() {
        saveTransaction(main, food, "10.00", TransactionType.EXPENSE);

        assertFalse(transactionRepository.existsByAccountId(savings.getId()));
    }

    //4 exists category
    @Test
    void existsByCategoryIdIsTrueWhenCategoryIsUsed() {
        saveTransaction(main, food, "10.00", TransactionType.EXPENSE);

        assertTrue(transactionRepository.existsByCategoryId(food.getId()));
    }

    //5
    @Test
    void existsByCategoryIdIsFalseWhenCategoryIsNotUsed() {
        Category holiday = categoryRepository.save(new Category(null, "Holiday"));
        saveTransaction(main, food, "10.00", TransactionType.EXPENSE);

        assertFalse(transactionRepository.existsByCategoryId(holiday.getId()));
    }

    //6
    @Test
    void existsByCategoryIdWorksWithIncomeWithoutCategory() {

        saveTransaction(main, null, "100.00", TransactionType.INCOME);

        assertFalse(transactionRepository.existsByCategoryId(food.getId()));
    }


}
