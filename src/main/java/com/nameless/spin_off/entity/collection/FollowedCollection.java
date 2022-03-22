package com.nameless.spin_off.entity.collection;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
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
public class FollowedCollection extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="followed_collection_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    @NotNull
    private Collection collection;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static FollowedCollection createFollowedCollection(Member member, Collection collection) {

        FollowedCollection followedCollection = new FollowedCollection();
        followedCollection.updateCollection(collection);
        followedCollection.updateMember(member);

        return followedCollection;
    }

    public static FollowedCollection createFollowedCollection(Long id) {

        FollowedCollection followedCollection = new FollowedCollection();
        followedCollection.updateId(id);

        return followedCollection;
    }

    //==수정 메소드==//
    public void updateId(Long id) {
        this.id = id;
    }
    public void updateCollection(Collection collection) {
        this.collection = collection;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

    @Override
    public int hashCode() {
        return Objects.hash(member, collection);
    }

    @Override
    public boolean equals(Object followedCollection) {
        if (followedCollection instanceof FollowedCollection) {
            if ((((FollowedCollection) followedCollection).getMember().equals(member))) {
                return ((FollowedCollection) followedCollection).getCollection().equals(collection);
            }
        }

        return false;
    }
}
