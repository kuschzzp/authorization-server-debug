package com.goodcol.muses.service;

import com.goodcol.muses.entity.OauthTestUser;
import com.goodcol.muses.repository.UserRepository;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Optional;

/**
 * Mysql的用户管理
 *
 * @author Zhangzp
 * @date 2022年12月19日 13:35
 */
public class MySQLUserDetailServiceImpl implements UserDetailsManager, UserDetailsPasswordService {

    @Resource
    private UserRepository userRepository;

    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<OauthTestUser> userByUsername = userRepository.findUserByUsername(username);
        if (!userByUsername.isPresent()) {
            throw new UsernameNotFoundException("用户不存在！");
        }
        return userByUsername.get();
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        Optional<OauthTestUser> userByUsername = userRepository.findUserByUsername(user.getUsername());
        if (userByUsername.isPresent()) {
            OauthTestUser testUser = userByUsername.get();
            testUser.setPassword(newPassword);
            userRepository.updadte(testUser);
            return testUser;
        }
        return null;
    }

    @Override
    public void createUser(UserDetails user) {
        OauthTestUser testUser = new OauthTestUser();
        testUser.setUsername(user.getUsername());
        testUser.setPassword(user.getPassword());
        testUser.setAuthCodes(StringUtils.join(user.getAuthorities(), ","));
        userRepository.save(testUser);
    }

    @Override
    public void updateUser(UserDetails user) {
        OauthTestUser testUser = new OauthTestUser();
        testUser.setUsername(user.getUsername());
        testUser.setPassword(user.getPassword());
        testUser.setAuthCodes(StringUtils.join(user.getAuthorities(), ","));
        userRepository.updadte(testUser);
    }

    @Override
    public void deleteUser(String username) {
        userRepository.delete(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = this.securityContextHolderStrategy.getContext().getAuthentication();
        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context " + "for current user.");
        }
        String username = currentUser.getName();
        Optional<OauthTestUser> userByUsername = userRepository.findUserByUsername(username);
        if (userByUsername.isPresent()) {
            OauthTestUser testUser = new OauthTestUser();
            testUser.setUsername(userByUsername.get().getUsername());
            testUser.setPassword(userByUsername.get().getPassword());
            testUser.setAuthCodes(StringUtils.join(currentUser.getAuthorities(), ","));
            userRepository.updadte(testUser);
        }
    }

    @Override
    public boolean userExists(String username) {
        Optional<OauthTestUser> userByUsername = userRepository.findUserByUsername(username);
        return userByUsername.isPresent();
    }


}
