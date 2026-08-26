package com.enviro.assessment.junior.florence.dto;

import com.enviro.assessment.junior.florence.model.Withdrawal;
import com.enviro.assessment.junior.florence.model.WithdrawalStatus;
import com.enviro.assessment.junior.florence.model.WithdrawalType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WithdrawalResponse {
    private Long id;
    private BigDecimal amount;
    private WithdrawalType type;
    private WithdrawalStatus status;
    private LocalDateTime createdAt;

    public WithdrawalResponse(Withdrawal withdrawal) {
        this.id = withdrawal.getId();
        this.amount = withdrawal.getAmount();
        this.type = withdrawal.getType();
        this.status = withdrawal.getStatus();
        this.createdAt = withdrawal.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public WithdrawalType getType() {
        return type;
    }

    public WithdrawalStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
