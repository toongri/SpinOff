package com.nameless.spin_off.service.member;

import com.nameless.spin_off.dto.MemberDto.CreateMemberVO;
import com.nameless.spin_off.exception.member.*;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.post.NotExistHashtagException;

public interface MemberService {

    Long insertMemberByMemberVO(CreateMemberVO memberVO) throws AlreadyAccountIdException, AlreadyNicknameException;

    Long insertFollowedMemberByMemberId(Long memberId, Long followedMemberId)
            throws NotExistMemberException, OverSearchFollowedMemberException, AlreadyFollowedMemberException;

    Long insertFollowedHashtagByHashtagId(Long memberId, Long hashtagId) throws NotExistMemberException, NotExistHashtagException, OverSearchFollowedHashtagException, AlreadyFollowedHashtagException;

    Long insertFollowedMovieByMovieId(Long memberId, Long movieId) throws NotExistMemberException, NotExistMovieException, AlreadyFollowedMovieException, OverSearchFollowedMovieException;
}
