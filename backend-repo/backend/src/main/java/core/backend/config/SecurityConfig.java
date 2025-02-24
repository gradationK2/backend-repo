package core.backend.config;

import core.backend.jwt.JwtFilter;
import core.backend.service.MemberDetailService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;

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
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션 비활성화 (JWT 인증 사용)
                .authorizeHttpRequests(auth -> auth
                        //인증 없이 접근 가능
                        .requestMatchers("/api/auth/**").permitAll() //로그인, 회원가입은 인증 없이 접근 가능
                        .requestMatchers("/api/food/**").permitAll() //음식 리스트, 상세 조회, 검색
                        .requestMatchers("/api/food/upload").permitAll() // 파일 업로드는 인증 없이 접근 가능
                        .requestMatchers("/reviews/users/**").permitAll() //리뷰 조회는 로그인 없이 가능
                        //.requestMatchers("/api/food/detail/**").permitAll() // 음식 상세 조회

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
