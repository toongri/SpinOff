package com.nameless.spin_off.entity.post;

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
public class LikedComment extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="liked_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    @NotNull
    private Comment comment;



    //==연관관계 메소드==//

    //==생성 메소드==//

    //==수정 메소드==//
    public void updateComment(Comment comment) {
        this.comment = comment;
    }
    //==비즈니스 로직==//

    //==조회 로직==//

}
