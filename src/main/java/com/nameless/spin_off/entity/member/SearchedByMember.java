package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.entity.enums.member.SearchedByMemberStatus;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchedByMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="searched_by_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "searched_by_member_status")
    private SearchedByMemberStatus searchedByMemberStatus;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static SearchedByMember createMemberSearch(
            Member member, String content, SearchedByMemberStatus searchedByMemberStatus) {

        SearchedByMember searchedByMember = new SearchedByMember();
        searchedByMember.updateMember(member);
        searchedByMember.updateContent(content);
        searchedByMember.updateSearchedByMemberStatus(searchedByMemberStatus);

        return searchedByMember;

    }

    //==수정 메소드==//

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    public void updateSearchedByMemberStatus(SearchedByMemberStatus searchedByMemberStatus) {
        this.searchedByMemberStatus = searchedByMemberStatus;
    }
    //==비즈니스 로직==//

    //==조회 로직==//

}
