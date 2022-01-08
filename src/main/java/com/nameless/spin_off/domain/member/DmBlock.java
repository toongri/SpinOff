package com.nameless.spin_off.domain.member;

import com.nameless.spin_off.domain.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DmBlock extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="dmblock_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dmblockedmember_id")
    @NotNull
    private Member dmBlockedMember;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static DmBlock createDmBlock(Member member, Member dmBlockedMember) {

        DmBlock dmBlock = new DmBlock();
        dmBlock.updateMember(member);
        dmBlock.updateDmBlockedMember(dmBlockedMember);

        return dmBlock;

    }

    //==수정 메소드==//
    private void updateDmBlockedMember(Member dmBlockedMember) {
        this.dmBlockedMember = dmBlockedMember;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
