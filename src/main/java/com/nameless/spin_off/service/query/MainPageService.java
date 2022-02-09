package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MainPageService {
    Slice<MainPagePostDto> getMainPagePostsOrderById(Pageable pageable);
}
