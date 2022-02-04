package com.nameless.spin_off.dto;

import com.nameless.spin_off.entity.post.PublicOfPostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class CommentDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCommentInPostVO {

        private Long memberId;
        private Long postId;
        private Long parentId;
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCommentInCollectionVO {

        private Long memberId;
        private Long collectionId;
        private Long parentId;
        private String content;
    }
}
