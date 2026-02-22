package com.movieWorld.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import lombok.RequiredArgsConstructor;


/**
 * Spring Security 설정
 * - 로그인/로그아웃 방식
 * - URL별 접근 권한 (인증 필요 경로, 관리자 전용 경로)
 * - 비밀번호 암호화 방식 (BCrypt)
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final UserDetailsService userDetailsService;

	/**
     * 비밀번호 암호화에 사용할 Encoder
     * BCrypt 사용 (회원가입 시 저장, 로그인 시 검증에 사용)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security 필터 체인 설정
     * - 어떤 URL은 로그인 없이 허용, 어떤 URL은 로그인/권한 필요
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            /* CSRF: 폼 제출 시 토큰 검증 (기본 활성화) */
            .csrf(csrf -> csrf.disable())

            /* URL별 접근 제어 */
            .authorizeHttpRequests(auth -> auth
                /* 정적 리소스, 로그인/회원가입 페이지: 누구나 접근 */
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/login", "/signup", "/").permitAll()
                .requestMatchers("/boards").permitAll()
                .requestMatchers("/boards/write", "/boards/*/edit").authenticated()
                .requestMatchers( "/movies/**").permitAll()
                /* 관리자 경로: ROLE_ADMIN만 */
                .requestMatchers("/admin/**").hasRole("ADMIN")
                /* 그 외: 로그인한 사용자만 */
                .anyRequest().authenticated()
            )

            /* 폼 로그인 설정 */
            .formLogin(form -> form
                .loginPage("/login")           /* 로그인 페이지 URL */
                .defaultSuccessUrl("/", true)   /* 로그인 성공 시 이동 */
                .failureUrl("/login?error")    /* 로그인 실패 시 이동 */
				.usernameParameter("username")  // 로그인 폼의 username 필드명
				.passwordParameter("password")  // 로그인 폼의 password 필드명
                .permitAll()
            )

            /* 로그아웃 설정 */
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)   /* 세션 무효화 */
				.deleteCookies("JSESSIONID")  // 쿠키 삭제
                .permitAll()
            )
            .userDetailsService(userDetailsService);  // id pw 외에 다른 사용자정보를 가져온다.
        	

        return http.build();
    }
	
}
