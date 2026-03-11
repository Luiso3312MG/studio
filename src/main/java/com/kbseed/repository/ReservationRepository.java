package com.kbseed.repository;

import com.kbseed.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    boolean existsByClassIdAndClientId(Long classId, Long clientId);
}