package com.kbseed.repository;

import com.kbseed.entity.ClassTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassTypeRepository extends JpaRepository<ClassTypeEntity, Long> {
    List<ClassTypeEntity> findByStudioId(Long studioId);
    List<ClassTypeEntity> findByStudioIdAndCategoryAndIsActiveTrue(Long studioId, String category);
    Optional<ClassTypeEntity> findByStudioIdAndName(Long studioId, String name);
}
