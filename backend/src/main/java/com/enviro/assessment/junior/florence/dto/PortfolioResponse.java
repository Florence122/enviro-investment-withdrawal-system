package com.enviro.assessment.junior.florence.dto;

import java.util.List;

public class PortfolioResponse {
    private Long id;
    private String name;
    private int age;
    private List<ProductResponse> products;

    public PortfolioResponse(Long id, String name, int age, List<ProductResponse> products) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.products = products;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public List<ProductResponse> getProducts() {
        return products;
    }
}
