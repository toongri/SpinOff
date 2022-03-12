package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.entity.enums.member.EmailAuthProviderStatus;
import com.nameless.spin_off.entity.member.EmailAuth;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.nameless.spin_off.entity.member.QEmailAuth.emailAuth;


@Repository
@RequiredArgsConstructor
public class EmailAuthQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<EmailAuth> findValidAuthByEmail(
            String email, String authToken, LocalDateTime currentTime, EmailAuthProviderStatus provider) {
        EmailAuth firstEmailAuth = jpaQueryFactory
                .selectFrom(emailAuth)
                .where(
                        emailAuth.email.eq(email),
                        emailAuth.authToken.eq(authToken),
                        emailAuth.expireDate.goe(currentTime),
                        emailAuth.expired.eq(false),
                        emailAuth.provider.eq(provider))
                .orderBy(emailAuth.id.desc())
                .fetchFirst();

        return Optional.ofNullable(firstEmailAuth);
    }
}
