package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

    private LocalDateTime birth;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String email;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static Member createMember(String accountId, String accountPw,
                                      String name, LocalDateTime birth, String phoneNumber, String email) {

        Member member = new Member();
        member.updateAccountId(accountId);
        member.updateAccountPw(accountPw);
        member.updateName(name);
        member.updateBirth(birth);
        member.updatePhoneNumber(phoneNumber);
        member.updateEmail(email);

        return member;

    }
    //==수정 메소드==//
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

    private void updateBirth(LocalDateTime birth) {
        this.birth = birth;
    }

    private void updateName(String name) {
        this.name = name;
    }

    //==비즈니스 로직==//

    //==조회 로직==//


}
