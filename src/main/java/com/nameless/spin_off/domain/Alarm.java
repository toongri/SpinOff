package com.nameless.spin_off.domain;

import com.nameless.spin_off.domain.member.Member;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    //==수정 메소드==//

    //==비즈니스 로직==//

    //==조회 로직==//

}
