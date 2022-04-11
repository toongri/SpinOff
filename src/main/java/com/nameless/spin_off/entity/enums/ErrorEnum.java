package com.nameless.spin_off.entity.enums;

import static com.nameless.spin_off.entity.enums.ContentsLengthEnum.POST_IMAGE_MAX;
import static com.nameless.spin_off.entity.enums.collection.CollectionContentLimitEnum.CONTENT_LENGTH_MAX;
import static com.nameless.spin_off.entity.enums.collection.CollectionContentLimitEnum.TITLE_LENGTH_MAX;

public enum ErrorEnum {

    UNKNOWN("ERR000","알 수 없는 오류"),
    //SECURITY
    INVALID_REFRESH_TOKEN("ERR001", "리프레쉬 토큰이 유효하지 않습니다."),
    AUTHENTICATION_ENTRY("ERR002", "접근 권한이 필요합니다. 토큰을 발급해주세요."),
    ACCESS_DENIED("ERR003", "접근 권한이 없습니다."),
    SOCIAL("ERR004", "소셜 로그인에 실패하였습니다. 고객센터에 문의바랍니다."),
    USERNAME_NOT_FOUND("ERR005", "토큰값이 부정확합니다."),
    FILE_SIZE_LIMIT_EXCEEDED("ERR006", "파일 사이즈 제한을 넘었습니다."),
    CREDENTIALS_EXPIRED("ERR007", "비밀번호가 유효기간이 만료되었습니다. 관리자에게 문의하세요."),
    ALREADY_AUTH_EMAIL("ERR008", "해당 이메일은 이미 인증된 이메일입니다. 로그인 후 연동하여 사용해주세요."),

    //SIGN
    ALREADY_EMAIL("ERR009", "해당 이메일은 이미 존재합니다."),
    ALREADY_LINKAGE_EMAIL("ERR010", "이미 연동된 이메일입니다."),
    ALREADY_NICKNAME("ERR011", "이미 존재하는 닉네임입니다."),
    DONT_HAVE_AUTHORITY("ERR012", "접근 권한이 없습니다."),
    EMAIL_NOT_AUTHENTICATED("ERR013", "이메일 인증이 필요합니다."),
    INCORRECT_ACCOUNT_ID("ERR014", "아이디의 형식이 맞지 않습니다."),
    INCORRECT_ACCOUNT_PW("ERR015", "비밀번호의 형식이 맞지 않습니다."),
    INCORRECT_EMAIL("ERR016", "이메일의 형식이 맞지 않습니다."),
    INCORRECT_NICKNAME("ERR017", "닉네임의 형식이 맞지 않습니다."),
    NOT_CORRECT_EMAIL("ERR018", "요청한 이메일이 일치하지 않습니다."),
    NOT_EXIST_ACCOUNT_ID("ERR019", "해당 아이디는 존재하지 않습니다."),
    NOT_EXIST_EMAIL_AUTH_TOKEN("ERR020", "해당 이메일 인증이 존재하지 않습니다."),
    NOT_EXIST_EMAIL_LINKAGE_TOKEN("ERR021", "현재 이메일 연동 토큰을 찾을 수 없습니다."),
    NOT_MATCH_ACCOUNT_PW("ERR022", "비밀번호가 일치하지 않습니다."),
    INCORRECT_PROVIDER("ERR056", "요청한 provider가 존재하지 않습니다."),

    //COLLECTION
    ALREADY_COLLECTED_POST("ERR023", "이미 컬렉션에 포스트가 포함되있습니다."),
    ALREADY_FOLLOWED_COLLECTION("ERR024", "이미 해당 컬렉션을 팔로우 했습니다."),
    ALREADY_LIKED_COLLECTION("ERR025", "이미 해당 컬렉션을 좋아요 했습니다."),
    CANT_FOLLOW_OWN_COLLECTION("ERR026", "본인의 컬렉션은 팔로우 할 수 없습니다."),
    INCORRECT_CONTENT_OF_COLLECTION("ERR027", "글의 본문은 "+ CONTENT_LENGTH_MAX.getValue() +"자를 넘겨서는 안됩니다."),
    INCORRECT_TITLE_OF_COLLECTION("ERR028", "글의 제목은 "+ TITLE_LENGTH_MAX.getValue() +"자를 넘겨서는 안됩니다."),
    NOT_EXIST_COLLECTION("ERR029", "컬렉션이 존재하지 않습니다."),
    NOT_MATCH_COLLECTION("ERR030", "컬렉션 ID가 옳바르지 않습니다."),

    //COMMENT
    ALREADY_LIKED_COMMENT_IN_COLLECTION("ERR031", "이미 해당 댓글을 좋아요 했습니다."),
    ALREADY_LIKED_COMMENT_IN_POST("ERR032", "이미 해당 댓글을 좋아요 했습니다."),
    NOT_EXIST_COMMENT_IN_COLLECTION("ERR033", "댓글이 존재하지 않습니다."),
    NOT_EXIST_COMMENT_IN_POST("ERR034", "댓글이 존재하지 않습니다."),

    //HASHTAG
    INCORRECT_HASHTAG_CONTENT("ERR035", "해시태그가 형식에 맞지 않습니다."),
    NOT_EXIST_HASHTAG("ERR036", "해당 해시태그는 존재하지 않습니다."),

    //HELP
    UNKNOWN_CONTENT_TYPE("ERR037", "해당 컨텐츠 속성은 존재하지 않습니다."),

    //MEMBER
    ALREADY_ACCOUNT_ID("ERR038", "해당 아이디는 이미 존재합니다."),
    ALREADY_BLOCKED_MEMBER("ERR039", "이미 해당 유저를 차단 했습니다."),
    ALREADY_COMPLAIN("ERR040", "이미 신고가 접수 되었습니다."),
    ALREADY_FOLLOWED_HASHTAG("ERR041", "이미 해당 해시태그를 팔로우 했습니다."),
    ALREADY_FOLLOWED_MEMBER("ERR042", "이미 해당 유저를 팔로우 했습니다."),
    ALREADY_FOLLOWED_MOVIE("ERR043", "이미 해당 영화를 팔로우 했습니다."),
    NOT_EXIST_DM("ERR044", "해당 DM은 존재하지 않습니다."),
    NOT_EXIST_MEMBER("ERR045", "해당 유저는 존재하지 않습니다."),

    //MOVIE
    NOT_EXIST_MOVIE("ERR046", "해당 영화는 존재하지 않습니다."),

    //POST
    ALREADY_LIKED_POST("ERR047", "이미 해당 유저를 팔로우 했습니다."),
    ALREADY_POSTED_HASHTAG("ERR048", "이미 해당 해시태그를 태그 했습니다."),
    INCORRECT_CONTENT_OF_POST("ERR049", "글의 본문은 "+ CONTENT_LENGTH_MAX.getValue() +"자를 넘겨서는 안됩니다."),
    INCORRECT_POST_IMAGE_LENGTH("ERR050", "글에 허용되는 이미지의 갯수는 " + POST_IMAGE_MAX.getLength() + "개가 최대입니다."),
    INCORRECT_TITLE_OF_POST("ERR051", "글의 제목은 " + TITLE_LENGTH_MAX.getValue() + "자를 넘겨서는 안됩니다."),
    NOT_EXIST_POST("ERR052", "해당 포스트가 존재하지 않습니다."),

    //SEARCH
    INCORRECT_LENGTH_RELATED_KEYWORD("ERR053", "키워드 길이 제한을 확인 해주시기 바랍니다."),

    //ETC
    RUNTIME("ERR054", "예상하지 못한 예외입니다."),
    MISSING_REQUEST_VALUE("ERR055", "요청 파라미터가 부족합니다.");


    private final String code;
    private final String message;

    ErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
