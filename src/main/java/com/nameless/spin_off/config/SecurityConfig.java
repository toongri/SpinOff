package com.nameless.spin_off.config;

import com.nameless.spin_off.config.AccessDeniedHandler.CustomAccessDeniedHandler;
import com.nameless.spin_off.config.AuthenticationEntryPoint.CustomAuthenticationEntryPoint;
import com.nameless.spin_off.config.jwt.ExceptionHandlerFilter;
import com.nameless.spin_off.config.jwt.JwtAuthenticationFilter;
import com.nameless.spin_off.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource())
                .and()
                    .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                        .antMatchers("/api/member/search/**").hasRole("USER")
                        .antMatchers("/api/collection/**").hasRole("USER")
                        .antMatchers("/api/member/password/**").hasRole("USER")
                        .antMatchers("/api/member/info/**").hasRole("USER")
                        .antMatchers("/api/member/block/**").hasRole("USER")
                        .antMatchers(HttpMethod.POST, "/api/movie/kobis/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.POST, "/api/movie/naver").hasRole("ADMIN")

                        .antMatchers(HttpMethod.GET,"/api/hashtag/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/member/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/post/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/collection/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/movie/**").permitAll()

                        .antMatchers("/api/collection/**").hasRole("USER")
                        .antMatchers("/api/comment/**").hasRole("USER")
                        .antMatchers("/api/hashtag/**").hasRole("USER")
                        .antMatchers("/api/help/**").hasRole("USER")
                        .antMatchers("/api/main-page/following").hasRole("USER")
                        .antMatchers("/api/member/**").hasRole("USER")
                        .antMatchers("/api/movie/**").hasRole("USER")
                        .antMatchers("/api/post/**").hasRole("USER")
                        .anyRequest().permitAll()
                .and()
                    .exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint)
                .and()
                    .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("https://developer.spinoff-story.com"); //허용 출처
        configuration.addAllowedOrigin("https://develop.d1rld0sjpnulkl.amplifyapp.com"); //허용 출처
        configuration.setAllowedHeaders(Arrays.asList("X-AUTH-TOKEN", "Content-Type"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
