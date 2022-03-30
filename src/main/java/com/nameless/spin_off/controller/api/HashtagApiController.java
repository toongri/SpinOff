package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.ResultDto.SingleApiResult;
import com.nameless.spin_off.exception.hashtag.NotExistHashtagException;
import com.nameless.spin_off.exception.member.AlreadyFollowedHashtagException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.hashtag.HashtagService;
import com.nameless.spin_off.service.query.SearchQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.nameless.spin_off.dto.ResultDto.SingleApiResult.getResult;

@Slf4j
@Api(tags = {"해시태그 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/hashtag")
public class HashtagApiController {

    private final HashtagService hashtagService;
    private final SearchQueryService searchQueryService;

    @ApiOperation(value = "해시태그 팔로우 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "hashtagId",
                    value = "해시태그 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @PostMapping("/{hashtagId}/follow")
    public SingleApiResult<Long> createFollowOne(@LoginMember MemberDetails currentMember, @PathVariable Long hashtagId)
            throws AlreadyFollowedHashtagException, NotExistMemberException, NotExistHashtagException {

        log.info("createFollowOne");
        log.info("hashtagId : {}", hashtagId);
        log.info("memberId : {}", currentMember.getId());

        return getResult(hashtagService.insertFollowedHashtagByHashtagId(currentMember.getId(), hashtagId));
    }

    @ApiOperation(value = "가장 인기있는 해시태그 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "length",
                    value = "갯수 설정",
                    dataType = "int",
                    paramType = "query",
                    required = true,
                    example = "123")
    })
    @GetMapping("/most-popular")
    public SingleApiResult<List<MostPopularHashtag>> getMostPopularHashtag(int length) {

        log.info("getMostPopularHashtag");
        log.info("length : {}", length);

        return getResult(searchQueryService.getMostPopularHashtagLimit(length));
    }
}
