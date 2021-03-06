package me.toy.atdd.subwayadmin.line.application;

import me.toy.atdd.subwayadmin.line.domain.Line;
import me.toy.atdd.subwayadmin.line.domain.LineRepository;
import me.toy.atdd.subwayadmin.line.dto.LineRequest;
import me.toy.atdd.subwayadmin.line.dto.LineResponse;
import me.toy.atdd.subwayadmin.section.domain.Distance;
import me.toy.atdd.subwayadmin.section.domain.Section;
import me.toy.atdd.subwayadmin.section.dto.SectionRequest;
import me.toy.atdd.subwayadmin.station.application.StationService;
import me.toy.atdd.subwayadmin.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationService.selectStationById(request.getUpStationId());
        Station downStation = stationService.selectStationById(request.getDownStationId());
        Line newLine = Line.builder()
                .name(request.getName())
                .color(request.getColor())
                .upStation(upStation)
                .downStation(downStation)
                .distance(request.getDistance())
                .build();

        Line persistLine = lineRepository.save(newLine);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return LineResponse.of(findLineById(id));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line persisLine = findLineById(id);
        persisLine.update(lineRequest.getName(), lineRequest.getColor());
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public void deleteById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findLineById(lineId);
        Section newSection = toSection(line, sectionRequest);
        line.addSection(newSection);
        return LineResponse.of(line);
    }

    private Section toSection(Line line, SectionRequest sectionRequest) {
        Station upStation = stationService.selectStationById(sectionRequest.getUpStationId());
        Station downStation = stationService.selectStationById(sectionRequest.getDownStationId());

        return Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(new Distance(sectionRequest.getDistance()))
                .build();
    }

    public LineResponse removeSection(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        Station station = stationService.selectStationById(stationId);
        line.removeStation(station);
        return LineResponse.of(line);
    }
}
