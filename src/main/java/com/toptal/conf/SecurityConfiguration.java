/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Alexey Saenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.toptal.conf;

import com.toptal.controller.EntryController;
import com.toptal.controller.SignupController;
import com.toptal.controller.UserController;
import com.toptal.dao.UserDao;
import com.toptal.entities.User;
import com.toptal.entities.User.Role;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring security configuration.
 *
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * User DAO.
     */
    @Autowired
    private transient UserDao dao;

    @Override
    public final void configure(final AuthenticationManagerBuilder auth)
        throws Exception {
        auth.userDetailsService(this.userDetails());
        auth.authenticationProvider(this.authenticationProvider());
    }

    /**
     * Creates password encoder.
     * @return Password encoder.
     * @checkstyle DesignForExtensionCheck (5 lines)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates dao authentication provider.
     * @return Authentication provider.
     * @checkstyle DesignForExtensionCheck (10 lines)
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider prov = new DaoAuthenticationProvider();
        prov.setUserDetailsService(this.userDetails());
        prov.setPasswordEncoder(this.passwordEncoder());
        return prov;
    }

    @Override
    protected final void configure(final HttpSecurity http) throws Exception {
        final String format = "%s/*";
        http.csrf().disable();
        http.httpBasic();
        http.authorizeRequests()
            .antMatchers(HttpMethod.POST, SignupController.PATH).anonymous()
            .antMatchers(UserController.PATH).hasRole(Role.ROLE_MANAGER.text())
            .antMatchers(String.format(format, UserController.PATH))
            .hasRole(Role.ROLE_MANAGER.text())
            .antMatchers(EntryController.PATH).hasRole(Role.ROLE_USER.text())
            .antMatchers(String.format(format, EntryController.PATH))
            .hasRole(Role.ROLE_USER.text());
    }

    /**
     * User details service bean.
     * @return Singleton bean.
     * @checkstyle DesignForExtensionCheck (5 lines)
     * @checkstyle AnonInnerLengthCheck (30 lines)
     */
    @Bean
    @SuppressWarnings("PMD.DefaultPackage")
    UserDetailsService userDetails() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(final String name) {
                log.debug("Load user by username '{}'", name);
                final User user =
                    SecurityConfiguration.this.dao.findByName(name);
                if (user == null) {
                    throw new UsernameNotFoundException(
                        String.format("User '%s' not found", name)
                    );
                }
                log.debug(
                    "User '{}' has authorities {}",
                    user.getName(),
                    this.authorities(user)
                );
                return new org.springframework.security.core.userdetails.User(
                    user.getName(),
                    user.getPassword(),
                    this.authorities(user)
                );
            }
            private List<GrantedAuthority> authorities(final User user) {
                final List<GrantedAuthority> result =
                    AuthorityUtils.createAuthorityList(
                        User.Role.ROLE_USER.toString()
                    );
                if (user.isManager()) {
                    result.addAll(
                        AuthorityUtils.createAuthorityList(
                            User.Role.ROLE_MANAGER.toString()
                        )
                    );
                }
                return result;
            }
        };
    }

}
