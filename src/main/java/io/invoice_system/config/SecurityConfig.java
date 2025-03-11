package io.invoice_system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

import io.invoice_system.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
//   @PreAuthorize("hasRole('ADMIN')")
	/*requestMatchers(HttpMethod.DELETE, "/topics/**").hasRole("ADMIN") 
                .requestMatchers(HttpMethod.POST, "/topics").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/topics/**").hasRole("ADMIN")*/
    private final JwtAuthEntryPoint authEntryPoint;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthEntryPoint authEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.authEntryPoint = authEntryPoint;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Apply CORS settings
            .csrf(csrf -> csrf.disable()) // Disable CSRF
            .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/signup", "/login", "/oauth2/**").permitAll() 
                .requestMatchers(HttpMethod.DELETE, "/invoices/**").hasAnyRole("SUPPORT_USER", "SUPERUSER")
                .requestMatchers(HttpMethod.POST, "/invoices").hasAnyRole("SUPPORT_USER", "SUPERUSER")
                .requestMatchers(HttpMethod.PUT, "/invoices/**").hasAnyRole("SUPPORT_USER", "SUPERUSER")
                .requestMatchers(HttpMethod.POST, "/execute").hasAnyRole("AUDITOR", "SUPERUSER")
                .requestMatchers(HttpMethod.POST, "/gemini/promote").hasAnyRole("AUDITOR", "SUPERUSER")
                .requestMatchers(HttpMethod.POST, "/jan/promote").hasAnyRole("AUDITOR", "SUPERUSER")
                .requestMatchers(HttpMethod.GET, "/reports/**").hasAnyRole("AUDITOR", "SUPERUSER")
                .requestMatchers(HttpMethod.GET, "/invoices_history").hasAnyRole("AUDITOR", "SUPERUSER")
                .requestMatchers(HttpMethod.GET, "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll() 
                .anyRequest().authenticated() 
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .oauth2Login()
            .defaultSuccessUrl("/", true) 
            .failureUrl("/login?error=true");

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); 
        configuration.setAllowCredentials(true); // Allow cookies, tokens, etc.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
    
    
