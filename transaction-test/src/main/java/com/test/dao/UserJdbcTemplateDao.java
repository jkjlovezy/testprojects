package com.test.dao;

import com.test.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserJdbcTemplateDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(User user) {
        jdbcTemplate.update("insert into user(username,password) values(?,?)", user.getUsername(), user.getPassword());
    }


}
