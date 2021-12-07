package com.nameless.spin_off.domain.member;

import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPreference {

    @Id
    @GeneratedValue
    @Column(name="memberpreference_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    private int score;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MemberPreferenceStatus status;
}
