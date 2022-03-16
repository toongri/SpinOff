package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.entity.enums.member.AuthorityOfMemberStatus;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorityOfMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="authority_of_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority_of_member_status")
    private AuthorityOfMemberStatus authorityOfMemberStatus;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static AuthorityOfMember createAuthorityOfMember(Member member, AuthorityOfMemberStatus authorityOfMemberStatus) {

        AuthorityOfMember authorityOfMember = new AuthorityOfMember();
        authorityOfMember.updateMember(member);
        authorityOfMember.updateAuthorityOfMemberStatus(authorityOfMemberStatus);

        return authorityOfMember;

    }

    //==수정 메소드==//
    private void updateAuthorityOfMemberStatus(AuthorityOfMemberStatus authorityOfMemberStatus) {
        this.authorityOfMemberStatus = authorityOfMemberStatus;
    }

    private void updateMember(Member member) {
        this.member = member;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
