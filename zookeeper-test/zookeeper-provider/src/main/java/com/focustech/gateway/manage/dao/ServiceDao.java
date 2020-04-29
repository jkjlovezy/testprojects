package com.focustech.gateway.manage.dao;

import com.focustech.gateway.manage.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceDao extends JpaRepository<ServiceEntity, Integer> {
    ServiceEntity findById(int id);
}
