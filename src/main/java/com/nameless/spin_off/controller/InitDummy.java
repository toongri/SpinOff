package com.nameless.spin_off.controller;

import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.collections.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Hashtag;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;
import com.nameless.spin_off.repository.collections.CollectedPostRepository;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInCollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInPostRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.comment.CommentInCollectionService;
import com.nameless.spin_off.service.comment.CommentInPostService;
import com.nameless.spin_off.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Profile("dummy")
@Component
@RequiredArgsConstructor
public class InitDummy {

    private final InitDummyService initDummyService;

    @PostConstruct
    public void init() throws Exception {
        initDummyService.init();
    }

    @Component
    @RequiredArgsConstructor
    static class InitDummyService {

        private final PostRepository postRepository;
        private final MemberRepository memberRepository;
        private final CollectionRepository collectionRepository;
        private final HashtagRepository hashtagRepository;
        private final PostService postService;
        private final CollectionService collectionService;
        private final CommentInCollectionService commentInCollectionService;
        private final CommentInPostService commentInPostService;
        private final CommentInCollectionRepository commentInCollectionRepository;
        private final CommentInPostRepository commentInPostRepository;

        @Transactional
        public void init() throws Exception {

            List<Member> members = new ArrayList<>();

            members.add(Member.createMember("jitndk@gmail.com", "abcdefg",
                    "SooAh", "", "지수아", LocalDate.of(1998, 2, 26),
                    "010-3959-3232", "jitndk@gmail.com"));
            members.add(Member.createMember("fpxldmswl12@gmail.com", "abcdefg",
                    "onegoldsky", "", "이은지", LocalDate.of(1998, 11, 23),
                    "010-3959-3522", "fpxldmswl12@gmail.com"));
            members.add(Member.createMember("hyeongyungim7@gmail.com", "abcdefg",
                    "yun", "", "김형윤", LocalDate.of(1996, 9, 20),
                    "010-3959-1233", "hyeongyungim7@gmail.com"));
            members.add(Member.createMember("jhkim03284@gmail.com", "abcdefg",
                    "toongri", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-3959-5195", "jhkim03284@gmail.com"));
            members.add(Member.createMember("jhkimkkk0923@naver.com", "dfsdfd",
                    "퉁그리", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-2322-5195", "jhkimkkk0923@naver.com"));
            members.add(Member.createMember("jhkimkkk0923@naver.com", "abcdefg",
                    "투루투루퉁그리", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-3959-5195", "jhkim03284@gmail.com"));
            members.add(Member.createMember("jhkim03284@gmail.com", "abcdefg",
                    "t00ngri", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-3959-5195", "jhkim03284@gmail.com"));
            members.add(Member.createMember("jhkim03284@gmail.com", "dldld",
                    "투투투퉁그리", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-3959-23123", "jhkim03284@gmail.com"));
            members.add(Member.createMember("jhkim03284@gmail.com", "dkfjkd",
                    "투퉁그리", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-3959-23123", "jhkim03284@gmail.com"));
            members.add(Member.createMember("jhkim03284@gmail.com", "dfddd",
                    "퉁투퉁퉁퉁그리", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-3959-23123", "jhkim03284@gmail.com"));
            members.add(Member.createMember("jhkim03284@gmail.com", "ewhrtwjtr",
                    "투루투루퉁퉁그리", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-3959-23123", "jhkim03284@gmail.com"));
            members.add(Member.createMember("jhkim03284@gmail.com", "adfwfr",
                    "앵그리퉁그리", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-3959-23123", "jhkim03284@gmail.com"));
            members.add(Member.createMember("jhkim03284@gmail.com", "uikki7",
                    "쏘앵그리퉁그리", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-3959-23123", "jhkim03284@gmail.com"));
            members.add(Member.createMember("jhkim03284@gmail.com", "nrtyhyh",
                    "엉터리퉁그리", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-3959-23123", "jhkim03284@gmail.com"));
            members.add(Member.createMember("jhkim03284@gmail.com", "aetezwq",
                    "mc퉁그리", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-3959-23123", "jhkim03284@gmail.com"));
            members.add(Member.createMember("jhkim03284@gmail.com", "ghdrth",
                    "투두두두두퉁그리", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-3959-23123", "jhkim03284@gmail.com"));
            members.add(Member.createMember("jhkim03284@gmail.com", "gsgdfssg",
                    "퉁퉁퉁퉁그리", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-3959-23123", "jhkim03284@gmail.com"));
            members.add(Member.createMember("jhkim03284@gmail.com", "twtwru",
                    "퉁퉁그리", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-3959-23123", "jhkim03284@gmail.com"));
            members.add(Member.createMember("jhkim03284@gmail.com", "hjgjty",
                    "퉁기덕퉁그리", "", "김준형", LocalDate.of(1994, 9, 23),
                    "010-3959-23123", "jhkim03284@gmail.com"));

            memberRepository.saveAll(members);

            List<Collection> collections = new ArrayList<>();

            for (Member member : members) {
                collections.add(Collection.createDefaultCollection(member));
            }
            collections.add(Collection.createCollection(members.get(0), "감성쓰", "갬성스러운 컬렉션이에유", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(0), "spinoff레퍼런스", "개인레퍼런스", PublicOfCollectionStatus.PRIVATE));
            collections.add(Collection.createCollection(members.get(0), "졸작프로젝트", "졸작레퍼런스", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(1), "spinoff", "레퍼런스", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(1), "스트릿", "멋있는 옷", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(1), "영감", "내게 영감을 주는 것들", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(1), "독립영화", "독립영화 컬렉션", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(1), "상업영화", "상업영화 너무 달아~~~", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(2), "그거 진심이였어?", "이걸 드라이브를 진짜가네 ㄹㅇㅋㅋ", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(3), "퉁그리", "퉁그리 퉁그리 퉁그리 퉁그리", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(3), "퉁그리 퉁그리?", "퉁그리 퉁그리 퉁퉁그리", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(4), "test", "독립영화 컬렉션", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(5), "test", "상업영화 너무 달아~~~", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(6), "test?", "이걸 드라이브를 진짜가네 ㄹㅇㅋㅋ", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(7), "test?", "이걸 드라이브를 진짜가네 ㄹㅇㅋㅋ", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(8), "test", "독립영화 컬렉션", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(9), "test", "상업영화 너무 달아~~~", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(10), "test?", "이걸 드라이브를 진짜가네 ㄹㅇㅋㅋ", PublicOfCollectionStatus.PUBLIC));
            collections.add(Collection.createCollection(members.get(11), "test?", "이걸 드라이브를 진짜가네 ㄹㅇㅋㅋ", PublicOfCollectionStatus.PUBLIC));

            collectionRepository.saveAll(collections);

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

            hashtagRepository.saveAll(hashtags);
            List<Post> posts = new ArrayList<>();

            posts.add(Post.createPost(members.get(0), "갬성1", "갬성스러운 글", otoTstMeth(List.of("갬성2", "갬성3", "갬성4", "갬성5"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(0), "갬성2", "갬성스러운 글", otoTstMeth(List.of("갬성1", "갬성3", "갬성4", "갬성5"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(0), "갬성3", "갬성스러운 글", otoTstMeth(List.of("갬성1", "갬성2", "갬성3", "갬성4", "갬성5"), hashtags), new ArrayList<>(),null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(1), "트위터", "sns", otoTstMeth(List.of("sns", "sns2", "sns3", "sns4"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(1), "인스타", "sns", otoTstMeth(List.of("sns", "sns2", "sns3", "sns4"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(3), "스스므", "패션 커뮤니티", otoTstMeth(List.of("커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "NRG", "에스피오나지", "LMC", "노매뉴얼", "비슬로우", "프리즘웍스", "유니폼브릿지", "칼하트", "모드나인", "예스아이씨"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(3), "펨코", "축구 커뮤니티", otoTstMeth(List.of("커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "축구", "축구2", "축구3", "축구4"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(1), "텀블러", "블로그", otoTstMeth(List.of("커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(1), "네이버블로그", "블로그", otoTstMeth(List.of("커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(2), "영종도", "마시안 해변", otoTstMeth(List.of("맨유1", "맨유2", "스핀오프", "잘해보자", "꼭_서비스", "성공하길", "유지보수도", "되서_애들", "취직할_때", "제출할_수", "있기를", "인천", "마포", "영종도", "송도", "마시안_해변"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(3), "익무", "영화 커뮤니티", otoTstMeth(List.of("OTT1", "OTT2", "OTT3", "OTT4", "OTT5", "커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(3), "알싸", "아이러브싸커ㅠㅠ", otoTstMeth(List.of("커뮤니티2", "커뮤니티3", "커뮤니티5", "축구2", "축구3"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(3), "싸줄", "싸커라인ㅜㅠ", otoTstMeth(List.of("커뮤니티1", "커뮤니티3", "커뮤니티4", "커뮤니티5", "축구", "축구3", "축구4"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(2), "맹구따리맹구따", "맹강딱", otoTstMeth(List.of("맨유1", "맨유2", "첼시1", "첼시2", "축구", "축구2", "축구3", "축구4"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(3), "해충갤", "해충", otoTstMeth(List.of("커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "축구", "축구2", "축구3", "축구4"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(1), "스핀오프", "갬성 읏되는 영화 커뮤니티", otoTstMeth(List.of("스핀오프", "잘해보자", "꼭_서비스", "성공하길", "유지보수도", "되서_애들", "취직할_때", "제출할_수", "있기를"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(1), "왓챠", "영화 평점 사이트", otoTstMeth(List.of("OTT1", "OTT2", "OTT3", "OTT4", "OTT5", "플랫폼", "어플", "영화", "오픈소스"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(0), "넷플릭스", "여기 취직하고싶다", otoTstMeth(List.of("OTT1", "OTT2", "OTT3", "OTT4", "OTT5", "플랫폼", "어플", "영화", "오픈소스"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(3), "첼시", "축구 개킹받게 잘함", otoTstMeth(List.of("축구", "축구2", "축구3", "축구4", "맨유1", "맨유2", "첼시1", "첼시2"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(0), "디즈니 플러스", "최대 스케일의 IP", otoTstMeth(List.of("OTT1", "OTT2", "OTT3", "OTT4", "OTT5", "플랫폼", "어플", "영화", "오픈소스", "스파이더맨", "아이언맨", "닥터_스트레인지", "캡틴아메리카"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(2), "스파이더맨3", "애새끼맨 어깨 실화냐?", otoTstMeth(List.of("플랫폼", "어플", "영화", "오픈소스", "스파이더맨", "아이언맨", "닥터_스트레인지", "캡틴아메리카"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(3), "MUSINSA", "여기에 대체 얼마를 꼴아박은거냐?", otoTstMeth(List.of("베이프", "스투시", "앵글런", "니들스", "아디다스", "슈퍼스타", "NRG", "에스피오나지", "LMC", "노매뉴얼", "비슬로우", "프리즘웍스", "유니폼브릿지", "칼하트", "모드나인", "예스아이씨", "아웃스탠딩", "알파인더스트리", "세븐셀라"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(3), "KREAM", "여기에 대체 얼마를 꼴아박은거냐?2", otoTstMeth(List.of("플랫폼", "어플", "신발", "조던하이", "에어포스", "데이브레이크", "캐치볼", "반스", "퓨마", "올드스쿨", "반스어센틱", "에어맥스", "덩크로우", "베이프", "스투시"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(1), "CULT", "블랙앤화이트 깔끔의 정석 최고의 레퍼런스", otoTstMeth(List.of("플랫폼", "어플", "베이프", "스투시", "앵글런", "니들스", "아디다스", "슈퍼스타", "NRG", "LMC", "노매뉴얼", "비슬로우", "칼하트", "모드나인", "예스아이씨", "커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(1), "현대카드 DIVE", "UI가 진짜 이쁘고 매끄러움 최고의 레퍼런스2", otoTstMeth(List.of("코딩", "개발", "취업", "네카라쿠베", "플랫폼", "어플", "영화", "오픈소스", "갬성1", "갬성2", "갬성3", "갬성4", "갬성5", "sns", "sns2", "sns3", "sns4", "커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "커뮤니티6"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(1), "텍스쳐", "힙스럽지 않지만 차분한 UI", otoTstMeth(List.of("코딩", "개발", "취업", "네카라쿠베", "플랫폼", "어플", "영화", "오픈소스", "갬성1", "갬성2", "갬성3", "갬성4", "갬성5", "커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "커뮤니티6"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(3), "ADIDAS", "붐디다스 짱은 온다", otoTstMeth(List.of("신발", "조던하이", "에어포스", "데이브레이크", "캐치볼", "반스", "퓨마", "올드스쿨", "반스어센틱", "에어맥스", "덩크로우", "베이프", "스투시", "앵글런"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(1), "브런치", "작가의 장벽을 줄여준다는 의미에서 비슷한 장르", otoTstMeth(List.of("코딩", "개발", "취업", "네카라쿠베", "플랫폼", "어플", "영화", "오픈소스", "갬성1", "갬성2", "갬성3", "갬성4", "갬성5", "커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "커뮤니티6"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(2), "아케인", "내가 본 애니 중 역대급 압도적 최고", otoTstMeth(List.of("영화", "신지드", "징크스", "바이", "에코", "워윅", "그레이브즈", "제이스", "빅토르", "하이머딩거", "바루스", "럭스", "가렌"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(1), "왕좌의 게임", "용이 망쳐버린 비운의 명작", otoTstMeth(List.of("갬성1", "갬성2", "갬성3", "갬성4", "갬성5", "커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "커뮤니티6", "영화"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(1), "티빙", "과연 국내 OTT서비스는 생존할 수 있을것인가", otoTstMeth(List.of("OTT1", "OTT2", "OTT3", "OTT4", "OTT5", "플랫폼", "어플", "영화", "오픈소스"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(3), "토스", "피드백이 일상인 회사. 너무 가고싶다", otoTstMeth(List.of("코딩", "개발", "취업", "네카라쿠베", "플랫폼", "어플", "영화", "오픈소스", "커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "커뮤니티6"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(0), "네이버", "가고싶다. 간지나자나", otoTstMeth(List.of("코딩", "개발", "취업", "네카라쿠베", "플랫폼", "어플", "영화", "오픈소스", "커뮤니티1", "커뮤니티2", "커뮤니티3", "커뮤니티4", "커뮤니티5", "커뮤니티6"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(0), "FIGMA", "기능 개쩜. 좋은 협업 프로그램", otoTstMeth(List.of("코딩", "개발", "취업", "네카라쿠베", "플랫폼", "어플"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(1), "pinterest", "힙한 플랫폼", otoTstMeth(List.of("코딩", "개발", "취업", "네카라쿠베", "플랫폼", "어플"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));
            posts.add(Post.createPost(members.get(1), "스타트업", "솔직히 사수만 좋다고 보장해주면 대기업보다 여길 가고 싶어", otoTstMeth(List.of("코딩", "개발", "취업", "네카라쿠베", "플랫폼", "어플"), hashtags), new ArrayList<>(), null, PublicOfPostStatus.PUBLIC));

            int hashNum = hashtags.size();
            int mnum = members.size();
            for (int i = 0; i < 80; i++) {
                int memNum = (int)(Math.random() * mnum);
                List<Hashtag> arr = new ArrayList<>();
                for (int j = 0; j < 7; j++) {
                    arr.add(hashtags.get((int)(Math.random() * hashNum)));
                }
                posts.add(Post.createPost(members.get(memNum), ""+i, ""+i, arr, List.of(), null, PublicOfPostStatus.PUBLIC));
            }

            postRepository.saveAll(posts);

            int max, mathNum, memberNum, postNum, collectionNum, parentNum;
            Long memberId, postId, collectionId, parentId;

            max = collections.size() * posts.size();
            int memberSize = members.size();
            int postSize = posts.size();
            int collectionSize = collections.size();

            for (int j = 0; j < max; j++) {
                mathNum = (int) (Math.random() * 10);
                memberNum = (int) (Math.random() * memberSize);
                postNum = (int) (Math.random() * postSize);
                collectionNum = (int) (Math.random() * collectionSize);
                memberId = members.get(memberNum).getId();
                collectionId = collections.get(collectionNum).getId();

                try {
                    if (mathNum <= 5) {
                        collectionService.insertViewedCollectionByIp("" + (int) (Math.random() * 100000), collectionId, LocalDateTime.now(), 0L);
                    } else if (mathNum == 6) {
                        collectionService.insertFollowedCollectionByMemberId(memberId, collectionId);
                    } else if (mathNum == 7) {
                        collectionService.insertLikedCollectionByMemberId(memberId, collectionId);
                    } else if (mathNum < 9){
                        commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CreateCommentInCollectionVO(memberId, collectionId, null, ""));
                    } else if (mathNum >= 9){
                        Collection collection = collectionRepository.findById(collectionId).get();
                        List<CommentInCollection> comment = commentInCollectionRepository.findParentsByCollectionId(collection);
                        int size = comment.size();
                        if (size == 0) {
                            commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CreateCommentInCollectionVO(memberId, collectionId, null, ""));
                        } else {
                            parentNum = (int) (Math.random() * size);

                            parentId = comment.get(parentNum).getId();
                            commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CreateCommentInCollectionVO(memberId, collectionId, parentId, ""));
                        }
                    }

                } catch (Exception e) {
                    Collection collection = collectionRepository.findById(collectionId).get();
                    List<CommentInCollection> comment = commentInCollectionRepository.findParentsByCollectionId(collection);
                    int size = comment.size();
                    if (size == 0) {
                        commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CreateCommentInCollectionVO(memberId, collectionId, null, ""));
                    }else {
                        parentNum = (int) (Math.random() * size);

                        parentId = comment.get(parentNum).getId();
                        commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CreateCommentInCollectionVO(memberId, collectionId, parentId, ""));
                    }
                }

            }

            max = members.size() * posts.size() * 2;
            for (int j = 0; j < max; j++) {
                mathNum = (int) (Math.random() * 11);
                memberNum = (int) (Math.random() * memberSize);
                postNum = (int) (Math.random() * postSize);
                collectionNum = (int) (Math.random() * collectionSize);
                memberId = members.get(memberNum).getId();
                postId = posts.get(postNum).getId();
                collectionId = collections.get(collectionNum).getId();

                try {
                    if (mathNum == 0) {
                        postService.insertLikedPostByMemberId(memberId, postId);
                    } else if (mathNum <= 5) {
                        postService.insertViewedPostByIp("" + (int) (Math.random() * 100000), postId, LocalDateTime.now(), 0L);
                    } else if (mathNum <= 7) {
                        Collection collection = collectionRepository.findById(collectionId).get();
                        collectionService.insertCollectedPosts(collection.getMember().getId(), postId, List.of(collectionId));
                    } else if (mathNum < 10) {
                        commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(memberId, postId, null, ""));
                    } else if (mathNum >= 10) {
                        Post post = postRepository.findById(postId).get();
                        List<CommentInPost> comment = commentInPostRepository.findParentsByPost(post);
                        int size = comment.size();
                        if (size == 0) {
                            commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(memberId, postId, null, ""));
                        } else {
                            parentNum = (int) (Math.random() * size);

                            parentId = comment.get(parentNum).getId();
                            commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(memberId, postId, parentId, ""));
                        }
                    }
                } catch (Exception e) {
                    Post post = postRepository.findById(postId).get();
                    List<CommentInPost> comment = commentInPostRepository.findParentsByPost(post);
                    int size = comment.size();
                    if (size == 0) {
                        commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(memberId, postId, null, ""));
                    } else {
                        parentNum = (int) (Math.random() * size);

                        parentId = comment.get(parentNum).getId();
                        commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(memberId, postId, parentId, ""));
                    }
                }

            }
        }

        private List<Hashtag> otoTstMeth(List<String> strings, List<Hashtag> hashtags) {
            return hashtags.stream().filter(hashtag -> strings.contains(hashtag.getContent())).collect(Collectors.toList());
        }
    }
}