package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.entity.member.DirectMessage;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.nameless.spin_off.entity.member.QDirectMessage.directMessage;

@Repository
public class DirectMessageQueryRepository extends Querydsl4RepositorySupport {
    public DirectMessageQueryRepository() {
        super(DirectMessage.class);
    }

    public Optional<Long> getDMOwnerId(Long dmId) {
        return Optional.ofNullable(getQueryFactory()
                .select(directMessage.member.id)
                .from(directMessage)
                .where(
                        directMessage.id.eq(dmId))
                .fetchFirst());
    }
}
