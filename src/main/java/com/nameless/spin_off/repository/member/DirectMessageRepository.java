package com.nameless.spin_off.repository.member;

import com.nameless.spin_off.entity.member.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

    @Query("SELECT DISTINCT directMessage FROM DirectMessage directMessage " +
            "JOIN FETCH directMessage.member m " +
            "LEFT JOIN FETCH m.complains complain " +
            "WHERE directMessage.id = :id")
    Optional<DirectMessage> findOneByIdWithComplainOfMember(@Param("id") Long id);
}
