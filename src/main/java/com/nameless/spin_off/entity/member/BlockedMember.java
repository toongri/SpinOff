package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockedMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="blocked_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocking_member_id")
    @NotNull
    private Member blockingMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "blocked_member_status")
    @NotNull
    private BlockedMemberStatus blockedMemberStatus;


    //==연관관계 메소드==//

    //==생성 메소드==//
    public static BlockedMember createBlockedMember(Member blockingMember, Member member, BlockedMemberStatus blockedMemberStatus) {
        BlockedMember blockedMember1 = new BlockedMember();
        blockedMember1.updateBlockedMemberStatus(blockedMemberStatus);
        blockedMember1.updateBlockingMember(blockingMember);
        blockedMember1.updateMember(member);

        return blockedMember1;
    }

    //==수정 메소드==//
    public void updateBlockingMember(Member blockingMember) {
        this.blockingMember = blockingMember;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    public void updateBlockedMemberStatus(BlockedMemberStatus blockedMemberStatus) {
        this.blockedMemberStatus = blockedMemberStatus;
    }

    //==비즈니스 로직==//

    //==조회 로직==//
    @Override
    public int hashCode() {
        return Objects.hash(member, blockingMember);
    }

    @Override
    public boolean equals(Object blockedMember) {
        if (blockedMember instanceof BlockedMember) {
            if ((((BlockedMember) blockedMember).getMember().equals(member))) {
                return ((BlockedMember) blockedMember).getBlockingMember().equals(blockingMember);
            }
        }

        return false;
    }
}
