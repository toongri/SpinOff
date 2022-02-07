package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.AlreadyLikedCollectionException;
import com.nameless.spin_off.exception.collection.NotSearchCollectionException;
import com.nameless.spin_off.exception.collection.OverSearchFollowedCollectionException;
import com.nameless.spin_off.exception.collection.OverSearchViewedCollectionByIpException;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.exception.post.NotSearchPostException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.collections.ViewedCollectionByIpRepository;
import com.nameless.spin_off.repository.collections.VisitedCollectionByMemberRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaCollectionService implements CollectionService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CollectionRepository collectionRepository;
    private final ViewedCollectionByIpRepository viewedCollectionByIpRepository;
    private final VisitedCollectionByMemberRepository visitedCollectionByMemberRepository;


    @Transactional(readOnly = false)
    @Override
    public Long saveCollectionByCollectionVO(CreateCollectionVO collectionVO) throws NotSearchMemberException {

        Member member = getMemberById(collectionVO.getMemberId());
        Collection collection = Collection.createCollection(member, collectionVO.getTitle(), collectionVO.getContent(), collectionVO.getPublicOfCollectionStatus());

        return collectionRepository.save(collection).getId();
    }

    @Transactional(readOnly = false)
    @Override
    public Collection updateLikedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotSearchMemberException, NotSearchCollectionException,
            OverSearchViewedCollectionByIpException, AlreadyLikedCollectionException {

        Member member = getMemberById(memberId);
        Collection collection = getCollectionByIdWithLikedCollection(collectionId);

        if (collection.isNotMemberAlreadyLikeCollection(member)) {
            collection.addLikedCollectionByMember(member);
        } else {
            throw new AlreadyLikedCollectionException();
        }

        return collection;
    }

    @Transactional(readOnly = false)
    @Override
    public Collection updateViewedCollectionByIp(String ip, Long collectionId, LocalDateTime timeNow, Long minuteDuration)
            throws NotSearchCollectionException, OverSearchViewedCollectionByIpException {

        Collection collection = getCollectionByIdWithViewedIp(collectionId);

        if (collection.isNotIpAlreadyView(ip, timeNow, minuteDuration)) {
            collection.addViewedCollectionByIp(ip);
        }

        return collection;
    }

    @Transactional(readOnly = false)
    @Override
    public Collection updateFollowedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotSearchMemberException, NotSearchCollectionException,
            OverSearchFollowedCollectionException, AlreadyLikedCollectionException {

        Member member = getMemberById(memberId);
        Collection collection = getCollectionByIdWithFollowedCollection(collectionId);

        if (collection.isNotMemberAlreadyFollowCollection(member)) {
            collection.addFollowedCollectionByMember(member);
        } else {
            throw new AlreadyLikedCollectionException();
        }

        return collection;
    }

    private Member getMemberById(Long memberId) throws NotSearchMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(NotSearchMemberException::new);
    }

    private Collection getCollectionByIdWithViewedIp(Long collectionId) throws NotSearchCollectionException {
        Optional<Collection> optionalCollection =
                collectionRepository.findOneByIdIncludeViewedCollectionByIpOrderByViewedIpId(collectionId);

        return optionalCollection.orElseThrow(NotSearchCollectionException::new);
    }

    private Collection getCollectionByIdWithLikedCollection(Long collectionId) throws NotSearchCollectionException {
        Optional<Collection> optionalCollection =
                collectionRepository.findOneByIdIncludeLikedCollection(collectionId);

        return optionalCollection.orElseThrow(NotSearchCollectionException::new);
    }

    private Collection getCollectionByIdWithFollowedCollection(Long collectionId) throws NotSearchCollectionException {
        Optional<Collection> optionalCollection =
                collectionRepository.findOneByIdIncludeFollowedCollection(collectionId);

        return optionalCollection.orElseThrow(NotSearchCollectionException::new);
    }
}
