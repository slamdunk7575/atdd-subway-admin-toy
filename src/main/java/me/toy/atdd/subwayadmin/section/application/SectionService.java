package me.toy.atdd.subwayadmin.section.application;

import me.toy.atdd.subwayadmin.line.domain.Line;
import me.toy.atdd.subwayadmin.section.domain.Distance;
import me.toy.atdd.subwayadmin.section.domain.Section;
import me.toy.atdd.subwayadmin.station.application.StationService;
import me.toy.atdd.subwayadmin.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {

    private StationService stationService;

    public SectionService(StationService stationService) {
        this.stationService = stationService;
    }

    public Section createSection(Line line, Long upStationId, Long downStationId, int distance) {
        Station upStation = stationService.selectStationById(upStationId);
        Station downStation = stationService.selectStationById(downStationId);

        Section section = Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(new Distance(distance))
                .build();

        return section;
    }

}
