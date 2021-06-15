package me.toy.atdd.subwayadmin.line.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import me.toy.atdd.subwayadmin.common.BaseEntity;
import me.toy.atdd.subwayadmin.section.domain.Distance;
import me.toy.atdd.subwayadmin.section.domain.Section;
import me.toy.atdd.subwayadmin.section.domain.Sections;
import me.toy.atdd.subwayadmin.station.domain.Station;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "color")
    private String color;

    @Embedded
    private Sections sections;

    @Builder
    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        Section upSection = new Section(this, null, upStation, new Distance(0));
        Section downSection = new Section(this, upStation, downStation, new Distance(distance));
        this.sections = new Sections(upSection, downSection);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        this.sections.addSection(section);
        section.addLine(this);
    }

    public void removeStation(Station station) {
        this.sections.removeSection(station);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getLineSections() {
        return sections.getSections();
    }

}
