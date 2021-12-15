package com.nameless.spin_off.domain.theme;

import com.nameless.spin_off.domain.member.Member;
import com.nameless.spin_off.domain.post.Post;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Theme {

    @Id @GeneratedValue
    @Column(name="theme_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    private String title;

    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "themepublic_status")
    private ThemePublicStatus themePublicStatus;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static Theme createTheme(Member member, Post post, String title,
                                    String content, ThemePublicStatus themePublicStatus) {

        Theme theme = new Theme();
        theme.updateMember(member);
        theme.updatePost(post);
        theme.updateTitle(title);
        theme.updateContent(content);
        theme.updateThemePublicStatus(themePublicStatus);
        theme.updateCreateAtNow();
        theme.updateModifiedAtNow();

        return theme;

    }

    //==수정 메소드==//
    private void updateModifiedAtNow() {
        this.modifiedAt = LocalDateTime.now();
    }

    private void updateCreateAtNow() {
        this.createdAt = LocalDateTime.now();
    }

    private void updateThemePublicStatus(ThemePublicStatus themePublicStatus) {
        this.themePublicStatus = themePublicStatus;
    }

    private void updateContent(String content) {
        this.content = content;
    }

    private void updateTitle(String title) {
        this.title = title;
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
