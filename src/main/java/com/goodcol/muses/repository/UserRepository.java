package com.goodcol.muses.repository;

import com.goodcol.muses.entity.OauthTestUser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 操作用于调试的用户表
 *
 * @author Mr.kusch
 * @date 2022/12/19 11:30
 */
@Slf4j
@Repository
public class UserRepository {

    @Resource
    private JdbcOperations jdbcOperations;

    public Optional<OauthTestUser> findUserByUsername(String username) {
        OauthTestUser query = jdbcOperations
                .queryForObject(
                        "select username, password, auth_codes from oauth_test_user where username =?",
                        new BeanPropertyRowMapper<>(OauthTestUser.class),
                        username);
        return Optional.ofNullable(query);
    }

    public void save(OauthTestUser user) {
        int update = jdbcOperations.update(
                "insert into oauth_test_user(username, password, auth_codes) values (?,?,?)",
                user.getUsername(),
                user.getPassword(),
                user.getAuthCodes());
        if (update == 0) {
            log.warn("oauth_test_user数据入库失败！");
        }
    }

    public void updadte(OauthTestUser user) {
        int update = jdbcOperations.update("update oauth_test_user set password=?,auth_codes=? where " +
                        "username=?",
                user.getPassword(),
                user.getAuthCodes(),
                user.getUsername());
        if (update == 0) {
            log.warn("oauth_test_user数据更新入库失败！");
        }
    }

    public void delete(String username) {
        int update = jdbcOperations.update("delete from oauth_test_user where username=?", username);
        if (update == 0) {
            log.warn("oauth_test_user数据删除失败！");
        }
    }

}
