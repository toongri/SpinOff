package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.MemberDto.*;
import com.nameless.spin_off.dto.ResultDto.SingleApiResult;
import com.nameless.spin_off.dto.SignDto;
import com.nameless.spin_off.dto.SignDto.AccountIdRequestDto;
import com.nameless.spin_off.dto.SignDto.AuthCodeRequestDto;
import com.nameless.spin_off.dto.SignDto.CreateEmailRequestDto;
import com.nameless.spin_off.dto.SignDto.LinkageEmailRequestDto;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.member.EmailLinkageServiceEnum;
import com.nameless.spin_off.enums.member.MemberCondition;
import com.nameless.spin_off.exception.sign.IncorrectProviderException;
import com.nameless.spin_off.exception.sign.NotCorrectEmailRequest;
import com.nameless.spin_off.service.member.SignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.nameless.spin_off.dto.ResultDto.SingleApiResult.getResult;

@Slf4j
@Api(tags = {"계정 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sign")
public class SignApiController {
    private final SignService signService;

    @ApiOperation(value = "유저 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "회원가입 정보",
                    required = true,
                    paramType = "body",
                    dataType = "MemberRegisterRequestDto")
    })
    @PostMapping("/register")
    public SingleApiResult<MemberLoginResponseDto> register(@RequestBody MemberRegisterRequestDto requestDto) {

        log.info("**** POST :: /sign/register");
        log.info("accountId : {}", requestDto.getAccountId());
        log.info("accountPw : {}", requestDto.getAccountPw());
        log.info("name : {}", requestDto.getName());
        log.info("nickname : {}", requestDto.getNickname());
        log.info("birth : {}", requestDto.getBirth());
        log.info("email : {}", requestDto.getEmail());
        log.info("authToken : {}", requestDto.getAuthToken());
        return getResult(signService.registerMember(requestDto));
    }

    @ApiOperation(value = "소셜 유저 생성 및 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "provider",
                    value = "소셜 로그인 종류",
                    required = true,
                    paramType = "path",
                    dataType = "string",
                    example = "naver"),
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "인증 코드",
                    required = true,
                    paramType = "body",
                    dataType = "AuthCodeRequestDto")
    })
    @PostMapping("/login/social/{provider}")
    public SingleApiResult<SocialLoginResponseDto> loginBySocial(
            @RequestBody AuthCodeRequestDto requestDto, @PathVariable String provider) {

        log.info("**** POST :: /sign/login/social/{provider}");
        log.info("provider : {}", provider);
        log.info("authCode : {}", requestDto.getAuthCode());

        return getResult(signService.loginBySocial(requestDto.getAuthCode(), provider));
    }

    @ApiOperation(value = "로그인", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "로그인 정보",
                    required = true,
                    paramType = "body",
                    dataType = "MemberLoginRequestDto")
    })
    @PatchMapping("/login")
    public SingleApiResult<MemberLoginResponseDto> login(@RequestBody MemberLoginRequestDto requestDto) {

        log.info("**** PATCH :: /sign/login");
        log.info("accountId : {}", requestDto.getAccountId());
        log.info("accountPw : {}", requestDto.getAccountPw());

        return getResult(signService.loginMember(requestDto));
    }

    @ApiOperation(value = "토큰 조회 및 수정", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "tokenRequestDto",
                    value = "토큰 정보",
                    required = true,
                    paramType = "body",
                    dataType = "TokenRequestDto")
    })
    @PatchMapping("/reissue")
    public SingleApiResult<TokenResponseDto> reIssue(@RequestBody TokenRequestDto tokenRequestDto) {

        log.info("**** PATCH :: /sign/reissue");
        log.info("refreshToken : {}", tokenRequestDto.getRefreshToken());
        log.info("accessToken : {}", tokenRequestDto.getAccessToken());

        return getResult(signService.reIssue(tokenRequestDto));
    }

    @ApiOperation(value = "이메일 인증 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "이메일 정보",
                    required = true,
                    paramType = "body",
                    dataType = "CreateEmailRequestDto")
    })
    @PostMapping("/auth-email")
    public SingleApiResult<Boolean> authEmail(@RequestBody CreateEmailRequestDto requestDto) {

        log.info("**** POST :: /sign/auth-email");
        log.info("email : {}", requestDto.getEmail());

        return getResult(signService.sendEmailForAuth(requestDto.getEmail()));
    }

    @ApiOperation(value = "이메일 인증 확인", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "이메일 인증 정보",
                    required = true,
                    paramType = "body",
                    dataType = "EmailAuthRequestDto")
    })
    @PatchMapping("/auth-email")
    public SingleApiResult<Boolean> confirmEmail(@RequestBody EmailAuthRequestDto requestDto) {

        log.info("**** PATCH :: /sign/auth-email");
        log.info("authToken : {}", requestDto.getAuthToken());
        log.info("email : {}", requestDto.getEmail());

        return getResult(signService.confirmEmail(requestDto));
    }

    @ApiOperation(value = "소셜 연동 생성", notes = "연동을 시도하는 api")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "provider",
                    value = "이메일 종류",
                    required = true,
                    paramType = "path",
                    dataType = "String",
                    example = "naver"),
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "이메일",
                    required = true,
                    paramType = "body",
                    dataType = "LinkageEmailRequestDto")
    })
    @PostMapping("/linkage-email/{provider}")
    public SingleApiResult<String> createLinkageEmail(
            @LoginMember MemberDetails currentMember,
            @RequestBody LinkageEmailRequestDto requestDto,
            @PathVariable String provider) {

        log.info("**** POST :: /sign/linkage-email/{provider}");
        log.info("provider : {}", provider);
        log.info("email : {}", requestDto.getEmail());

        try {
            if (isNotCorrectEmail(requestDto.getEmail(), EmailLinkageServiceEnum.valueOf(provider))) {
                throw new NotCorrectEmailRequest(ErrorEnum.NOT_CORRECT_EMAIL);
            }
        } catch (IllegalArgumentException e) {
            throw new IncorrectProviderException(ErrorEnum.INCORRECT_PROVIDER);
        }

        signService.updateEmailLinkage(requestDto.getEmail(), currentMember.getUsername(), provider);
        return getResult("메일을 확인하여 주시기 바랍니다.");
    }

    @ApiOperation(value = "소셜 연동 수정", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "소셜 인증 정보",
                    required = true,
                    paramType = "body",
                    dataType = "EmailLinkageCheckRequestDto")
    })
    @PatchMapping("/linkage-email")
    public SingleApiResult<String> updateLinkageEmail(@RequestBody EmailLinkageCheckRequestDto requestDto) {

        log.info("**** PATCH :: /sign/linkage-email");
        log.info("email : {}", requestDto.getEmail());
        log.info("authToken : {}", requestDto.getAuthToken());
        log.info("accountId : {}", requestDto.getAccountId());

        signService.checkEmailLinkage(requestDto);
        return getResult("연동이 완료되었습니다.");
    }

    @ApiOperation(value = "닉네임 존재 여부 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "nickname",
                    value = "닉네임",
                    required = true,
                    paramType = "query",
                    dataType = "string",
                    example = "퉁그리")
    })
    @GetMapping("/exist/nickname")
    public SingleApiResult<Boolean> checkDuplicateNickname(@RequestParam String nickname) {

        log.info("**** GET :: /sign/exist/nickname");
        log.info("nickname : {}", nickname);

        return getResult(signService.checkDuplicateNickname(nickname));
    }

    @ApiOperation(value = "아이디 존재 여부 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "accountId",
                    value = "아이디",
                    required = true,
                    paramType = "query",
                    dataType = "string",
                    example = "toongri")
    })
    @GetMapping("/exist/account-id")
    public SingleApiResult<Boolean> checkDuplicateAccountId(@RequestParam String accountId) {

        log.info("**** GET :: /sign/exist/account-id");
        log.info("accountId : {}", accountId);

        return getResult(signService.checkDuplicateAccountId(accountId));
    }

    @ApiOperation(value = "아이디 정보 조회 및 메일 발송", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "email",
                    value = "이메일",
                    required = true,
                    paramType = "query",
                    dataType = "string",
                    example = "spinoff232@gmail.com")
    })
    @GetMapping("/forget/account-id")
    public SingleApiResult<Boolean> readForgetAccountId(@RequestParam String email) {

        log.info("**** GET :: /sign/forget/account-id");
        log.info("email : {}", email);

        return getResult(signService.sendEmailForAccountId(email));
    }

    @ApiOperation(value = "비밀번호 정보 조회 및 수정, 메일 발송", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "아이디",
                    required = true,
                    paramType = "body",
                    dataType = "AccountIdRequestDto")
    })
    @PatchMapping("/forget/account-pw")
    public SingleApiResult<Boolean> readForgetAccountPw(@RequestBody AccountIdRequestDto requestDto) {

        log.info("**** PATCH :: /sign/forget/account-pw");
        log.info("accountId : {}", requestDto.getAccountId());

        return getResult(signService.sendEmailForAccountPw(requestDto.getAccountId()));
    }

    private Boolean isNotCorrectEmail(String email, EmailLinkageServiceEnum provider) {
        return !getProviderByEmail(email).equals(provider.getValue()) || MemberCondition.EMAIL.isNotCorrect(email);
    }

    private String getProviderByEmail(String email) {
        return email.substring(email.indexOf("@") + 1, email.indexOf("."));
    }
}
