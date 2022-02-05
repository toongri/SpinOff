package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.collections.ViewedCollectionByIp;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.NoSuchCollectionException;
import com.nameless.spin_off.exception.member.NoSuchMemberException;
import com.nameless.spin_off.exception.post.NoSuchPostException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.collections.ViewedCollectionByIpRepository;
import com.nameless.spin_off.repository.collections.VisitedCollectionByMemberRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.post.ViewedPostByIpRepository;
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
    public Long saveCollectionByCollectionVO(CreateCollectionVO collectionVO) throws NoSuchMemberException {

        Member member = getMemberById(collectionVO.getMemberId());
        Collection collection = Collection.createCollection(member, collectionVO.getTitle(), collectionVO.getContent(), collectionVO.getPublicOfCollectionStatus());

        return collectionRepository.save(collection).getId();
    }

    @Transactional(readOnly = false)
    @Override
    public Collection updateLikedCollectionByMemberId(Long memberId, Long collectionId) throws NoSuchMemberException, NoSuchCollectionException {
        Member member = getMemberById(memberId);
        Collection collection = getCollectionById(collectionId);
        collection.addLikedCollectionByMember(member);

        return collection;
    }

    @Transactional(readOnly = false)
    @Override
    public Collection updateViewedCollectionByIp(String ip, Long collectionId, LocalDateTime startTime, LocalDateTime endTime) throws NoSuchCollectionException {

        Collection collection = getCollectionById(collectionId);

        Optional<ViewedCollectionByIp> optionalCollection = viewedCollectionByIpRepository
                .findOneByCreatedDateBetweenAndIpAndCollection(startTime, endTime, ip, collection);

        if (optionalCollection.isEmpty()) {
            collection.addViewedCollectionByIp(ip);
        }
        return collection;
    }

    @Transactional(readOnly = false)
    @Override
    public Collection updateFollowedCollectionByMemberId(Long memberId, Long collectionId) throws NoSuchMemberException, NoSuchCollectionException {

        Member member = getMemberById(memberId);
        Collection collection = getCollectionById(collectionId);
        collection.addFollowedCollectionByMember(member);

        return collection;
    }

    private Member getMemberById(Long memberId) throws NoSuchMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(NoSuchMemberException::new);
    }

    private Post getPostById(Long postId) throws NoSuchPostException {
        Optional<Post> optionalPost = postRepository.findById(postId);

        return optionalPost.orElseThrow(NoSuchPostException::new);
    }

    private Collection getCollectionById(Long collectionId) throws NoSuchCollectionException {
        Optional<Collection> optionalCollection = collectionRepository.findById(collectionId);

        return optionalCollection.orElseThrow(NoSuchCollectionException::new);
    }
}
