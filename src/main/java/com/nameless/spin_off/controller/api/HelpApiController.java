package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.entity.enums.help.ComplainStatus;
import com.nameless.spin_off.entity.enums.help.ContentTypeStatus;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.help.UnknownContentTypeException;
import com.nameless.spin_off.exception.member.AlreadyComplainException;
import com.nameless.spin_off.exception.member.NotExistDMException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.service.help.ComplainService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/help")
public class HelpApiController {

    private final ComplainService complainService;

    @PostMapping("/complain")
    public HelpResult<Long> createOne(
            @LoginMember MemberDetails currentMember, @RequestParam Long contentId,
            @RequestParam ContentTypeStatus contentTypeStatus,
            @RequestParam("status") ComplainStatus complainStatus) throws
            NotExistPostException, NotExistCollectionException, AlreadyComplainException, NotExistMemberException,
            UnknownContentTypeException, NotExistDMException, NotExistCommentInPostException,
            NotExistCommentInCollectionException {

        log.info("createOne");
        log.info("memberId : {}", currentMember.getId());
        log.info("contentId : {}", contentId);
        log.info("contentTypeStatus : {}", contentTypeStatus);
        log.info("complainStatus : {}", complainStatus);

        return getResult(complainService.insertComplain(currentMember.getId(), contentId, contentTypeStatus, complainStatus));
    }

    @Data
    @AllArgsConstructor
    public static class HelpResult<T> {
        private T data;
        Boolean isSuccess;
        String code;
        String message;
    }
    public <T> HelpResult<T> getResult(T data) {
        return new HelpResult<>(data, true, "0", "성공");
    }
}
