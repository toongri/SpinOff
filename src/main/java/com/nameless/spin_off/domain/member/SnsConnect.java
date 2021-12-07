package com.nameless.spin_off.domain.member;

import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SnsConnect {

    @Id
    @GeneratedValue
    @Column(name="snsconnect_id")
    private Long id;

    @Column(name="sns_id")
    private String snsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "snsconnect_status")
    private SnsConnectStatus status;
}
