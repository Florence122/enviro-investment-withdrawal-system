package com.enviro.assessment.junior.florence.service;

import com.enviro.assessment.junior.florence.model.*;
import com.enviro.assessment.junior.florence.repository.InvestorRepository;
import com.enviro.assessment.junior.florence.repository.ProductRepository;
import com.enviro.assessment.junior.florence.repository.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WithdrawalService {

    private final InvestorRepository investorRepository;
    private final ProductRepository productRepository;
    private final WithdrawalRepository withdrawalRepository;

    @Autowired
    public WithdrawalService(InvestorRepository investorRepository,
                              ProductRepository productRepository,
                              WithdrawalRepository withdrawalRepository) {
        this.investorRepository = investorRepository;
        this.productRepository = productRepository;
        this.withdrawalRepository = withdrawalRepository;
    }

    public Withdrawal processWithdrawal(Long investorId, BigDecimal amount, WithdrawalType type) {

        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new RuntimeException("Investor not found with id: " + investorId));

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }

        // Rule 1: Retirement withdrawals only allowed if age > 65
        if (type == WithdrawalType.RETIREMENT && investor.getAge() <= 65) {
            Withdrawal rejected = new Withdrawal(investor, amount, type, WithdrawalStatus.REJECTED);
            withdrawalRepository.save(rejected);
            throw new IllegalArgumentException(
                    "Retirement withdrawals are only allowed for investors older than 65.");
        }

        List<Product> products = productRepository.findByInvestorId(investorId);
        BigDecimal totalBalance = products.stream()
                .map(Product::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Rule 2: Cannot exceed balance
        if (amount.compareTo(totalBalance) > 0) {
            Withdrawal rejected = new Withdrawal(investor, amount, type, WithdrawalStatus.REJECTED);
            withdrawalRepository.save(rejected);
            throw new IllegalArgumentException("Withdrawal amount cannot exceed your available balance.");
        }

        // Rule 3: Cannot exceed 90% of balance
        BigDecimal maxAllowed = totalBalance.multiply(new BigDecimal("0.90"));
        if (amount.compareTo(maxAllowed) > 0) {
            Withdrawal rejected = new Withdrawal(investor, amount, type, WithdrawalStatus.REJECTED);
            withdrawalRepository.save(rejected);
            throw new IllegalArgumentException("Withdrawal amount cannot exceed 90% of your available balance.");
        }

        // All rules passed — approve
        Withdrawal approved = new Withdrawal(investor, amount, type, WithdrawalStatus.APPROVED);
        return withdrawalRepository.save(approved);
    }

    public List<Withdrawal> getWithdrawalHistory(Long investorId) {
        return withdrawalRepository.findByInvestorId(investorId);
    }
}
