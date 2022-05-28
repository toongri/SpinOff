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
                .cors()
                .and()
                    .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                        .antMatchers("/api/member/search").hasRole("USER")
                        .antMatchers("/api/collection/one").hasRole("USER")
                        .antMatchers("/api/collection/all").hasRole("USER")
                        .antMatchers("/api/member/check/**").hasRole("USER")

                        .antMatchers(HttpMethod.GET,"/api/hashtag/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/member/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/post/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/collection/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/movie/**").permitAll()

                        .antMatchers(HttpMethod.POST, "/api/movie/kobis/**").permitAll()

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
//        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class);
    }
//
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/v2/api-docs", "/configuration/**", "/swagger-resources/**",  "/swagger-ui.html", "/webjars/**", "/api-docs/**");
//    }
}
