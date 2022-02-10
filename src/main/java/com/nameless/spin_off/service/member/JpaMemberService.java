package com.nameless.spin_off.service.member;

import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaMemberService {

    private final MemberRepository memberRepository;
    private final CollectionRepository collectionRepository;

    @Transactional
    public void insertMember() {
        Member save = memberRepository.save(Member.buildMember().build());
        collectionRepository.save(Collection.createDefaultCollection(save));
    }
}
