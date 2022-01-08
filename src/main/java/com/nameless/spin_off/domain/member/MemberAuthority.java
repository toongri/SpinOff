package com.nameless.spin_off.domain.member;

import com.nameless.spin_off.domain.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAuthority extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="memberauthority_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "memberauthority_status")
    private MemberAuthorityStatus memberAuthorityStatus;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static MemberAuthority createMemberAuthority(Member member, MemberAuthorityStatus memberAuthorityStatus) {

        MemberAuthority memberAuthority = new MemberAuthority();
        memberAuthority.updateMember(member);
        memberAuthority.updateMemberAuthorityStatus(memberAuthorityStatus);

        return memberAuthority;

    }

    //==수정 메소드==//
    private void updateMemberAuthorityStatus(MemberAuthorityStatus memberAuthorityStatus) {
        this.memberAuthorityStatus = memberAuthorityStatus;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
