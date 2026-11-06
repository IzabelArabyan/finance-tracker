package com.example.finance.tracker.account;
import java.util.List;

import com.example.finance.tracker.account.dto.AccountResponse;
import com.example.finance.tracker.account.dto.CreateAccountRequest;
import com.example.finance.tracker.account.dto.UpdateAccountRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountResponse> findAll() {
        return accountService.findAll().stream()
                .map(AccountResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public AccountResponse findById(@PathVariable Long id){
        return AccountResponse.from(accountService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request){
        return AccountResponse.from(accountService.create(request));
    }

    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable Long id, @Valid @RequestBody UpdateAccountRequest request) {
        return AccountResponse.from(accountService.update(id, request));
    }
    //api/accounts/5
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        accountService.delete(id);
    }


}
