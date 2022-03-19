package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.entity.enums.help.ContentTypeStatus;
import com.nameless.spin_off.entity.help.Complain;
import com.nameless.spin_off.repository.support.Querydsl4RepositorySupport;
import org.springframework.stereotype.Repository;

import static com.nameless.spin_off.entity.help.QComplain.complain;

@Repository
public class ComplainQueryRepository extends Querydsl4RepositorySupport {
    public ComplainQueryRepository() {
        super(Complain.class);
    }

    public boolean isExistComplain(Long memberId, Long contentId, ContentTypeStatus contentTypeStatus) {
        Integer fetchOne = getQueryFactory()
                .selectOne()
                .from(complain)
                .where(
                        complain.member.id.eq(memberId),
                        complain.contentId.eq(contentId),
                        complain.contentTypeStatus.eq(contentTypeStatus))
                .fetchFirst();

        return fetchOne != null;
    }
}
