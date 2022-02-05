package com.nameless.spin_off.service.post;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.member.NoSuchMemberException;
import com.nameless.spin_off.exception.movie.NoSuchMovieException;
import com.nameless.spin_off.exception.post.NoSuchPostException;
import com.nameless.spin_off.exception.post.OverSuchViewedPostByIpException;

import java.time.LocalDateTime;

public interface PostService {
    Long savePostByPostVO(PostDto.CreatePostVO postVO) throws NoSuchMemberException, NoSuchMovieException;
    Post updateLikedPostByMemberId(Long memberId, Long postId) throws NoSuchMemberException, NoSuchPostException;
    Post updateViewedPostByIp(String ip, Long postId, LocalDateTime timeNow, Long minuteDuration)
            throws NoSuchPostException, OverSuchViewedPostByIpException;
}
