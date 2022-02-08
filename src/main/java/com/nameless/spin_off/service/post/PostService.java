package com.nameless.spin_off.service.post;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.NotSearchCollectionException;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.exception.movie.NotSearchMovieException;
import com.nameless.spin_off.exception.post.AlreadyLikedPostException;
import com.nameless.spin_off.exception.post.NotSearchPostException;
import com.nameless.spin_off.exception.post.OverSearchLikedPostException;
import com.nameless.spin_off.exception.post.OverSearchViewedPostByIpException;

import java.time.LocalDateTime;
import java.util.List;

public interface PostService {

    Long insertPostByPostVO(PostDto.CreatePostVO postVO)
            throws NotSearchMemberException, NotSearchMovieException, NotSearchCollectionException;

    Post insertLikedPostByMemberId(Long memberId, Long postId)
            throws NotSearchMemberException, NotSearchPostException,
            OverSearchLikedPostException, AlreadyLikedPostException;

    Post insertViewedPostByIp(String ip, Long postId, LocalDateTime timeNow, Long minuteDuration)
            throws NotSearchPostException, OverSearchViewedPostByIpException;
}
