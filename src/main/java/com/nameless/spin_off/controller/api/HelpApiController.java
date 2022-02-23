package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.entity.help.ComplainStatus;
import com.nameless.spin_off.entity.help.ContentTypeStatus;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/help")
public class HelpApiController {

    private final ComplainService complainService;

    @PostMapping("/complain")
    public HelpResult<Long> createComplain(
            @RequestParam("id") Long id, @RequestParam() Long contentId,
            @RequestParam() ContentTypeStatus contentTypeStatus,
            @RequestParam("status") ComplainStatus complainStatus) throws
            NotExistPostException, NotExistCollectionException, AlreadyComplainException, NotExistMemberException,
            UnknownContentTypeException, NotExistDMException, NotExistCommentInPostException,
            NotExistCommentInCollectionException {
        Long complainId = complainService.insertComplain(id, contentId, contentTypeStatus, complainStatus);

        return new HelpResult<Long>(complainId);
    }

    @Data
    @AllArgsConstructor
    public static class HelpResult<T> {
        private T data;
    }
}
