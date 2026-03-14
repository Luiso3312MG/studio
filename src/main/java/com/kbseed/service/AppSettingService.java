package com.kbseed.service;

import com.kbseed.repository.AppSettingRepository;
import org.springframework.stereotype.Service;

@Service
public class AppSettingService {
    public static final String RESERVATION_CANCEL_MINUTES = "RESERVATION_CANCEL_MINUTES";

    private final AppSettingRepository appSettingRepository;

    public AppSettingService(AppSettingRepository appSettingRepository) {
        this.appSettingRepository = appSettingRepository;
    }

    public int getReservationCancelMinutes() {
        return appSettingRepository.findBySettingKey(RESERVATION_CANCEL_MINUTES)
                .map(setting -> Integer.parseInt(setting.getSettingValue()))
                .orElse(30);
    }
}
