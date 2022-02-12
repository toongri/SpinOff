package com.nameless.spin_off.service.post;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.post.*;

import java.time.LocalDateTime;

public interface PostService {

    Long insertPostByPostVO(PostDto.CreatePostVO postVO)
            throws NotExistMemberException, NotExistMovieException, NotExistCollectionException, InCorrectHashtagContentException;

    Post insertLikedPostByMemberId(Long memberId, Long postId)
            throws NotExistMemberException, NotExistPostException,
            OverSearchLikedPostException, AlreadyLikedPostException;

    Post insertViewedPostByIp(String ip, Long postId, LocalDateTime timeNow, Long minuteDuration)
            throws NotExistPostException, OverSearchViewedPostByIpException;
}
