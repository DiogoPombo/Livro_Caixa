package com.Livro.Caixa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("DiogoPombo")
                .password("{noop}990099") // {noop} = sem criptografia
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated() // todas as rotas exigem login
            )
            .formLogin(form -> form.defaultSuccessUrl("/", true))
            .logout(logout -> logout.permitAll());

        // Necessário para o H2 Console funcionar mesmo protegido
        http.csrf().disable();
        http.headers().frameOptions().disable();

        return http.build();
    }
}