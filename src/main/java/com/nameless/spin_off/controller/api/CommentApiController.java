package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.CommentDto.CreateCommentInPostVO;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.exception.comment.NotSearchCommentInPostException;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.exception.post.NotSearchPostException;
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

    @PostMapping("/post")
    public PostApiResult createLikeOne(@RequestBody CreateCommentInPostVO commentVO) throws NotSearchMemberException, NotSearchPostException, NotSearchCommentInPostException {
        CommentInPost commentInPost = commentInPostService.saveCommentInPostByCommentVO(commentVO);
        return new PostApiResult(commentInPost);
    }

    @Data
    @AllArgsConstructor
    public static class PostApiResult<T> {
        private T data;
    }
}
