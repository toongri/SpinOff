package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.service.comment.CommentInCollectionService;
import com.nameless.spin_off.service.comment.CommentInPostService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = {"댓글 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/comment")
public class CommentApiController {

    private final CommentInPostService commentInPostService;
    private final CommentInCollectionService commentInCollectionService;
}
