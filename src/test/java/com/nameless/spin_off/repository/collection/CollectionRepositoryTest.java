package com.nameless.spin_off.repository.collection;

import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class CollectionRepositoryTest {

    @Autowired CollectionRepository collectionRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    public void findAllByIdInIncludePostOrderByPostId() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);

        List<Collection> collectionList = new ArrayList<>();


        for (int i = 0; i < 5; i++) {
            collectionList.add(Collection.createDefaultCollection(mem));
        }
        List<Collection> collections = collectionRepository.saveAll(collectionList);
        List<Long> collectIds = collections.stream().map(Collection::getId).collect(Collectors.toList());

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        List<Collection> collects = collectionRepository.findAllByIdInIncludePost(collectIds, mem.getId());

        //then
        assertThat(collects.size()).isEqualTo(collectIds.size());

    }
}
