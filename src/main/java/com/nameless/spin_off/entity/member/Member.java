package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.dto.MemberDto;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    private String accountId;
    private String accountPw;
    private String name;
    private String nickname;
    private LocalDate birth;
    private String phoneNumber;
    private String email;
    private String profileImg;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static Member createMember(String accountId, String accountPw, String nickname, String profileImg,
                                      String name, LocalDate birth, String phoneNumber, String email) {

        Member member = new Member();
        member.updateAccountId(accountId);
        member.updateAccountPw(accountPw);
        member.updateName(name);
        member.updateBirth(birth);
        member.updatePhoneNumber(phoneNumber);
        member.updateEmail(email);
        member.updateNickname(nickname);
        member.updateProfileImg(profileImg);

        return member;

    }

    public static MemberDto.MemberBuilder buildMember() {
        return new MemberDto.MemberBuilder();
    }

    //==수정 메소드==//

    private void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    private void updateProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    private void updateAccountId(String accountId) {
        this.accountId = accountId;
    }

    private void updateAccountPw(String accountPw) {
        this.accountPw = accountPw;
    }

    private void updateEmail(String email) {
        this.email = email;
    }

    private void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private void updateBirth(LocalDate birth) {
        this.birth = birth;
    }

    private void updateName(String name) {
        this.name = name;
    }

    //==비즈니스 로직==//

    //==조회 로직==//


}
