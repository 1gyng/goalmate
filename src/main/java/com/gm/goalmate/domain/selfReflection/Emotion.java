package com.gm.goalmate.domain.selfReflection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Emotion {
    PROUD("뿌듯해요", "😎"),
    EXCITED("신나요", "😆"),
    GRATEFUL("감사해요", "🥰"),
    NEUTRAL("무난해요","😐"),
    ANXIOUS("불안해요","😰"),
    TIRED("피곤해요", "🫠"),
    FRUSTRATED("답답해요", "😖"),
    SAD("슬퍼요", "😭");

    private final String description;
    private final String emoji;
}