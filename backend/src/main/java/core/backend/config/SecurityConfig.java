package core.backend.config;

import java.util.Arrays;
import core.backend.jwt.JwtFilter;
import core.backend.service.MemberDetailService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// Spring Security 설정 클래스
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final MemberDetailService memberDetailService;
    private final JwtFilter jwtFilter;

    // Spring Security 설정(보안 필터 체인 구성)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) //CSRF 비활성화 (REST API에서는 필요 없음)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // cors 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션 비활성화 (JWT 인증 사용)
                .authorizeHttpRequests(auth -> auth
                        //인증 없이 접근 가능
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll() //로그인, 회원가입은 인증 없이 접근 가능
                        .requestMatchers("/api/food/**").permitAll() //음식 리스트, 상세 조회, 검색
                        .requestMatchers("/api/food/upload").permitAll() // 파일 업로드는 인증 없이 접근 가능
                        .requestMatchers("/reviews/users/**").permitAll() //리뷰 조회는 로그인 없이 가능
                        //.requestMatchers("/api/food/detail/**").permitAll() // 음식 상세 조회

                        // TODO : GET 요청 중 인증없이 응답해야 하는 엔드포인트
                        // reviews/users/{userId} : 후기 리스트 조회
                        // heart/{foodId} : 음식 좋아요 개수 조회
                        // users/likes/{userId} : 회원이 좋아요한 음식 목록 조회
                        // users/badge/{userId} : 뱃지 조회

                        //인증 필요
                        .requestMatchers("/reviews").authenticated() //리뷰 작성, 수정, 삭제는 로그인 필요
                        .requestMatchers("/heart/**").authenticated() //좋아요 관련 api
                        .requestMatchers("/users/likes/**").authenticated() // 좋아요 목록 조회 로그인 필요
                        .requestMatchers("/users/badge/**").authenticated() // 뱃지 조회 로그인 필요
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요

                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json; charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"error\": \"Access Denied\", \"message\": \"로그인이 필요합니다.\"}");
                        })
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); //JWT 필터 적용

        return http.build();
    }

    @Bean // cors 오류 해결
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://www.asd1.store"));
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // 요청
        configuration.setExposedHeaders(Arrays.asList("Authorization")); // 응답
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 하위 디렉토리
        return source;
    }

    // 비밀번호 암호화를 위한 passwordEncoder설정
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager 설정 (로그인 시 필요)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
}
