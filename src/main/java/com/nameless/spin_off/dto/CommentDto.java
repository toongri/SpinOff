package com.nameless.spin_off.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CommentDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCommentInPostVO {
        private Long postId;
        private Long parentId;
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCommentInCollectionVO {
        private Long collectionId;
        private Long parentId;
        private String content;
    }
}
