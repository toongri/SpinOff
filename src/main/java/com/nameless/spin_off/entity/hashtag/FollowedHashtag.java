package com.nameless.spin_off.entity.hashtag;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.BlockedMember;
import com.nameless.spin_off.entity.member.Member;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowedHashtag extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "followed_hashtag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    @NotNull
    private Hashtag hashtag;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static FollowedHashtag createFollowedHashtag(Hashtag hashtag) {

        FollowedHashtag followedHashtag = new FollowedHashtag();
        followedHashtag.updateHashtag(hashtag);

        return followedHashtag;
    }

    //==수정 메소드==//
    public void updateMember(Member member) {
        this.member = member;
    }

    public void updateHashtag(Hashtag hashtag) {
        this.hashtag = hashtag;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

    @Override
    public int hashCode() {
        return Objects.hash(member, hashtag);
    }

    @Override
    public boolean equals(Object followedHashtag) {
        if (followedHashtag instanceof FollowedHashtag) {
            if ((((FollowedHashtag) followedHashtag).getMember().equals(member))) {
                return ((FollowedHashtag) followedHashtag).getHashtag().equals(hashtag);
            }
        }

        return false;
    }
}
