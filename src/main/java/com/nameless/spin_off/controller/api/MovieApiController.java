package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.MovieDto.ReadMovieDto;
import com.nameless.spin_off.dto.PostDto.RelatedPostDto;
import com.nameless.spin_off.dto.ResultDto.SingleApiResult;
import com.nameless.spin_off.exception.member.AlreadyFollowedMovieException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.service.movie.MovieService;
import com.nameless.spin_off.service.query.MovieQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.nameless.spin_off.dto.ResultDto.SingleApiResult.getResult;

@Slf4j
@Api(tags = {"영화 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/movie")
public class MovieApiController {

    private final MovieService movieService;
    private final MovieQueryService movieQueryService;

    @ApiOperation(value = "kobis api 영화 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "startPage",
                    value = "api page",
                    required = true,
                    paramType = "path",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "size",
                    value = "api size",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123")
    })
    @PostMapping("/kobis/{startPage}")
    public int test(@PathVariable int startPage, int size) {
        return movieService.updateMovieApi(startPage, size);
    }

    @ApiOperation(value = "영화 팔로우 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "movieId",
                    value = "팔로우할 영화 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @PostMapping("/{movieId}/follow")
    public SingleApiResult<Long> createFollowMovie(
            @LoginMember MemberDetails currentMember, @PathVariable Long movieId)
            throws NotExistMovieException, AlreadyFollowedMovieException, NotExistMemberException {

        log.info("createFollowMovie");
        log.info("memberId : {}", currentMember.getId());
        log.info("movieId : {}", movieId);

        return getResult(movieService.insertFollowedMovieByMovieId(currentMember.getId(), movieId));
    }

    @ApiOperation(value = "영화 팔로우 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "movieId",
                    value = "팔로우할 영화 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{movieId}/follow")
    public SingleApiResult<List<MembersByContentDto>> readFollowMovie(@LoginMember MemberDetails currentMember,
                                                                      @PathVariable Long movieId)
            throws NotExistMovieException, AlreadyFollowedMovieException, NotExistMemberException {

        Long currentMemberId = getCurrentMemberId(currentMember);
        log.info("readFollowMovie");
        log.info("memberId : {}", currentMemberId);
        log.info("movieId : {}", movieId);

        return getResult(movieQueryService.getFollowMovieMembers(currentMemberId, movieId));
    }

    @ApiOperation(value = "영화 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "movieId",
                    value = "조회할 영화 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{movieId}")
    public SingleApiResult<ReadMovieDto> readMovie(@LoginMember MemberDetails currentMember,
                                                   @PathVariable Long movieId)
            throws NotExistMovieException, AlreadyFollowedMovieException, NotExistMemberException {

        Long currentMemberId = getCurrentMemberId(currentMember);
        log.info("readMovie");
        log.info("memberId : {}", currentMemberId);
        log.info("movieId : {}", movieId);

        return getResult(movieQueryService.getMovieForRead(currentMemberId, movieId));
    }

    @ApiOperation(value = "영화 관련 글 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "movieId",
                    value = "조회할 영화 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123"),
            @ApiImplicitParam(
                    name = "page",
                    value = "페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "size",
                    value = "페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "sort",
                    value = "페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "popularity,desc")
    })
    @GetMapping("/{movieId}/post")
    public SingleApiResult<Slice<RelatedPostDto>> readMoviePost(
            @LoginMember MemberDetails currentMember, @PathVariable Long movieId,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable)
            throws NotExistMovieException, AlreadyFollowedMovieException, NotExistMemberException {

        Long currentMemberId = getCurrentMemberId(currentMember);
        log.info("readMoviePost");
        log.info("memberId : {}", currentMemberId);
        log.info("movieId : {}", movieId);
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());

        return getResult(movieQueryService.getRelatedPostsByMovieIdSliced(currentMemberId, movieId, pageable));
    }

    private Long getCurrentMemberId(MemberDetails currentMember) {
        if (currentMember != null) {
            return currentMember.getId();
        } else {
            return null;
        }
    }
}
