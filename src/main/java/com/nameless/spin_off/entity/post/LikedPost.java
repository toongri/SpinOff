package com.nameless.spin_off.entity.post;

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
public class LikedPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="liked_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static LikedPost createLikedPost(Member member, Post post) {
        LikedPost likedPost = new LikedPost();
        likedPost.updateMember(member);
        likedPost.updatePost(post);

        return likedPost;

    }

    //==수정 메소드==//
    public void updatePost(Post post) {
        this.post = post;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
