package com.nameless.spin_off.controller;

import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.collections.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.member.FollowedMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInCollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInPostRepository;
import com.nameless.spin_off.repository.member.FollowedMemberRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.comment.CommentInCollectionService;
import com.nameless.spin_off.service.comment.CommentInPostService;
import com.nameless.spin_off.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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
        private final List<String> arr = List.of("박봄", "씨엘", "이루", "승리", "태양", "빅뱅", "써니", "윤아", "수영", "허각", "존박", "상추", "이특", "희철", "동해", "한경", "예성", "강인", "신동", "소율", "성민", "은혁", "혜리", "민아", "유라", "시원", "려욱", "기범", "규현", "예은", "소희", "선예", "혜림", "유빈", "지연", "효민", "보람", "은정화영", "큐리", "소연", "아라", "수지", "지아", "페이", "보람", "우영", "택연", "찬성", "소연", "루나", "설리", "은혁", "은정", "유리", "화영", "선예", "온유", "민호", "비키", "세리", "지율", "아영", "가은", "수빈", "진온이유", "예준", "니엘", "창조", "천지", "니콜", "카라", "진운", "조권", "창민", "시완", "동준", "유이", "혜빈", "연우", "주이", "제인", "나윤", "아인", "낸시", "로제", "지수", "제니", "리사", "레어", "입술", "글자", "자두", "사과", "레몬", "소환", "포도", "딸기", "꼬마", "소녀", "애교", "매력", "꼼돌", "사랑", "내꺼", "뿅뿅", "마눌", "신부", "언니내꼬", "아기", "애긔", "쫑이", "인나", "햇님", "달님", "별님", "까꿍", "부인", "행복냥이", "앙큼", "상큼", "우리", "봄이", "손글", "데비", "방출", "사신", "튀김", "우동", "라면", "짬뽕", "짜장", "카레", "가람", "가루", "김치", "기류", "김구", "강신", "강류", "강화", "재련", "백범", "사자", "늑대", "할일", "누나", "언니", "오빠", "동생", "사촌", "바람", "부유", "반지", "웃음", "빛나", "붉은", "푸른", "명성", "리우", "도쿄", "덴버", "랄프", "라비", "렉시", "퍼피", "렉스", "리키", "태일", "철구", "임다", "물주", "링고", "망고", "자몽", "수박", "로비", "로사", "로이", "지우", "릴카", "로지", "해인", "소혜", "결정", "수정", "루비", "셀리", "사샤", "사리", "슈가", "셀프", "셀카", "소피", "방탄", "탈리", "테드", "티나", "티니", "토모", "베라", "비류", "비비", "윌터", "웬디", "윌리", "윈디", "듀크", "생명", "진실", "잔상", "영웅", "성수", "방랑", "공작", "에코", "에덴", "엘프", "요정", "여성", "여자", "남자", "사냥", "고귀", "메이", "엘사", "엘런", "앨런", "벨라", "베티", "베릴", "찰스", "캐시", "서약", "신의", "신뢰", "행운", "다운", "순결", "사람", "캐쉬", "이던", "이든", "현아", "한나", "패니", "자유", "은총", "야생", "생화", "전격", "헬렌", "헨리", "가장", "순결", "카렌", "키티", "결혼", "비행", "카모", "카머", "신사", "숙녀", "루시", "매그", "넬리", "마사", "마샤", "진주", "바다", "영어", "초심", "수학", "정육", "연습", "노래", "습기", "습지", "혜원", "루인", "넬라", "넬리", "오웬", "플라", "루피", "레이", "폴라", "젊은", "횃불", "멸공", "암살", "절개", "기쁨", "진실", "백합", "튤립", "풍차", "물병", "커피", "오더", "우주", "할리", "리타", "티나", "제이", "세라", "새라", "티아", "조이", "베컴", "아서", "베스", "벨리", "행운", "친구", "제넷", "베넷", "신아", "여인", "여안", "소인", "대인", "나은", "세부", "세비", "온화", "아이", "지나", "화원", "하원", "희원", "미녀", "기품", "고의", "세화", "인원", "활력", "매우", "어진", "의진", "비원", "이령", "초빈", "화월", "월화", "초아", "나은", "지율", "천령", "세림", "동주", "서애", "월향", "월하", "하월", "하율", "은한", "은묘", "은빛", "고양", "천하", "제일", "무적", "독존", "은묘", "묘월", "비인", "기운", "가희", "가화", "비유", "월광", "화련", "연화", "이류", "푸른", "연희", "준희", "맑은", "동백", "작약", "장미", "수국", "쑥떡", "사리", "변덕", "평정", "석주", "목향", "목화", "향목", "수연", "칸나", "존경", "청순", "판다", "팬더", "초롱", "목련", "괴사", "과실", "괴물", "풍란", "신념", "청소", "영광", "우빈", "보영", "만두", "임자", "즉석", "제조", "음료", "시다", "별하", "아란", "벼리", "에반", "아크", "르네", "하람", "나리", "벼리", "벼루", "나봄", "새봄", "수아", "수예", "수인", "온도", "연두", "가온", "누리", "가람", "나인", "나비", "세상", "소담", "고운", "살자", "라온", "라라", "해솔", "해조", "하랑", "진의", "진이", "꿀벌", "벌레", "꽃별", "나래", "노을", "누리", "늘솜", "언제", "인생", "사랑", "다솜", "다원", "다한", "다인", "다희", "미라", "미르", "바우", "세찬", "은솔", "은솜", "보나", "보예", "두나", "지은", "지음", "조은", "이웃", "값진", "걸레", "행주", "타나", "피나", "하람", "한별", "혜윤", "혜성", "희라", "노루", "나라", "놀이", "남이", "남진", "피카", "다롱", "다람", "다리", "도룡", "비룡", "화살", "예의", "예우", "도리", "도의", "동아", "라인", "리인", "카톡", "깜지", "율희", "율히", "무루", "무주", "무한", "미자", "미주", "케이", "예인", "보로", "부루", "부류", "비리", "비수", "비연", "시리", "사리", "여리", "자리", "조루", "지리", "주류", "지애", "자의", "제무", "채무", "차키", "치루", "츄희", "츄정", "보겸", "하루", "하리", "하람", "해루", "해목", "에고", "소드", "헤라", "치프", "슈비", "슈슈", "빅맥", "롯데", "엘지", "한화", "키움", "명품", "헤어", "레디", "카린", "린스", "사회", "국어", "도덕", "츄릅", "피자", "초키", "소림", "두준", "오늘", "우주", "이브", "언어", "오전", "오후", "욕심", "애교", "인격", "격의", "엔젤", "유럽", "연맹", "워낭", "와인", "요구", "요청", "월차", "월세", "전세", "연기", "홍대", "얌얌", "응축", "응답", "야외", "애플", "유령", "양팡", "앙팡", "양파", "융합", "여제", "엽서", "염색", "여유", "유성", "유희", "무희", "악몽", "악당", "잉어", "윽박", "운석", "재벌", "젤리", "전화", "직업", "자연", "제리", "저녁", "지식", "잠옷", "정시", "정각", "종각", "종류", "제제", "존엄", "존경", "잠수", "정신", "잔디", "점프", "장관", "조화", "진동", "지갑", "제약", "저금", "저당", "진리", "장관", "점막", "저항", "정상", "중심", "증권", "지존", "즉석", "출석", "침략", "찬성", "축가", "출산", "체취", "채소", "야채", "체육", "쵸파", "초코", "출항", "출입", "청록", "체리", "치킨", "최대", "최소", "칭찬", "참외", "참견", "추락", "치약", "초록", "치즈", "모짜", "축하", "찰빵", "개떡", "코코", "키즈", "코드", "코트", "농구", "야구", "캐롤", "크롬", "컨택", "카스", "코난", "클릭", "칼날", "코어", "케어", "케첩", "캐슬", "킹콩", "쿠팡", "코덱", "코닥", "키링", "키스", "킬링", "벌스", "코피", "키커", "클린", "키위", "캔슬", "카드", "클럽", "나이", "타로", "투견", "타투", "통로", "동토", "탱커", "퇴직", "티끌", "모아", "태산", "튼튼", "토토", "로또", "뽀또", "산도", "틀니", "트집", "통영", "통발", "태닝", "토핑", "톱니", "틀딱", "티탄", "타이", "태극", "트리", "탈론", "타조", "펭귄", "기린", "팡야", "팬티", "속옷", "브라", "풍선", "필드", "파편", "퐁듀", "핑크", "폐가", "표현", "픽사", "포화", "푸시", "평창", "서울", "대전", "대구", "부산", "울산", "창원", "창녕", "해남", "제주", "독도", "파리", "포토", "포옹", "허그", "피파", "판매", "파탄", "파산", "핑퐁", "탁구", "풍경", "포항", "표지", "표창", "표류", "포획", "포텐", "포켓", "포기", "회계", "한국", "헌혈", "헌법", "후추", "해적", "화보", "형제", "하마", "효과", "혜성", "혐오", "황해", "황도", "한자", "횟집", "횟수", "할배", "황후", "황제", "죄수", "환갑", "한복", "햄벅", "활동", "합창", "혼자", "햇살", "하마", "혼혈", "하프", "호프", "홍조", "훼손", "혈청", "행성", "환수", "쿠시", "코쿤");
        private final int randomSize = arr.size();
        
        @Transactional
        public void init() throws Exception {

        }
    }
}
