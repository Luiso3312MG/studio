package com.kbseed.repository;

import com.kbseed.entity.MembershipDisciplineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipDisciplineRepository extends JpaRepository<MembershipDisciplineEntity, Long> {
    List<MembershipDisciplineEntity> findByClientMembershipId(Long clientMembershipId);
    void deleteByClientMembershipId(Long clientMembershipId);
}
