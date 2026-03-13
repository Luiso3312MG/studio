package com.kbseed.repository;

import com.kbseed.entity.ClassTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassTypeRepository extends JpaRepository<ClassTypeEntity, Long> {

    List<ClassTypeEntity> findByStudioId(Long studioId);
}
