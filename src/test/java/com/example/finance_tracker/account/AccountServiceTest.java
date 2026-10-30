package com.example.finance_tracker.account;

import com.example.finance_tracker.account.dto.CreateAccountRequest;
import com.example.finance_tracker.account.dto.UpdateAccountRequest;
import com.example.finance_tracker.account.repository.InMemoryAccountRepository;
import com.example.finance_tracker.transaction.repository.InMemoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");
    @Mock
    private InMemoryAccountRepository accountRepository;

    @Mock
    private InMemoryTransactionRepository transactionRepository;

    private AccountService accountService;

    @BeforeEach
    void setUp(){
        Clock clock = Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneId.of("UTC"));
        accountService = new AccountService(accountRepository, transactionRepository, clock);
    }

    //create
    //1
    @Test
    void createWithNameAndBalance(){
        when(accountRepository.save(any(Account.class))).then(returnsFirstArg());

        Account result = accountService.create(new CreateAccountRequest("Main", new BigDecimal("1000.00")));

        assertEquals("Main", result.getName());
        assertEquals(new BigDecimal("1000.00"), result.getBalance());
    }
    //2
    @Test
    void createRemovesSpacesAroundTheName() {
        when(accountRepository.save(any(Account.class))).then(returnsFirstArg());

        Account result = accountService.create(new CreateAccountRequest("  Main  ", BigDecimal.TEN));

        assertEquals("Main", result.getName());
    }
    //3
    @Test
    void createWithoutBalanceStartsAtZero() {
        when(accountRepository.save(any(Account.class))).then(returnsFirstArg());

        Account result = accountService.create(new CreateAccountRequest("Main", null));

        assertEquals(BigDecimal.ZERO, result.getBalance());
    }
    //4
    @Test
    void createWithZeroBalanceIsAllowed() {
        when(accountRepository.save(any(Account.class))).then(returnsFirstArg());

        Account result = accountService.create(new CreateAccountRequest("Main", BigDecimal.ZERO));

        assertEquals(BigDecimal.ZERO, result.getBalance());
    }
    //5
    @Test
    void createWithTwoDecimalsIsAllowed() {
        when(accountRepository.save(any(Account.class))).then(returnsFirstArg());

        Account result = accountService.create(new CreateAccountRequest("Main", new BigDecimal("10.55")));

        assertEquals(new BigDecimal("10.55"), result.getBalance());
    }
    //6

    @Test
    void createWithExtraZerosIsAllowed() {
        // 10.500 = 10.5
        when(accountRepository.save(any(Account.class))).then(returnsFirstArg());

        Account result = accountService.create(new CreateAccountRequest("Main", new BigDecimal("10.500")));

        assertEquals(new BigDecimal("10.500"), result.getBalance());
    }


    //7
    @Test
    void createWithNullNameGives400() {
        expectBadRequest(new CreateAccountRequest(null, BigDecimal.TEN),
                "Account name must not be blank");
    }
    //8
    @Test
    void createWithEmptyNameGives400() {
        expectBadRequest(new CreateAccountRequest("", BigDecimal.TEN),
                "Account name must not be blank");
    }
    //9
    @Test
    void createWithOnlySpacesInNameGives400() {
        expectBadRequest(new CreateAccountRequest("   ", BigDecimal.TEN),
                "Account name must not be blank");
    }
    //10
    @Test
    void createWithNegativeBalanceGives400() {
        expectBadRequest(new CreateAccountRequest("Main", new BigDecimal("-1")),
                "Initial balance must be 0 or more");
    }
    //11
    @Test
    void createWithThreeDecimalsGives400() {
        expectBadRequest(new CreateAccountRequest("Main", new BigDecimal("10.999")),
                "Initial balance can have at most 2 decimal places");
    }

    private void expectBadRequest(CreateAccountRequest request, String expectedMessage) {
        try {
            accountService.create(request);
            fail("Expected a 400 error");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }
        verify(accountRepository, never()).save(any(Account.class));
    }

    //Update
    //1
    @Test
    void UpdateChangesTheName(){
        Account account = new Account(1L, "Old", new BigDecimal("500.00"), NOW);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).then(returnsFirstArg());

        Account result = accountService.update(1L, new UpdateAccountRequest("New"));

        assertEquals("New", result.getName());
        verify(accountRepository).save(account);
    }
    //2
    @Test
    void updateRemovesSpacesAroundTheName() {
        Account account = new Account(1L, "Old", new BigDecimal("500.00"), NOW);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).then(returnsFirstArg());

        Account result = accountService.update(1L, new UpdateAccountRequest("  New  "));

        assertEquals("New", result.getName());
    }
    //3
    @Test
    void updateKeepsIdBalanceAndCreatedAt() {
        Account account = new Account(1L, "Old", new BigDecimal("500.00"), NOW);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).then(returnsFirstArg());

        Account result = accountService.update(1L, new UpdateAccountRequest("New"));

        assertEquals(1L, result.getId());
        assertEquals(new BigDecimal("500.00"), result.getBalance());
        assertEquals(NOW, result.getCreatedAt());
    }
    //4
    @Test
    void updateWithNullNameGives400() {
        expectUpdateBadRequest(new UpdateAccountRequest(null));
    }
    //5
    @Test
    void updateWithEmptyNameGives400() {
        expectUpdateBadRequest(new UpdateAccountRequest(""));
    }
    //6
    @Test
    void updateWithOnlySpacesInNameGives400() {
        expectUpdateBadRequest(new UpdateAccountRequest("   "));
    }
    //7
    @Test
    void updateGives404WhenAccountIsMissing() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            accountService.update(99L, new UpdateAccountRequest("New"));
            fail("Expected a 404 error");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
        verify(accountRepository, never()).save(any(Account.class));
    }

    private void expectUpdateBadRequest(UpdateAccountRequest request) {
        try {
            accountService.update(1L, request);
            fail("Expected a 400 error");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }
        verify(accountRepository, never()).save(any(Account.class));
    }
}
