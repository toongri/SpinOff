package com.nameless.spin_off.entity.postcollection;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowingPostCollection extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="following_post_collection_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    @NotNull
    private PostCollection postCollection;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static FollowingPostCollection createFollowingPostCollection(Member member, PostCollection postCollection) {

        FollowingPostCollection followingPostCollection = new FollowingPostCollection();
        followingPostCollection.updateMember(member);
        followingPostCollection.updatePostCollection(postCollection);

        return followingPostCollection;

    }

    //==수정 메소드==//
    private void updatePostCollection(PostCollection postCollection) {
        this.postCollection = postCollection;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
