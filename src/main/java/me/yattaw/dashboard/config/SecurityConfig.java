package me.yattaw.dashboard.config;

import me.yattaw.dashboard.entities.RoleTypes;
import me.yattaw.dashboard.service.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserService userService() {
        return new UserService();
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .requestMatchers("/create").permitAll()
                .requestMatchers("/register").permitAll()
                .requestMatchers("/", "/home").hasAnyAuthority(RoleTypes.userAuthorities())
                .requestMatchers("/user").hasAnyAuthority(RoleTypes.userAuthorities())
                .requestMatchers("/admin").hasAnyAuthority(RoleTypes.adminAuthorities())
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll();
        return http.build();
    }

    @Bean
    ApplicationListener<AuthenticationSuccessEvent> successEvent() {
        return event -> System.out.println(
                "Success Login " +
                        event.getAuthentication().getClass().getSimpleName() +
                        " - " +
                        event.getAuthentication().getName()
        );
    }

    @Bean
    ApplicationListener<AuthenticationFailureBadCredentialsEvent> failureEvent() {
        return event -> System.err.println(
                "Bad Credentials Login " +
                        event.getAuthentication().getClass().getSimpleName() +
                        " - " + event.getAuthentication().getName()
        );
    }


}