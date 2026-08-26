package com.enviro.assessment.junior.florence.repository;

import com.enviro.assessment.junior.florence.model.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestorRepository extends JpaRepository<Investor, Long> {
}
