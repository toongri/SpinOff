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
public class FollowedMember extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "followed_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_member_id")
    @NotNull
    private Member followingMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;


    //==연관관계 메소드==//

    //==생성 메소드==//

    public static FollowedMember createFollowedMember(Member member) {

        FollowedMember newFollowedMember = new FollowedMember();
        newFollowedMember.updateMember(member);

        return newFollowedMember;
    }

    //==수정 메소드==//
    public void updateMember(Member member) {
        this.member = member;
    }

    public void updateFollowingMember(Member followingMember) {
        this.followingMember = followingMember;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
