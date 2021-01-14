package com.socatel.configurations;

import com.socatel.handlers.CustomAuthenticationFailureHandler;
import com.socatel.handlers.CustomAuthenticationSuccessHandler;
import com.socatel.handlers.CustomLogoutSuccessHandler;
import com.socatel.services.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired private DataSource dataSource;
    @Autowired private UserServiceImpl userService;
    @Autowired private CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final String defaultSuccessUrl = "/dashboard";

    /*@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .usersByUsernameQuery(USERS_QUERY)
                .authoritiesByUsernameQuery(ROLES_QUERY)
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder());
    }*/

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // Endpoints
                    .requestMatchers(EndpointRequest.to("health", "liveness", "readiness")).permitAll()
                // Static files (js, css, images)
                    .antMatchers("/images/**", "/assets/**", "/front/**", "/global_assets/**", "/home/**", "/favicon.ico", "/static/**").permitAll()
                // Public functions
                    .antMatchers("/show-image/**", "/contactForm").permitAll()
                // WP4 components
                    .antMatchers("/resource_registration/**").permitAll()
                // Public files
                    .antMatchers("/", "/index", "/index.html", "/error", "/errors/**", "/faq", "/faq.html",
                            "/topic-find", "/topic-find.html", "/topic/**", "/topic.html/**","/privacy-policy",
                            "/privacy-policy.html", "/terms-conditions", "/terms-conditions.html",
                            "/cookie-policy", "/cookie-policy.html", "/explore-services", "/explore-services.html")
                            .permitAll()
                // Registration and Login
                    .antMatchers("/login", "/login.html", "/registration", "/registration.html", "/registrationConfirm", "/regSucc").permitAll()
                // New registration token
                    .antMatchers("/resendRegistrationToken", "/registration-token-email").permitAll()
                // Forgot password
                    .antMatchers("/forgot-password", "/forgot-password.html", "/reset-password").permitAll()
                // Reject organisation invitation
                    .antMatchers("/reject-invitation").permitAll()
                // Roles
                    .antMatchers("/admin/**").hasRole("ADMIN").anyRequest().authenticated()
                    .antMatchers("/moderator/**").hasRole("MODERATOR").anyRequest().authenticated()
                .and()
                // Login
                    .formLogin()
                    .loginPage("/login").permitAll()
                    .defaultSuccessUrl(defaultSuccessUrl)
                    .failureUrl("/login?error=true")
                    .successHandler(customAuthenticationSuccessHandler())
                    .failureHandler(customAuthenticationFailureHandler())
                .and()
                // Logout
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/")
                    .logoutSuccessHandler(customLogoutSuccessHandler)
                    .deleteCookies("JSESSIONID").
                and()
                    .headers()
                        .cacheControl()
                        .and().contentTypeOptions()
                        .and().httpStrictTransportSecurity()
                        .and().frameOptions()
                        .and().xssProtection();
                //.and()
                // HTTPS
                    //.requiresChannel().anyRequest().requiresSecure();//.and().portMapper().http(8080).mapsTo(8443);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler(defaultSuccessUrl);
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
        db.setDataSource(dataSource);
        return db;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}