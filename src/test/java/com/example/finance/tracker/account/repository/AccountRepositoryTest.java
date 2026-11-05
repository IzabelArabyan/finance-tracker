package com.example.finance.tracker.account.repository;

import com.example.finance.tracker.TestcontainersConfiguration;
import com.example.finance.tracker.account.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
public class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;

    //1
    @Test
    void totalBalanceIsZeroWhenThereAreNoAccounts() {
        BigDecimal total = accountRepository.totalBalance();

        assertEquals(0, total.compareTo(BigDecimal.ZERO));
    }

    //2
    @Test
    void totalBalanceAddsUpAllAccounts() {
        accountRepository.save(new Account(null, "Main", new BigDecimal("100.00"), Instant.now()));
        accountRepository.save(new Account(null, "Savings", new BigDecimal("250.50"), Instant.now()));

        BigDecimal total = accountRepository.totalBalance();

        // 100.00 + 250.50
        assertEquals(0, total.compareTo(new BigDecimal("350.50")));
    }
}
