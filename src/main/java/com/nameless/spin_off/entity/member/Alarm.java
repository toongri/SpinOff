package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="alarm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "about_member_id")
    @NotNull
    private Member aboutMember;

    @NotNull
    private String url;

    private String content;

    private Boolean isCheck;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static Alarm createAlarm(Member member, Member aboutMember, String url, String content) {

        Alarm alarm = new Alarm();
        alarm.updateAboutMember(aboutMember);
        alarm.updateMember(member);
        alarm.updateUrl(url);
        alarm.updateContent(content);
        alarm.updateIsCheck(false);

        return alarm;

    }

    //==수정 메소드==//

    private void updateIsCheck(boolean bool) {
        this.isCheck = bool;
    }

    private void updateContent(String content) {
        this.content = content;
    }

    private void updateUrl(String url) {
        this.url = url;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    private void updateAboutMember(Member aboutMember) {
        this.aboutMember = aboutMember;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
