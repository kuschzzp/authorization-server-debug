package com.goodcol.muses.configurations;

import com.goodcol.muses.repository.AuthorizationConsentRepository;
import com.goodcol.muses.repository.AuthorizationRepository;
import com.goodcol.muses.repository.ClientRepository;
import com.goodcol.muses.service.DefaultOAuth2AuthorizationConsentServiceImpl;
import com.goodcol.muses.service.DefaultOAuth2AuthorizationServiceImpl;
import com.goodcol.muses.service.DefaultRegisteredClientRepositoryImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 * 一些Bean的注册
 *
 * @author Zhangzp
 * @date 2022年12月27日 16:26
 */
@Configuration(proxyBeanMethods = false)
public class OAuth2Configuration {

    /**
     * 授权同意信息service
     *
     * @param authorizationConsentRepository
     * @param registeredClientRepository
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService(
            AuthorizationConsentRepository authorizationConsentRepository,
            RegisteredClientRepository registeredClientRepository) {
        return new DefaultOAuth2AuthorizationConsentServiceImpl(authorizationConsentRepository,
                registeredClientRepository);
    }

    /**
     * 授权service
     *
     * @param authorizationRepository
     * @param registeredClientRepository
     * @return org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
     * @author Mr.kusch
     * @date 2022/12/27 16:32
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2AuthorizationService oAuth2AuthorizationService(AuthorizationRepository authorizationRepository,
                                                                 RegisteredClientRepository registeredClientRepository) {
        return new DefaultOAuth2AuthorizationServiceImpl(authorizationRepository, registeredClientRepository);
    }

    /**
     * 注册的客户端service
     *
     * @param clientRepository
     * @return org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
     * @author Mr.kusch
     * @date 2022/12/27 16:32
     */
    @Bean
    @ConditionalOnMissingBean
    public RegisteredClientRepository registeredClientRepository(ClientRepository clientRepository) {
        return new DefaultRegisteredClientRepositoryImpl(clientRepository);
    }



    /**
     * 操作授权同意的sql
     *
     * @param
     * @return com.goodcol.muses.oauth.repository.AuthorizationConsentRepository
     * @author Mr.kusch
     * @date 2022/12/30 10:43
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthorizationConsentRepository authorizationConsentRepository() {
        return new AuthorizationConsentRepository();
    }

    /**
     * 操作授权sql
     *
     * @param
     * @return com.goodcol.muses.oauth.repository.AuthorizationRepository
     * @author Mr.kusch
     * @date 2022/12/30 10:43
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthorizationRepository authorizationRepository() {
        return new AuthorizationRepository();
    }

    /**
     * 操作客户端sql
     *
     * @param
     * @return com.goodcol.muses.oauth.repository.ClientRepository
     * @author Mr.kusch
     * @date 2022/12/30 10:43
     */
    @Bean
    @ConditionalOnMissingBean
    public ClientRepository clientRepository() {
        return new ClientRepository();
    }


}
