package com.nameless.spin_off.controller;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.dto.MemberDto;
import com.nameless.spin_off.dto.MemberDto.CreateMemberVO;
import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.collections.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.member.FollowedMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInCollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInPostRepository;
import com.nameless.spin_off.repository.member.FollowedMemberRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.comment.CommentInCollectionService;
import com.nameless.spin_off.service.comment.CommentInPostService;
import com.nameless.spin_off.service.hashtag.HashtagService;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.movie.MovieService;
import com.nameless.spin_off.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Profile("add")
@Component
@RequiredArgsConstructor
public class InitAddDummy {

    private final InitAddDummyService initAddDummyService;

    @PostConstruct
    public void init() throws Exception {
        initAddDummyService.init();
    }

    @Component
    @RequiredArgsConstructor
    static class InitAddDummyService {

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
        private final FollowedMemberRepository followedMemberRepository;
        private final MovieRepository movieRepository;
        private final MemberService memberService;
        private final MovieService movieService;
        private final HashtagService hashtagService;
        private final List<String> arr = List.of("듀크", "괴물", "두준", "브라", "세림", "슈슈", "엔젤", "재련", "바우", "부인", "파리", "강인", "사신", "벼리", "맨유", "설리", "잔상", "도덕", "하원", "조은", "산도", "온화", "잉어", "규현", "퐁듀", "새라", "습지", "개떡", "도쿄", "국어", "고의", "틀니", "표창", "체육", "강류", "로제", "젊은", "진동", "츄릅", "언니", "짬뽕", "하랑", "탱커", "칸나", "혜리", "혼혈", "피파", "효과", "지음", "클린", "영웅", "나은", "카머", "장관", "제제", "허각", "보나", "방탄", "카라", "카레", "에코", "인격", "임다", "포획", "유리", "희철", "신사", "리키", "수예", "청록", "연우", "신동", "존엄", "하마", "여제", "치킨", "오후", "보예", "남진", "찰스", "여리", "제넷", "통로", "부류", "온유", "에덴", "파산", "앨런", "웬디", "묘월", "행운", "체취", "카린", "코피", "횟수", "햇님", "베스", "푸른", "쿠팡", "제니", "엘사", "예인", "할배", "벌스", "코난", "목련", "소환", "오전", "표류", "헌혈", "양파", "연화", "지존", "로이", "혼자", "만두", "결정", "욕심", "엽서", "오늘", "레몬", "지우", "천령", "남이", "예의", "하프", "킬링", "전화", "명성", "헬렌", "이던", "모짜", "토핑", "수박", "훼손", "여유", "혜림", "사촌", "시완", "이든", "로비", "축하", "지애", "연맹", "카렌", "엘프", "할일", "캐시", "희라", "테드", "여성", "태닝", "찰빵", "서애", "제인", "트집", "판다", "행복", "미자", "팡야", "한복", "누나", "상큼", "나리", "백합", "연습", "멸공", "친구", "남자", "하월", "괴사", "레어", "보로", "저금", "내꼬", "직업", "르네", "수지", "수아", "정시", "축가", "픽사", "응축", "지아", "릴카", "평창", "트리", "진실", "야생", "세리", "얌얌", "이특", "준희", "월하", "가은", "꼼돌", "청소", "해솔", "미주", "기린", "창조", "오웬", "창민", "호프", "전격", "초코", "쑥떡", "염색", "다한", "빛나", "하루", "뽀또", "탁구", "추락", "사자", "연기", "예성", "아란", "라인", "황도", "케첩", "환수", "자의", "나인", "희원", "해인", "소림", "암살", "속옷", "신뢰", "종각", "카톡", "워낭", "티니", "치루", "진온", "형윤", "철구", "케이", "렉스", "꽃별", "자연", "사리", "루피", "홍대", "에고", "유성", "와인", "살자", "무주", "아영", "월광", "예우", "초록", "가화", "티아", "키즈", "앙팡", "렉시", "가희", "최대", "정육", "데비", "카모", "윌터", "타이", "해조", "격의", "칭찬", "파편", "사회", "생명", "언제", "타나", "정신", "사람", "출석", "팬티", "영광", "수영", "빅뱅", "아이", "월향", "환갑", "횃불", "린스", "매력", "동생", "율히", "진운", "비원", "나봄", "생화", "코트", "해루", "투견", "초아", "붉은", "축구", "여인", "케어", "연희", "쫑이", "바람", "지리", "코어", "핑크", "전세", "방랑", "쿠시", "소인", "루시", "우동", "창녕", "은솔", "셀리", "비룡", "초키", "슈가", "태산", "부유", "찬성", "코쿤", "입술", "탈리", "하율", "타로", "요정", "중심", "풍경", "라면", "다운", "루비", "김구", "기류", "캐쉬", "보람", "키위", "지연", "카드", "존경", "미라", "넬리", "작약", "아기", "포토", "퍼피", "제이", "표현", "셀프", "세화", "포텐", "월세", "코닥", "다람", "베티", "동토", "활력", "소드", "티나", "연두", "요청", "소희", "황후", "비키", "은한", "비인", "츄정", "수연", "라라", "튀김", "놀이", "윌리", "요구", "걸레", "비리", "소녀", "로지", "키스", "수학", "동준", "글자", "은지", "틀딱", "이류", "황해", "목향", "쵸파", "토모", "캔슬", "서울", "혐오", "절개", "레디", "점막", "기범", "화살", "톱니", "세찬", "침략", "늘솜", "행주", "종류", "도룡", "클럽", "제리", "유라", "한국", "나윤", "앙큼", "양팡", "캐슬", "변덕", "비수", "햄벅", "독존", "진리", "매우", "영어", "출입", "초롱", "패니", "은혁", "두나", "포켓", "성수", "화련", "라비", "명품", "웃음", "포항", "습기", "한자", "대전", "늑대", "벨라", "가루", "노래", "융합", "루나", "해목", "로사", "독도", "모아", "민호", "야외", "풍선", "신부", "롯데", "잠옷", "반지", "달님", "해적", "유이", "아크", "사랑", "새봄", "화월", "커피", "과실", "서약", "냥이", "은묘", "파탄", "혜윤", "씨엘", "꼬마", "다리", "울산", "타투", "동해", "한별", "내꺼", "수인", "딸기", "강화", "죄수", "풍란", "별님", "혜원", "메이", "숙녀", "언어", "포기", "합창", "참외", "피자", "비연", "고귀", "진주", "장미", "보겸", "화보", "표지", "자두", "다인", "망고", "평정", "인생", "시다", "나래", "값진", "나라", "헤라", "큐리", "증권", "키티", "수정", "베넷", "슈비", "방출", "아인", "도의", "카스", "튤립", "마사", "유럽", "캐롤", "회계", "채소", "무한", "판매", "태일", "유희", "부루", "후추", "다솜", "리인", "세라", "헤어", "율희", "족발", "우빈", "오빠", "인나", "고운", "신아", "코드", "리타", "필드", "코코", "키커", "홍조", "은총", "비행", "깜지", "츄희", "헌법", "포도", "플라", "진의", "악몽", "윤아", "정각", "코덱", "넬라", "혈청", "에반", "대구", "체리", "민아", "나비", "성민", "결혼", "천하", "조이", "소혜", "해남", "이루", "마샤", "천지", "시리", "예준", "박봄", "향목", "즉석", "베라", "제조", "유빈", "손글", "순결", "아라", "신념", "미녀", "노루", "매그", "벨리", "예은", "무적", "로또", "잔디", "대인", "하람", "통발", "레이", "제주", "려욱", "폴라", "헨리", "무루", "황제", "꿀벌", "여자", "어진", "지갑", "혜성", "타조", "퉁그리", "다롱", "사샤", "베릴", "여안", "횟집", "소피", "조루", "기쁨", "덴버", "오더", "자리", "엘지", "지율", "포옹", "비비", "강신", "애플", "페이", "자유", "이령", "채무", "소담", "벌레", "사과", "공작", "은빛", "치약", "한경", "출산", "승리", "재벌", "핑퐁", "수빈", "행성", "효민", "크롬", "잠수", "티끌", "의진", "빅맥", "우주", "야채", "튼튼", "탈론", "목화", "리사", "랄프", "니엘", "셀카", "현아", "할리", "도리", "시원", "무희", "백범", "참견", "지수", "조화", "보영", "세상", "노을", "맑은", "동아", "진이", "차키", "저항", "이브", "자몽", "칼날", "사냥", "운석", "이유", "허그", "가장", "가온", "엘런", "한나", "응답", "이웃", "주류", "기운", "출항", "기품", "다희", "상추", "유령", "까꿍", "리우", "비유", "저녁", "폐가", "야구", "하리", "태양", "물병", "세부", "지은", "은솜", "컨택", "피카", "애교", "동주", "지나", "첼시", "팬더", "형제", "초심", "농구", "청순", "악당", "낸시", "부산", "니콜", "누리", "제약", "화영", "봄이", "짜장", "애긔", "젤리", "비류", "키링", "선예", "임자", "가람", "창원", "벼루", "정상", "윈디", "푸시", "키움", "루인", "아서", "펭귄", "온도", "지식", "윽박", "세비", "월화", "나이", "제일", "주이", "뿅뿅", "저당", "제무", "활동", "화원", "고양", "한화", "치즈", "택연", "은정", "치프", "클릭", "월차", "미르", "포화", "음료", "존박", "써니", "태극", "우영", "우리", "소율", "동백", "링고", "수국", "킹콩", "별하", "통영", "조권", "소연", "최소", "물주", "김치", "피나", "다원", "석주", "초빈", "준형", "신의", "라온", "마눌", "베컴", "토토", "햇살", "티탄", "인원", "퇴직", "점프", "혜빈", "풍차", "바다");
        private final int randomSize = arr.size();
        private final int hashtagStrSize = randomSize / 8;
        private final int hashtagSize = hashtagStrSize * hashtagStrSize;

        @Transactional()
        public void init() throws Exception {

            List<CreateMemberVO> createMemberVOs = new ArrayList<>();
            List<Post> posts = new ArrayList<>();
            List<Collection> collections = new ArrayList<>();
            List<FollowedMember> followedMembers = new ArrayList<>();
            List<CommentInPost> commentInPosts = new ArrayList<>();
            List<CommentInCollection> commentInCollections = new ArrayList<>();
            List<CommentInPost> childCommentInPosts = new ArrayList<>();
            List<CommentInCollection> childCommentInCollections = new ArrayList<>();

            List<Hashtag> hashtags = hashtagRepository.findAll();
            List<Movie> movies = movieRepository.findAll();

            int movieSize = movies.size();

            //멤버 생성
            int i2 = (int) (Math.random() * 10);

            for (int i = 0; i < i2; i++) {
                List<Member> all = memberRepository.findAll();
                String accountId = arr.get((int) (Math.random() * randomSize)) + "" + arr.get((int) (Math.random() * randomSize)) + "" + arr.get((int) (Math.random() * randomSize));
                String accountPw = arr.get((int) (Math.random() * randomSize)) + "" + arr.get((int) (Math.random() * randomSize)) + "" + arr.get((int) (Math.random() * randomSize));
                String name = arr.get((int) (Math.random() * randomSize)) + "" + arr.get((int) (Math.random() * randomSize));
                String nickname = arr.get((int) (Math.random() * randomSize)) + "" + arr.get((int) (Math.random() * randomSize));
                String email = arr.get((int) (Math.random() * randomSize)) + arr.get((int) (Math.random() * randomSize)) + "@" + arr.get((int) (Math.random() * randomSize)) + ".com";
                if (all.stream().noneMatch(mem -> mem.getAccountId().equals(accountId) && mem.getNickname().equals(nickname)))
                    createMemberVOs.add(new CreateMemberVO(accountId, accountPw, name, nickname, LocalDate.now(), email, null));
            }

            for (CreateMemberVO createMemberVO : createMemberVOs) {
                memberService.insertMemberByMemberVO(createMemberVO);
            }
            List<Member> members = memberRepository.findAll();
            int memberSize = members.size();


            //글 생성
            int newPostNumber, newPostTitleLength, newPostHashtagNumber;
            int newCollectionNumber, newCollectionTitleLength;
            StringBuilder title, content;
            List<String> newHashtagContentList;

            for (Member member : members) {

                //컬렉션 생성
                newCollectionNumber = (int) (Math.random() * 6);
                newCollectionTitleLength = (int) (Math.random() * 2) + 2;

                for (int i = 0; i < newCollectionNumber; i++) {
                    title = new StringBuilder();
                    content = new StringBuilder();

                    for (int j = 0; j < newCollectionTitleLength; j++) {
                        title.append(arr.get((int) (Math.random() * randomSize))).append(" ");
                        content.append(arr.get((int) (Math.random() * randomSize))).append(" ");
                        content.append(arr.get((int) (Math.random() * randomSize))).append(" ");
                    }
                    title.deleteCharAt(title.length() - 1);
                    content.deleteCharAt(content.length() - 1);

                    collections.add(collectionRepository.getById(collectionService.insertCollectionByCollectionVO(new CollectionDto.CreateCollectionVO(
                            members.get((int)(Math.random() * members.size())).getId(),
                            title.toString(),
                            content.toString(),
                            PublicOfCollectionStatus.values()[(int) (Math.random() * PublicOfCollectionStatus.values().length)]))));
                }

                //글 생성
                newPostNumber = (int) (Math.random() * 6);
                newPostTitleLength = (int) (Math.random() * 2) + 3;
                newPostHashtagNumber = (int) (Math.random() * 16) + 15;

                for (int i = 0; i < newPostNumber; i++) {
                    title = new StringBuilder();
                    content = new StringBuilder();
                    newHashtagContentList = new ArrayList<>();

                    for (int j = 0; j < newPostTitleLength; j++) {
                        title.append(arr.get((int) (Math.random() * randomSize))).append(" ");
                        content.append(arr.get((int) (Math.random() * randomSize))).append(" ");
                        content.append(arr.get((int) (Math.random() * randomSize))).append(" ");
                    }
                    title.deleteCharAt(title.length() - 1);
                    content.deleteCharAt(content.length() - 1);

                    for (int j = 0; j < newPostHashtagNumber; j++) {
                        newHashtagContentList.add(arr.get((int) (Math.random() * hashtagStrSize)) + "_"+ arr.get((int) (Math.random() * hashtagStrSize)));
                    }
                    int dd = (int) (Math.random() * 100);
                    Long movieId = (long)(Math.random()*movieSize);
                    if (dd == 99) {
                        movieId = null;
                    }
                    posts.add(postRepository.getById(postService.insertPostByPostVO(new PostDto.CreatePostVO(member.getId(), title.toString(), content.toString(),
                            movieId, null,
                            PublicOfPostStatus.values()[(int) (Math.random() * PublicOfPostStatus.values().length)],
                            newHashtagContentList,
                            List.of(), List.of()))));
                }
            }

            int collectionSize = collections.size();
            int postSize = posts.size();
            int memberCollectionSize, newCollectedPostNumber, newParentCommentInPostNumber, newCommentContentLength,
                    newParentCommentInCollectionNumber;
            List<Collection> allByMember;

            for (Member member : members) {

                //멤버 팔로우
                int randomI = (int) (Math.random() * members.size() / 8);

                for (int i = 0; i < randomI; i++) {

                    int i1 = (int) (Math.random() * members.size());
                    while (members.get(i1).equals(member))
                        i1 = (int) (Math.random() * members.size());

                    Member byId = memberRepository.getById(member.getId());
                    Long id = members.get(i1).getId();
                    if (byId.getFollowedMembers().stream().noneMatch(followedMember -> followedMember.getMember().getId().equals(id))) {
                        memberService.insertFollowedMemberByMemberId(member.getId(), id);
                    }
                }

                //멤버 차단
                randomI = (int) (Math.random() * member.getFollowedMembers().size() / 16);

                for (int i = 0; i < randomI; i++) {

                    int i1 = (int) (Math.random() * members.size());
                    while (members.get(i1).equals(member))
                        i1 = (int) (Math.random() * members.size());

                    Member byId = memberRepository.getById(member.getId());
                    Member df = members.get(i1);
                    if (byId.getBlockedMembers().stream()
                            .noneMatch(blockedMember -> blockedMember.getMember().equals(df))) {
                        memberService.insertBlockedMemberByMemberId(member.getId(), df.getId(), BlockedMemberStatus.ALL);
                    }
                }

                //컬렉션 팔로우
                randomI = (int) (Math.random() * collections.size() / 8);

                for (int i = 0; i < randomI; i++) {

                    int i1 = (int) (Math.random() * collections.size());
                    while (collections.get(i1).getMember().equals(member))
                        i1 = (int) (Math.random() * collections.size());

                    List<Member> membersByFollowingMemberId = memberRepository.findAllByFollowingMemberId(member.getId());
                    if (collections.get(i1).getPublicOfCollectionStatus() == PublicOfCollectionStatus.PUBLIC ||
                            (collections.get(i1).getPublicOfCollectionStatus() == PublicOfCollectionStatus.FOLLOWER &&
                                    membersByFollowingMemberId.contains(collections.get(i1).getMember()))) {

                        Collection byId = collectionRepository.getById(collections.get(i1).getId());
                        if (byId.getFollowedCollections().stream()
                                .noneMatch(followedCollection -> followedCollection.getMember().equals(member))) {
                            collectionService.insertFollowedCollectionByMemberId(member.getId(), collections.get(i1).getId());
                        }
                    }
                }

                //영화 팔로우
                randomI = (int) (Math.random() * 3);

                for (int i = 0; i < randomI; i++) {
                    int i1 = (int) (Math.random() * movieSize);

                    Member byId = memberRepository.getById(member.getId());
                    if (byId.getFollowedMovies().stream()
                            .noneMatch(followedMovie -> followedMovie.getMovie().getId().equals(movies.get(i1).getId()))) {
                        movieService.insertFollowedMovieByMovieId(member.getId(), movies.get(i1).getId());
                    }
                }

                //해시태그 팔로우
                randomI = (int) (Math.random() * 10);

                for (int i = 0; i < randomI; i++) {
                    int i1 = (int) (Math.random() * hashtagSize);
                    Member byId = memberRepository.getById(member.getId());
                    if (byId.getFollowedHashtags().stream()
                            .noneMatch(followedHashtag -> followedHashtag.getHashtag().equals(hashtags.get(i1)))) {
                        hashtagService.insertFollowedHashtagByHashtagId(member.getId(), hashtags.get(i1).getId());
                    }
                }

                // 글 컬렉션 삽입
                allByMember = collectionRepository.findAllByMember(member);
                memberCollectionSize = allByMember.size();
                newCollectedPostNumber = (int) (Math.random() * 6 * memberCollectionSize);

                for (int i = 0; i < newCollectedPostNumber; i++) {

                    int postI = (int) (Math.random() * postSize);
                    int collectionI = (int) (Math.random() * memberCollectionSize);

                    if (posts.get(postI).getPublicOfPostStatus() == PublicOfPostStatus.PUBLIC ||
                            (posts.get(postI).getPublicOfPostStatus() == PublicOfPostStatus.FOLLOWER &&
                                    member.getFollowedMembers().stream().anyMatch(followedMember -> followedMember.getMember().equals(posts.get(postI).getMember())))) {

                        Collection byId = collectionRepository.getById(allByMember.get(collectionI).getId());
                        if (byId.getCollectedPosts().stream()
                                .noneMatch(collectedPost -> collectedPost.getPost() == posts.get(postI))) {
                            postService.insertCollectedPosts(member.getId(),
                                    posts.get(postI).getId(),
                                    List.of(allByMember.get(collectionI).getId()));
                        }
                    }
                }

                //글댓글 생성
                newParentCommentInPostNumber = (int) (Math.random() * postSize / 4);

                for (int i = 0; i < newParentCommentInPostNumber; i++) {
                    content = new StringBuilder();
                    newCommentContentLength = (int) (Math.random() * 2) + 2;

                    for (int j = 0; j < newCommentContentLength; j++) {
                        content.append(arr.get((int) (Math.random() * randomSize))).append(" ");
                    }
                    content.deleteCharAt(content.length() - 1);
                    int postI = (int) (Math.random() * postSize);

                    if (posts.get(postI).getPublicOfPostStatus() == PublicOfPostStatus.PUBLIC ||
                            (posts.get(postI).getPublicOfPostStatus() == PublicOfPostStatus.FOLLOWER &&
                                    member.getFollowedMembers().stream().anyMatch(followedMember -> followedMember.getMember().equals(posts.get(postI).getMember())))) {

                        commentInPosts.add(commentInPostRepository.getById(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(member.getId(),
                                posts.get(postI).getId(),
                                null, content.toString()))));
                    }
                }

                //컬렉션댓글 생성
                newParentCommentInCollectionNumber = (int) (Math.random() * collectionSize / 4);

                for (int i = 0; i < newParentCommentInCollectionNumber; i++) {

                    content = new StringBuilder();
                    newCommentContentLength = (int) (Math.random() * 2) + 2;

                    for (int j = 0; j < newCommentContentLength; j++) {
                        content.append(arr.get((int) (Math.random() * randomSize))).append(" ");
                    }
                    content.deleteCharAt(content.length() - 1);
                    int collectionI = (int) (Math.random() * collectionSize);

                    if (collections.get(collectionI).getPublicOfCollectionStatus() == PublicOfCollectionStatus.PUBLIC ||
                            (collections.get(collectionI).getPublicOfCollectionStatus() == PublicOfCollectionStatus.FOLLOWER &&
                                    member.getFollowedMembers().stream().anyMatch(followedMember -> followedMember.getMember().equals(collections.get(collectionI).getMember())))) {

                        commentInCollections.add(commentInCollectionRepository.getById(commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CreateCommentInCollectionVO(member.getId(),
                                collections.get(collectionI).getId(),
                                null, content.toString()))));
                    }
                }
            }

            int newChildCommentInPostNumber, newChildCommentInCollectionNumber;

            for (Member member : members) {

                //대댓글 글 생성
                newChildCommentInPostNumber = (int) (Math.random() * postSize / 8);

                for (int i = 0; i < newChildCommentInPostNumber; i++) {
                    content = new StringBuilder();
                    newCommentContentLength = (int) (Math.random() * 2) + 2;

                    for (int j = 0; j < newCommentContentLength; j++) {
                        content.append(arr.get((int) (Math.random() * randomSize))).append(" ");
                    }
                    content.deleteCharAt(content.length() - 1);

                    int postI = (int) (Math.random() * postSize);

                    if (posts.get(postI).getPublicOfPostStatus() == PublicOfPostStatus.PUBLIC ||
                            (posts.get(postI).getPublicOfPostStatus() == PublicOfPostStatus.FOLLOWER &&
                                    member.getFollowedMembers().stream().anyMatch(followedMember -> followedMember.getMember().equals(posts.get(postI).getMember())))) {

                        List<CommentInPost> parentsByPost = commentInPostRepository.findParentsByPost(posts.get(postI));

                        Long parentId = null;
                        if (!parentsByPost.isEmpty()) {
                            parentId = parentsByPost.get((int) (Math.random() * parentsByPost.size())).getId();
                        }
                        childCommentInPosts.add(commentInPostRepository.getById(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(member.getId(),
                                posts.get(postI).getId(),
                                parentId, content.toString()))));
                    }
                }

                //대댓글 컬렉션 생성
                newChildCommentInCollectionNumber = (int) (Math.random() * collectionSize / 8);

                for (int i = 0; i < newChildCommentInCollectionNumber; i++) {

                    content = new StringBuilder();
                    newCommentContentLength = (int) (Math.random() * 2) + 2;

                    for (int j = 0; j < newCommentContentLength; j++) {
                        content.append(arr.get((int) (Math.random() * randomSize))).append(" ");
                    }
                    content.deleteCharAt(content.length() - 1);
                    int collectionI = (int) (Math.random() * collectionSize);

                    if (collections.get(collectionI).getPublicOfCollectionStatus() == PublicOfCollectionStatus.PUBLIC ||
                            (collections.get(collectionI).getPublicOfCollectionStatus() == PublicOfCollectionStatus.FOLLOWER &&
                                    member.getFollowedMembers().stream().anyMatch(followedMember -> followedMember.getMember().equals(collections.get(collectionI).getMember())))) {

                        List<CommentInCollection> parentsByCollection =
                                commentInCollectionRepository.findParentsByCollection(collections.get(collectionI));

                        Long parentId = null;
                        if (!parentsByCollection.isEmpty()) {
                            parentId = parentsByCollection.get((int) (Math.random() * parentsByCollection.size())).getId();
                        }

                        childCommentInCollections.add(commentInCollectionRepository.getById(commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CreateCommentInCollectionVO(member.getId(),
                                collections.get(collectionI).getId(),
                                parentId, content.toString()))));
                    }
                }
            }
            commentInPosts.addAll(childCommentInPosts);
            commentInCollections.addAll(childCommentInCollections);
            int commentInPostSize = commentInPosts.size();
            int commentInCollectionSize = commentInCollections.size();
            int randomI;

            for (Member member : members) {

                //컬렉션 좋아요 생성
                randomI = (int) (Math.random() * collectionSize / 4);

                for (int i = 0; i < randomI; i++) {

                    int i1 = (int) (Math.random() * collectionSize);
                    if (collections.get(i1).getPublicOfCollectionStatus() == PublicOfCollectionStatus.PUBLIC ||
                            (collections.get(i1).getPublicOfCollectionStatus() == PublicOfCollectionStatus.FOLLOWER &&
                                    member.getFollowedMembers().stream().anyMatch(followedMember -> followedMember.getMember().equals(collections.get(i1).getMember())))) {
                        Collection byId = collectionRepository.getById(collections.get(i1).getId());
                        if (byId.getLikedCollections().stream()
                                .noneMatch(likedCollection -> likedCollection.getMember().equals(member))) {
                            collectionService.insertLikedCollectionByMemberId(member.getId(), collections.get(i1).getId());
                        }
                    }
                }

                //글 좋아요 생성
                randomI = (int) (Math.random() * postSize / 4);

                for (int i = 0; i < randomI; i++) {

                    int i1 = (int) (Math.random() * postSize);
                    if (posts.get(i1).getPublicOfPostStatus() == PublicOfPostStatus.PUBLIC ||
                            (posts.get(i1).getPublicOfPostStatus() == PublicOfPostStatus.FOLLOWER &&
                                    member.getFollowedMembers().stream().anyMatch(followedMember -> followedMember.getMember().equals(posts.get(i1).getMember())))) {

                        Post byId = postRepository.getById(posts.get(i1).getId());
                        if (byId.getLikedPosts().stream()
                                .noneMatch(likedPost -> likedPost.getMember().equals(member))) {
                            postService.insertLikedPostByMemberId(member.getId(), posts.get(i1).getId());
                        }
                    }
                }

                //글댓글 좋아요 생성
                randomI = (int) (Math.random() * commentInPostSize / 16);

                for (int i = 0; i < randomI; i++) {

                    int i1 = (int) (Math.random() * commentInPostSize);
                    if (commentInPosts.get(i1).getPost().getPublicOfPostStatus() == PublicOfPostStatus.PUBLIC ||
                            (commentInPosts.get(i1).getPost().getPublicOfPostStatus() == PublicOfPostStatus.FOLLOWER &&
                                    member.getFollowedMembers().stream().anyMatch(followedMember -> followedMember.getMember().equals(commentInPosts.get(i1).getPost().getMember())))) {

                        CommentInPost byId = commentInPostRepository.getById(commentInPosts.get(i1).getId());
                        if (byId.getLikedCommentInPosts().stream()
                                .noneMatch(likedCommentInPost -> likedCommentInPost.getMember().equals(member))) {
                            commentInPostService.insertLikedCommentByMemberId(member.getId(), commentInPosts.get(i1).getId());
                        }
                    }
                }

                //컬렉션댓글 좋아요 생성
                randomI = (int) (Math.random() * commentInCollectionSize / 16);

                for (int i = 0; i < randomI; i++) {

                    int i1 = (int) (Math.random() * commentInCollectionSize);
                    if (commentInCollections.get(i1).getCollection().getPublicOfCollectionStatus() == PublicOfCollectionStatus.PUBLIC ||
                            (commentInCollections.get(i1).getCollection().getPublicOfCollectionStatus() == PublicOfCollectionStatus.FOLLOWER &&
                                    member.getFollowedMembers().stream().anyMatch(followedMember -> followedMember.getMember().equals(commentInCollections.get(i1).getCollection().getMember())))) {

                        if (commentInCollections.get(i1).getLikedCommentInCollections().stream()
                                .noneMatch(likedCommentInCollection -> likedCommentInCollection.getMember().equals(member))) {
                            commentInCollectionService.insertLikedCommentByMemberId(member.getId(), commentInCollections.get(i1).getId());
                        }
                    }
                }

                //글 조회수 생성
                randomI = (int) (Math.random() * 150);

                for (int i = 0; i < randomI; i++) {
                    int postI = (int) (Math.random() * postSize);

                    if ((posts.get(postI).getPublicOfPostStatus() == PublicOfPostStatus.PRIVATE &&
                            member.equals(posts.get(postI).getMember())) ||
                            posts.get(postI).getPublicOfPostStatus() == PublicOfPostStatus.PUBLIC ||
                            (posts.get(postI).getPublicOfPostStatus() == PublicOfPostStatus.FOLLOWER &&
                                    member.getFollowedMembers().stream().anyMatch(followedMember -> followedMember.getMember().equals(posts.get(postI).getMember())))) {

                        postService.insertViewedPostByIp(""+(int)(Math.random() * 500 * memberSize), posts.get(postI).getId());
                    }
                }

                //컬렉션 조회수 생성
                for (int i = 0; i < randomI; i++) {
                    int collectionI = (int) (Math.random() * collectionSize);

                    if ((collections.get(collectionI).getPublicOfCollectionStatus() == PublicOfCollectionStatus.PRIVATE &&
                            member.equals(collections.get(collectionI).getMember())) ||
                            collections.get(collectionI).getPublicOfCollectionStatus() == PublicOfCollectionStatus.PUBLIC ||
                            (collections.get(collectionI).getPublicOfCollectionStatus() == PublicOfCollectionStatus.FOLLOWER &&
                                    member.getFollowedMembers().stream().anyMatch(followedMember -> followedMember.getMember().equals(collections.get(collectionI).getMember())))) {

                        collectionService.insertViewedCollectionByIp(""+(int)(Math.random() * 500 * memberSize),
                                collections.get(collectionI).getId());
                    }
                }

                //영화 조회수 생성
                for (int i = 0; i < randomI; i++) {
                    movieService.insertViewedMovieByIp(""+(int)(Math.random() * 500 * memberSize),
                            movies.get((int) (Math.random() * movieSize)).getId());
                }

                //해시태그 조회수 생성
                for (int i = 0; i < randomI; i++) {
                    hashtagService.insertViewedHashtagByIp(""+(int)(Math.random() * 500 * memberSize),
                            hashtags.get((int) (Math.random() * hashtagSize)).getId());
                }
            } //여기 감싸면됨
        }//여기가 전체
    }
}
