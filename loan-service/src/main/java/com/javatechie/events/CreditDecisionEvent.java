package com.javatechie.events;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDecisionEvent {
    private Long loanId;
    private int userId;
    private boolean approved;
    private String message;
}