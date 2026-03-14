package com.kbseed.repository;

import com.kbseed.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    List<ClassEntity> findByStudioIdOrderByClassDateAscStartTimeAsc(Long studioId);
    List<ClassEntity> findByStudioIdAndClassDate(Long studioId, LocalDate classDate);
    List<ClassEntity> findByIdIn(Collection<Long> ids);
}
