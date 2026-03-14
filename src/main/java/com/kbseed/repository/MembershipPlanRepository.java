package com.kbseed.repository;

import com.kbseed.entity.MembershipPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlanEntity, Long> {
    List<MembershipPlanEntity> findByStudioIdAndIsActiveTrueOrderByPriceAsc(Long studioId);
}
