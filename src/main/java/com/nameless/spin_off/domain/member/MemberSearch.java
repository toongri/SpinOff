package com.nameless.spin_off.domain.member;

import com.nameless.spin_off.domain.member.Member;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSearch {

    @Id
    @GeneratedValue
    @Column(name="membersearch_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static MemberSearch createMemberSearch(Member member, String content) {

        MemberSearch memberSearch = new MemberSearch();
        memberSearch.updateMember(member);
        memberSearch.updateContent(content);
        memberSearch.updateCreatedAtNow();

        return memberSearch;

    }

    //==수정 메소드==//
    private void updateCreatedAtNow() {
        this.createdAt = LocalDateTime.now();
    }

    private void updateContent(String content) {
        this.content = content;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
