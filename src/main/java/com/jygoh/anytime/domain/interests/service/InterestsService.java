package com.jygoh.anytime.domain.interests.service;

import java.util.List;

public interface InterestsService {

    void addOrUpdateInterests(List<String> interests, String token);
}
