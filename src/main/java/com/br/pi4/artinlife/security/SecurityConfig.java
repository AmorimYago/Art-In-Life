package com.br.pi4.artinlife.security;

import com.br.pi4.artinlife.service.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final AppUserDetailsService userDetailsService;
    private final CustomSuccessHandler successHandler; // ✅ injeta aqui

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // rotas da pasta admin – protegidas
                        .requestMatchers("/admin/productsadm").hasAnyRole("STOCKER", "ADMIN")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN")

                        // rotas de API – protegidas
                        .requestMatchers("/api/me").hasAnyRole("ADMIN", "STOCKER")
                        /*.requestMatchers("/api/products").hasAnyRole("ADMIN", "STOCKER")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")*/

                        // tudo mais é público
                        .requestMatchers("/**").permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/loginadm")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler) // ✅ aqui usa o handler customizado
                        .failureUrl("/loginadm?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/loginadm?logout")
                )
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
