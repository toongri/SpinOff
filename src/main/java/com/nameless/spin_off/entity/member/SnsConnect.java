package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.entity.enums.member.SnsConnectStatus;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SnsConnect extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sns_connect_id")
    private Long id;

    private String snsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "sns_connect_status")
    private SnsConnectStatus snsConnectStatus;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static SnsConnect createSnsConnect(String snsId, Member member, SnsConnectStatus snsConnectStatus) {

        SnsConnect snsConnect = new SnsConnect();
        snsConnect.updateSnsId(snsId);
        snsConnect.updateMember(member);
        snsConnect.updateSnsConnectStatus(snsConnectStatus);

        return snsConnect;

    }

    //==수정 메소드==//
    private void updateSnsConnectStatus(SnsConnectStatus snsConnectStatus) {
        this.snsConnectStatus = snsConnectStatus;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    private void updateSnsId(String snsId) {
        this.snsId = snsId;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
