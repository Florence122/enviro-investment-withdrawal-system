package com.enviro.assessment.junior.florence.service;

import com.enviro.assessment.junior.florence.model.Investor;
import com.enviro.assessment.junior.florence.repository.InvestorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvestorService {

    private final InvestorRepository investorRepository;

    @Autowired
    public InvestorService(InvestorRepository investorRepository) {
        this.investorRepository = investorRepository;
    }

    public Investor getInvestorById(Long id) {
        return investorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investor not found with id: " + id));
    }
}
