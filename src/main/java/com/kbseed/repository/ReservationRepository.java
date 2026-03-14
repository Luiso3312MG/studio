package com.kbseed.repository;

import com.kbseed.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    boolean existsByClassIdAndClientIdAndReservationStatusIn(Long classId, Long clientId, Collection<String> statuses);
    List<ReservationEntity> findByClassId(Long classId);
    List<ReservationEntity> findByClientIdAndReservationStatusIn(Long clientId, Collection<String> statuses);
    List<ReservationEntity> findByClientId(Long clientId);
    long countByClassIdAndReservationStatusIn(Long classId, Collection<String> statuses);
}
