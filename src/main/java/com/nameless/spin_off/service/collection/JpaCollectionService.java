package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaCollectionService implements CollectionService {

    private final MemberRepository memberRepository;
    private final CollectionRepository collectionRepository;
    private final PostRepository postRepository;


    @Transactional(readOnly = false)
    @Override
    public Long insertCollectionByCollectionVO(CreateCollectionVO collectionVO) throws NotExistMemberException {

        Member member = getMemberById(collectionVO.getMemberId());
        Collection collection = Collection.createCollection(member, collectionVO.getTitle(), collectionVO.getContent(), collectionVO.getPublicOfCollectionStatus());

        return collectionRepository.save(collection).getId();
    }

    @Transactional(readOnly = false)
    @Override
    public Collection insertLikedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException,
            OverSearchLikedCollectionException, AlreadyLikedCollectionException {

        Member member = getMemberById(memberId);
        Collection collection = getCollectionByIdWithLikedCollection(collectionId);

        collection.insertLikedCollectionByMember(member);

        return collection;
    }

    @Transactional(readOnly = false)
    @Override
    public Collection insertViewedCollectionByIp(String ip, Long collectionId, LocalDateTime timeNow, Long minuteDuration)
            throws NotExistCollectionException, OverSearchViewedCollectionByIpException {

        Collection collection = getCollectionByIdWithViewedIp(collectionId);

        collection.insertViewedCollectionByIp(ip, timeNow, minuteDuration);

        return collection;
    }

    @Transactional(readOnly = false)
    @Override
    public Collection insertFollowedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException,
            OverSearchFollowedCollectionException, AlreadyFollowedCollectionException {

        Member member = getMemberById(memberId);
        Collection collection = getCollectionByIdWithFollowedCollection(collectionId);

        collection.insertFollowedCollectionByMember(member);

        return collection;
    }

    @Transactional(readOnly = false)
    @Override
    public List<Collection> insertCollectedPosts(Long memberId, Long postId, List<Long> collectionIds)
            throws NotExistMemberException, NotExistCollectionException,
            NotExistPostException, OverSearchCollectedPostException, AlreadyCollectedPostException {

        Post post = getPost(postId);
        List<Collection> collections = getCollectionsWithPost(memberId, collectionIds);

        for (Collection collection : collections) {
            collection.insertCollectedPostByPost(post);
        }

        return collections;
    }

    private List<Collection> getCollectionsWithPost(Long memberId, List<Long> collectionIds)
            throws NotExistCollectionException {
        List<Collection> collections = collectionRepository.findAllByIdInAndMemberIdIncludePost(collectionIds, memberId);

        if (collections.size() != collectionIds.size()) {
            throw new NotExistCollectionException();
        }
        return collections;
    }

    private Post getPost(Long postId) throws NotExistPostException {
        Optional<Post> optionalPost = postRepository.findById(postId);
        Post post = optionalPost.orElseThrow(NotExistPostException::new);
        return post;
    }

    private Member getMemberById(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Collection getCollectionByIdWithViewedIp(Long collectionId) throws NotExistCollectionException {
        Optional<Collection> optionalCollection =
                collectionRepository.findOneByIdIncludeViewedCollectionByIpOrderByViewedIpId(collectionId);

        return optionalCollection.orElseThrow(NotExistCollectionException::new);
    }

    private Collection getCollectionByIdWithLikedCollection(Long collectionId) throws NotExistCollectionException {
        Optional<Collection> optionalCollection =
                collectionRepository.findOneByIdIncludeLikedCollection(collectionId);

        return optionalCollection.orElseThrow(NotExistCollectionException::new);
    }

    private Collection getCollectionByIdWithFollowedCollection(Long collectionId) throws NotExistCollectionException {
        Optional<Collection> optionalCollection =
                collectionRepository.findOneByIdIncludeFollowedCollection(collectionId);

        return optionalCollection.orElseThrow(NotExistCollectionException::new);
    }
}
