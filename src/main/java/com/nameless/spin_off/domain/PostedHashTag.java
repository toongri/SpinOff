package com.nameless.spin_off.domain;

import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostedHashTag {

    @Id
    @GeneratedValue
    @Column(name="postedhashtag_id")
    private Long id;

    private String tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;
}
