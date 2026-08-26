package com.enviro.assessment.junior.florence.controller;

import com.enviro.assessment.junior.florence.dto.PortfolioResponse;
import com.enviro.assessment.junior.florence.dto.ProductResponse;
import com.enviro.assessment.junior.florence.model.Investor;
import com.enviro.assessment.junior.florence.model.Product;
import com.enviro.assessment.junior.florence.repository.ProductRepository;
import com.enviro.assessment.junior.florence.service.InvestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investors")
public class InvestorController {

    private final InvestorService investorService;
    private final ProductRepository productRepository;

    @Autowired
    public InvestorController(InvestorService investorService, ProductRepository productRepository) {
        this.investorService = investorService;
        this.productRepository = productRepository;
    }

    @GetMapping("/{id}/portfolio")
    public PortfolioResponse getPortfolio(@PathVariable Long id) {
        Investor investor = investorService.getInvestorById(id);
        List<Product> products = productRepository.findByInvestorId(id);

        List<ProductResponse> productResponses = products.stream()
                .map(p -> new ProductResponse(p.getName(), p.getBalance()))
                .toList();

        return new PortfolioResponse(investor.getId(), investor.getName(), investor.getAge(), productResponses);
    }
}
