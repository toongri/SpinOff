package com.nameless.spin_off.service.post;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.exception.collection.AlreadyCollectedPostException;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.collection.NotMatchCollectionException;
import com.nameless.spin_off.exception.hashtag.InCorrectHashtagContentException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.post.*;

import java.util.List;

public interface PostService {

    Long insertPostByPostVO(PostDto.CreatePostVO postVO)
            throws NotExistMemberException, NotExistMovieException, NotExistCollectionException,
            InCorrectHashtagContentException, AlreadyPostedHashtagException,
            AlreadyCollectedPostException, AlreadyPAuthorityOfPostStatusException;
    Long insertLikedPostByMemberId(Long memberId, Long postId)
            throws NotExistMemberException, NotExistPostException, AlreadyLikedPostException;
    Long insertViewedPostByIp(String ip, Long postId)
            throws NotExistPostException;
    List<Long> insertCollectedPosts(Long memberId, Long postId, List<Long> collectionIds)
            throws NotExistMemberException, NotMatchCollectionException,
            NotExistPostException, AlreadyCollectedPostException;
}
