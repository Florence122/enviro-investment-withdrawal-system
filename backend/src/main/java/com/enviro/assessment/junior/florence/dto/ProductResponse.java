package com.enviro.assessment.junior.florence.dto;

import java.math.BigDecimal;

public class ProductResponse {
    private String name;
    private BigDecimal balance;

    public ProductResponse(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
