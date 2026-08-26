package com.enviro.assessment.junior.florence.service;

import com.enviro.assessment.junior.florence.model.*;
import com.enviro.assessment.junior.florence.repository.InvestorRepository;
import com.enviro.assessment.junior.florence.repository.ProductRepository;
import com.enviro.assessment.junior.florence.repository.WithdrawalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawalServiceTest {

    @Mock
    private InvestorRepository investorRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WithdrawalRepository withdrawalRepository;

    @InjectMocks
    private WithdrawalService withdrawalService;

    private Investor eligibleInvestor;
    private Investor youngInvestor;
    private Product product;

    @BeforeEach
    void setUp() {
        eligibleInvestor = new Investor("Florence Chauke", "florence@email.com", 68);
        youngInvestor = new Investor("John Smith", "john@email.com", 40);
        product = new Product("Retirement Fund", new BigDecimal("100000"), eligibleInvestor);

        lenient().when(withdrawalRepository.save(any(Withdrawal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void approvesValidWithdrawalWithinLimits() {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(eligibleInvestor));
        when(productRepository.findByInvestorId(1L)).thenReturn(List.of(product));

        Withdrawal result = withdrawalService.processWithdrawal(1L, new BigDecimal("50000"), WithdrawalType.SAVINGS);

        assertEquals(WithdrawalStatus.APPROVED, result.getStatus());
        assertEquals(0, new BigDecimal("50000").compareTo(result.getAmount()));
        verify(withdrawalRepository).save(argThat(w -> w.getStatus() == WithdrawalStatus.APPROVED));
    }

    @Test
    void rejectsRetirementWithdrawalWhenInvestorIsNotOver65() {
        when(investorRepository.findById(2L)).thenReturn(Optional.of(youngInvestor));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                withdrawalService.processWithdrawal(2L, new BigDecimal("5000"), WithdrawalType.RETIREMENT));

        assertTrue(ex.getMessage().contains("older than 65"));
        verify(withdrawalRepository).save(argThat(w -> w.getStatus() == WithdrawalStatus.REJECTED));
    }

    @Test
    void allowsRetirementWithdrawalWhenInvestorIsOver65() {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(eligibleInvestor));
        when(productRepository.findByInvestorId(1L)).thenReturn(List.of(product));

        Withdrawal result = withdrawalService.processWithdrawal(1L, new BigDecimal("10000"), WithdrawalType.RETIREMENT);

        assertEquals(WithdrawalStatus.APPROVED, result.getStatus());
    }

    @Test
    void rejectsWithdrawalExceedingTotalBalance() {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(eligibleInvestor));
        when(productRepository.findByInvestorId(1L)).thenReturn(List.of(product));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                withdrawalService.processWithdrawal(1L, new BigDecimal("150000"), WithdrawalType.SAVINGS));

        assertTrue(ex.getMessage().contains("exceed your available balance"));
        verify(withdrawalRepository).save(argThat(w -> w.getStatus() == WithdrawalStatus.REJECTED));
    }

    @Test
    void rejectsWithdrawalExceeding90PercentOfBalance() {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(eligibleInvestor));
        when(productRepository.findByInvestorId(1L)).thenReturn(List.of(product));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                withdrawalService.processWithdrawal(1L, new BigDecimal("95000"), WithdrawalType.SAVINGS));

        assertTrue(ex.getMessage().contains("90%"));
        verify(withdrawalRepository).save(argThat(w -> w.getStatus() == WithdrawalStatus.REJECTED));
    }

    @Test
    void allowsWithdrawalExactlyAt90PercentOfBalance() {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(eligibleInvestor));
        when(productRepository.findByInvestorId(1L)).thenReturn(List.of(product));

        Withdrawal result = withdrawalService.processWithdrawal(1L, new BigDecimal("90000"), WithdrawalType.SAVINGS);

        assertEquals(WithdrawalStatus.APPROVED, result.getStatus());
    }

    @Test
    void throwsWhenInvestorDoesNotExist() {
        when(investorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                withdrawalService.processWithdrawal(99L, new BigDecimal("1000"), WithdrawalType.SAVINGS));
    }

    @Test
    void rejectsZeroOrNegativeAmount() {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(eligibleInvestor));

        assertThrows(IllegalArgumentException.class, () ->
                withdrawalService.processWithdrawal(1L, new BigDecimal("0"), WithdrawalType.SAVINGS));
        assertThrows(IllegalArgumentException.class, () ->
                withdrawalService.processWithdrawal(1L, new BigDecimal("-500"), WithdrawalType.SAVINGS));
    }

    @Test
    void getWithdrawalHistoryReturnsRecordsForInvestor() {
        Withdrawal past = new Withdrawal(eligibleInvestor, new BigDecimal("20000"), WithdrawalType.SAVINGS, WithdrawalStatus.APPROVED);
        when(withdrawalRepository.findByInvestorId(1L)).thenReturn(List.of(past));

        List<Withdrawal> history = withdrawalService.getWithdrawalHistory(1L);

        assertEquals(1, history.size());
        assertEquals(WithdrawalStatus.APPROVED, history.get(0).getStatus());
    }
}
