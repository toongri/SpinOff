package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockedMember extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="blocked_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_members_id")
    @NotNull
    private Member BlockedMember;

    @Enumerated(EnumType.STRING)
    @Column(name = "blocked_member_status")
    private BlockedMemberStatus blockedMemberStatus;


    //==연관관계 메소드==//

    //==생성 메소드==//
    public static BlockedMember createBlockedMember(Member member, Member blockedMember) {

        BlockedMember blockedMember1 = new BlockedMember();
        blockedMember1.updateMember(member);
        blockedMember1.updateDmBlockedMember(blockedMember);

        return blockedMember1;

    }

    //==수정 메소드==//
    private void updateDmBlockedMember(Member dmBlockedMember) {
        this.BlockedMember = dmBlockedMember;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
