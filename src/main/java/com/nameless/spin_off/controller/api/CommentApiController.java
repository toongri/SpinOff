package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.dto.CommentDto.CreateCommentInPostVO;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.comment.CommentInPost;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/comment")
public class CommentApiController {

    private final CommentInPostService commentInPostService;
    private final CommentInCollectionService commentInCollectionService;

    @PostMapping("/post")
    public CommentApiResult<Long> createOneInPost(@RequestBody CreateCommentInPostVO commentVO)
            throws NotExistMemberException, NotExistPostException, NotExistCommentInPostException {
        Long commentInPostId = commentInPostService.insertCommentInPostByCommentVO(commentVO);
        return new CommentApiResult<Long>(commentInPostId);
    }

    @PostMapping("/collection")
    public CommentApiResult<Long> createOneInCollection(
            @RequestBody CommentDto.CreateCommentInCollectionVO commentVO)
            throws NotExistMemberException, NotExistCollectionException, NotExistCommentInCollectionException {

        Long commentId = commentInCollectionService.insertCommentInCollectionByCommentVO(commentVO);
        return new CommentApiResult<Long>(commentId);
    }

    @PostMapping("/post/like")
    public CommentApiResult<Long> createLikeOneInPost(
            @RequestParam Long memberId, @RequestParam Long commentId)
            throws NotExistMemberException, NotExistCommentInPostException, AlreadyLikedCommentInPostException {

        Long likedCommentId = commentInPostService.insertLikedCommentByMemberId(memberId, commentId);
        return new CommentApiResult<Long>(likedCommentId);
    }

    @PostMapping("/collection/like")
    public CommentApiResult<Long> createLikeOneInCollection(
            @RequestParam Long memberId, @RequestParam Long commentId)
            throws NotExistMemberException, AlreadyLikedCommentInCollectionException, NotExistCommentInCollectionException {

        Long likedCommentId = commentInCollectionService.insertLikedCommentByMemberId(memberId, commentId);
        return new CommentApiResult<Long>(likedCommentId);
    }

    @Data
    @AllArgsConstructor
    public static class CommentApiResult<T> {
        private T data;
    }
}
