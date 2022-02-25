package com.nameless.spin_off.config;

import com.nameless.spin_off.entity.enums.EnumMapper;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
@RequiredArgsConstructor
public class SpringConfig {

    private final EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }

    @Bean
    public EnumMapper enumMapper() {
        EnumMapper enumMapper = new EnumMapper();

        enumMapper.put("PublicOfPostStatus", PublicOfPostStatus.class);

        return enumMapper;
    }
}
