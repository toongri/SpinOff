package com.nameless.spin_off.repository.member;

import com.nameless.spin_off.entity.member.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

}
