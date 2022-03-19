package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.entity.member.EmailLinkage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.nameless.spin_off.entity.member.QEmailLinkage.emailLinkage;


@Repository
@RequiredArgsConstructor
public class EmailLinkageQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<EmailLinkage> findValidLinkageByEmail(
            String accountId, String email,
            String authToken, LocalDateTime currentTime) {
        EmailLinkage firstEmailLinkage = jpaQueryFactory
                .selectFrom(emailLinkage)
                .where(
                        emailLinkage.accountId.eq(accountId),
                        emailLinkage.email.eq(email),
                        emailLinkage.authToken.eq(authToken),
                        emailLinkage.createdDate.after(currentTime),
                        emailLinkage.expired.eq(false))
                .orderBy(emailLinkage.id.desc())
                .fetchFirst();

        return Optional.ofNullable(firstEmailLinkage);
    }
}
