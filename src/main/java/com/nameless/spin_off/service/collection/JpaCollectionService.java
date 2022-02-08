package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.exception.post.NotSearchPostException;
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
    public Long insertCollectionByCollectionVO(CreateCollectionVO collectionVO) throws NotSearchMemberException {

        Member member = getMemberById(collectionVO.getMemberId());
        Collection collection = Collection.createCollection(member, collectionVO.getTitle(), collectionVO.getContent(), collectionVO.getPublicOfCollectionStatus());

        return collectionRepository.save(collection).getId();
    }

    @Transactional(readOnly = false)
    @Override
    public Collection insertLikedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotSearchMemberException, NotSearchCollectionException,
            OverSearchLikedCollectionException, AlreadyLikedCollectionException {

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
    public Collection insertViewedCollectionByIp(String ip, Long collectionId, LocalDateTime timeNow, Long minuteDuration)
            throws NotSearchCollectionException, OverSearchViewedCollectionByIpException {

        Collection collection = getCollectionByIdWithViewedIp(collectionId);

        if (collection.isNotIpAlreadyView(ip, timeNow, minuteDuration)) {
            collection.addViewedCollectionByIp(ip);
        }

        return collection;
    }

    @Transactional(readOnly = false)
    @Override
    public Collection insertFollowedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotSearchMemberException, NotSearchCollectionException,
            OverSearchFollowedCollectionException, AlreadyFollowedCollectionException {

        Member member = getMemberById(memberId);
        Collection collection = getCollectionByIdWithFollowedCollection(collectionId);

        if (collection.isNotMemberAlreadyFollowCollection(member)) {
            collection.addFollowedCollectionByMember(member);
        } else {
            throw new AlreadyFollowedCollectionException();
        }

        return collection;
    }

    @Transactional(readOnly = false)
    @Override
    public List<Collection> insertCollectedPosts(Long memberId, Long postId, List<Long> collectionIds)
            throws NotSearchMemberException, NotSearchCollectionException,
            NotSearchPostException, OverSearchCollectedPostException, AlreadyCollectedPostException {

        Member member = getMemberById(memberId);

        Post post = getPost(postId);

        List<Collection> collections = getCollectionsWithPost(memberId, collectionIds);

        for (Collection collection : collections) {
            if (collection.isNotAlreadyCollectedPost(post)) {
                collection.addCollectedPostByPost(post);
            } else {
                throw new AlreadyCollectedPostException();
            }
        }

        return collections;
    }

    private List<Collection> getCollectionsWithPost(Long memberId, List<Long> collectionIds)
            throws NotSearchCollectionException {
        List<Collection> collections = collectionRepository.findAllByIdInIncludePost(collectionIds, memberId);

        if (collections.size() != collectionIds.size()) {
            throw new NotSearchCollectionException();
        }
        return collections;
    }

    private Post getPost(Long postId) throws NotSearchPostException {
        Optional<Post> optionalPost = postRepository.findById(postId);
        Post post = optionalPost.orElseThrow(NotSearchPostException::new);
        return post;
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
