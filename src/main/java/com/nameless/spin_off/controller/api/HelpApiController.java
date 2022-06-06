package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.ResultDto.SingleApiResult;
import com.nameless.spin_off.enums.help.ComplainStatus;
import com.nameless.spin_off.enums.help.ContentTypeStatus;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.help.UnknownContentTypeException;
import com.nameless.spin_off.exception.member.AlreadyComplainException;
import com.nameless.spin_off.exception.member.NotExistDMException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.service.help.ComplainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.nameless.spin_off.dto.ResultDto.SingleApiResult.getResult;

@Slf4j
@Api(tags = {"유저편의 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/help")
public class HelpApiController {

    private final ComplainService complainService;

    @ApiOperation(value = "신고 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "contentId",
                    value = "컨텐츠 id",
                    required = true,
                    paramType = "query",
                    dataType = "Long",
                    example = "123"),
            @ApiImplicitParam(
                    name = "contentTypeStatus",
                    value = "컨텐츠 타입",
                    required = true,
                    paramType = "query",
                    dataType = "ContentTypeStatus",
                    example = "A"),
            @ApiImplicitParam(
                    name = "complainStatus",
                    value = "신고 타입",
                    required = true,
                    paramType = "query",
                    dataType = "ComplainStatus",
                    example = "A")
    })
    @PostMapping("/complain")
    public SingleApiResult<Long> createComplain(
            @LoginMember MemberDetails currentMember, @RequestParam Long contentId,
            @RequestParam ContentTypeStatus contentTypeStatus,
            @RequestParam ComplainStatus complainStatus) throws
            NotExistPostException, NotExistCollectionException, AlreadyComplainException, NotExistMemberException,
            UnknownContentTypeException, NotExistDMException, NotExistCommentInPostException,
            NotExistCommentInCollectionException {

        log.info("createComplain");
        log.info("memberId : {}", currentMember.getId());
        log.info("contentId : {}", contentId);
        log.info("contentTypeStatus : {}", contentTypeStatus);
        log.info("complainStatus : {}", complainStatus);

        return getResult(complainService.insertComplain(currentMember.getId(), contentId, contentTypeStatus, complainStatus));
    }

//    @ApiOperation(value = "신고 생성", notes = "")
//    @ApiImplicitParams({
//    })
//    @GetMapping("/complain")
//    public SingleApiResult<Long> createComplain(
//            @LoginMember MemberDetails currentMember) throws
//            NotExistPostException, NotExistCollectionException, AlreadyComplainException, NotExistMemberException,
//            UnknownContentTypeException, NotExistDMException, NotExistCommentInPostException,
//            NotExistCommentInCollectionException {
//
//        log.info("createComplain");
//        log.info("memberId : {}", currentMember.getId());
//
//        return getResult(complainService.insertComplain(currentMember.getId(), contentId, contentTypeStatus, complainStatus));
//    }
}
