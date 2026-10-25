package com.example.finance_tracker.account;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<Account> findAll() {
        return accountService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account create(@RequestBody CreateAccountRequest request) {
        return accountService.create(request.getName(), request.getBalance());
    }

    @PatchMapping("/{id}")
    public Account rename(@PathVariable Long id, @RequestBody RenameAccountRequest request) {
        return getOrThrow(accountService.rename(id, request.getName()), id);
    }
    private Account getOrThrow(Optional<Account> account, Long id) {
        if (account.isEmpty()) {
            throw new AccountNotFoundException(id);
        }
        return account.get();
    }
    @PostMapping("/{id}/deposit")
    public Account deposit(@PathVariable Long id, @RequestBody AmountRequest request) {
        return getOrThrow(accountService.deposit(id, request.getAmount()), id);
    }
    @PostMapping("/{id}/withdraw")
    public Account withdraw(@PathVariable Long id, @RequestBody AmountRequest request) {
        return getOrThrow(accountService.withdraw(id, request.getAmount()), id);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        getOrThrow(accountService.delete(id), id);
    }
}
