package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.MovieDto.KobisRequestDto;
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

    @ApiOperation(value = "naver api 영화 수정", notes = "")
    @ApiImplicitParams({
    })
    @PatchMapping("/naver")
    public int updateMovieByNaver() {
        log.info("**** PATCH :: /movie/naver");

        return movieService.updateMovieByNaver();
    }

    @ApiOperation(value = "kobis api 영화배우 업데이트", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "api 요청",
                    required = true,
                    paramType = "body",
                    dataType = "KobisRequestDto")
    })
    @PatchMapping("/kobis/actor")
    public int updateMovieActorByKobis(@RequestBody KobisRequestDto requestDto) {

        log.info("**** PATCH :: /movie/kobis/actor");
        log.info("startPage : {}", requestDto.getStartPage());
        log.info("size : {}", requestDto.getSize());
        return movieService.updateMovieActorByKobis(requestDto.getStartPage(), requestDto.getSize());
    }

    @ApiOperation(value = "kobis api 영화 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "api 요청",
                    required = true,
                    paramType = "body",
                    dataType = "KobisRequestDto")
    })
    @PostMapping("/kobis")
    public int createMovieByKobis(@RequestBody KobisRequestDto requestDto) {

        log.info("**** POST :: /movie/kobis");
        log.info("startPage : {}", requestDto.getStartPage());
        log.info("size : {}", requestDto.getSize());
        return movieService.createMoviesByKobis(requestDto.getStartPage(), requestDto.getSize(), false);
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
            @LoginMember MemberDetails currentMember,
            @PathVariable Long movieId)
            throws NotExistMovieException, AlreadyFollowedMovieException, NotExistMemberException {

        log.info("**** POST :: /movie/{movieId}/follow");
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
    public SingleApiResult<List<MembersByContentDto>> readFollowMovie(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long movieId)
            throws NotExistMovieException, AlreadyFollowedMovieException, NotExistMemberException {

        log.info("**** GET :: /movie/{movieId}/follow");
        log.info("movieId : {}", movieId);

        return getResult(movieQueryService.getFollowMovieMembers(getCurrentMemberId(currentMember), movieId));
    }

    @ApiOperation(value = "영화 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "movieId",
                    value = "조회할 영화 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123"),
            @ApiImplicitParam(
                    name = "ip",
                    value = "ip주소",
                    required = true,
                    paramType = "query",
                    dataType = "string",
                    example = "192.168.0.1")
    })
    @GetMapping("/{movieId}")
    public SingleApiResult<ReadMovieDto> readMovie(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long movieId,
            @RequestParam String ip)
            throws NotExistMovieException, AlreadyFollowedMovieException, NotExistMemberException {

        log.info("**** GET :: /movie/{movieId}");
        log.info("movieId : {}", movieId);
        ReadMovieDto movieForRead = movieQueryService.getMovieForRead(getCurrentMemberId(currentMember), movieId);
        movieService.insertViewedMovieByIp(ip, movieId);
        return getResult(movieForRead);
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
            @LoginMember MemberDetails currentMember,
            @PathVariable Long movieId,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable)
            throws NotExistMovieException, AlreadyFollowedMovieException, NotExistMemberException {

        log.info("**** GET :: /movie/{movieId}/post");
        log.info("movieId : {}", movieId);
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());

        return getResult(movieQueryService.getRelatedPostsByMovieIdSliced(getCurrentMemberId(currentMember), movieId, pageable));
    }

    private Long getCurrentMemberId(MemberDetails currentMember) {
        if (currentMember != null) {
            return currentMember.getId();
        } else {
            return null;
        }
    }
}
