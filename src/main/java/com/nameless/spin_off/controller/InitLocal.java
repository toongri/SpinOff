package com.nameless.spin_off.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitLocal {

    private final InitLocalService initLocalService;

    @PostConstruct
    public void init() {
        initLocalService.init();
    }

    @Component
    static class InitLocalService {

        @PersistenceContext
        EntityManager em;

        @Transactional
        public void init() {

        }
    }
}