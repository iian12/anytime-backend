package com.jygoh.anytime.domain.hashtag.model;

import com.jygoh.anytime.domain.member.model.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HashtagFollower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "hashtag_id", nullable = false)
    private Hashtag hashtag;

    public HashtagFollower(Member member, Hashtag hashtag) {
        this.member = member;
        this.hashtag = hashtag;
    }


}
