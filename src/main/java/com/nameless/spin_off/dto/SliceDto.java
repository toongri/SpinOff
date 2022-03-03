package com.nameless.spin_off.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Slice;

public class SliceDto {

    @Data
    @AllArgsConstructor
    public static class ContentLessSliceDto<T> {
        int getNumber;
        int getSize;
        boolean isFirst;
        boolean isLast;
        boolean hasNext;

        public ContentLessSliceDto(Slice<T> slice) {
            this.getNumber = slice.getNumber();
            this.getSize = slice.getSize();
            this.isFirst = slice.isFirst();
            this.isLast = slice.isLast();
            this.hasNext = slice.hasNext();
        }
    }
}
