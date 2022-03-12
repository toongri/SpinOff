package com.nameless.spin_off.repository.member;

import com.nameless.spin_off.entity.member.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {
}
