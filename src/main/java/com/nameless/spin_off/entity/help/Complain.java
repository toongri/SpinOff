package com.nameless.spin_off.entity.help;

import com.nameless.spin_off.entity.enums.help.ComplainStatus;
import com.nameless.spin_off.entity.enums.help.ContentTypeStatus;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Complain extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="faq_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complained_member_id")
    private Member complainedMember;

    private Long contentId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ContentTypeStatus contentTypeStatus;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ComplainStatus complainStatus;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static Complain createComplain(Member member, Member complainedMember, Long contentId,
                                          ContentTypeStatus contentTypeStatus, ComplainStatus complainStatus) {

        Complain complain = new Complain();
        complain.updateMember(member);
        complain.updateComplainedMember(complainedMember);
        complain.updateContentId(contentId);
        complain.updateContentTypeStatus(contentTypeStatus);
        complain.updateComplainStatus(complainStatus);

        return complain;
    }

    //==수정 메소드==//
    public void updateMember(Member member) {
        this.member = member;
    }

    public void updateComplainedMember(Member complainedMember) {
        this.complainedMember = complainedMember;
    }

    public void updateContentId(Long contentId) {
        this.contentId = contentId;
    }

    public void updateContentTypeStatus(ContentTypeStatus contentTypeStatus) {
        this.contentTypeStatus = contentTypeStatus;
    }

    public void updateComplainStatus(ComplainStatus complainStatus) {
        this.complainStatus = complainStatus;
    }

    //==비즈니스 로직==//

    //==조회 로직==//


    @Override
    public int hashCode() {
        return Objects.hash(member, contentId, contentTypeStatus);
    }

    @Override
    public boolean equals(Object complain) {
        if (complain instanceof Complain) {
            if ((((Complain) complain).getMember().equals(member))) {
                if (((Complain) complain).getContentTypeStatus().equals(contentTypeStatus)) {
                    if (contentTypeStatus == ContentTypeStatus.F) {
                        return ((Complain) complain).getComplainedMember().equals(complainedMember);
                    } else {
                        return ((Complain) complain).getContentId().equals(contentId);
                    }

                }
            }
        }

        return false;
    }
}
