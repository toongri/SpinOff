package com.nameless.spin_off.service.post;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.exception.collection.AlreadyCollectedPostException;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.collection.NotMatchCollectionException;
import com.nameless.spin_off.exception.hashtag.IncorrectHashtagContentException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.post.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {

    Long insertPostByPostVO(PostDto.CreatePostVO postVO, Long memberId, List<MultipartFile> multipartFiles)
            throws NotExistMemberException, NotExistMovieException, NotExistCollectionException,
            IncorrectHashtagContentException, AlreadyPostedHashtagException,
            AlreadyCollectedPostException,
            IncorrectTitleOfPostException, IncorrectContentOfPostException, NotMatchCollectionException, IOException;
    Long insertLikedPostByMemberId(Long memberId, Long postId)
            throws NotExistMemberException, NotExistPostException, AlreadyLikedPostException;
    Long insertViewedPostByIp(String ip, Long postId)
            throws NotExistPostException;
    List<Long> updateCollectedPosts(Long memberId, Long postId, List<Long> collectionIds)
            throws NotExistMemberException, NotMatchCollectionException,
            NotExistPostException, AlreadyCollectedPostException;
    Long insertCollectedPost(Long memberId, Long postId, Long collectionId);

    Long deletePost(MemberDetails currentMember, Long postId);
    int updateAllPopularity();
}