package com.nameless.spin_off.domain.member;

import com.nameless.spin_off.domain.post.Media;
import com.nameless.spin_off.domain.post.Post;
import com.nameless.spin_off.domain.post.PostPublicStatus;
import com.nameless.spin_off.domain.post.PostedHashTag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private LocalDateTime birth;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String email;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static Member createMember(String name, LocalDateTime birth, String phoneNumber, String email) {

        Member member = new Member();
        member.updateName(name);
        member.updateCreatedAtNow();
        member.updateBirth(birth);
        member.updatePhoneNumber(phoneNumber);
        member.updateEmail(email);

        return member;

    }
    //==수정 메소드==//
    private void updateEmail(String email) {
        this.email = email;
    }

    private void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private void updateBirth(LocalDateTime birth) {
        this.birth = LocalDateTime.now();
    }

    private void updateCreatedAtNow() {
        this.createdAt = LocalDateTime.now();
    }

    private void updateName(String name) {
        this.name = name;
    }

    //==비즈니스 로직==//

    //==조회 로직==//


}
