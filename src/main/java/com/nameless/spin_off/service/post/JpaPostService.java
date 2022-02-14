package com.nameless.spin_off.service.post;

import com.nameless.spin_off.StaticVariable;
import com.nameless.spin_off.dto.PostDto.CreatePostVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.*;
import com.nameless.spin_off.exception.collection.AlreadyCollectedPostException;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.collection.OverSearchCollectedPostException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.post.*;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Long insertPostByPostVO(CreatePostVO postVO)
            throws NotExistMemberException, NotExistMovieException, NotExistCollectionException, InCorrectHashtagContentException, AlreadyPostedHashtagException {

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

        List<Collection> collections = getCollectionsByIdIn(postVO.getCollectionIds(), postVO.getMemberId());
        collections.forEach(collection -> collection.addCollectedPostByPost(post));
        return postRepository.save(post).getId();
    }

    @Transactional(readOnly = false)
    @Override
    public Long insertLikedPostByMemberId(Long memberId, Long postId)
            throws NotExistMemberException, NotExistPostException,
            OverSearchLikedPostException, AlreadyLikedPostException {

        Member member = getMemberById(memberId);
        Post post = getPostByIdWithLikedPost(postId);

        post.insertLikedPostByMember(member);

        return post.getId();
    }

    @Transactional(readOnly = false)
    @Override
    public Long insertViewedPostByIp(String ip, Long postId)
            throws NotExistPostException, OverSearchViewedPostByIpException {

        Post post = getPostByIdWithViewedIp(postId);

        post.insertViewedPostByIp(ip);

        return post.getId();
    }

    @Transactional(readOnly = false)
    @Override
    public Long insertCollectedPosts(Long memberId, Long postId, List<Long> collectionIds)
            throws NotExistMemberException, NotExistCollectionException,
            NotExistPostException, OverSearchCollectedPostException, AlreadyCollectedPostException {

        Post post = getPost(postId);
        List<Collection> collections = getCollectionsWithPost(memberId, collectionIds);

        for (Collection collection : collections) {
            collection.insertCollectedPostByPost(post);
        }

        return post.getId();
    }

    private List<Collection> getCollectionsWithPost(Long memberId, List<Long> collectionIds)
            throws NotExistCollectionException {
        List<Collection> collections = collectionRepository.findAllByIdInAndMemberIdIncludePost(collectionIds, memberId);

        if (collections.size() != collectionIds.size()) {
            throw new NotExistCollectionException();
        }
        return collections;
    }

    private Post getPost(Long postId) throws NotExistPostException {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.orElseThrow(NotExistPostException::new);
    }

    private List<Collection> getCollectionsByIdIn(List<Long> collectionIds, Long memberId) throws NotExistCollectionException {
        List<Collection> collections = collectionRepository.findAllByIdInAndMemberIdIncludePost(collectionIds, memberId);

        if (collections.size() == collectionIds.size()) {
            return collections;
        } else {
            throw new NotExistCollectionException();
        }
    }

    private List<Hashtag> saveHashtagsByString(List<String> hashtagContents) throws InCorrectHashtagContentException {

        if (isNotContainCantChar(hashtagContents).isEmpty()) {
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
        } else {
            throw new InCorrectHashtagContentException();
        }
    }

    private Optional<String> isNotContainCantChar(List<String> hashtagContents) {

        for (String hashtagContent : hashtagContents) {
            for (String cantContainChar : StaticVariable.CANT_CONTAIN_AT_HASHTAG) {
                if (hashtagContent.contains(cantContainChar)) {
                    return Optional.of(hashtagContent);
                }
            }
        }
        return Optional.empty();
    }

    private Member getMemberById(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Movie getMovieById(Long movieId) throws NotExistMovieException {
        if (movieId == null) {
            return null;
        }
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);

        return optionalMovie.orElseThrow(NotExistMovieException::new);
    }

    private Post getPostByIdWithViewedIp(Long postId) throws NotExistPostException {
        Optional<Post> optionalPost = postRepository.findOneByIdFetchJoinViewedByIpOrderByViewedIpId(postId);

        return optionalPost.orElseThrow(NotExistPostException::new);
    }

    private Post getPostByIdWithLikedPost(Long postId) throws NotExistPostException {
        Optional<Post> optionalPost = postRepository.findOneByIdFetchJoinLikedPost(postId);

        return optionalPost.orElseThrow(NotExistPostException::new);
    }
}
