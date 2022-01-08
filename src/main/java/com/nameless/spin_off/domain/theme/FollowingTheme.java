package com.nameless.spin_off.domain.theme;

import com.nameless.spin_off.domain.BaseTimeEntity;
import com.nameless.spin_off.domain.member.Member;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowingTheme extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="followingtheme_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    @NotNull
    private Theme theme;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static FollowingTheme createFollowingTheme(Member member, Theme theme) {

        FollowingTheme followingTheme = new FollowingTheme();
        followingTheme.updateMember(member);
        followingTheme.updateTheme(theme);

        return followingTheme;

    }

    //==수정 메소드==//
    private void updateTheme(Theme theme) {
        this.theme = theme;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
