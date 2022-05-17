package com.nameless.spin_off.entity.help;

import com.nameless.spin_off.enums.help.InquirePublicStatus;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquire extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="inquire_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "inquire_public_status")
    @NotNull
    private InquirePublicStatus inquirePublicStatus;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static Inquire createInquire(Member member, String title, String content,
                                        InquirePublicStatus inquirePublicStatus) {

        Inquire inquire = new Inquire();
        inquire.updateMember(member);
        inquire.updateTitle(title);
        inquire.updateContent(content);
        inquire.updateInquirePublicStatus(inquirePublicStatus);

        return inquire;

    }

    private void updateInquirePublicStatus(InquirePublicStatus inquirePublicStatus) {
        this.inquirePublicStatus = inquirePublicStatus;
    }

    private void updateContent(String content) {
        this.content = content;
    }

    private void updateTitle(String title) {
        this.title = title;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    //==수정 메소드==//

    //==비즈니스 로직==//

    //==조회 로직==//

}
