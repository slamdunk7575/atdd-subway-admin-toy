package me.toy.atdd.subwayadmin.line.dto;

import lombok.Builder;

import java.util.Objects;

public class LineRequest {
    private static final String NOT_FOUND_VALUE_ERROR_MESSAGE = "라인 생성시 상행역과 하행역은 필수 입력값 입니다.";

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    @Builder
    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        validateStationId(upStationId, downStationId);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateStationId(Long upStationId, Long downStationId) {
        if (Objects.isNull(upStationId) && Objects.isNull(downStationId)) {
            throw new IllegalArgumentException(NOT_FOUND_VALUE_ERROR_MESSAGE);
        }
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
