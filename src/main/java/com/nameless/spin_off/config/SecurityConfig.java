package com.nameless.spin_off.config;

import com.nameless.spin_off.config.AccessDeniedHandler.CustomAccessDeniedHandler;
import com.nameless.spin_off.config.AuthenticationEntryPoint.CustomAuthenticationEntryPoint;
import com.nameless.spin_off.config.auth.CustomOAuth2UserService;
import com.nameless.spin_off.config.auth.OAuth2FailureHandler;
import com.nameless.spin_off.config.auth.OAuth2SuccessHandler;
import com.nameless.spin_off.config.jwt.JwtAuthenticationFilter;
import com.nameless.spin_off.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    private final CustomOAuth2UserService oAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2SuccessHandler successHandler;
    private final OAuth2FailureHandler failureHandler;

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
                        .antMatchers("/api/view/**").permitAll()
                        .antMatchers("/api/search/**").permitAll()
                        .antMatchers("/api/sign/**").permitAll()
                        .antMatchers("/api/main-page/discovery").permitAll()
                        .antMatchers("/api/hashtag/most-popular").permitAll()
                        .antMatchers("/api/post/post-public-categories").permitAll()
                        .anyRequest().hasRole("USER")
                .and()
                    .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                    .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                    .oauth2Login()
                        .failureHandler(failureHandler)
                        .successHandler(successHandler)
                        .userInfoEndpoint().userService(oAuth2UserService);

        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
    }
}
