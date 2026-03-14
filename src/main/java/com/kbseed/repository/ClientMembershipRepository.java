package com.kbseed.repository;

import com.kbseed.entity.ClientMembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClientMembershipRepository extends JpaRepository<ClientMembershipEntity, Long> {
    Optional<ClientMembershipEntity> findTopByClientIdOrderByEndDateDesc(Long clientId);
    List<ClientMembershipEntity> findByStudioId(Long studioId);
    List<ClientMembershipEntity> findByClientId(Long clientId);
    Optional<ClientMembershipEntity> findTopByClientIdAndStatusAndEndDateGreaterThanEqualOrderByEndDateDesc(Long clientId, String status, LocalDate date);
}
