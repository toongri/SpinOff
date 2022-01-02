package com.nameless.spin_off.domain.post;

import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostAuthority {

    @Id
    @GeneratedValue
    @Column(name="postauthority_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(name = "postauthority_status")
    private PostAuthorityStatus postAuthorityStatus;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static PostAuthority createPostAuthority(Post post, PostAuthorityStatus postAuthorityStatus) {

        PostAuthority postAuthority = new PostAuthority();
        postAuthority.updatePost(post);
        postAuthority.updatePostAuthorityStatus(postAuthorityStatus);

        return postAuthority;

    }
    //==수정 메소드==//
    private void updatePostAuthorityStatus(PostAuthorityStatus postAuthorityStatus) {
        this.postAuthorityStatus = postAuthorityStatus;
    }

    private void updatePost(Post post) {
        this.post = post;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
