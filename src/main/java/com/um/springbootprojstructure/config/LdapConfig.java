package com.um.springbootprojstructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
public class LdapConfig {

    @Bean
    public LdapContextSource ldapContextSource(
            @Value("${app.ldap.urls}") String urls,
            @Value("${app.ldap.base-dn}") String baseDn,
            @Value("${app.ldap.bind-dn}") String bindDn,
            @Value("${app.ldap.bind-password}") String bindPassword
    ) {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(urls);
        contextSource.setBase(baseDn);
        contextSource.setUserDn(bindDn);
        contextSource.setPassword(bindPassword);
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate(LdapContextSource ldapContextSource) {
        return new LdapTemplate(ldapContextSource);
    }
}
