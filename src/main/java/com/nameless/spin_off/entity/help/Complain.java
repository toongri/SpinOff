package com.nameless.spin_off.entity.help;

import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.FollowedMovie;
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
public class Complain extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="faq_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complained_member_id")
    private Member complainedMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    private Collection collection;

    @Enumerated(EnumType.STRING)
    @Column(name = "complain_status")
    @NotNull
    private ComplainStatus complainStatus;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static Complain createComplain(Member member, Member complainedMember, Post post, Collection collection, ComplainStatus complainStatus) {

        Complain complain = new Complain();
        complain.updateMember(member);
        complain.updateComplainedMember(complainedMember);
        complain.updatePost(post);
        complain.updateCollection(collection);
        complain.updateComplainStatus(complainStatus);

        return complain;
    }

    //==수정 메소드==//
    public void updateMember(Member member) {
        this.member = member;
    }

    public void updateComplainedMember(Member complainedMember) {
        this.complainedMember = complainedMember;
    }

    public void updatePost(Post post) {
        this.post = post;
    }

    public void updateCollection(Collection collection) {
        this.collection = collection;
    }

    public void updateComplainStatus(ComplainStatus complainStatus) {
        this.complainStatus = complainStatus;
    }

    //==비즈니스 로직==//

    //==조회 로직==//


    @Override
    public int hashCode() {
        return Objects.hash(member, post, collection);
    }

    @Override
    public boolean equals(Object complain) {
        if (complain instanceof Complain) {
            if ((((Complain) complain).getMember().equals(member))) {
                if (((Complain) complain).getPost() == post) {
                    return ((Complain) complain).getCollection() == collection;
                }
            }
        }

        return false;
    }
}
