package com.example.finance.tracker.transfer;

import com.example.finance.tracker.account.Account;
import com.example.finance.tracker.account.repository.AccountRepository;
import com.example.finance.tracker.common.exception.InsufficientFundsException;
import com.example.finance.tracker.common.exception.NotFoundException;
import com.example.finance.tracker.common.exception.ValidationException;
import com.example.finance.tracker.security.CurrentUser;
import com.example.finance.tracker.transaction.Transaction;
import com.example.finance.tracker.transaction.TransactionType;
import com.example.finance.tracker.transaction.repository.TransactionRepository;
import com.example.finance.tracker.transfer.dto.TransferRequest;
import com.example.finance.tracker.transfer.dto.TransferResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentUser currentUser;

    private final Clock clock;

    public TransferService(AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           CurrentUser currentUser,
                           Clock clock) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.currentUser = currentUser;
        this.clock = clock;
    }


    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        if (request.fromAccountId().equals(request.toAccountId())) {
            throw new ValidationException("Cannot transfer to the same account");
        }

        Account from = getAccount(request.fromAccountId());
        Account to = getAccount(request.toAccountId());
        BigDecimal amount = request.amount();

        //balance should not go -
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(from.getId());
        }

        //debit
        from.setBalance(from.getBalance().subtract(amount));
        accountRepository.save(from);

        //credit
        to.setBalance(to.getBalance().add(amount));
        accountRepository.save(to);

        UUID transferId = UUID.randomUUID();
        LocalDate today = LocalDate.now(clock);

        Transaction out = new Transaction(null, from, null, amount, TransactionType.EXPENSE, today,
                "Transfer to account " + to.getId());
        out.setTransferId(transferId);

        Transaction in = new Transaction(null, to, null, amount, TransactionType.INCOME, today,
                "Transfer from account " + from.getId());
        in.setTransferId(transferId);

        transactionRepository.save(out);
        transactionRepository.save(in);

        return new TransferResponse(transferId, from.getId(), to.getId(), amount, today,
                from.getBalance(), to.getBalance());
    }

    private Account getAccount(Long id) {
        return accountRepository.findByIdAndOwnerId(id, currentUser.id())
                .orElseThrow(() -> new NotFoundException("Account", id));
    }
}