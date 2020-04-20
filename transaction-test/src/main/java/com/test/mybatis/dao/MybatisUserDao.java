package com.test.mybatis.dao;

import com.test.entity.User;
import org.apache.ibatis.annotations.Mapper;

public interface MybatisUserDao {
    int insert(User user);
    int countUser();
}
