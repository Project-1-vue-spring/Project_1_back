package com.project_1.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_1.jwt.JwtAuthenticationFilter;
import com.project_1.jwt.JwtTokenProvider;
import com.project_1.jwt.UserDetailService;
import com.project_1.oauth2.CustomOAuth2UserService;
import com.project_1.oauth2.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {


//    private final CustomOAuth2UserService customOAuth2UserService;
//
//    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
//        this.customOAuth2UserService = customOAuth2UserService;
//    }

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailService userService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, UserDetailService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers("/swagger-ui/**").hasRole(Role.ADMIN.name()) // 스웨거 주소는 관리자권한
                        .requestMatchers( "/v3/api-docs/**").hasRole(Role.ADMIN.name()) // 스웨거 주소는 관리자권한
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exceptionConfig) ->
                        exceptionConfig.authenticationEntryPoint(unauthorizedEntryPoint).accessDeniedHandler(accessDeniedHandler)
                ); // 401 403 관련 예외처리
//                .oauth2Login(oauth2Login ->
//                        oauth2Login.userInfoEndpoint(userInfoEndpointConfig ->
//                                userInfoEndpointConfig.userService(customOAuth2UserService)))
        ;
        return http.build();
    }


    private final AuthenticationEntryPoint unauthorizedEntryPoint =
            (request, response, authException) -> {
                ErrorResponse fail = new ErrorResponse(HttpStatus.UNAUTHORIZED, "Spring security unauthorized...");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                String json = new ObjectMapper().writeValueAsString(fail);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                PrintWriter writer = response.getWriter();
                writer.write(json);
                writer.flush();
            };

    private final AccessDeniedHandler accessDeniedHandler =
            (request, response, accessDeniedException) -> {
                ErrorResponse fail = new ErrorResponse(HttpStatus.FORBIDDEN, "Spring security forbidden...");
                response.setStatus(HttpStatus.FORBIDDEN.value());
                String json = new ObjectMapper().writeValueAsString(fail);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                PrintWriter writer = response.getWriter();
                writer.write(json);
                writer.flush();
            };

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final HttpStatus status;
    private final String message;
}

}

//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final JWTUtil jwtUtil;
//
//    private final CustomSuccessHandler customSuccessHandler;
//
//    private final CustomOAuth2UserService customOAuth2UserService;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) {
//
//        http
//                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
//
//                    @Override
//                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//
//                        CorsConfiguration configuration = new CorsConfiguration();
//
//                        configuration.setAllowedOrigins(Collections.singletonList("*")); // http://localhost:3000와 같이 주소로 허용가능
//                        configuration.setAllowedMethods(Collections.singletonList("*"));
//                        configuration.setAllowCredentials(true);
//                        configuration.setAllowedHeaders(Collections.singletonList("*"));
//                        configuration.setMaxAge(3600L);
//
//                        // 우리쪽 서버에서 보낼때
//                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
//                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));
//
//                        return configuration;
//                    }
//                }));
//
//        //csrf disable
//        http
//                .csrf((auth) -> auth.disable());
//
//        //From 로그인 방식 disable
//        http
//                .formLogin((auth) -> auth.disable());
//
//        //HTTP Basic 인증 방식 disable
//        http
//                .httpBasic((auth) -> auth.disable());
//
//        //JWT Filter
//        http
//                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
//
//        //oauth2
//        http
//                .oauth2Login(oauth2 -> oauth2
//                        .successHandler(customSuccessHandler)
//                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
//                                .userService(customOAuth2UserService)));
//
//        //경로별 인가 작업
//        http
//                .authorizeHttpRequests((auth) -> auth
//                        .requestMatchers("/", "/reissue").permitAll()
//                        .anyRequest().authenticated());
//
//        //세션 설정 : STATELESS
//        http
//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        return http.build();
//
//    }
//}