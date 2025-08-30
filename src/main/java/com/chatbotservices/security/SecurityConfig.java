package com.chatbotservices.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class SecurityConfig implements WebMvcConfigurer {


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // Disable CSRF (for testing; enable in production)
				.authorizeHttpRequests(
						auth -> auth.requestMatchers(AntPathRequestMatcher.antMatcher("/**")).permitAll());

		return http.build();
	}
	
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Applies to all endpoints
            .allowedOrigins("*") // Allowed origins
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
            .allowedHeaders("*") // Allows all headers
            .allowCredentials(false) // Allows sending credentials (e.g., cookies, authorization headers)
            .maxAge(3600); // Cache preflight response for 1 hour
    }
}

