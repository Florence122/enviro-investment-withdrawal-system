package com.enviro.assessment.junior.florence.repository;

import com.enviro.assessment.junior.florence.model.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
    List<Withdrawal> findByInvestorId(Long investorId);
}
