package me.toy.atdd.subwayadmin.station.dto;

import me.toy.atdd.subwayadmin.station.domain.Station;

public class StationRequest {
    private String name;

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
