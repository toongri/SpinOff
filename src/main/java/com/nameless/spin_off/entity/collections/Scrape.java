package com.nameless.spin_off.entity.collections;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrape extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="scrape_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(name = "scrape_public_status")
    private ScrapePublicStatus scrapePublicStatus;

    //==연관관계 메소드==//

    //==생성 메소드==//

    public static Scrape createScrape(Member member, Post post, ScrapePublicStatus scrapePublicStatus) {

        Scrape scrape = new Scrape();
        scrape.updateMember(member);
        scrape.updatePost(post);
        scrape.updateScrapePublicStatus(scrapePublicStatus);
        return scrape;

    }

    //==수정 메소드==//
    private void updateScrapePublicStatus(ScrapePublicStatus scrapePublicStatus) {
        this.scrapePublicStatus = scrapePublicStatus;
    }

    private void updatePost(Post post) {
        this.post = post;
    }

    private void updateMember(Member member) {
        this.member = member;
    }
    //==비즈니스 로직==//

    //==조회 로직==//
}
