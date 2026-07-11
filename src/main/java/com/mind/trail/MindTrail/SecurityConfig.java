package com.mind.trail.MindTrail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
        
            .csrf(csrf -> csrf.disable())

            // .sessionManagement(session ->
            //     session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            // )



            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login.html", "/register.html").permitAll()
                .requestMatchers("/mood/**","/user/**","/habit/**").authenticated()
                .requestMatchers("/dashboard.html", "/habit.html", "/mood.html").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            )

            // Use Basic Auth
            // .httpBasic(Customizer.withDefaults());

            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/user/login") // spring security intercepts it
                .defaultSuccessUrl("/dashboard.html", true)
                .failureUrl("/login.html?error=true")
                .permitAll()
        )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html?logout=true")
            );

            // Use Form Login
            // .formLogin(Customizer.withDefaults());

        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
