package com.nameless.spin_off.entity.comment;

import com.nameless.spin_off.entity.comment.CommentInCollection;
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
public class LikedCommentInCollection extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="liked_comment_in_collection_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_in_collection_id")
    @NotNull
    private CommentInCollection commentInCollection;


    //==연관관계 메소드==//

    //==생성 메소드==//
    public static LikedCommentInCollection createLikedCommentInCollection(Member member) {
        LikedCommentInCollection likedCommentInCollection = new LikedCommentInCollection();
        likedCommentInCollection.updateMember(member);

        return likedCommentInCollection;
    }

    //==수정 메소드==//
    public void updateCommentInCollection(CommentInCollection commentInCollection) {
        this.commentInCollection = commentInCollection;
    }
    public void updateMember(Member member) {
        this.member = member;
    }
    //==비즈니스 로직==//

    //==조회 로직==//

}
