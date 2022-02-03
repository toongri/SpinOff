package com.nameless.spin_off.service.post;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.exception.member.NoSuchMemberException;

public interface PostService {
    Long savePostByPostVO(PostDto.CreatePostVO postVO) throws NoSuchMemberException;

}
