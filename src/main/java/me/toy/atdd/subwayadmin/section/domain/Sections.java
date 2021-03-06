package me.toy.atdd.subwayadmin.section.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.toy.atdd.subwayadmin.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Sections {

    private static final String SECTION_ALREADY_EXIST_ERROR_MESSAGE = "노선에 이미 구간이 등록되어 있습니다.";
    private static final String NOT_MATCH_STATION_ERROR_MESSAGE = "노선에 선택한 상행역과 하행역 둘다 포함되어 있지 않습니다.";
    private static final int REMOVABLE_SECTION_SIZE = 3;
    private static final String NOT_REMOVE_ONLY_ONE_SECTION = "노선에 구간이 하나이기 때문에 제거할 수 없습니다.";
    private static final String NOT_REMOVE_UNREGISTERED_STATION = "노선에 등록되지 않은 역은 제거할 수 없습니다.";

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections(Section upSection, Section downSection) {
        this.sections = Arrays.asList(upSection, downSection);
    }

    public void addSection(Section newSection) {
        boolean isUpStationInSection = isStationInSection(newSection.getUpStation());
        boolean isDownStationInSection = isStationInSection(newSection.getDownStation());

        validateSection(isUpStationInSection, isDownStationInSection);

        if (isUpStationInSection) {
            this.sections.stream()
                    .filter(oldSection -> oldSection.isUpStationInSection(newSection.getUpStation()))
                    .findFirst()
                    .ifPresent(section -> section.updateUpStationToNewDownStation(newSection.getDownStation(), newSection.getDistance()));
        }

        if (isDownStationInSection) {
            this.sections.stream()
                    .filter(oldSection -> oldSection.isDownStationInSection(newSection.getDownStation()))
                    .findFirst()
                    .ifPresent(section -> section.updateDownStationToNewUpStation(newSection.getUpStation(), newSection.getDistance()));
        }

        this.sections.add(newSection);
    }

    private void validateSection(boolean isUpStationInSection, boolean isDownStationInSection) {
        if (isUpStationInSection && isDownStationInSection) {
            throw new IllegalArgumentException(SECTION_ALREADY_EXIST_ERROR_MESSAGE);
        }

        if (!isUpStationInSection && !isDownStationInSection) {
            throw new IllegalArgumentException(NOT_MATCH_STATION_ERROR_MESSAGE);
        }
    }

    private boolean isStationInSection(Station station) {
        return this.sections.stream()
                .anyMatch(section -> section.isDownStationInSection(station));
    }

    public List<Station> getStations() {
        return this.sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public void removeSection(Station stationId) {
        validateRemovableSection();
        Section removeSection = findSection(stationId);
        updateSectionByRemove(removeSection);
        this.sections.remove(removeSection);
    }

    private void validateRemovableSection() {
        if (this.sections.size() < REMOVABLE_SECTION_SIZE) {
            throw new IllegalArgumentException(NOT_REMOVE_ONLY_ONE_SECTION);
        }
    }

    private Section findSection(Station stationId) {
        return this.sections.stream()
                .filter(section -> section.isDownStationInSection(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_REMOVE_UNREGISTERED_STATION));
    }

    private void updateSectionByRemove(Section removeSection) {
        this.sections.stream()
                .filter(section -> section.isUpStationInSection(removeSection.getDownStation()))
                .findFirst()
                .ifPresent(section -> section.updateUpStationToRemove(removeSection.getUpStation(), removeSection.getDistance()));
    }

    public List<Section> getSections() {
        return sections;
    }
}
