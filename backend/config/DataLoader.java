package com.enviro.assessment.junior.florence.config;

import com.enviro.assessment.junior.florence.model.Investor;
import com.enviro.assessment.junior.florence.model.Product;
import com.enviro.assessment.junior.florence.repository.InvestorRepository;
import com.enviro.assessment.junior.florence.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final InvestorRepository investorRepository;
    private final ProductRepository productRepository;

    public DataLoader(InvestorRepository investorRepository, ProductRepository productRepository) {
        this.investorRepository = investorRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        Investor florence = new Investor("Florence Chauke", "florence@email.com", 68);
        investorRepository.save(florence);

        Product retirementFund = new Product("Retirement Fund", new BigDecimal("100000"), florence);
        productRepository.save(retirementFund);

        Investor john = new Investor("John Smith", "john@email.com", 40);
        investorRepository.save(john);

        Product savingsPlan = new Product("Savings Plan", new BigDecimal("50000"), john);
        productRepository.save(savingsPlan);

        System.out.println("Sample data loaded: 2 investors, 2 products.");
    }
}