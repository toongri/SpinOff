package com.nameless.spin_off.service.member;

import com.nameless.spin_off.dto.MemberDto;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.exception.member.*;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.hashtag.NotExistHashtagException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class JpaMemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired MovieRepository movieRepository;
    @Autowired EntityManager em;

    @Test
    public void 멤버_회원가입() throws Exception{
        //given
        String accountId = "aa";
        String accountPw = "aa";
        String name = "dd";
        String nickname = "ddd";
        LocalDate birth = LocalDate.now();
        String email = "cc";
        String profileImg = null;

        MemberDto.CreateMemberVO createMemberVO = new MemberDto
                .CreateMemberVO(accountId, accountPw, name, nickname,
                birth, email, profileImg);

        //when
        Long aLong = memberService.insertMemberByMemberVO(createMemberVO);

        //then
        Member member = memberRepository.getById(aLong);

        assertThat(member.getAccountId()).isEqualTo(accountId);
        assertThat(member.getAccountPw()).isEqualTo(accountPw);
        assertThat(member.getName()).isEqualTo(name);
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getBirth()).isEqualTo(birth);
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getProfileImg()).isEqualTo(profileImg);
    }

    @Test
    public void 멤버_회원가입_예외처리() throws Exception{
        //given
        String accountId = "aa";
        String accountPw = "aa";
        String name = "dd";
        String nickname = "ddd";
        LocalDate birth = LocalDate.now();
        String email = "cc";
        String profileImg = null;

        MemberDto.CreateMemberVO createMemberVO = new MemberDto
                .CreateMemberVO(accountId, accountPw, name, nickname,
                birth, email, profileImg);

        //when
        Long aLong = memberService.insertMemberByMemberVO(createMemberVO);

        //then
        assertThatThrownBy(() -> memberService.insertMemberByMemberVO(createMemberVO))
                .isInstanceOf(AlreadyAccountIdException.class);

        createMemberVO.setNickname("");
        assertThatThrownBy(() -> memberService.insertMemberByMemberVO(createMemberVO))
                .isInstanceOf(AlreadyAccountIdException.class);

        createMemberVO.setNickname(nickname);
        createMemberVO.setAccountId("");
        assertThatThrownBy(() -> memberService.insertMemberByMemberVO(createMemberVO))
                .isInstanceOf(AlreadyNicknameException.class);
    }

    @Test
    public void 멤버_팔로우_해시태그() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();
        Hashtag hashtag = Hashtag.createHashtag("팔로우_해시태그");
        Long hashtagId = hashtagRepository.save(hashtag).getId();

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        Long aLong = memberService.insertFollowedHashtagByHashtagId(memberId, hashtagId);

        System.out.println("멤버함수");
        Member newMember = memberRepository.getById(aLong);
        //then
        assertThat(newMember.getId()).isEqualTo(memberId);
        assertThat(newMember.getFollowedHashtags().size()).isEqualTo(1);
        assertThat(newMember.getFollowedHashtags().get(0).getHashtag().getId()).isEqualTo(hashtagId);
    }

    @Test
    public void 멤버_팔로우_해시태그_예외처리() throws Exception{

        //given

        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();
        Hashtag hashtag = Hashtag.createHashtag("팔로우_해시태그");
        Long hashtagId = hashtagRepository.save(hashtag).getId();

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        Long aLong = memberService.insertFollowedHashtagByHashtagId(memberId, hashtagId);

        System.out.println("멤버함수");
        Member newMember = memberRepository.getById(aLong);

        //then
        assertThatThrownBy(() -> memberService.insertFollowedHashtagByHashtagId(memberId, hashtagId))
                .isInstanceOf(AlreadyFollowedHashtagException.class);
        assertThatThrownBy(() -> memberService.insertFollowedHashtagByHashtagId(0L, hashtagId))
                .isInstanceOf(NotExistMemberException.class);
        assertThatThrownBy(() -> memberService.insertFollowedHashtagByHashtagId(memberId, 0L))
                .isInstanceOf(NotExistHashtagException.class);
    }

    @Test
    public void 멤버_팔로우_멤버() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();
        Member followedMember = Member.buildMember().build();
        Long followedMemberId = memberRepository.save(followedMember).getId();

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        Long aLong = memberService.insertFollowedMemberByMemberId(memberId, followedMemberId);

        System.out.println("멤버함수");
        Member newMember = memberRepository.getById(aLong);
        //then
        assertThat(newMember.getId()).isEqualTo(memberId);
        assertThat(newMember.getFollowedMembers().size()).isEqualTo(1);
        assertThat(newMember.getFollowedMembers().get(0).getMember().getId()).isEqualTo(followedMemberId);
    }

    @Test
    public void 멤버_팔로우_멤버_예외처리() throws Exception{
        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();
        Member followedMember = Member.buildMember().build();
        Long followedMemberId = memberRepository.save(followedMember).getId();

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        Long aLong = memberService.insertFollowedMemberByMemberId(memberId, followedMemberId);

        System.out.println("멤버함수");
        Member newMember = memberRepository.getById(aLong);

        //then
        assertThatThrownBy(() -> memberService.insertFollowedMemberByMemberId(memberId, followedMemberId))
                .isInstanceOf(AlreadyFollowedMemberException.class);
        assertThatThrownBy(() -> memberService.insertFollowedMemberByMemberId(0L, followedMemberId))
                .isInstanceOf(NotExistMemberException.class);
        assertThatThrownBy(() -> memberService.insertFollowedMemberByMemberId(memberId, 0L))
                .isInstanceOf(NotExistMemberException.class);
    }

    @Test
    public void 멤버_팔로우_영화() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();
        Movie movie = Movie.createMovie(0L, "abc", "d");
        Long movieId = movieRepository.save(movie).getId();

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        Long aLong = memberService.insertFollowedMovieByMovieId(memberId, movieId);

        System.out.println("멤버함수");
        Member newMember = memberRepository.getById(aLong);

        //then
        assertThat(newMember.getId()).isEqualTo(memberId);
        assertThat(newMember.getFollowedMovies().size()).isEqualTo(1);
        assertThat(newMember.getFollowedMovies().get(0).getMovie().getId()).isEqualTo(movieId);
    }

    @Test
    public void 멤버_팔로우_영화_예외처리() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();
        Movie movie = Movie.createMovie(0L, "abc", "d");
        Long movieId = movieRepository.save(movie).getId();

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        Long aLong = memberService.insertFollowedMovieByMovieId(memberId, movieId);

        System.out.println("멤버함수");
        Member newMember = memberRepository.getById(aLong);

        //then
        assertThatThrownBy(() -> memberService.insertFollowedMovieByMovieId(memberId, movieId))
                .isInstanceOf(AlreadyFollowedMovieException.class);
        assertThatThrownBy(() -> memberService.insertFollowedMovieByMovieId(0L, movieId))
                .isInstanceOf(NotExistMemberException.class);
        assertThatThrownBy(() -> memberService.insertFollowedMovieByMovieId(memberId, -1L))
                .isInstanceOf(NotExistMovieException.class);
    }

}