package com.enviro.assessment.junior.florence.controller;

import com.enviro.assessment.junior.florence.dto.WithdrawalRequest;
import com.enviro.assessment.junior.florence.dto.WithdrawalResponse;
import com.enviro.assessment.junior.florence.model.Withdrawal;
import com.enviro.assessment.junior.florence.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @Autowired
    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @PostMapping("/withdrawals")
    public WithdrawalResponse createWithdrawal(@Valid @RequestBody WithdrawalRequest request) {
        Withdrawal withdrawal = withdrawalService.processWithdrawal(
                request.getInvestorId(), request.getAmount(), request.getType());
        return new WithdrawalResponse(withdrawal);
    }

    @GetMapping("/investors/{id}/withdrawals")
    public List<WithdrawalResponse> getWithdrawalHistory(@PathVariable Long id) {
        return withdrawalService.getWithdrawalHistory(id).stream()
                .map(WithdrawalResponse::new)
                .toList();
    }
}
