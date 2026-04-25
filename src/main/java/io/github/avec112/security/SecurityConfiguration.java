package io.github.avec112.security;

import static com.vaadin.flow.spring.security.VaadinSecurityConfigurer.vaadin;

import io.github.avec112.views.login.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain vaadinSecurityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/actuator/health", "/actuator/prometheus").permitAll()
                .requestMatchers("/images/*.png", "/*.css").permitAll()
                .requestMatchers("/line-awesome/**").permitAll()
        );

        http.with(vaadin(), vaadin -> {
            vaadin.loginView(LoginView.class);
        });

        return http.build();
    }

}
