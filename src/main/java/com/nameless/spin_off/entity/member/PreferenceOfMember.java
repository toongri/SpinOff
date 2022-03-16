package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.entity.enums.member.PreferenceOfMemberStatus;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreferenceOfMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="preference_of_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    private int score;

    @Enumerated(EnumType.STRING)
    @Column(name = "preference_of_member__status")
    private PreferenceOfMemberStatus preferenceOfMemberStatus;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static PreferenceOfMember createPreferenceOfMember(Member member, int score, PreferenceOfMemberStatus preferenceOfMemberStatus) {

        PreferenceOfMember preferenceOfMember = new PreferenceOfMember();
        preferenceOfMember.updateMember(member);
        preferenceOfMember.updateScore(score);
        preferenceOfMember.updatePreferenceOfMemberStatus(preferenceOfMemberStatus);

        return preferenceOfMember;

    }

    //==수정 메소드==//
    private void updateScore(int score) {
        this.score = score;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    private void updatePreferenceOfMemberStatus(PreferenceOfMemberStatus preferenceOfMemberStatus) {
        this.preferenceOfMemberStatus = preferenceOfMemberStatus;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
