package me.toy.atdd.subwayadmin.line.application;

import me.toy.atdd.subwayadmin.line.domain.Line;
import me.toy.atdd.subwayadmin.line.domain.LineRepository;
import me.toy.atdd.subwayadmin.line.dto.LineRequest;
import me.toy.atdd.subwayadmin.line.dto.LineResponse;
import me.toy.atdd.subwayadmin.section.application.SectionService;
import me.toy.atdd.subwayadmin.section.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    public LineResponse saveLine(LineRequest request) {
        Line line = request.toLine();
        line.addSection(sectionService.createSection(line, request.getUpStationId(), request.getDownStationId(), request.getDistance()));
        Line persistLine = lineRepository.save(line);
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
        line.addSection(sectionService.createSection(line, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance()));
        return LineResponse.of(line);
    }

}
