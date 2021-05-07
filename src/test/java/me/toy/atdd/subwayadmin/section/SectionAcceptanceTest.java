package me.toy.atdd.subwayadmin.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import me.toy.atdd.subwayadmin.AcceptanceTest;
import me.toy.atdd.subwayadmin.line.LineAcceptanceTest;
import me.toy.atdd.subwayadmin.line.dto.LineRequest;
import me.toy.atdd.subwayadmin.line.dto.LineResponse;
import me.toy.atdd.subwayadmin.section.dto.SectionRequest;
import me.toy.atdd.subwayadmin.section.dto.SectionResponse;
import me.toy.atdd.subwayadmin.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private LineResponse lineNumber8;
    private Long 천호역;
    private Long 문정역;
    private Long 잠실역;

    @BeforeEach
    void init() {
        천호역 = StationAcceptanceTest.지하철역_등록되어_있음("천호역");
        문정역 = StationAcceptanceTest.지하철역_등록되어_있음("문정역");
        잠실역 = StationAcceptanceTest.지하철역_등록되어_있음("잠실역");

        LineRequest lineRequest = new LineRequest("8호선", "pink", 천호역, 문정역, 10);
        lineNumber8 = LineAcceptanceTest.지하철_노선_생성_요청(lineRequest).as(LineResponse.class);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록하는 경우 : 기존 상행역 같고 - 새로운 하행역 추가")
    void addSectionSameUpStation() {
        // given
        SectionRequest sectionRequest = new SectionRequest(천호역, 잠실역, 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(lineNumber8.getId(), sectionRequest);

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선에_등록한_구간_포함됨(response, Arrays.asList("천호역", "잠실역", "문정역"));

    }

    private void 지하철_노선에_등록한_구간_포함됨(ExtractableResponse<Response> response, List<String> expectedStations) {
        List<String> resultStations = response.jsonPath().getList("stations", SectionResponse.class).stream()
                .map(SectionResponse::getName)
                .collect(Collectors.toList());
        assertThat(resultStations).containsAll(expectedStations);
    }


    private ExtractableResponse<Response> 지하철_노선에_구간_등록_요청(Long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    private void 지하철_노선에_구간_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}