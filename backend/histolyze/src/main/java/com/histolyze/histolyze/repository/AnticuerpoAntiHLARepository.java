package com.histolyze.histolyze.repository;

import com.histolyze.histolyze.model.AnticuerpoAntiHLA;
import com.histolyze.histolyze.model.DSA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnticuerpoAntiHLARepository extends JpaRepository<AnticuerpoAntiHLA, Long> {

    List<AnticuerpoAntiHLA> findByDsa(DSA dsa);
}
