package com.nameless.spin_off.domain.post;

import com.nameless.spin_off.domain.member.Member;
import com.nameless.spin_off.domain.post.Comment;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLike {

    @Id
    @GeneratedValue
    @Column(name="commentlike_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    @NotNull
    private Comment comment;

    public void updateComment(Comment comment) {
        this.comment = comment;
    }


    //==연관관계 메소드==//

    //==생성 메소드==//

    //==수정 메소드==//

    //==비즈니스 로직==//

    //==조회 로직==//

}
