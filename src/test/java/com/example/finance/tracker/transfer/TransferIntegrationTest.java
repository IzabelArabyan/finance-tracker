package com.example.finance.tracker.transfer;

import com.example.finance.tracker.TestcontainersConfiguration;
import com.example.finance.tracker.account.Account;
import com.example.finance.tracker.account.repository.AccountRepository;
import com.example.finance.tracker.common.exception.InsufficientFundsException;
import com.example.finance.tracker.transaction.Transaction;
import com.example.finance.tracker.transaction.TransactionType;
import com.example.finance.tracker.transaction.repository.TransactionRepository;
import com.example.finance.tracker.transfer.dto.TransferRequest;
import com.example.finance.tracker.transfer.dto.TransferResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;



@SpringBootTest
@Import(TestcontainersConfiguration.class)
class TransferIntegrationTest {

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    @MockitoBean
    private Clock clock;

    private Account main;
    private Account savings;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();


        when(clock.instant()).thenReturn(NOW);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);


        main = accountRepository.save(new Account(null, "Main", new BigDecimal("500.00"), NOW));
        savings = accountRepository.save(new Account(null, "Savings", new BigDecimal("100.00"), NOW));
    }


    private BigDecimal balanceInDatabase(Account account) {
        return accountRepository.findById(account.getId()).orElseThrow().getBalance();
    }

    //1
    @Test
    void successfulTransferMovesMoneyAndStoresTwoLinkedTransactions() {
        TransferResponse response = transferService.transfer(
                new TransferRequest(main.getId(), savings.getId(), new BigDecimal("200.00")));

        // 500 - 200 , 100 + 200
        assertEquals(new BigDecimal("300.00"), balanceInDatabase(main));
        assertEquals(new BigDecimal("300.00"), balanceInDatabase(savings));

        assertEquals(2, transactionRepository.count());

        List<Transaction> out = transactionRepository.findByAccountId(main.getId());
        List<Transaction> in = transactionRepository.findByAccountId(savings.getId());
        assertEquals(1, out.size());
        assertEquals(1, in.size());
        assertEquals(TransactionType.EXPENSE, out.get(0).getType());
        assertEquals(TransactionType.INCOME, in.get(0).getType());

       //transfer id should be same
        assertNotNull(out.get(0).getTransferId());
        assertEquals(out.get(0).getTransferId(), in.get(0).getTransferId());
        assertEquals(response.transferId(), out.get(0).getTransferId());
    }

    //2
    @Test
    void insufficientFundsLeavesBothBalancesUnchanged() {
        try {
            transferService.transfer(
                    new TransferRequest(main.getId(), savings.getId(), new BigDecimal("800.00")));
            fail("Expected InsufficientFundsException");
        } catch (InsufficientFundsException e) {
            assertEquals("Not enough money in account" + main.getId(), e.getMessage());
        }

        //db stayed sme
        assertEquals(new BigDecimal("500.00"), balanceInDatabase(main));
        assertEquals(new BigDecimal("100.00"), balanceInDatabase(savings));
        assertEquals(0, transactionRepository.count());
    }

    //3
    @Test
    void failureAfterTheDebitRollsBackEverything() {
        when(clock.instant()).thenThrow(new IllegalStateException("Forced failure after the debit"));

        try {
            transferService.transfer(
                    new TransferRequest(main.getId(), savings.getId(), new BigDecimal("200.00")));
            fail("Expected the forced failure");
        } catch (IllegalStateException e) {
            assertEquals("Forced failure after the debit", e.getMessage());
        }

        // gives roll back
        assertEquals(new BigDecimal("500.00"), balanceInDatabase(main));
        assertEquals(new BigDecimal("100.00"), balanceInDatabase(savings));

        assertEquals(0, transactionRepository.count()); // no save in db
    }
}