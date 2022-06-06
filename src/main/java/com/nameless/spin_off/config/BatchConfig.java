package com.nameless.spin_off.config;

import com.nameless.spin_off.config.movie.MovieApiService;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.nameless.spin_off.enums.collection.CollectionScoreEnum.COLLECTION_VIEW;
import static com.nameless.spin_off.enums.hashtag.HashtagScoreEnum.HASHTAG_VIEW;
import static com.nameless.spin_off.enums.member.MemberScoreEnum.MEMBER_FOLLOW;
import static com.nameless.spin_off.enums.movie.MovieApiEnum.KOBIS_API_REQUEST_NUMBER_MAX;
import static com.nameless.spin_off.enums.movie.MovieScoreEnum.MOVIE_VIEW;
import static com.nameless.spin_off.enums.post.PostScoreEnum.POST_VIEW;

@Slf4j  // log 사용을 위한 lombok Annotation
@RequiredArgsConstructor
@Configuration
public class BatchConfig {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CollectionRepository collectionRepository;
    private final HashtagRepository hashtagRepository;
    private final MovieRepository movieRepository;
    private final MovieApiService movieApiService;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job popularityJob() {
        log.info("********** This is popularityJob");
        return jobBuilderFactory.get("popularityJob")  // 1_1
                .preventRestart()  // 1_2
                .start(popularityMemberJob())  // 1_3
                .next(popularityPostJob()) // 1_4
                .next(popularityCollectionJob()) // 1_5
                .next(popularityHashtagJob()) // 1_6
                .next(popularityMovieJob()) // 1_7
                .build(); // 1_8
    }

    @Bean
    public Job movieApiJob() {
        log.info("********** This is movieApiJob");
        return jobBuilderFactory.get("movieApiJob")  // 1_1
                .preventRestart()  // 1_2
                .start(movieApiStepJob())  // 1_3
                .build(); // 1_8
    }

    @Bean
    public Step popularityMemberJob() {
        log.info("********** This is popularityMemberJob");
        return stepBuilderFactory.get("popularityMemberJob")  // 2_1
                .<Member, Member> chunk(100)  // 2_2
                .reader(popularityMemberReader())  // 2_3
                .processor(this.popularityMemberProcessor())  // 2_4
                .writer(this.popularityMemberWriter())  // 2_5
                .build();  // 2_6
    }

    @Bean
    public Step popularityPostJob() {
        log.info("********** This is popularityPostJob");
        return stepBuilderFactory.get("popularityPostJob")  // 3_1
                .<Post, Post> chunk(100)  // 3_2
                .reader(popularityPostReader())  // 3_3
                .processor(this.popularityPostProcessor())  // 3_4
                .writer(this.popularityPostWriter())  // 3_5
                .build();  // 3_6
    }

    @Bean
    public Step popularityCollectionJob() {
        log.info("********** This is popularityCollectionJob");
        return stepBuilderFactory.get("popularityCollectionJob")  // 4_1
                .<Collection, Collection> chunk(100)  // 4_2
                .reader(popularityCollectionReader())  // 4_3
                .processor(this.popularityCollectionProcessor())  // 4_4
                .writer(this.popularityCollectionWriter())  // 4_5
                .build();  // 4_6
    }

    @Bean
    public Step popularityHashtagJob() {
        log.info("********** This is popularityHashtagJob");
        return stepBuilderFactory.get("popularityHashtagJob")  // 5_1
                .<Hashtag, Hashtag> chunk(100)  // 5_2
                .reader(popularityHashtagReader())  // 5_3
                .processor(this.popularityHashtagProcessor())  // 5_4
                .writer(this.popularityHashtagWriter())  // 5_5
                .build();  // 5_6
    }

    @Bean
    public Step popularityMovieJob() {
        log.info("********** This is popularityMovieJob");
        return stepBuilderFactory.get("popularityMovieJob")  // 6_1
                .<Movie, Movie> chunk(10)  // 6_2
                .reader(popularityMovieReader())  // 6_3
                .processor(this.popularityMovieProcessor())  // 6_4
                .writer(this.popularityMovieWriter())  // 6_5
                .build();  // 6_6
    }

    @Bean
    public Step movieApiStepJob() {
        log.info("********** This is movieApiStepJob");
        return stepBuilderFactory.get("movieApiStepJob")  // 6_1
                .<Movie, Movie> chunk(10)  // 6_2
                .reader(movieApiReader())  // 6_3
                .processor(this.movieApiProcessor())  // 6_4
                .writer(this.movieApiWriter())  // 6_5
                .build();  // 6_6
    }

    @Bean
    @StepScope  // 1
    public ListItemReader<Member> popularityMemberReader() {
        log.info("********** This is popularityMemberReader");
        List<Member> activeMembers = memberRepository.findAllByViewAfterTime(MEMBER_FOLLOW.getOldestDate());
        log.info("          - activeMember SIZE : " + activeMembers.size());  // 2
        return new ListItemReader<>(activeMembers);  // 3
    }

    @Bean
    @StepScope  // 1
    public ListItemReader<Post> popularityPostReader() {
        log.info("********** This is popularityPostReader");
        List<Post> activePosts = postRepository.findAllByViewAfterTime(POST_VIEW.getOldestDate());
        log.info("          - activeMember SIZE : " + activePosts.size());  // 2
        return new ListItemReader<>(activePosts);  // 3
    }

    @Bean
    @StepScope  // 1
    public ListItemReader<Collection> popularityCollectionReader() {
        log.info("********** This is popularityCollectionReader");
        List<Collection> activeCollections = collectionRepository.findAllByViewAfterTime(COLLECTION_VIEW.getOldestDate());
        log.info("          - activeMember SIZE : " + activeCollections.size());  // 2
        return new ListItemReader<>(activeCollections);  // 3
    }

    @Bean
    @StepScope  // 1
    public ListItemReader<Hashtag> popularityHashtagReader() {
        log.info("********** This is popularityHashtagReader");
        List<Hashtag> activeHashtags = hashtagRepository.findAllByViewAfterTime(HASHTAG_VIEW.getOldestDate());
        log.info("          - activeMember SIZE : " + activeHashtags.size());  // 2
        return new ListItemReader<>(activeHashtags);  // 3
    }

    @Bean
    @StepScope  // 1
    public ListItemReader<Movie> popularityMovieReader() {
        log.info("********** This is popularityMovieReader");
        List<Movie> activeMovies = movieRepository.findAllByViewAfterTime(MOVIE_VIEW.getOldestDate());
        log.info("          - activeMember SIZE : " + activeMovies.size());  // 2
        return new ListItemReader<>(activeMovies);  // 3
    }

    @Bean
    @StepScope  // 1
    public ListItemReader<Movie> movieApiReader() {
        log.info("********** This is movieApiReader");
        List<Movie> activeMovies = movieApiService.findAllNew(1, KOBIS_API_REQUEST_NUMBER_MAX.getValue());
        log.info("          - activeMember SIZE : " + activeMovies.size());  // 2
        return new ListItemReader<>(activeMovies);  // 3
    }

    public ItemProcessor<Member, Member> popularityMemberProcessor() {
        return new ItemProcessor<Member, Member>() {  // 1
            @Override
            public Member process(Member member) throws Exception {
                log.info("********** This is popularityMemberProcessor");
                member.updatePopularity();
                return member;  // 2
            }
        };
    }

    public ItemProcessor<Post, Post> popularityPostProcessor() {
        return new ItemProcessor<Post, Post>() {  // 1
            @Override
            public Post process(Post post) throws Exception {
                log.info("********** This is popularityPostProcessor");
                post.updatePopularity();
                return post;  // 2
            }
        };
    }

    public ItemProcessor<Collection, Collection> popularityCollectionProcessor() {
        return new ItemProcessor<Collection, Collection>() {  // 1
            @Override
            public Collection process(Collection collection) throws Exception {
                log.info("********** This is popularityCollectionProcessor");
                collection.updatePopularity();
                return collection;  // 2
            }
        };
    }

    public ItemProcessor<Hashtag, Hashtag> popularityHashtagProcessor() {
        return new ItemProcessor<Hashtag, Hashtag>() {  // 1
            @Override
            public Hashtag process(Hashtag hashtag) throws Exception {
                log.info("********** This is popularityHashtagProcessor");
                hashtag.updatePopularity();
                return hashtag;  // 2
            }
        };
    }

    public ItemProcessor<Movie, Movie> popularityMovieProcessor() {
        return new ItemProcessor<Movie, Movie>() {  // 1
            @Override
            public Movie process(Movie movie) throws Exception {
                log.info("********** This is popularityMovieProcessor");
                movie.updatePopularity();
                return movie;  // 2
            }
        };
    }

    public ItemProcessor<Movie, Movie> movieApiProcessor() {
        return new ItemProcessor<Movie, Movie>() {  // 1
            @Override
            public Movie process(Movie movie) throws Exception {
                log.info("********** This is movieApiProcessor");
                movieApiService.updateThumbnailAndUrlByMovie(movie);
                movieApiService.updateActorsMovie(movie);
                movie.updateLastModifiedDate();
                return movie;  // 2
            }
        };
    }

    public ItemWriter<Member> popularityMemberWriter() {
        log.info("********** This is popularityMemberWriter");
        return (memberRepository::saveAll);
    }

    public ItemWriter<Post> popularityPostWriter() {
        log.info("********** This is popularityPostWriter");
        return (postRepository::saveAll);
    }

    public ItemWriter<Collection> popularityCollectionWriter() {
        log.info("********** This is popularityCollectionWriter");
        return (collectionRepository::saveAll);
    }

    public ItemWriter<Hashtag> popularityHashtagWriter() {
        log.info("********** This is popularityHashtagWriter");
        return (hashtagRepository::saveAll);
    }

    public ItemWriter<Movie> popularityMovieWriter() {
        log.info("********** This is popularityMovieWriter");
        return (movieRepository::saveAll);
    }

    public ItemWriter<Movie> movieApiWriter() {
        log.info("********** This is movieApiWriter");
        return (movieRepository::saveAll);
    }
}
