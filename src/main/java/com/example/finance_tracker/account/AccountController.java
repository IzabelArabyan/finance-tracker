package com.example.finance_tracker.account;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.finance_tracker.account.dto.AccountResponse;
import com.example.finance_tracker.account.dto.CreateAccountRequest;
import com.example.finance_tracker.account.dto.UpdateAccountRequest;
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
        List<Account> accounts = accountService.findAll();
        List<AccountResponse> result = new ArrayList<>();
        for (int i = 0; i < accounts.size(); i++) {
            result.add(AccountResponse.from(accounts.get(i)));
        }
        return result;
    }

    @GetMapping("/{id}")
    public AccountResponse findById(@PathVariable Long id){
        return AccountResponse.from(accountService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@RequestBody CreateAccountRequest request){
        return AccountResponse.from(accountService.create(request));
    }

    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable Long id, @RequestBody UpdateAccountRequest request) {
        return AccountResponse.from(accountService.update(id, request));
    }
    //api/accounts/5
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        accountService.delete(id);
    }


}
