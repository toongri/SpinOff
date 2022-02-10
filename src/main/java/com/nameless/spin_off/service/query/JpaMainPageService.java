package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.FollowedHashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Hashtag;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.query.MainPageQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaMainPageService implements MainPageService{

    private final MainPageQueryRepository mainPageQueryRepository;
    private final MemberRepository memberRepository;
    private final HashtagRepository hashtagRepository;

    @Override
    public Slice<MainPagePostDto> getPostsOrderById(Pageable pageable) {
        Slice<Post> postSlice = mainPageQueryRepository.findPostsOrderByIdBySliced(pageable);

        return postSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPagePostDto> getPostsOrderByPopularityBySlicingAfterLocalDateTime(
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        Slice<Post> postSlice = mainPageQueryRepository
                .findPostsOrderByPopularityAfterLocalDateTimeSliced(pageable, startDateTime, endDateTime);

        return postSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsOrderByPopularityBySlicingAfterLocalDateTime(
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        Slice<Collection> collectionSlice = mainPageQueryRepository
                .findCollectionsOrderByPopularityAfterLocalDateTimeSliced(pageable, startDateTime, endDateTime);
        return collectionSlice.map(MainPageCollectionDto::new);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowedHashtagOrderByIdSliced(Pageable pageable, Long memberId) throws NotSearchMemberException {

        Member member = getMemberByIdIncludeHashtags(memberId);

        List<Hashtag> hashtags =
                member.getFollowedHashtags().stream().map(FollowedHashtag::getHashtag).collect(Collectors.toList());

        Slice<Post> postsSlice = mainPageQueryRepository
                .findPostsByFollowedHashtagOrderByIdSliced(pageable, hashtags);

        return postsSlice.map(MainPagePostDto::new);
    }

    private Member getMemberByIdIncludeHashtags(Long memberId) throws NotSearchMemberException {
        Optional<Member> optionalMember = memberRepository.findMemberByIdIncludeHashtag(memberId);

        return optionalMember.orElseThrow(NotSearchMemberException::new);
    }

}
