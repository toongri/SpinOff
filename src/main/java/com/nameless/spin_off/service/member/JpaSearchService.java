package com.nameless.spin_off.service.member;

import com.nameless.spin_off.dto.SearchDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchDto;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.member.SearchedByMember;
import com.nameless.spin_off.entity.member.SearchedByMemberStatus;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.member.SearchRepository;
import com.nameless.spin_off.repository.query.SearchQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaSearchService implements SearchService{

    private final MemberRepository memberRepository;
    private final SearchQueryRepository searchQueryRepository;

    @Transactional()
    @Override
    public Long insertSearch(Long memberId, String content, SearchedByMemberStatus searchedByMemberStatus) throws NotExistMemberException {

        Member member = getMemberWithSearch(memberId);

        return member.addSearch(content, searchedByMemberStatus);
    }

    @Override
    public List<SearchedByMember> getLastSearchesByMember(Long memberId) throws NotExistMemberException {
        Member member = getMemberWithSearchOrderBySearches(memberId);

        return member.getLastSearches();
    }

    @Override
    public RelatedSearchDto getRelatedSearchByKeyword(String keyword) {

        return new RelatedSearchDto(
                searchQueryRepository.getPostsAboutKeyword(keyword),
                searchQueryRepository.getMoviesAboutKeyword(keyword),
                searchQueryRepository.getHashtagsAboutKeyword(keyword),
                searchQueryRepository.getMembersAboutKeyword(keyword),
                searchQueryRepository.getCollectionsAboutKeyword(keyword));
    }

    private Member getMemberWithSearch(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithSearch(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Member getMemberWithSearchOrderBySearches(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithSearchOrderBySearches(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }
}
