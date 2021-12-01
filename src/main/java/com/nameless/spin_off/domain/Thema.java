package com.nameless.spin_off.domain;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;

public class Thema {

    @Id @GeneratedValue
    @Column(name="thema_id")
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
    @Column(name = "public_status")
    private themaPublicStatus publicStatus;
}
