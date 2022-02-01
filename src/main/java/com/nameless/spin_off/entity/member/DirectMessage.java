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
public class DirectMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="direct_message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_member_id")
    @NotNull
    private Member receivedMember;

    private String content;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static DirectMessage createDirectMessage(Member member, Member receivedMember, String content) {

        DirectMessage directMessage = new DirectMessage();
        directMessage.updateMember(member);
        directMessage.updateReceivedMember(member);
        directMessage.updateContent(content);

        return directMessage;

    }

    //==수정 메소드==//

    private void updateContent(String content) {
        this.content = content;
    }

    private void updateReceivedMember(Member member) {
        this.receivedMember = member;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
