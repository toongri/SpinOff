package com.nameless.spin_off.entity.post;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
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
public class Hashtag extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="hashtag_id")
    private Long id;

    private String content;

    @OneToMany(mappedBy = "hashtag", fetch = FetchType.LAZY)
    private List<PostedHashtag> postedHashtags = new ArrayList<>();

    //==연관관계 메소드==//
    public void addPostedHashtag(PostedHashtag postedHashtag) {
        this.postedHashtags.add(postedHashtag);
        postedHashtag.updateHashTag(this);
    }

    //==생성 메소드==//
    public static Hashtag createHashtag(String content) {

        Hashtag hashtag = new Hashtag();
        hashtag.updateContent(content);

        return hashtag;
    }

    //==수정 메소드==//
    private void updateContent(String content) {
        this.content = content;
    }

    public void updatePostedHashtag(List<PostedHashtag> postedHashtags) {
        for (PostedHashtag postedHashTag : postedHashtags) {
            this.addPostedHashtag(postedHashTag);
        }
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
