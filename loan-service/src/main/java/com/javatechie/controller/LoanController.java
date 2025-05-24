package com.javatechie.controller;

import com.javatechie.entity.LoanDO;
import com.javatechie.service.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Slf4j
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/process")
    public ResponseEntity<String> processLoan(@RequestBody LoanDO loanDO) {
        log.info("LoanController::processLoan received loan application request for userId: {}", loanDO.getUserId());
        String loanTransactionId = loanService.processLoanApplication(loanDO);
        String responseMessage = String.format("Loan application received, credit check in progress. Transaction ID: %s", loanTransactionId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseMessage);
    }
}
