package com.nameless.spin_off.dto;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Slice;

public class MainPageDto {

    @Data
    @AllArgsConstructor
    public static class MainPageDiscoveryDto {

        @ApiModelProperty(value = "인기순 글")
        Slice<MainPagePostDto> postsByPopular;

        @ApiModelProperty(value = "최신순 글")
        Slice<MainPagePostDto> postsById;

        @ApiModelProperty(value = "최신순 컬렉션")
        Slice<MainPageCollectionDto> collections;
    }

    @Data
    @AllArgsConstructor
    public static class MainPageFollowDto {
        @ApiModelProperty(value = "팔로우 멤버 관련 글")
        Slice<MainPagePostDto> postsByFollowedMember;

        @ApiModelProperty(value = "팔로우 해시태그 관련 글")
        Slice<MainPagePostDto> postsByFollowedHashtag;

        @ApiModelProperty(value = "팔로우 영화 관련 글")
        Slice<MainPagePostDto> postsByFollowedMovie;

        @ApiModelProperty(value = "팔로우 컬렉션 관련 컬렉션")
        Slice<MainPageCollectionDto> collections;
    }
}
