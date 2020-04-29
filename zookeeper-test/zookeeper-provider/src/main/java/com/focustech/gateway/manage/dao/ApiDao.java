package com.focustech.gateway.manage.dao;

import com.focustech.gateway.manage.dto.ApiZkNodeData;
import com.focustech.gateway.manage.entity.ApiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApiDao extends JpaRepository<ApiEntity, Integer> {
    ApiEntity findById(int id);

    @Query(value = "select a.authent_strategy, a.request_timeout,a.flowlimit_enable,a.rule_group_id ,b.service_path " +
            "from gw_api a join gw_service b on a.service_id = b.id where a.id=:id", nativeQuery = true)
    ApiZkNodeData findApiZkNodeData(@Param("id") int id);
}
