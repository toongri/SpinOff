package com.nameless.spin_off.service.post;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.member.NoSuchMemberException;
import com.nameless.spin_off.exception.post.NoSuchPostException;

public interface PostService {
    Long savePostByPostVO(PostDto.CreatePostVO postVO) throws NoSuchMemberException;
    Post saveLikedPostByMemberIdAndPostId(Long memberId, Long postId) throws NoSuchMemberException, NoSuchPostException;
}
