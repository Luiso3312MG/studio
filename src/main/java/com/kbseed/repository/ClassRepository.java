package com.kbseed.repository;

import com.kbseed.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    List<ClassEntity> findByStudioIdOrderByClassDateAscStartTimeAsc(Long studioId);
    List<ClassEntity> findByStudioIdAndClassDate(Long studioId, LocalDate classDate);
    List<ClassEntity> findByIdIn(Collection<Long> ids);

    @Modifying
    @Query("""
            update ClassEntity c
            set c.capacity = c.capacity - 1,
                c.status = case when (c.capacity - 1) <= 0 then 'LLENA' else c.status end
            where c.id = :classId and c.studioId = :studioId and c.capacity > 0
            """)
    int decrementCapacityIfAvailable(@Param("classId") Long classId, @Param("studioId") Long studioId);

    @Modifying
    @Query("""
            update ClassEntity c
            set c.capacity = c.capacity + 1,
                c.status = 'DISPONIBLE'
            where c.id = :classId and c.studioId = :studioId and c.capacity < c.capacityTotal
            """)
    int incrementCapacityIfBelowTotal(@Param("classId") Long classId, @Param("studioId") Long studioId);
}
