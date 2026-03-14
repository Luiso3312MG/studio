package com.kbseed.repository;

import com.kbseed.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
    List<ClientEntity> findByStudioIdOrderByFirstNameAscLastNameAsc(Long studioId);
}
