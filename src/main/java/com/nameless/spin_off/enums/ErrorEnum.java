package com.nameless.spin_off.enums;

import static com.nameless.spin_off.enums.ContentsLengthEnum.*;

public enum ErrorEnum {

    //SECURITY
    INVALID_REFRESH_TOKEN("ERR000", "리프레쉬 토큰이 유효하지 않습니다."),
    AUTHENTICATION_ENTRY("ERR001", "접근 권한이 필요합니다. 토큰을 발급해주세요."),
    ACCESS_DENIED("ERR002", "접근 권한이 없습니다."),
    SOCIAL("ERR003", "소셜 로그인에 실패하였습니다. 고객센터에 문의바랍니다."),
    USERNAME_NOT_FOUND("ERR004", "토큰값이 부정확합니다."),
    FILE_SIZE_LIMIT_EXCEEDED("ERR005", "파일 사이즈 제한을 넘었습니다."),
    CREDENTIALS_EXPIRED("ERR006", "비밀번호가 유효기간이 만료되었습니다. 관리자에게 문의하세요."),
    ALREADY_AUTH_EMAIL("ERR007", "해당 이메일은 이미 인증된 이메일입니다. 로그인 후 연동하여 사용해주세요."),

    //SIGN
    ALREADY_EMAIL("ERR100", "해당 이메일은 이미 존재합니다."),
    ALREADY_LINKAGE_EMAIL("ERR101", "이미 연동된 이메일입니다."),
    ALREADY_NICKNAME("ERR102", "이미 존재하는 닉네임입니다."),
    DONT_HAVE_AUTHORITY("ERR103", "접근 권한이 없습니다."),
    EMAIL_NOT_AUTHENTICATED("ERR104", "이메일 인증이 필요합니다."),
    INCORRECT_ACCOUNT_ID("ERR105", "아이디의 형식이 맞지 않습니다."),
    INCORRECT_ACCOUNT_PW("ERR106", "비밀번호의 형식이 맞지 않습니다."),
    INCORRECT_EMAIL("ERR107", "이메일의 형식이 맞지 않습니다."),
    INCORRECT_NICKNAME("ERR108", "닉네임의 형식이 맞지 않습니다."),
    INCORRECT_PHONE_NUMBER("ERR109", "전화번호의 형식이 맞지 않습니다."),
    NOT_CORRECT_EMAIL("ERR110", "요청한 이메일이 일치하지 않습니다."),
    NOT_EXIST_ACCOUNT_ID("ERR111", "해당 아이디는 존재하지 않습니다."),
    NOT_EXIST_EMAIL_AUTH_TOKEN("ERR112", "해당 이메일 인증이 존재하지 않습니다."),
    NOT_EXIST_EMAIL_LINKAGE_TOKEN("ERR113", "현재 이메일 연동 토큰을 찾을 수 없습니다."),
    NOT_MATCH_ACCOUNT_PW("ERR114", "비밀번호가 일치하지 않습니다."),
    INCORRECT_PROVIDER("ERR115", "요청한 provider가 존재하지 않습니다."),
    INCORRECT_BIO("ERR116", "소개글의 형식이 맞지 않습니다."),

    //COLLECTION
    ALREADY_COLLECTED_POST("ERR200", "이미 컬렉션에 포스트가 포함되있습니다."),
    ALREADY_FOLLOWED_COLLECTION("ERR201", "이미 해당 컬렉션을 팔로우 했습니다."),
    ALREADY_LIKED_COLLECTION("ERR202", "이미 해당 컬렉션을 좋아요 했습니다."),
    CANT_FOLLOW_OWN_COLLECTION("ERR203", "본인의 컬렉션은 팔로우 할 수 없습니다."),
    INCORRECT_CONTENT_OF_COLLECTION("ERR204", "글의 본문은 "+ COLLECTION_CONTENT_MAX.getLength() +"자를 넘겨서는 안됩니다."),
    INCORRECT_TITLE_OF_COLLECTION("ERR205", "글의 제목은 "+ COLLECTION_TITLE_MAX.getLength() +"자를 넘겨서는 안됩니다."),
    NOT_EXIST_COLLECTION("ERR206", "컬렉션이 존재하지 않습니다."),
    NOT_MATCH_COLLECTION("ERR207", "컬렉션 ID가 옳바르지 않습니다."),
    ALREADY_LIKED_COMMENT_IN_COLLECTION("ERR208", "이미 해당 댓글을 좋아요 했습니다."),
    NOT_EXIST_COMMENT_IN_COLLECTION("ERR209", "댓글이 존재하지 않습니다."),

    //POST
    ALREADY_LIKED_POST("ERR400", "이미 해당 유저를 팔로우 했습니다."),
    ALREADY_POSTED_HASHTAG("ERR401", "이미 해당 해시태그를 태그 했습니다."),
    INCORRECT_CONTENT_OF_POST("ERR402", "글의 본문은 "+ POST_CONTENT_MAX.getLength() +"자를 넘겨서는 안됩니다."),
    INCORRECT_POST_IMAGE_LENGTH("ERR403", "글에 허용되는 이미지의 갯수는 " + POST_IMAGE_MAX.getLength() + "개가 최대입니다."),
    INCORRECT_TITLE_OF_POST("ERR404", "글의 제목은 " + POST_TITLE_MAX.getLength() + "자를 넘겨서는 안됩니다."),
    NOT_EXIST_POST("ERR405", "해당 포스트가 존재하지 않습니다."),
    ALREADY_LIKED_COMMENT_IN_POST("ERR406", "이미 해당 댓글을 좋아요 했습니다."),
    NOT_EXIST_COMMENT_IN_POST("ERR407", "댓글이 존재하지 않습니다."),

    //MEMBER
    ALREADY_ACCOUNT_ID("ERR500", "해당 아이디는 이미 존재합니다."),
    ALREADY_BLOCKED_MEMBER("ERR501", "이미 해당 유저를 차단 했습니다."),
    ALREADY_COMPLAIN("ERR502", "이미 신고가 접수 되었습니다."),
    ALREADY_FOLLOWED_HASHTAG("ERR503", "이미 해당 해시태그를 팔로우 했습니다."),
    ALREADY_FOLLOWED_MEMBER("ERR504", "이미 해당 유저를 팔로우 했습니다."),
    ALREADY_FOLLOWED_MOVIE("ERR505", "이미 해당 영화를 팔로우 했습니다."),
    NOT_EXIST_DM("ERR506", "해당 DM은 존재하지 않습니다."),
    NOT_EXIST_MEMBER("ERR507", "해당 유저는 존재하지 않습니다."),
    NOT_MATCH_EMAIL("ERR508", "이메일이 일치하지 않습니다."),
    ALREADY_REGISTER_EMAIL("ERR509", "이미 등록한 이메일입니다."),

    //MOVIE
    NOT_EXIST_MOVIE("ERR600", "해당 영화는 존재하지 않습니다."),
    OVER_KOBIS_MOVIE_API_LIMIT("ERR601", "api 허용량을 초과했습니다."),

    //HASHTAG
    INCORRECT_HASHTAG_CONTENT("ERR700", "해시태그가 형식에 맞지 않습니다."),
    NOT_EXIST_HASHTAG("ERR=701", "해당 해시태그는 존재하지 않습니다."),

    //ETC
    UNKNOWN("ERR900","알 수 없는 오류"),
    INCORRECT_LENGTH_RELATED_KEYWORD("ERR901", "키워드 길이 제한을 확인 해주시기 바랍니다."),
    UNKNOWN_CONTENT_TYPE("ERR902", "해당 컨텐츠 속성은 존재하지 않습니다."),
    RUNTIME("ERR0903", "예상하지 못한 예외입니다."),
    MISSING_REQUEST_VALUE("ERR904", "요청 파라미터가 부족합니다.");


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
