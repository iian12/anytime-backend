package com.jygoh.anytime.domain.chat.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;


@Getter
@MappedSuperclass
public abstract class Chat {

    @Id @Tsid
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}