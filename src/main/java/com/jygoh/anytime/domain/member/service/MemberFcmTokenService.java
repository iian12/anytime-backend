package com.jygoh.anytime.domain.member.service;

public interface MemberFcmTokenService {

    void addFcmToken(String currentMemberToken, String fcmToken);

}
