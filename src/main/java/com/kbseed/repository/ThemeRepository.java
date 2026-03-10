package com.kbseed.repository;

import com.kbseed.entity.ThemeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeRepository extends JpaRepository<ThemeEntity, Long> {
}
