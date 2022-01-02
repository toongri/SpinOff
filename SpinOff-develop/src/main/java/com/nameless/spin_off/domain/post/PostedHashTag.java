package com.nameless.spin_off.domain.post;

import com.nameless.spin_off.domain.post.Post;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostedHashTag {

    @Id
    @GeneratedValue
    @Column(name="postedhashtag_id")
    private Long id;

    private String tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static PostedHashTag createPostedHashTag(String tag, Post post) {

        PostedHashTag postedHashTag = new PostedHashTag();
        postedHashTag.updateTag(tag);
        postedHashTag.updatePost(post);

        return postedHashTag;

    }

    //==수정 메소드==//
    private void updateTag(String tag) {
        this.tag = tag;
    }

    public void updatePost(Post post) {
        this.post = post;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
