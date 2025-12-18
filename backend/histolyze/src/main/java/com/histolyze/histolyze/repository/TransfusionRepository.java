package com.histolyze.histolyze.repository;

import com.histolyze.histolyze.model.Transfusion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransfusionRepository extends JpaRepository<Transfusion, Long> {
}