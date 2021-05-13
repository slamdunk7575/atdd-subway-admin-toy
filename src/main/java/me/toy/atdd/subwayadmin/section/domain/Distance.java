package me.toy.atdd.subwayadmin.section.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Distance {

    private static final String INVALID_VALUE_ERROR_MESSAGE = "거리는 0보다 커야 합니다.";
    private static final String INVALID_DISTANCE_ERROR_MESSAGE = "구간을 등록시 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.";
    private static final int ZERO = 0;

    private int value;

    public Distance(int distance) {
        validateInitDistance(distance);
        this.value = distance;
    }

    private void validateInitDistance(int distance) {
        if (distance < ZERO) {
            throw new IllegalArgumentException(INVALID_VALUE_ERROR_MESSAGE);
        }
    }

    public void updateDistance(Distance newDistance) {
        int newSectionDistance = newDistance.getValue();
        if (this.value != 0) {
            validateDistance(newSectionDistance);
            this.value -= newSectionDistance;
        }
        this.value = newSectionDistance;
    }

    private void validateDistance(int newDistance) {
        if (this.value <= newDistance) {
            throw new IllegalArgumentException(INVALID_DISTANCE_ERROR_MESSAGE);
        }
    }
}
