package com.nameless.spin_off.domain.member;

import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPreference {

    @Id
    @GeneratedValue
    @Column(name="memberpreference_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    private int score;

    @Enumerated(EnumType.STRING)
    @Column(name = "memberpreference_status")
    private MemberPreferenceStatus memberPreferenceStatus;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static MemberPreference createMemberPreference(Member member, int score, MemberPreferenceStatus memberPreferenceStatus) {

        MemberPreference memberPreference = new MemberPreference();
        memberPreference.updateMember(member);
        memberPreference.updateScore(score);
        memberPreference.updateMemberPreferenceStatus(memberPreferenceStatus);

        return memberPreference;

    }

    //==수정 메소드==//
    private void updateScore(int score) {
        this.score = score;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    private void updateMemberPreferenceStatus(MemberPreferenceStatus memberPreferenceStatus) {
        this.memberPreferenceStatus = memberPreferenceStatus;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
