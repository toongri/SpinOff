package com.nameless.spin_off.service.post;

import com.nameless.spin_off.dto.PostDto.CreatePostVO;
import com.nameless.spin_off.entity.collections.CollectedPost;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.movie.PostedMovie;
import com.nameless.spin_off.entity.post.Hashtag;
import com.nameless.spin_off.entity.post.PostedMedia;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PostedHashtag;
import com.nameless.spin_off.exception.member.NoSuchMemberException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaPostService implements PostService{

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final MovieRepository movieRepository;
    private final HashtagRepository hashtagRepository;
    private final CollectionRepository collectionRepository;

    @Transactional(readOnly = false)
    @Override
    public Long savePostByPostVO(CreatePostVO postVO) throws NoSuchMemberException {

        Member member = getMemberById(postVO.getMemberId());

        List<PostedMedia> postedMedia = PostedMedia.createPostedMedias(postVO.getMediaUrls());

        List<PostedHashtag> postedHashtags = getPostedHashtags(postVO.getHashtagContents());

        List<PostedMovie> postedMovies = getPostedMovies(postVO.getMovieIds());

        Post post =  Post.buildPost()
                .setMember(member)
                .setPostPublicStatus(postVO.getPublicOfPostStatus())
                .setPostedMedias(postedMedia)
                .setPostedHashTags(postedHashtags)
                .setTitle(postVO.getTitle())
                .setContent(postVO.getContent())
                .setPostedMovies(postedMovies)
                .build();

        List<Collection> collections = collectionRepository.findCollectionsByIdIn(postVO.getCollectionIds());
        collections.forEach(collection -> collection.addCollectedPost(CollectedPost.createCollectedPosts(post)));

        return postRepository.save(post).getId();
    }

    private List<PostedMovie> getPostedMovies(List<Long> movieIds) {
        List<Movie> movies = movieRepository.findTagsByIdIn(movieIds);

        return movies.stream()
                .map(PostedMovie::createPostedMovie)
                .collect(Collectors.toList());
    }

    private List<PostedHashtag> getPostedHashtags(List<String> hashtagContents) {

        List<Hashtag> hashtags = getHashtags(hashtagContents);

        List<PostedHashtag> postedHashtags = new ArrayList<>();

        for (Hashtag hashtag : hashtags) {
            PostedHashtag postedHashtag = PostedHashtag.createPostedHashtag(hashtag);
            if (hashtag.getId() == null) {
                hashtagRepository.save(hashtag);
            }
            postedHashtags.add(postedHashtag);
        }
        return postedHashtags;
    }

    private List<Hashtag> getHashtags(List<String> hashtagContents) {
        List<Hashtag> hashtags = hashtagRepository.findTagsByContentIn(hashtagContents);
        List<String> contentsByTag = hashtags.stream().map(Hashtag::getContent).collect(Collectors.toList());
        List<Hashtag> anotherTags = hashtagContents.stream().filter(tag -> !contentsByTag.contains(tag))
                .map(Hashtag::createHashtag)
                .collect(Collectors.toList());
        hashtags.addAll(anotherTags);
        return hashtags;
    }

    private Member getMemberById(Long memberId) throws NoSuchMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(NoSuchMemberException::new);
    }
}
