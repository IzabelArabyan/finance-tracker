package com.example.finance_tracker.transaction;

import com.example.finance_tracker.transaction.dto.TransactionRequest;
import com.example.finance_tracker.transaction.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionResponse> findAll(){
        List<TransactionResponse> res = new ArrayList<>();
        for(Transaction transaction : transactionService.findAll()){
            res.add(TransactionResponse.from(transaction));
        }
        return res;
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
