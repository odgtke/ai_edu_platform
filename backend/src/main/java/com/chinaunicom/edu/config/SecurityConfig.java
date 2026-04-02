package com.chinaunicom.edu.config;

import com.chinaunicom.edu.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )
                .authorizeHttpRequests(authz -> authz
                        // 公开访问的静态资源
                        .requestMatchers(
                            new AntPathRequestMatcher("/"),
                            new AntPathRequestMatcher("/home"),
                            new AntPathRequestMatcher("/index.html"),
                            new AntPathRequestMatcher("/*.html"),
                            new AntPathRequestMatcher("/static/**"),
                            new AntPathRequestMatcher("/css/**"),
                            new AntPathRequestMatcher("/js/**"),
                            new AntPathRequestMatcher("/images/**"),
                            new AntPathRequestMatcher("/assets/**")
                        ).permitAll()
                        
                        // 认证相关API
                        .requestMatchers(
                            new AntPathRequestMatcher("/auth/login"),
                            new AntPathRequestMatcher("/auth/register"),
                            new AntPathRequestMatcher("/auth/public-key")
                        ).permitAll()
                        
                        // 健康检查端点
                        .requestMatchers(
                            new AntPathRequestMatcher("/actuator/**")
                        ).permitAll()
                        
                        // 临时开放用户和课程API供测试使用
                        .requestMatchers(new AntPathRequestMatcher("/users/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/courses/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/assessments/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/recommendations/**")).permitAll()
                        
                        // 学习记录API - 临时开放供测试
                        .requestMatchers(new AntPathRequestMatcher("/learning/**")).permitAll()
                        
                        // 资源管理API - 临时开放供测试
                        .requestMatchers(new AntPathRequestMatcher("/resources/**")).permitAll()
                        
                        // 权限管理API - 临时开放供测试
                        .requestMatchers(new AntPathRequestMatcher("/grades/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/classes/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/user-classes/**")).permitAll()
                        
                        // 消息通知API - 临时开放供测试
                        .requestMatchers(new AntPathRequestMatcher("/notifications/**")).permitAll()
                        
                        // 智能体相关API
                        .requestMatchers(new AntPathRequestMatcher("/agent/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/langchain/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/langchain/**")).permitAll()
                        
                        // 其他API都需要认证
                        // .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}