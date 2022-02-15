package com.nameless.spin_off.service.member;

import com.nameless.spin_off.dto.MemberDto.CreateMemberVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.exception.member.*;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.hashtag.NotExistHashtagException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaMemberService implements MemberService {

    private final MemberRepository memberRepository;
    private final CollectionRepository collectionRepository;
    private final HashtagRepository hashtagRepository;
    private final MovieRepository movieRepository;

    @Transactional(readOnly = false)
    @Override
    public Long insertMemberByMemberVO(CreateMemberVO memberVO) throws AlreadyAccountIdException, AlreadyNicknameException {

        List<Member> memberList = memberRepository
                .findAllByAccountIdOrNickname(memberVO.getAccountId(), memberVO.getNickname());

        if (memberList.isEmpty()) {
            Member member = memberRepository.save(Member.createMemberByCreateVO(memberVO));
            collectionRepository.save(Collection.createDefaultCollection(member));
            return member.getId();
        } else {
            if (memberList.stream().noneMatch(member -> member.getAccountId().equals(memberVO.getAccountId()))) {
                throw new AlreadyNicknameException();
            } else {
                throw new AlreadyAccountIdException();
            }
        }
    }

    @Override
    public Long insertFollowedMemberByMemberId(Long memberId, Long followedMemberId)
            throws NotExistMemberException, AlreadyFollowedMemberException {

        Member member = getMemberByIdIncludeFollowedMember(memberId);
        Member followedMember = getMember(followedMemberId);

        member.insertFollowedMemberByMember(followedMember);

        return member.getId();
    }

    @Override
    public Long insertFollowedHashtagByHashtagId(Long memberId, Long hashtagId) throws
            NotExistMemberException, NotExistHashtagException, AlreadyFollowedHashtagException {

        Member member = getMemberByIdIncludeHashtag(memberId);
        Hashtag hashtag = getHashtag(hashtagId);

        member.insertFollowedHashtagByHashtag(hashtag);

        return member.getId();
    }

    @Override
    public Long insertFollowedMovieByMovieId(Long memberId, Long movieId) throws
            NotExistMemberException, NotExistMovieException,
            AlreadyFollowedMovieException {

        Member member = getMemberByIdIncludeMovie(memberId);
        Movie movie = getMovie(movieId);

        member.insertFollowedMovieByMovie(movie);

        return member.getId();
    }

    private Member getMember(Long followedMemberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findById(followedMemberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Hashtag getHashtag(Long hashtagId) throws NotExistHashtagException {
        Optional<Hashtag> optionalHashtag = hashtagRepository.findById(hashtagId);

        return optionalHashtag.orElseThrow(NotExistHashtagException::new);
    }

    private Movie getMovie(Long movieId) throws NotExistMovieException {
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);

        return optionalMovie.orElseThrow(NotExistMovieException::new);
    }

    private Member getMemberByIdIncludeFollowedMember(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedMember(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Member getMemberByIdIncludeMovie(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithMovie(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Member getMemberByIdIncludeHashtag(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithHashtag(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }
}
