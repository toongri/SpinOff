package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CommentDto.CreateCommentInPostVO;
import com.nameless.spin_off.dto.ResultDto.SingleApiResult;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInPostException;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.service.comment.CommentInCollectionService;
import com.nameless.spin_off.service.comment.CommentInPostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.nameless.spin_off.dto.ResultDto.SingleApiResult.getResult;

@Slf4j
@Api(tags = {"댓글 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/comment")
public class CommentApiController {

    private final CommentInPostService commentInPostService;
    private final CommentInCollectionService commentInCollectionService;

    @ApiOperation(value = "글 댓글 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "commentVO",
                    value = "댓글 정보",
                    required = true,
                    paramType = "body",
                    dataType = "CreateCommentInPostVO")
    })
    @PostMapping("/post")
    public SingleApiResult<Long> createOneInPost(
            @LoginMember MemberDetails currentMember, @RequestBody CreateCommentInPostVO commentVO)
            throws NotExistMemberException, NotExistPostException, NotExistCommentInPostException {

        log.info("createOneInPost");
        log.info("memberId : {}", currentMember.getId());
        log.info("postId : {}", commentVO.getPostId());
        log.info("parentId : {}", commentVO.getParentId());
        log.info("content : {}", commentVO.getContent());

        return getResult(commentInPostService.insertCommentInPostByCommentVO(commentVO, currentMember.getId()));
    }

    @ApiOperation(value = "글 댓글 좋아요 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "commentId",
                    value = "댓글 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @PostMapping("/post/{commentId}/like")
    public SingleApiResult<Long> createLikeOneInPost(
            @LoginMember MemberDetails currentMember, @PathVariable Long commentId)
            throws NotExistMemberException, NotExistCommentInPostException, AlreadyLikedCommentInPostException {

        log.info("createLikeOneInPost");
        log.info("memberId : {}", currentMember.getId());
        log.info("commentId : {}", commentId);

        return getResult(commentInPostService.insertLikedCommentByMemberId(currentMember.getId(), commentId));
    }
}
