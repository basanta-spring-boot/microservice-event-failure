package com.javatechie.service;

import com.javatechie.events.CreditDecisionEvent;
import com.javatechie.events.LoanApplicationSubmitEvent;
import com.javatechie.exception.InSufficientCreditScoreException;
import com.javatechie.util.CreditScoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CreditRiskConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CreditRiskConsumer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "loan-process-topic")
    public void onLoanApplicationReceived(LoanApplicationSubmitEvent event) {
        CreditDecisionEvent creditRiskCheckEvent = null;
        log.info("Received loan application event: {}", event);

        try {
            evaluateCreditRisk(event.getUserId());
            log.info("Credit risk check PASSED for userId: {}", event.getUserId());

            creditRiskCheckEvent = buildCreditCheckEvent(event, true, "Credit check PASSED");
            publishCreditDecisionEvent(creditRiskCheckEvent);
        }
        catch (InSufficientCreditScoreException ex) {
            log.warn("Credit risk check FAILED for userId: {} | Reason: {}", event.getUserId(), ex.getMessage());

            creditRiskCheckEvent = buildCreditCheckEvent(event, false, ex.getMessage());
            publishCreditDecisionEvent(creditRiskCheckEvent);
        }
    }


    private void evaluateCreditRisk(int userId) {
        int creditScore = CreditScoreUtils.creditScoreResults().getOrDefault(userId, 0);
        if (creditScore < 750) {
            throw new InSufficientCreditScoreException("Credit score is too low: " + creditScore);
        }
    }

    private CreditDecisionEvent buildCreditCheckEvent(LoanApplicationSubmitEvent event, boolean status, String message) {
        return CreditDecisionEvent.builder()
                .loanId(event.getLoanId())
                .userId(event.getUserId())
                .approved(status)
                .message(message)
                .build();
    }

    private void publishCreditDecisionEvent(CreditDecisionEvent event) {
        kafkaTemplate.send("credit-decision-topic", event);
        log.info("Published credit decision event: {}", event);
    }
}
