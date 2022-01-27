package com.nameless.spin_off.service.post;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.movie.MovieInPost;
import com.nameless.spin_off.entity.post.Hashtag;
import com.nameless.spin_off.entity.post.Media;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PostedHashtag;
import com.nameless.spin_off.exception.member.NoSuchMemberException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.post.PostedHashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostedHashtagRepository postedHashTagRepository;
    private final MovieRepository movieRepository;
    private final HashtagRepository hashtagRepository;

    @Transactional(readOnly = false)
    public Long savePostByPostVO(PostDto.CreatePostVO postVO) throws NoSuchMemberException {

        Optional<Member> optionalMember = memberRepository.findById(postVO.getMemberId());
        Member member = optionalMember.orElseThrow(NoSuchMemberException::new);
        List<Media> medias = Media.createMedias(postVO.getMediaUrls());

        List<Hashtag> hashtags = hashtagRepository.findTagsByContentIn(postVO.getHashtagContents());
        List<String> contentsByTag = hashtags.stream().map(Hashtag::getContent).collect(Collectors.toList());
        List<Hashtag> anotherTags = postVO.getHashtagContents().stream().filter(tag -> !contentsByTag.contains(tag))
                .map(Hashtag::createHashtag)
                .collect(Collectors.toList());
        hashtags.addAll(anotherTags);

        List<PostedHashtag> postedHashtags = new ArrayList<>();

        for (Hashtag hashtag : hashtags) {
            PostedHashtag postedHashtag = PostedHashtag.createPostedHashtag(hashtag, null);
            hashtag.addPostedHashtag(postedHashtag);
            if (hashtag.getId() == null) {
                hashtagRepository.save(hashtag);
            }
            postedHashtags.add(postedHashtag);
        }

        List<Movie> movies = movieRepository.findTagsByIdIn(postVO.getMovieIds());

        List<MovieInPost> movieInPosts = movies.stream()
                .map(movie -> MovieInPost.createMovieInPost(null, movie))
                .collect(Collectors.toList());


        Post post =  Post.buildPost()
                .setMember(member)
                .setPostPublicStatus(postVO.getPostPublicStatus())
                .setMedias(medias)
                .setPostedHashTags(postedHashtags)
                .setTitle(postVO.getTitle())
                .setContent(postVO.getContent())
                .setMovieInPosts(movieInPosts)
                .build();

        return postRepository.save(post).getId();
    }
}
