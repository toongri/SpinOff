package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMemberId;
import com.nameless.spin_off.dto.CommentDto.CreateCommentInCollectionVO;
import com.nameless.spin_off.dto.CommentDto.CreateCommentInPostVO;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInCollectionException;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInPostException;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.service.comment.CommentInCollectionService;
import com.nameless.spin_off.service.comment.CommentInPostService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/comment")
public class CommentApiController {

    private final CommentInPostService commentInPostService;
    private final CommentInCollectionService commentInCollectionService;

    @PostMapping("/post")
    public CommentApiResult<Long> createOneInPost(
            @RequestBody CreateCommentInPostVO commentVO, @LoginMemberId Long memberId)
            throws NotExistMemberException, NotExistPostException, NotExistCommentInPostException {

        log.info("createOneInPost");
        log.info("memberId : {}", memberId);
        log.info("postId : {}", commentVO.getPostId());
        log.info("parentId : {}", commentVO.getParentId());
        log.info("content : {}", commentVO.getContent());

        return getResult(commentInPostService.insertCommentInPostByCommentVO(commentVO, memberId));
    }

    @PostMapping("/collection")
    public CommentApiResult<Long> createOneInCollection(
            @RequestBody CreateCommentInCollectionVO commentVO, @LoginMemberId Long memberId)
            throws NotExistMemberException, NotExistCollectionException, NotExistCommentInCollectionException {

        log.info("createOneInCollection");
        log.info("memberId : {}", memberId);
        log.info("collectionId : {}", commentVO.getCollectionId());
        log.info("parentId : {}", commentVO.getParentId());
        log.info("content : {}", commentVO.getContent());

        Long commentId = commentInCollectionService.insertCommentInCollectionByCommentVO(commentVO, memberId);
        return getResult(commentId);
    }

    @PostMapping("/post/{commentId}/like")
    public CommentApiResult<Long> createLikeOneInPost(
            @LoginMemberId Long memberId, @PathVariable Long commentId)
            throws NotExistMemberException, NotExistCommentInPostException, AlreadyLikedCommentInPostException {

        log.info("createLikeOneInPost");
        log.info("memberId : {}", memberId);
        log.info("commentId : {}", commentId);

        return getResult(commentInPostService.insertLikedCommentByMemberId(memberId, commentId));
    }

    @PostMapping("/collection/{commentId}/like")
    public CommentApiResult<Long> createLikeOneInCollection(
            @LoginMemberId Long memberId, @PathVariable Long commentId)
            throws NotExistMemberException, AlreadyLikedCommentInCollectionException,
            NotExistCommentInCollectionException {

        log.info("createLikeOneInCollection");
        log.info("memberId : {}", memberId);
        log.info("commentId : {}", commentId);

        return getResult(commentInCollectionService.insertLikedCommentByMemberId(memberId, commentId));
    }

    @Data
    @AllArgsConstructor
    public static class CommentApiResult<T> {
        private T data;
        Boolean isSuccess;
        String code;
        String message;
    }
    public <T> CommentApiResult<T> getResult(T data) {
        return new CommentApiResult<>(data, true, "0", "성공");
    }
}
