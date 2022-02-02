package com.nameless.spin_off.controller;

import com.nameless.spin_off.entity.collections.CollectedPost;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.collections.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Hashtag;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PostedHashtag;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;
import com.nameless.spin_off.repository.collections.CollectedPostRepository;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Profile("test")
@Component
@RequiredArgsConstructor
public class InitTest {

    private final InitTestService initTestService;

    @PostConstruct
    public void init() {
        initTestService.init();
    }

    @Component
    @RequiredArgsConstructor
    static class InitTestService {

        private final PostRepository postRepository;
        private final MemberRepository memberRepository;
        private final CollectionRepository collectionRepository;
        private final CollectedPostRepository collectedPostRepository;
        private final HashtagRepository hashtagRepository;
        private final PostService postService;

        @Transactional
        public void init() {
            Member member1 = Member.createMember("jitndk@gmail.com", "abcdefg",
                    "SooAh", "", "지수아", LocalDate.of(1998, 2, 26),
                    "010-3959-3232", "jitndk@gmail.com");
            Member member2 = Member.createMember("fpxldmswl12@gmail.com", "abcdefg",
                    "onegoldsky", "", "이은지", LocalDate.of(1998, 11, 23),
                    "010-3959-3522", "fpxldmswl12@gmail.com");
            Member member3 = Member.createMember("hyeongyungim7@gmail.com", "abcdefg",
                    "yun", "", "김형윤", LocalDate.of(1996, 9, 20),
                    "010-3959-1233", "hyeongyungim7@gmail.com");
            Member member4 = Member.createMember("jhkim03284@gmail.com", "abcdefg",
                    "toongri", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-3959-5195", "jhkim03284@gmail.com");

            memberRepository.save(member1);
            memberRepository.save(member2);
            memberRepository.save(member3);
            memberRepository.save(member4);

            List<Collection> collections = new ArrayList<>();
            Collection collection1 = Collection.createCollection(member1, "감성쓰", "갬성스러운 컬렉션이에유", PublicOfCollectionStatus.PUBLIC);
            Collection collection2 = Collection.createCollection(member1, "spinoff레퍼런스", "개인레퍼런스", PublicOfCollectionStatus.PRIVATE);
            Collection collection3 = Collection.createCollection(member1, "졸작프로젝트", "졸작레퍼런스", PublicOfCollectionStatus.PUBLIC);
            Collection collection4 = Collection.createCollection(member2, "spinoff", "레퍼런스", PublicOfCollectionStatus.PUBLIC);
            Collection collection5 = Collection.createCollection(member2, "스트릿", "멋있는 옷", PublicOfCollectionStatus.PUBLIC);
            Collection collection6 = Collection.createCollection(member2, "영감", "내게 영감을 주는 것들", PublicOfCollectionStatus.PUBLIC);
            Collection collection7 = Collection.createCollection(member2, "독립영화", "독립영화 컬렉션", PublicOfCollectionStatus.PUBLIC);
            Collection collection8 = Collection.createCollection(member2, "상업영화", "상업영화 너무 달아~~~", PublicOfCollectionStatus.PUBLIC);
            Collection collection9 = Collection.createCollection(member3, "그거 진심이였어?", "이걸 드라이브를 진짜가네 ㄹㅇㅋㅋ", PublicOfCollectionStatus.PUBLIC);

            collectionRepository.save(collection1);
            collectionRepository.save(collection2);
            collectionRepository.save(collection3);
            collectionRepository.save(collection4);
            collectionRepository.save(collection5);
            collectionRepository.save(collection6);
            collectionRepository.save(collection7);
            collectionRepository.save(collection8);
            collectionRepository.save(collection9);

            List<String> strings = List.of("갬성1", "갬성2", "갬성3", "갬성4", "갬성5", "sns", "sns2", "sns3", "sns4", "커뮤니티1",
                    "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "커뮤니티6", "축구", "축구2", "축구3", "축구4",
                    "맨유1", "맨유2", "첼시1", "첼시2", "스핀오프", "잘해보자", "꼭_서비스", "성공하길", "유지보수도",
                    "되서_애들", "취직할_때", "제출할_수", "있기를", "OTT1", "OTT2", "OTT3", "OTT4", "OTT5",
                    "인천", "마포", "영종도", "송도", "마시안_해변", "신발", "조던하이", "에어포스", "데이브레이크", "캐치볼",
                    "반스", "퓨마", "올드스쿨", "반스어센틱", "에어맥스", "덩크로우", "베이프", "스투시", "앵글런", "니들스",
                    "아디다스", "슈퍼스타", "NRG", "에스피오나지", "LMC", "노매뉴얼", "비슬로우", "프리즘웍스", "유니폼브릿지",
                    "칼하트", "모드나인", "예스아이씨", "아웃스탠딩", "알파인더스트리", "세븐셀라", "현대", "신지드", "징크스",
                    "바이", "에코", "워윅", "그레이브즈", "제이스", "빅토르", "하이머딩거", "바루스", "럭스", "가렌", "코딩",
                    "개발", "취업", "네카라쿠베", "플랫폼", "어플", "영화", "오픈소스", "스파이더맨", "아이언맨",
                    "닥터_스트레인지", "캡틴아메리카");

            List<Hashtag> hashtags = strings.stream().map(Hashtag::createHashtag).collect(Collectors.toList());

            List<PostedHashtag> postedHashtags = new ArrayList<>();

            for (Hashtag hashtag : hashtags) {
                PostedHashtag postedHashtag = PostedHashtag.createPostedHashtag(hashtag);
                hashtagRepository.save(hashtag);
                postedHashtags.add(postedHashtag);
            }
            List<Post> posts = new ArrayList<>();

            posts.add(Post.createPost(member1, "갬성1", "갬성스러운 글", otoTstMeth(List.of("갬성2", "갬성3", "갬성4", "갬성5"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member1, "갬성2", "갬성스러운 글", otoTstMeth(List.of("갬성1", "갬성3", "갬성4", "갬성5"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member1, "갬성3", "갬성스러운 글", otoTstMeth(List.of("갬성1", "갬성2", "갬성3", "갬성4", "갬성5"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member2, "트위터", "sns", otoTstMeth(List.of("sns", "sns2", "sns3", "sns4"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member2, "인스타", "sns", otoTstMeth(List.of("sns", "sns2", "sns3", "sns4"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member4, "스스므", "패션 커뮤니티", otoTstMeth(List.of("커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "NRG", "에스피오나지", "LMC", "노매뉴얼", "비슬로우", "프리즘웍스", "유니폼브릿지", "칼하트", "모드나인", "예스아이씨"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member4, "펨코", "축구 커뮤니티", otoTstMeth(List.of("커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "축구", "축구2", "축구3", "축구4"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member2, "텀블러", "블로그", otoTstMeth(List.of("커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member2, "네이버블로그", "블로그", otoTstMeth(List.of("커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member3, "영종도", "마시안 해변", otoTstMeth(List.of("맨유1", "맨유2", "스핀오프", "잘해보자", "꼭_서비스", "성공하길", "유지보수도", "되서_애들", "취직할_때", "제출할_수", "있기를", "인천", "마포", "영종도", "송도", "마시안_해변"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member4, "익무", "영화 커뮤니티", otoTstMeth(List.of("OTT1", "OTT2", "OTT3", "OTT4", "OTT5", "커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member4, "알싸", "아이러브싸커ㅠㅠ", otoTstMeth(List.of("커뮤니티2", "커뮤니티3", "커뮤니티5", "축구2", "축구3"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member4, "싸줄", "싸커라인ㅜㅠ", otoTstMeth(List.of("커뮤니티1", "커뮤니티3", "커뮤니티4", "커뮤니티5", "축구", "축구3", "축구4"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member3, "맹구따리맹구따", "맹강딱", otoTstMeth(List.of("맨유1", "맨유2", "첼시1", "첼시2", "축구", "축구2", "축구3", "축구4"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member4, "해충갤", "해충", otoTstMeth(List.of("커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "축구", "축구2", "축구3", "축구4"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member2, "스핀오프", "갬성 읏되는 영화 커뮤니티", otoTstMeth(List.of("스핀오프", "잘해보자", "꼭_서비스", "성공하길", "유지보수도", "되서_애들", "취직할_때", "제출할_수", "있기를"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member2, "왓챠", "영화 평점 사이트", otoTstMeth(List.of("OTT1", "OTT2", "OTT3", "OTT4", "OTT5", "플랫폼", "어플", "영화", "오픈소스"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member1, "넷플릭스", "여기 취직하고싶다", otoTstMeth(List.of("OTT1", "OTT2", "OTT3", "OTT4", "OTT5", "플랫폼", "어플", "영화", "오픈소스"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member4, "첼시", "축구 개킹받게 잘함", otoTstMeth(List.of("축구", "축구2", "축구3", "축구4", "맨유1", "맨유2", "첼시1", "첼시2"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member1, "디즈니 플러스", "최대 스케일의 IP", otoTstMeth(List.of("OTT1", "OTT2", "OTT3", "OTT4", "OTT5", "플랫폼", "어플", "영화", "오픈소스", "스파이더맨", "아이언맨", "닥터_스트레인지", "캡틴아메리카"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member3, "스파이더맨3", "애새끼맨 어깨 실화냐?", otoTstMeth(List.of("플랫폼", "어플", "영화", "오픈소스", "스파이더맨", "아이언맨", "닥터_스트레인지", "캡틴아메리카"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member4, "MUSINSA", "여기에 대체 얼마를 꼴아박은거냐?", otoTstMeth(List.of("베이프", "스투시", "앵글런", "니들스", "아디다스", "슈퍼스타", "NRG", "에스피오나지", "LMC", "노매뉴얼", "비슬로우", "프리즘웍스", "유니폼브릿지", "칼하트", "모드나인", "예스아이씨", "아웃스탠딩", "알파인더스트리", "세븐셀라"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member4, "KREAM", "여기에 대체 얼마를 꼴아박은거냐?2", otoTstMeth(List.of("플랫폼", "어플", "신발", "조던하이", "에어포스", "데이브레이크", "캐치볼", "반스", "퓨마", "올드스쿨", "반스어센틱", "에어맥스", "덩크로우", "베이프", "스투시"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member2, "CULT", "블랙앤화이트 깔끔의 정석 최고의 레퍼런스", otoTstMeth(List.of("플랫폼", "어플", "베이프", "스투시", "앵글런", "니들스", "아디다스", "슈퍼스타", "NRG", "LMC", "노매뉴얼", "비슬로우", "칼하트", "모드나인", "예스아이씨", "커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member2, "현대카드 DIVE", "UI가 진짜 이쁘고 매끄러움 최고의 레퍼런스2", otoTstMeth(List.of("코딩", "개발", "취업", "네카라쿠베", "플랫폼", "어플", "영화", "오픈소스", "갬성1", "갬성2", "갬성3", "갬성4", "갬성5", "sns", "sns2", "sns3", "sns4", "커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "커뮤니티6"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member2, "텍스쳐", "힙스럽지 않지만 차분한 UI", otoTstMeth(List.of("코딩", "개발", "취업", "네카라쿠베", "플랫폼", "어플", "영화", "오픈소스", "갬성1", "갬성2", "갬성3", "갬성4", "갬성5", "커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "커뮤니티6"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member4, "ADIDAS", "붐디다스 짱은 온다", otoTstMeth(List.of("신발", "조던하이", "에어포스", "데이브레이크", "캐치볼", "반스", "퓨마", "올드스쿨", "반스어센틱", "에어맥스", "덩크로우", "베이프", "스투시", "앵글런"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member2, "브런치", "작가의 장벽을 줄여준다는 의미에서 비슷한 장르", otoTstMeth(List.of("코딩", "개발", "취업", "네카라쿠베", "플랫폼", "어플", "영화", "오픈소스", "갬성1", "갬성2", "갬성3", "갬성4", "갬성5", "커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "커뮤니티6"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member3, "아케인", "내가 본 애니 중 역대급 압도적 최고", otoTstMeth(List.of("영화", "신지드", "징크스", "바이", "에코", "워윅", "그레이브즈", "제이스", "빅토르", "하이머딩거", "바루스", "럭스", "가렌"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member2, "왕좌의 게임", "용이 망쳐버린 비운의 명작", otoTstMeth(List.of("갬성1", "갬성2", "갬성3", "갬성4", "갬성5", "커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "커뮤니티6", "영화"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member2, "티빙", "과연 국내 OTT서비스는 생존할 수 있을것인가", otoTstMeth(List.of("OTT1", "OTT2", "OTT3", "OTT4", "OTT5", "플랫폼", "어플", "영화", "오픈소스"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member4, "토스", "피드백이 일상인 회사. 너무 가고싶다", otoTstMeth(List.of("코딩", "개발", "취업", "네카라쿠베", "플랫폼", "어플", "영화", "오픈소스", "커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "커뮤니티6"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member1, "네이버", "가고싶다. 간지나자나", otoTstMeth(List.of("코딩", "개발", "취업", "네카라쿠베", "플랫폼", "어플", "영화", "오픈소스", "커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "커뮤니티6"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member1, "FIGMA", "기능 개쩜. 좋은 협업 프로그램", otoTstMeth(List.of("코딩", "개발", "취업", "네카라쿠베", "플랫폼", "어플"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member4, "pinterest", "힙한 플랫폼", otoTstMeth(List.of("코딩", "개발", "취업", "네카라쿠베", "플랫폼", "어플"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(member4, "스타트업", "솔직히 사수만 좋다고 보장해주면 대기업보다 여길 가고 싶어", otoTstMeth(List.of("코딩", "개발", "취업", "네카라쿠베", "플랫폼", "어플"), postedHashtags), new ArrayList<>(), new ArrayList<>(), PublicOfPostStatus.PUBLIC));

            postRepository.saveAll(posts);

            List<CollectedPost> collectedPosts = new ArrayList<>();

            for (Post post : posts) {
                collectedPosts.add(CollectedPost.createCollectedPosts(post));
            }

            for (int i : List.of(1, 3, 4)) {

            }
            collection1.addCollectedPost(collectedPosts.get(2));
        }

        private List<PostedHashtag> otoTstMeth(List<String> strings, List<PostedHashtag> postedHashtags) {
            return postedHashtags.stream().filter(postedHashtag -> strings.contains(postedHashtag.getHashtag().getContent())).collect(Collectors.toList());
        }
    }
}