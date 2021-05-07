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
        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
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
        return LineResponse.of(selectLineById(id));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line persisLine = selectLineById(id);
        persisLine.update(lineRequest.toLine());
    }

    private Line selectLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public void deleteById(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = selectLineById(lineId);
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

}
