package com.kbseed.repository;

import com.kbseed.entity.AppSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppSettingRepository extends JpaRepository<AppSettingEntity, Long> {
    Optional<AppSettingEntity> findBySettingKey(String settingKey);
}
