package com.nameless.spin_off.entity.member;

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
public class FollowedMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public static FollowedMember createFollowedMember(Member followingMember, Member member) {

        FollowedMember newFollowedMember = new FollowedMember();
        newFollowedMember.updateFollowingMember(followingMember);
        newFollowedMember.updateMember(member);

        return newFollowedMember;
    }

    public static FollowedMember createFollowedMember(Long id) {

        FollowedMember newFollowedMember = new FollowedMember();
        newFollowedMember.updateId(id);

        return newFollowedMember;
    }

    //==수정 메소드==//
    public void updateId(Long id) {
        this.id = id;
    }
    public void updateMember(Member member) {
        this.member = member;
    }

    public void updateFollowingMember(Member followingMember) {
        this.followingMember = followingMember;
    }

    //==비즈니스 로직==//

    //==조회 로직==//
    @Override
    public int hashCode() {
        return Objects.hash(member, followingMember);
    }

    @Override
    public boolean equals(Object followedMember) {
        if (followedMember instanceof FollowedMember) {
            if (((FollowedMember) followedMember).getMember().equals(member)) {
                return ((FollowedMember) followedMember).getFollowingMember().equals(followingMember);
            }
        }

        return false;
    }
}
