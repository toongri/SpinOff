package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.service.query.MainPageService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/main-page")
public class MainPagePostApiController {

    private final MainPageService mainPageService;

    @GetMapping("/post/id")
    public MainPagePostResult<Slice<MainPagePostDto>> getMainPagePostsOrderById(
            @RequestParam("page") Integer page, @RequestParam("size") Integer size) {

        Slice<MainPagePostDto> slice = mainPageService.getMainPagePostsOrderById(PageRequest.of(page, size));
        return new MainPagePostResult<Slice<MainPagePostDto>>(slice);
    }

    @Data
    @AllArgsConstructor
    public static class MainPagePostResult<T> {
        private T data;
    }
}
