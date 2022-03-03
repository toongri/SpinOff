package com.nameless.spin_off.dto;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.dto.SliceDto.ContentLessSliceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Slice;

import java.util.List;

public class MainPageDto {

    @Data
    @AllArgsConstructor
    public static class MainPageDiscoveryDto {
        List<MainPagePostDto> posts;
        ContentLessSliceDto sliceOfPopularPost;
        ContentLessSliceDto sliceOfLatestPost;
        Slice<MainPageCollectionDto> collections;
    }

    @Data
    @AllArgsConstructor
    public static class MainPageFollowDto {
        List<MainPagePostDto> posts;
        ContentLessSliceDto sliceOfFollowingMember;
        ContentLessSliceDto sliceOfFollowingHashtag;
        ContentLessSliceDto sliceOfFollowingMovie;
        Slice<MainPageCollectionDto> collections;
    }
}
