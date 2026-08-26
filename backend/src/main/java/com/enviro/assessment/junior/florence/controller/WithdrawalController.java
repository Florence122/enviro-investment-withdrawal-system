package com.enviro.assessment.junior.florence.controller;

import com.enviro.assessment.junior.florence.dto.WithdrawalRequest;
import com.enviro.assessment.junior.florence.dto.WithdrawalResponse;
import com.enviro.assessment.junior.florence.model.Withdrawal;
import com.enviro.assessment.junior.florence.model.WithdrawalStatus;
import com.enviro.assessment.junior.florence.model.WithdrawalType;
import com.enviro.assessment.junior.florence.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/investors/{id}/withdrawals/export")
    public ResponseEntity<String> exportWithdrawalsCsv(
            @PathVariable Long id,
            @RequestParam(required = false) WithdrawalType type,
            @RequestParam(required = false) WithdrawalStatus status) {

        List<Withdrawal> withdrawals = withdrawalService.getFilteredWithdrawals(id, type, status);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Amount,Type,Status,CreatedAt\n");
        for (Withdrawal w : withdrawals) {
            csv.append(w.getId()).append(",")
               .append(w.getAmount()).append(",")
               .append(w.getType()).append(",")
               .append(w.getStatus()).append(",")
               .append(w.getCreatedAt()).append("\n");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=withdrawals-" + id + ".csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString());
    }
}
