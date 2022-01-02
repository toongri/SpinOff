package com.nameless.spin_off.domain.member;

import com.nameless.spin_off.domain.post.Media;
import com.nameless.spin_off.domain.post.Post;
import com.nameless.spin_off.domain.post.PostPublicStatus;
import com.nameless.spin_off.domain.post.PostedHashTag;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowingMember {

    @Id
    @GeneratedValue
    @Column(name = "followingmember_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followedmember_id")
    @NotNull
    private Member followedMember;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static FollowingMember createFollowingMember(Member member, Member followedMember) {

        FollowingMember followingMember = new FollowingMember();
        followingMember.updateMember(member);
        followingMember.updateFollowedMember(followedMember);

        return followingMember;

    }

    //==수정 메소드==//
    private void updateMember(Member member) {
        this.member = member;
    }

    private void updateFollowedMember(Member followedMember) {
        this.followedMember = followedMember;
    }


    //==비즈니스 로직==//

    //==조회 로직==//

}
