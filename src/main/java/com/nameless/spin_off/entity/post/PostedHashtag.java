package com.nameless.spin_off.entity.post;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostedHashtag extends BaseTimeEntity {

    @Id
    @GeneratedValue
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
    public static PostedHashtag createPostedHashtag(Hashtag hashtag) {

        PostedHashtag postedHashTag = new PostedHashtag();
        postedHashTag.updateHashTag(hashtag);

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
//    public static List<PostedHashtag> createPostedHashtags(List<Hashtag> hashtags) {
//        return hashtags.stream().map(hashtag -> PostedHashtag.createPostedHashtag(hashtag, null)).collect(Collectors.toList());
//    }
    //==조회 로직==//

}
