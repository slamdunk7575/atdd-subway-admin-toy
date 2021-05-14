package me.toy.atdd.subwayadmin.line.dto;

import lombok.Getter;
import me.toy.atdd.subwayadmin.line.domain.Line;
import me.toy.atdd.subwayadmin.section.dto.SectionResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<SectionResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate, List<SectionResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        List<SectionResponse> sectionResponses = line.getLineSections().stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());

        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate(), line.getModifiedDate(), sectionResponses);
    }
}
