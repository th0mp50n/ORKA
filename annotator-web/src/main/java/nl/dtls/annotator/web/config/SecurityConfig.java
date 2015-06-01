/*
 * Copyright (C) 2015 DTL (${email})
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.dtls.annotator.web.config;

import nl.dtls.annotator.web.security.OAuth2ResponseFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
@EnableWebSecurity
@ComponentScan("nl.dtls.annotator.web.security")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private OAuth2ResponseFilter oauth2ResponseFilter;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/auth/**").anonymous()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/auth")
                .and()
            .logout()
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth?logout")
                .and()
            .addFilterAfter(oauth2ResponseFilter, AbstractPreAuthenticatedProcessingFilter.class)
            // TODO integrate spring-sec csrf and angular xsrf
            .csrf().disable();
    }
}
