package com.example.finance.tracker.transaction;

import com.example.finance.tracker.transaction.dto.TransactionRequest;
import com.example.finance.tracker.transaction.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.example.finance.tracker.common.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    // GET /api/transactions?accountId=1&categoryId=2&from=2026-01-01&to=2026-01-31&page=0&size=20&sort=date,desc
    @GetMapping
    public PageResponse<TransactionResponse> search(
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 20, sort = {"date", "id"}, direction = Sort.Direction.DESC) Pageable pageable) {

        return PageResponse.from(transactionService.search(accountId, categoryId, from, to, pageable));
    }

    @GetMapping("/{id}")
    public TransactionResponse findById(@PathVariable Long id){
        return TransactionResponse.from(transactionService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody TransactionRequest request){
        return TransactionResponse.from(transactionService.create(request));
    }

    @PutMapping("/{id}")
    public TransactionResponse update(@PathVariable Long id, @Valid @RequestBody TransactionRequest request){
        return TransactionResponse.from(transactionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        transactionService.delete(id);
    }


}
