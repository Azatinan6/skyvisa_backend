package com.skyvisa.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skyvisa.entity.VisaRule;

@Repository
public interface VisaRuleRepository extends JpaRepository<VisaRule, Long> {

    List<VisaRule> findByTargetCountry_IsoCodeIn(Set<String> isoCodes);
}
