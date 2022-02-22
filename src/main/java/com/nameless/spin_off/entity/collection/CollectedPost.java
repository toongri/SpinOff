package com.nameless.spin_off.entity.collection;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.post.Post;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CollectedPost extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="collected_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    @NotNull
    private Collection collection;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static CollectedPost createCollectedPost(Collection collection, Post post) {
        CollectedPost collectedPost = new CollectedPost();
        collectedPost.updatePost(post);
        collectedPost.updateCollection(collection);
        return collectedPost;

    }

    //==수정 메소드==//
    public void updateCollection(Collection collection) {
        this.collection = collection;
    }

    public void updatePost(Post post) {
        this.post = post;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
