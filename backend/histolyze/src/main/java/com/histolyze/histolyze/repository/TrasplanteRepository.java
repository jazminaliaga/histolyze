package com.histolyze.histolyze.repository;

import com.histolyze.histolyze.model.Trasplante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrasplanteRepository extends JpaRepository<Trasplante, Long> {
}
