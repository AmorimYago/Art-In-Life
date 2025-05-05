package com.br.pi4.artinlife.security;

import com.br.pi4.artinlife.service.AppUserDetailsService;
import com.br.pi4.artinlife.service.ClientDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final AppUserDetailsService userDetailsService;
    private final ClientDetailsService clientDetailsService;
    private final CustomSuccessHandler successHandler;

    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**", "/loginadm", "/logout", "/login")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/productsadm").hasAnyRole("STOCKER", "ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/me").hasAnyRole("ADMIN", "STOCKER")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/loginadm")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler)
                        .failureUrl("/loginadm?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/loginadm?logout")
                )
                .authenticationProvider(adminAuthenticationProvider());

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain clientSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/client/**", "/cart/**", "/login-client", "/logoutclient", "/dologinclient", "/api/client/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/client/**", "/cart/**", "/api/client/me").hasRole("CLIENT")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login-client")
                        .loginProcessingUrl("/dologinclient")
                        .successHandler(successHandler)
                        .failureUrl("/login-client?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logoutclient")
                        .logoutSuccessUrl("/loginclient?logout=true")
                )
                .authenticationProvider(clientAuthenticationProvider());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public DaoAuthenticationProvider clientAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(clientDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}