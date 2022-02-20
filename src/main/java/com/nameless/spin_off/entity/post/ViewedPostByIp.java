package com.nameless.spin_off.entity.post;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ViewedPostByIp extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="viewed_post_by_ip_id")
    private Long id;

    private String ip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static ViewedPostByIp createViewedPostByIp(String ip, Post post) {
        ViewedPostByIp viewedPostByIp = new ViewedPostByIp();
        viewedPostByIp.updateIp(ip);
        viewedPostByIp.updatePost(post);

        return viewedPostByIp;

    }

    //==수정 메소드==//
    public void updatePost(Post post) {
        this.post = post;
    }

    private void updateIp(String ip) {
        this.ip = ip;
    }

    //==비즈니스 로직==//

    //==조회 로직==//
}
