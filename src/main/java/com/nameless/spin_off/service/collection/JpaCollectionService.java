package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaCollectionService implements CollectionService {

    private final MemberRepository memberRepository;
    private final CollectionRepository collectionRepository;


    @Transactional()
    @Override
    public Long insertCollectionByCollectionVO(CreateCollectionVO collectionVO)
            throws NotExistMemberException, OverTitleOfCollectionException, OverContentOfCollectionException {

        Member member = getMemberById(collectionVO.getMemberId());
        Collection collection = Collection.createCollection(
                member, collectionVO.getTitle(), collectionVO.getContent(), collectionVO.getPublicOfCollectionStatus());

        return collectionRepository.save(collection).getId();
    }

    @Transactional()
    @Override
    public Long insertLikedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException, AlreadyLikedCollectionException {

        Member member = getMemberById(memberId);
        Collection collection = getCollectionByIdWithLikedCollection(collectionId);

        return collection.insertLikedCollectionByMember(member);
    }

    @Transactional()
    @Override
    public Long insertViewedCollectionByIp(String ip, Long collectionId)
            throws NotExistCollectionException {

        Collection collection = getCollectionByIdWithViewedIp(collectionId);

        return collection.insertViewedCollectionByIp(ip);
    }

    @Transactional()
    @Override
    public Long insertFollowedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException, AlreadyFollowedCollectionException, CantFollowOwnCollectionException {

        Member member = getMemberById(memberId);
        Collection collection = getCollectionByIdWithFollowedCollection(collectionId);

        return collection.insertFollowedCollectionByMember(member);
    }

    private Member getMemberById(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Collection getCollectionByIdWithViewedIp(Long collectionId) throws NotExistCollectionException {
        Optional<Collection> optionalCollection =
                collectionRepository.findOneByIdWithViewedByIp(collectionId);

        return optionalCollection.orElseThrow(NotExistCollectionException::new);
    }

    private Collection getCollectionByIdWithLikedCollection(Long collectionId) throws NotExistCollectionException {
        Optional<Collection> optionalCollection =
                collectionRepository.findOneByIdWithLikedCollection(collectionId);

        return optionalCollection.orElseThrow(NotExistCollectionException::new);
    }

    private Collection getCollectionByIdWithFollowedCollection(Long collectionId) throws NotExistCollectionException {
        Optional<Collection> optionalCollection =
                collectionRepository.findOneByIdWithFollowedCollection(collectionId);

        return optionalCollection.orElseThrow(NotExistCollectionException::new);
    }
}
