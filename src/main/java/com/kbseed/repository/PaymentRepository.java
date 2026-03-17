package com.kbseed.repository;

import com.kbseed.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    List<PaymentEntity> findByStudioIdOrderByCreatedAtDesc(Long studioId);

    List<PaymentEntity> findByClientIdOrderByCreatedAtDesc(Long clientId);
}