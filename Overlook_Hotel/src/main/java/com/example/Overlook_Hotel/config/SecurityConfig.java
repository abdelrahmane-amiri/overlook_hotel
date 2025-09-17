package com.example.Overlook_Hotel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration /* Signifie qu'une classe contient des beans pour un conteneur Spring */
public class SecurityConfig {

    @Bean /* Un bean est un objet gérer par le conteneur */
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { /* HttpSecurity permet de configurer la sécurité HTTP */
        http
            .authorizeHttpRequests(auth -> auth /* TOUTES REQUES COMMENCANT PAR --- SERONT ACCESSIBLES SEULEMENT PAR --- */
                .requestMatchers("/admin/**").hasRole("GESTIONNAIRE") 
                .requestMatchers("/employe/**").hasAnyRole("EMPLOYE", "GESTIONNAIRE")
                .requestMatchers("/client/**").hasRole("CLIENT")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form  /* Indique qu'une session est ouverte ou fermer selon la connexion ou la deconnexion */
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout.permitAll());

        return http.build();
    }
}


