package com.focustech.gateway.manage.dao;

import com.focustech.gateway.manage.entity.ApiEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiDao extends JpaRepository<ApiEntity, Integer> {
}
