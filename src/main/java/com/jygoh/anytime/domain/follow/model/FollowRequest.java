package com.jygoh.anytime.domain.follow.model;

import com.jygoh.anytime.domain.member.model.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private Member requester;

    @ManyToOne
    @JoinColumn(name = "target_id")
    private Member target;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Builder
    public FollowRequest(Member requester, Member target) {
        this.requester = requester;
        this.target = target;
        this.status = RequestStatus.PENDING;
    }

    public void acceptRequest() {
        this.status = RequestStatus.ACCEPTED;
    }

    public void rejectRequest() {
        this.status = RequestStatus.REJECTED;
    }
}
