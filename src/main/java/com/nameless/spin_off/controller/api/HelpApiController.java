package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.HelpDto.ComplainRequestDto;
import com.nameless.spin_off.dto.ResultDto.SingleApiResult;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
                    name = "complainRequestDto",
                    value = "신고",
                    required = true,
                    paramType = "body",
                    dataType = "ComplainRequestDto")
    })
    @PostMapping("/complain")
    public SingleApiResult<Long> createComplain(
            @LoginMember MemberDetails currentMember,
            @RequestBody ComplainRequestDto complainRequestDto)
            throws
            NotExistPostException, NotExistCollectionException, AlreadyComplainException, NotExistMemberException,
            UnknownContentTypeException, NotExistDMException, NotExistCommentInPostException,
            NotExistCommentInCollectionException {

        log.info("**** POST :: /help/complain");
        log.info("contentId : {}", complainRequestDto.getContentId());
        log.info("contentTypeStatus : {}", complainRequestDto.getContentTypeStatus());
        log.info("complainStatus : {}", complainRequestDto.getComplainStatus());

        return getResult(
                complainService.insertComplain(currentMember.getId(),
                complainRequestDto.getContentId(),
                complainRequestDto.getContentTypeStatus(),
                complainRequestDto.getComplainStatus()));
    }
}
