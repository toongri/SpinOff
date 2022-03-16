package com.nameless.spin_off.entity.hashtag;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.movie.ViewedMovieByIp;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ViewedHashtagByIp extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="viewed_hashtag_by_ip_id")
    private Long id;

    private String ip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    @NotNull
    private Hashtag hashtag;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static ViewedHashtagByIp createViewedHashtagByIp(String ip, Hashtag hashtag) {
        ViewedHashtagByIp viewedHashtagByIp = new ViewedHashtagByIp();
        viewedHashtagByIp.updateIp(ip);
        viewedHashtagByIp.updateHashtag(hashtag);

        return viewedHashtagByIp;
    }

    //==수정 메소드==//
    public void updateHashtag(Hashtag hashtag) {
        this.hashtag = hashtag;
    }

    private void updateIp(String ip) {
        this.ip = ip;
    }

    //==비즈니스 로직==//

    //==조회 로직==//
}
