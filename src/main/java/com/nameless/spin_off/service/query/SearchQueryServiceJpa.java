package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchAllDto;
import com.nameless.spin_off.dto.SearchDto.SearchAllDto;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.member.BlockedMember;
import com.nameless.spin_off.entity.member.FollowedMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.search.OverLengthRelatedKeywordException;
import com.nameless.spin_off.exception.search.UnderLengthRelatedKeywordException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.enums.search.RelatedSearchEnum.RELATED_SEARCH_KEYWORD_MAX_STR;
import static com.nameless.spin_off.entity.enums.search.RelatedSearchEnum.RELATED_SEARCH_KEYWORD_MIN_STR;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchQueryServiceJpa implements SearchQueryService {
    private final SearchQueryRepositoryQuerydsl searchQueryRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final CollectionQueryRepository collectionQueryRepository;
    private final MovieQueryRepository movieQueryRepository;
    private final PostQueryRepository postQueryRepository;
    private final MemberRepository memberRepository;

    @Override
    public RelatedSearchAllDto getRelatedSearchAllByKeyword(String keyword, int length)
            throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException {

        if (keyword.length() < RELATED_SEARCH_KEYWORD_MIN_STR.getValue()) {
            throw new UnderLengthRelatedKeywordException();
        } else if (keyword.length() > RELATED_SEARCH_KEYWORD_MAX_STR.getValue()) {
            throw new OverLengthRelatedKeywordException();
        } else {
            return getRelatedSearchDtoByKeyword(keyword, length);
        }
    }

    @Override
    public List<RelatedSearchHashtagDto> getRelatedSearchHashtagByKeyword(String keyword, int length)
            throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException {
        return searchQueryRepository.findRelatedHashtagsAboutKeyword(keyword, length);
    }

    @Override
    public List<RelatedSearchMemberDto> getRelatedSearchMemberByKeyword(String keyword, int length)
            throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException {
        return searchQueryRepository.findRelatedMembersAboutKeyword(keyword, length);
    }

    @Override
    public List<MostPopularHashtag> getMostPopularHashtagLimit(int length) {
        return searchQueryRepository.findMostPopularHashtagsLimit(length);
    }

    @Override
    public List<LastSearchDto> getLastSearchesByMemberLimit(Long memberId, int length) {
        return searchQueryRepository.findLastSearchesByMemberIdLimit(memberId, length);
    }

    @Override
    public SearchAllDto getSearchPageDataAtAll(
            String keyword, Long memberId, Pageable postPageable, Pageable collectionPageable,
            Pageable memberPageable, Pageable moviePageable) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedMemberAndBlockedMember(memberId);

        List<Member> followedMembers = getFollowedMemberByMember(member);
        List<Member> blockedMembers = getBlockedMemberByMember(member);

        return new SearchAllDto(
                postQueryRepository.findAllSlicedForSearchPageAtAll(keyword, postPageable, blockedMembers),
                collectionQueryRepository.findAllSlicedForSearchPageAtAll(
                        keyword, collectionPageable, followedMembers, blockedMembers),
                movieQueryRepository.findAllSlicedForSearchPageAtAll(keyword, moviePageable),
                memberQueryRepository.findAllSlicedForSearchPageAtAll(keyword, memberPageable, blockedMembers));
    }

    private RelatedSearchAllDto getRelatedSearchDtoByKeyword(String keyword, int length) {
        return new RelatedSearchAllDto(
                searchQueryRepository.findRelatedPostsAboutKeyword(keyword, length),
                searchQueryRepository.findRelatedMoviesAboutKeyword(keyword, length),
                searchQueryRepository.findRelatedHashtagsAboutKeyword(keyword, length),
                searchQueryRepository.findRelatedMembersAboutKeyword(keyword, length),
                searchQueryRepository.findRelatedCollectionsAboutKeyword(keyword, length));
    }

    private Member getMemberByIdWithFollowedMemberAndBlockedMember(Long memberId) throws NotExistMemberException {
        if (memberId == null) {
            return null;
        }

        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedMemberAndBlockedMember(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private List<Member> getFollowedMemberByMember(Member member) {
        if (member != null) {
            return member.getFollowedMembers().stream()
                    .map(FollowedMember::getMember).collect(Collectors.toList());
        } else{
            return new ArrayList<>();
        }
    }

    private List<Member> getBlockedMemberByMember(Member member) {
        if (member != null) {
            return member.getBlockedMembers().stream()
                    .filter(blockedMember -> blockedMember.getBlockedMemberStatus().equals(BlockedMemberStatus.A))
                    .map(BlockedMember::getMember).collect(Collectors.toList());
        } else{
            return new ArrayList<>();
        }
    }
}
