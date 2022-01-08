package com.nameless.spin_off.domain.member;

import com.nameless.spin_off.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Faq extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="faq_id")
    private Long id;

    private String title;

    private String content;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static Faq createFaq(String title, String content) {

        Faq faq = new Faq();
        faq.updateTitle(title);
        faq.updateContent(content);

        return faq;

    }

    //==수정 메소드==//
    private void updateContent(String content) {
        this.content = content;
    }

    private void updateTitle(String title) {
        this.title = title;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
