package com.nameless.spin_off.domain.post;

import com.nameless.spin_off.domain.BaseTimeEntity;
import com.nameless.spin_off.domain.member.Member;
import com.nameless.spin_off.domain.post.Post;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="postlike_id")
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
    public static PostLike createPostLike(Member member, Post post) {
        PostLike postLike = new PostLike();
        postLike.updatePost(post);
        postLike.updateMember(member);

        return postLike;

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
