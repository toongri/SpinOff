package com.nameless.spin_off.entity.enums.member;

import java.util.List;

public enum MemberScoreEnum {
    MEMBER__FOLLOW(List.of(1.0, 0.5, 0.3, 0.1), List.of(0L, 7L, 14L, 21L, 28L));

    private final List<Double> scores;
    private final List<Long> days;

    MemberScoreEnum(List<Double> scores, List<Long> days) {
        this.scores = scores;
        this.days = days;
    }

    public List<Double> getScores() {
        return scores;
    }

    public List<Long> getDays() {
        return days;
    }

    public Double getLatestScore() {
        return scores.get(0);
    }
}
