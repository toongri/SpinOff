package com.nameless.spin_off.domain;

import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostAuthority {

    @Id
    @GeneratedValue
    @Column(name="postauthority_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority_status")
    private PostAuthorityStatus authorityStatus;
}
