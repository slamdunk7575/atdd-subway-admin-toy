package me.toy.atdd.subwayadmin.line.application;

import me.toy.atdd.subwayadmin.line.domain.Line;
import me.toy.atdd.subwayadmin.line.domain.LineRepository;
import me.toy.atdd.subwayadmin.line.dto.LineRequest;
import me.toy.atdd.subwayadmin.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

}
