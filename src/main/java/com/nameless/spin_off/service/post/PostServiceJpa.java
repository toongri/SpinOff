package com.nameless.spin_off.service.post;

import com.nameless.spin_off.dto.PostDto.CreatePostVO;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PostedMedia;
import com.nameless.spin_off.exception.collection.AlreadyCollectedPostException;
import com.nameless.spin_off.exception.collection.NotMatchCollectionException;
import com.nameless.spin_off.exception.hashtag.InCorrectHashtagContentException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.post.*;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.enums.BanListOfContentsEnum.CANT_CONTAIN_AT_HASHTAG;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceJpa implements PostService{

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final MovieRepository movieRepository;
    private final HashtagRepository hashtagRepository;
    private final CollectionRepository collectionRepository;

    @Transactional()
    @Override
    public Long insertPostByPostVO(CreatePostVO postVO)
            throws NotExistMemberException, NotExistMovieException, InCorrectHashtagContentException, AlreadyPostedHashtagException, AlreadyCollectedPostException, AlreadyPAuthorityOfPostStatusException, OverTitleOfPostException, OverContentOfPostException, NotMatchCollectionException {

        Member member = getMemberById(postVO.getMemberId());

        List<PostedMedia> postedMedia = PostedMedia.createPostedMedias(postVO.getMediaUrls());

        List<Hashtag> hashtags = saveHashtagsByString(postVO.getHashtagContents());

        Movie movie = getMovieById(postVO.getMovieId());
        List<Collection> collections = getCollectionsByIdIn(postVO.getMemberId(), postVO.getCollectionIds());

        Post post =  Post.buildPost()
                .setMember(member)
                .setPostPublicStatus(postVO.getPublicOfPostStatus())
                .setPostedMedias(postedMedia)
                .setHashTags(hashtags)
                .setMovie(movie)
                .setTitle(postVO.getTitle())
                .setContent(postVO.getContent())
                .setCollections(collections)
                .build();

        return postRepository.save(post).getId();
    }

    @Transactional()
    @Override
    public Long insertLikedPostByMemberId(Long memberId, Long postId)
            throws NotExistMemberException, NotExistPostException, AlreadyLikedPostException {

        Member member = getMemberById(memberId);
        Post post = getPostByIdWithLikedPost(postId);

        return post.insertLikedPostByMember(member);
    }

    @Transactional()
    @Override
    public Long insertViewedPostByIp(String ip, Long postId) throws NotExistPostException {

        Post post = getPostByIdWithViewedIp(postId);

        return post.insertViewedPostByIp(ip);
    }

    @Transactional()
    @Override
    public List<Long> insertCollectedPosts(Long memberId, Long postId, List<Long> collectionIds)
            throws NotMatchCollectionException,
            NotExistPostException, AlreadyCollectedPostException {

        Post post = getPostWithCollectedPost(postId);
        List<Collection> collections = getCollectionsByIdIn(memberId, collectionIds);

        return post.insertCollectedPostByCollections(collections);
    }

    private List<Collection> getCollectionsByIdIn(Long memberId, List<Long> collectionIds)
            throws NotMatchCollectionException {
        List<Collection> collections = collectionRepository.findAllByIdIn(collectionIds);

        if (collections.stream().allMatch(collection -> collection.getMember().getId().equals(memberId))) {
            return collections;
        } else {
            throw new NotMatchCollectionException();
        }

    }

    private Post getPostWithCollectedPost(Long postId) throws NotExistPostException {
        Optional<Post> optionalPost = postRepository.findOneByIdWithCollectedPost(postId);
        return optionalPost.orElseThrow(NotExistPostException::new);
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
            for (String cantContainChar : CANT_CONTAIN_AT_HASHTAG.getBanList()) {
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
        Optional<Post> optionalPost = postRepository.findOneByIdWithViewedByIp(postId);

        return optionalPost.orElseThrow(NotExistPostException::new);
    }

    private Post getPostByIdWithLikedPost(Long postId) throws NotExistPostException {
        Optional<Post> optionalPost = postRepository.findOneByIdWithLikedPost(postId);

        return optionalPost.orElseThrow(NotExistPostException::new);
    }
}
