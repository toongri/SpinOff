package com.nameless.spin_off.entity.hashtag;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.post.Post;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostedHashtag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="posted_hashtag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    @NotNull
    private Hashtag hashtag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static PostedHashtag createPostedHashtag(Hashtag hashtag, Post post) {

        PostedHashtag postedHashTag = new PostedHashtag();
        postedHashTag.updateHashTag(hashtag);
        postedHashTag.updatePost(post);

        return postedHashTag;

    }

    //==수정 메소드==//
    public void updateHashTag(Hashtag hashtag) {
        this.hashtag = hashtag;
    }

    public void updatePost(Post post) {
        this.post = post;
    }

    //==비즈니스 로직==//

    //==조회 로직==//
    @Override
    public int hashCode() {
        return Objects.hash(getPost(), getHashtag());
    }

    @Override
    public boolean equals(Object postedHashtag) {
        if (postedHashtag instanceof PostedHashtag) {
            if ((((PostedHashtag) postedHashtag).getPost().equals(post))) {
                return ((PostedHashtag) postedHashtag).getHashtag().equals(hashtag);
            }
        }

        return false;
    }
}
