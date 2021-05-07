package me.toy.atdd.subwayadmin.section.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.toy.atdd.subwayadmin.common.BaseEntity;
import me.toy.atdd.subwayadmin.line.domain.Line;
import me.toy.atdd.subwayadmin.station.domain.Station;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    @Embedded
    private Distance distance;

    @Builder
    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void addLine(Line line) {
        this.line = line;
    }

    public boolean isUpStationInSection(Station newUpStation) {
        if (this.upStation == null) {
            return false;
        }
        return this.upStation.equals(newUpStation);
    }

    public boolean isDownStationInSection(Station newDownStation) {
        return this.downStation.equals(newDownStation);
    }

    public void updateUpStationToNewDownStation(Station newDownStation, Distance distance) {
        this.upStation = newDownStation;
        this.distance.updateDistance(distance.getValue());
    }

    public void updateDownStationToNewUpStation(Station newUpStation, Distance distance) {
        this.downStation = newUpStation;
        this.distance.updateDistance(distance.getValue());
    }
}
