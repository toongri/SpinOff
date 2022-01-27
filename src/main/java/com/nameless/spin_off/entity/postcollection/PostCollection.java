package com.nameless.spin_off.entity.postcollection;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCollection extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name="post_collection_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_collection_public_status")
    private PostCollectionPublicStatus postCollectionPublicStatus;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static PostCollection createTheme(Member member, Post post, String title,
                                             String content, PostCollectionPublicStatus postCollectionPublicStatus) {

        PostCollection postCollection = new PostCollection();
        postCollection.updateMember(member);
        postCollection.updatePost(post);
        postCollection.updateTitle(title);
        postCollection.updateContent(content);
        postCollection.updateThemePublicStatus(postCollectionPublicStatus);

        return postCollection;

    }

    //==수정 메소드==//

    private void updateThemePublicStatus(PostCollectionPublicStatus postCollectionPublicStatus) {
        this.postCollectionPublicStatus = postCollectionPublicStatus;
    }

    private void updateContent(String content) {
        this.content = content;
    }

    private void updateTitle(String title) {
        this.title = title;
    }

    private void updatePost(Post post) {
        this.post = post;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
