package com.kbseed.repository;

import com.kbseed.entity.ThemeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ThemeRepository extends JpaRepository<ThemeEntity, Long> {

    Optional<ThemeEntity> findByStudioId(Long studioId);

}
