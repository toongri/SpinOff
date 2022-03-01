package com.nameless.spin_off.service.movie;

import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.exception.member.AlreadyFollowedMovieException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieServiceJpa implements MovieService{

    private final MovieRepository movieRepository;
    private final MemberRepository memberRepository;

    @Transactional()
    @Override
    public Long insertViewedMovieByIp(String ip, Long movieId) throws NotExistMovieException {

        Movie movie = getMovieByIdWithViewedIp(movieId);

        return movie.insertViewedMovieByIp(ip);
    }

    @Transactional()
    @Override
    public Long insertFollowedMovieByMovieId(Long memberId, Long movieId) throws
            NotExistMemberException, NotExistMovieException, AlreadyFollowedMovieException {

        Member member = getMemberByIdWithFollowedMovie(memberId);
        Movie movie = getMovieByIdWithFollowingMember(movieId);
        return member.addFollowedMovie(movie);
    }

    private Movie getMovieByIdWithFollowingMember(Long movieId) throws NotExistMovieException {
        Optional<Movie> optionalMovie = movieRepository.findOneByIdWithFollowingMember(movieId);

        return optionalMovie.orElseThrow(NotExistMovieException::new);
    }

    private Member getMemberByIdWithFollowedMovie(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedMovie(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Movie getMovieByIdWithViewedIp(Long movieId) throws NotExistMovieException {
        Optional<Movie> optionalMovie = movieRepository.findOneByIdWithViewedByIp(movieId);

        return optionalMovie.orElseThrow(NotExistMovieException::new);
    }
}
