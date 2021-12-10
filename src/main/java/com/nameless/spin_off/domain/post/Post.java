package com.nameless.spin_off.domain.post;

import com.nameless.spin_off.domain.member.Member;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id @GeneratedValue
    @Column(name="post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    private String title;

    private String content;

    private Long view;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostedHashTag> postedHashTags = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Media> medias = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "publicstatus")
    private PostPublicStatus publicStatus;

    //==연관관계 메소드==//

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.updatePost(this);
    }

    public void addPostLike(PostLike postLike) {
        this.postLikes.add(postLike);
        postLike.updatePost(this);
    }

    public void addPostedHashTag(PostedHashTag postedHashTag) {
        this.postedHashTags.add(postedHashTag);
        postedHashTag.updatePost(this);
    }

    public void addMedia(Media media) {
        this.medias.add(media);
        media.updatePost(this);
    }

    //==생성 로직==//
    public static Post createPost(Member member, String title, String content
            , List<PostedHashTag> postedHashTags, List<Media> medias
            , PostPublicStatus postPublicStatus) {
        Post post = new Post();
        post.updateMember(member);
        post.updateTitle(title);
        post.updateContent(content);
        post.updatePostedHashTag(postedHashTags);
        post.updateMedia(medias);
        post.updateCreateAt(LocalDateTime.now());
        post.updateModifiedAt(LocalDateTime.now());
        post.updateView(0);
        post.updatePublicStatus(postPublicStatus)

        return post;
    }
}


