package com.nameless.spin_off.entity.enums.hashtag;

import java.util.List;

public enum HashtagScoreEnum {
    HASHTAG_VIEW(0.3, List.of(1.0, 0.5, 0.3, 0.1), List.of(0L, 7L, 14L, 21L, 28L)),
    HASHTAG_POST(1.0, List.of(1.0, 0.5, 0.3, 0.1), List.of(0L, 7L, 14L, 21L, 28L)),
    HASHTAG_FOLLOW(0.5, List.of(1.0, 0.5, 0.3, 0.1), List.of(0L, 7L, 14L, 21L, 28L));

    private final Double rate;
    private final List<Double> scores;
    private final List<Long> days;

    HashtagScoreEnum(Double rate, List<Double> scores, List<Long> days) {
        this.rate = rate;
        this.scores = scores;
        this.days = days;
    }

    public Double getRate() {
        return rate;
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
    public Long getLatestDay() {
        return days.get(days.size() - 1);
    }
}
