package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.entity.enums.member.EmailAuthProviderStatus;
import com.nameless.spin_off.entity.member.EmailAuth;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.nameless.spin_off.entity.member.QEmailAuth.emailAuth;
import static com.nameless.spin_off.entity.member.QMember.member;


@Repository
@RequiredArgsConstructor
public class EmailAuthQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<EmailAuth> findValidAuthByEmail(String email, String authToken,
                                                    LocalDateTime currentTime, EmailAuthProviderStatus provider) {
        EmailAuth firstEmailAuth = jpaQueryFactory
                .selectFrom(emailAuth)
                .where(
                        emailAuth.email.eq(email),
                        emailAuth.authToken.eq(authToken),
                        emailAuth.createdDate.after(currentTime),
                        emailAuth.expired.eq(false),
                        emailAuth.provider.eq(provider))
                .fetchFirst();

        return Optional.ofNullable(firstEmailAuth);
    }

    public Boolean isExistAuthEmail(String email, String authToken, LocalDateTime currentTime, EmailAuthProviderStatus provider) {
        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(emailAuth)
                .where(
                        emailAuth.email.eq(email),
                        emailAuth.authToken.eq(authToken),
                        emailAuth.lastModifiedDate.after(currentTime),
                        emailAuth.expired.eq(true),
                        emailAuth.provider.eq(provider))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isExistEmail(String email) {
        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(member)
                .where(
                        member.email.eq(email))
                .fetchFirst();

        return fetchOne != null;
    }

    public Boolean isNotExistNickname(String nickname) {
        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(member)
                .where(
                        member.nickname.eq(nickname))
                .fetchFirst();

        return fetchOne == null;
    }

    public Boolean isNotExistAccountId(String accountId) {
        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(member)
                .where(
                        member.accountId.eq(accountId))
                .fetchFirst();

        return fetchOne == null;
    }
}
