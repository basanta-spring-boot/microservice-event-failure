package com.javatechie.service;

import com.javatechie.constants.LoanStatus;
import com.javatechie.entity.LoanDO;
import com.javatechie.events.CreditDecisionEvent;
import com.javatechie.repository.LoanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CreditDecisionConsumer {

    private final LoanRepository loanRepository;

    public CreditDecisionConsumer(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @KafkaListener(topics = "credit-decision-topic", groupId = "loan-service-group")
    public void consumeCreditDecision(CreditDecisionEvent event) {
        log.info("CreditDecisionConsumer::consumeCreditDecision - Received credit decision event {} ", event);

        LoanDO loan = loanRepository.findById(event.getLoanId())
                .orElse(null);

        if (loan != null) {
            if (event.isApproved()) {
                loan.setStatus(LoanStatus.APPROVED);
                log.info("CreditDecisionConsumer - LoanId: {} marked as APPROVED. Proceeding with disbursement logic.", loan.getLoanId());
            } else {
                loan.setStatus(LoanStatus.REJECTED);
                log.info("CreditDecisionConsumer - LoanId: {} marked as REJECTED. No further action required ", loan.getLoanId());
            }
            loanRepository.save(loan);
        }
    }
}
