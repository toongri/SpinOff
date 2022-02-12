package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.dto.CommentDto.CreateCommentInPostVO;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.service.comment.CommentInCollectionService;
import com.nameless.spin_off.service.comment.CommentInPostService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/comment")
public class CommentApiController {

    private final CommentInPostService commentInPostService;
    private final CommentInCollectionService commentInCollectionService;

    @PostMapping("/post")
    public CommentApiResult<CommentInPost> createCommentInPost(@RequestBody CreateCommentInPostVO commentVO)
            throws NotExistMemberException, NotExistPostException, NotExistCommentInPostException {
        CommentInPost commentInPost = commentInPostService.insertCommentInPostByCommentVO(commentVO);
        return new CommentApiResult<CommentInPost>(commentInPost);
    }

    @PostMapping("/collection")
    public CommentApiResult<CommentInCollection> createCommentInCollection(
            @RequestBody CommentDto.CreateCommentInCollectionVO commentVO)
            throws NotExistMemberException, NotExistCollectionException, NotExistCommentInCollectionException {

        CommentInCollection comment = commentInCollectionService.insertCommentInCollectionByCommentVO(commentVO);
        return new CommentApiResult<CommentInCollection>(comment);
    }

    @Data
    @AllArgsConstructor
    public static class CommentApiResult<T> {
        private T data;
    }
}
