package com.nameless.spin_off.entity.post;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Media extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="media_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    @NotNull
    private String url;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static Media createMedia(Post post, String url) {

        Media media = new Media();
        media.updatePost(post);
        media.updateUrl(url);

        return media;

    }

    //==수정 메소드==//
    public void updatePost(Post post) {
        this.post = post;
    }

    private void updateUrl(String url) {
        this.url = url;
    }

    //==비즈니스 로직==//
    public static List<Media> createMedias(List<String> urls) {
        return urls.stream().map(url -> Media.createMedia(null, url)).collect(Collectors.toList());
    }

    //==조회 로직==//

}
