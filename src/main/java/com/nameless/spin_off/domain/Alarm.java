package com.nameless.spin_off.domain;

import com.nameless.spin_off.domain.member.Member;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.el.parser.AstFalse;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm {

    @Id
    @GeneratedValue
    @Column(name="alarm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @NotNull
    private String url;

    private String content;

    private Boolean isCheck;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static Alarm createAlarm(Member member, String url, String content) {

        Alarm alarm = new Alarm();
        alarm.updateMember(member);
        alarm.updateUrl(url);
        alarm.updateContent(content);
        alarm.updateIsCheck(false);
        alarm.updateCreateAtNow();

        return alarm;

    }

    //==수정 메소드==//
    private void updateCreateAtNow() {
        this.createdAt = LocalDateTime.now();
    }

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

    //==비즈니스 로직==//

    //==조회 로직==//

}
