package com.jygoh.anytime.domain.chat.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.TableGenerator;
import java.util.UUID;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class Chat {

    @Id
    private Long id;

    @PrePersist
    protected void generateUniqueId() {
        if (this.id == null) {
            // 밀리초 단위의 현재 시간 + 고유한 랜덤 값 (UUID)
            this.id = System.currentTimeMillis() + UUID.randomUUID().hashCode();
        }
    }}