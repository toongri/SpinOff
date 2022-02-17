package com.nameless.spin_off.entity.post;

import com.nameless.spin_off.entity.hashtag.PostedHashtag;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorityOfPost extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="authority_of_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority_of_post_status")
    private AuthorityOfPostStatus authorityOfPostStatus;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static AuthorityOfPost createAuthorityOfPost(AuthorityOfPostStatus authorityOfPostStatus) {

        AuthorityOfPost authorityOfPost = new AuthorityOfPost();
        authorityOfPost.updateAuthorityOfPostStatus(authorityOfPostStatus);

        return authorityOfPost;

    }
    //==수정 메소드==//
    public void updateAuthorityOfPostStatus(AuthorityOfPostStatus authorityOfPostStatus) {
        this.authorityOfPostStatus = authorityOfPostStatus;
    }

    public void updatePost(Post post) {
        this.post = post;
    }

    //==비즈니스 로직==//

    //==조회 로직==//
    @Override
    public int hashCode() {
        return Objects.hash(post, authorityOfPostStatus);
    }
    @Override
    public boolean equals(Object authorityOfPost) {
        if (authorityOfPost instanceof AuthorityOfPost) {
            if ((((AuthorityOfPost) authorityOfPost).getPost().equals(post))) {
                return ((AuthorityOfPost) authorityOfPost).getAuthorityOfPostStatus().equals(authorityOfPostStatus);
            }
        }

        return false;
    }
}
