package com.nameless.spin_off.service.post;

import com.nameless.spin_off.dto.PostDto.CreatePostVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.*;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.exception.movie.NotSearchMovieException;
import com.nameless.spin_off.exception.post.AlreadyLikedPostException;
import com.nameless.spin_off.exception.post.NotSearchPostException;
import com.nameless.spin_off.exception.post.OverSearchViewedPostByIpException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public Long savePostByPostVO(CreatePostVO postVO) throws NotSearchMemberException, NotSearchMovieException {

        Member member = getMemberById(postVO.getMemberId());

        List<PostedMedia> postedMedia = PostedMedia.createPostedMedias(postVO.getMediaUrls());

        List<Hashtag> hashtags = saveHashtagsByString(postVO.getHashtagContents());

        Movie movie = getMovieById(postVO.getMovieId());

        Post post =  Post.buildPost()
                .setMember(member)
                .setPostPublicStatus(postVO.getPublicOfPostStatus())
                .setPostedMedias(postedMedia)
                .setHashTags(hashtags)
                .setMovie(movie)
                .setTitle(postVO.getTitle())
                .setContent(postVO.getContent())
                .build();

        List<Collection> collections = collectionRepository.findAllByIdIn(postVO.getCollectionIds());
        collections.forEach(collection -> collection.addCollectedPostByPost(post));

        return postRepository.save(post).getId();
    }

    @Transactional(readOnly = false)
    @Override
    public Post updateLikedPostByMemberId(Long memberId, Long postId)
            throws NotSearchMemberException, NotSearchPostException,
            OverSearchViewedPostByIpException, AlreadyLikedPostException {

        Member member = getMemberById(memberId);
        Post post = getPostByIdWithLikedPost(postId);

        if (post.isNotMemberAlreadyLikePost(member)) {
            post.addLikedPostByMember(member);
        } else {
            throw new AlreadyLikedPostException();
        }

        return post;
    }

    @Transactional(readOnly = false)
    @Override
    public Post updateViewedPostByIp(String ip, Long postId, LocalDateTime timeNow, Long minuteDuration)
            throws NotSearchPostException, OverSearchViewedPostByIpException {

        Post post = getPostByIdWithViewedIp(postId);

        if (post.isNotIpAlreadyView(ip, timeNow, minuteDuration)) {
            post.addViewedPostByIp(ip);
        }

        return post;
    }

    private List<Hashtag> saveHashtagsByString(List<String> hashtagContents) {

        List<Hashtag> alreadySavedHashtags = hashtagRepository.findAllByContentIn(hashtagContents);

        List<String> contentsAboutAlreadySavedHashtags =
                alreadySavedHashtags.stream().map(Hashtag::getContent).collect(Collectors.toList());

        List<Hashtag> anotherTags = hashtagContents.stream()
                .filter(tag -> !contentsAboutAlreadySavedHashtags.contains(tag))
                .map(Hashtag::createHashtag)
                .collect(Collectors.toList());

        hashtagRepository.saveAll(anotherTags);

        alreadySavedHashtags.addAll(anotherTags);

        return alreadySavedHashtags;
    }

    private Member getMemberById(Long memberId) throws NotSearchMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(NotSearchMemberException::new);
    }

    private Movie getMovieById(Long movieId) throws NotSearchMovieException {
        if (movieId == null) {
            return null;
        }
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);

        return optionalMovie.orElseThrow(NotSearchMovieException::new);
    }

    private Post getPostByIdWithViewedIp(Long postId) throws NotSearchPostException {
        Optional<Post> optionalPost = postRepository.findOneByIdFetchJoinViewedPostByIpOrderByViewedIpId(postId);

        return optionalPost.orElseThrow(NotSearchPostException::new);
    }

    private Post getPostByIdWithLikedPost(Long postId) throws NotSearchPostException {
        Optional<Post> optionalPost = postRepository.findOneByIdFetchJoinLikedPost(postId);

        return optionalPost.orElseThrow(NotSearchPostException::new);
    }
}
