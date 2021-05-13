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
import me.toy.atdd.subwayadmin.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    @DisplayName("역 사이에 새로운 역을 등록하는 경우 : 기존 상행역 같음 - 새로운 하행역 추가")
    void addSectionSameUpStation() {
        // given
        SectionRequest sectionRequest = getSectionRequest(천호역, 잠실역, 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(lineNumber8.getId(), sectionRequest);

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선에_등록한_구간_포함됨(response, Arrays.asList("천호역", "잠실역", "문정역"));
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록하는 경우 : 새로운 상행역 추가 - 기존 하행역 같음")
    void addSectionSameDownStation() {
        // given
        SectionRequest sectionRequest = getSectionRequest(잠실역, 문정역, 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(lineNumber8.getId(), sectionRequest);

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선에_등록한_구간_포함됨(response, Arrays.asList("천호역", "잠실역", "문정역"));
    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    public void addSectionNewUpStation() {
        // given
        StationResponse 암사역 = StationAcceptanceTest.지하철역_생성_요청("암사역").as(StationResponse.class);
        SectionRequest sectionRequest = getSectionRequest(암사역.getId(), 천호역, 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(lineNumber8.getId(), sectionRequest);

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선에_등록한_구간_포함됨(response, Arrays.asList("암사역", "천호역", "문정역"));
    }

    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    public void addSectionNewDownStation() {
        // given
        StationResponse 모란역 = StationAcceptanceTest.지하철역_생성_요청("모란역").as(StationResponse.class);
        SectionRequest sectionRequest = getSectionRequest(문정역, 모란역.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(lineNumber8.getId(), sectionRequest);

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선에_등록한_구간_포함됨(response, Arrays.asList("천호역", "문정역", "모란역"));
    }

    @DisplayName("새로운 역을 등록할 경우 기존 구간 길이보다 크거나 같으면 등록할 수 없음 : 기존 상행역 같음 - 새로운 하행역 추가")
    @ParameterizedTest
    @ValueSource(ints = {10, 30})
    void addInvalidDistanceBasedUpStation(int distance) {
        // given
        SectionRequest sectionRequest = getSectionRequest(천호역, 잠실역, distance);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(lineNumber8.getId(), sectionRequest);

        // then
        지하철_노선에_유효하지_않은_구간_등록할수없음(response);
    }


    @DisplayName("새로운 역을 등록할 경우 기존 구간 길이보다 크거나 같으면 등록할 수 없음 : 새로운 상행역 추가 - 기존 하행역 같음")
    @ParameterizedTest
    @ValueSource(ints = {10, 30})
    void addInvalidDistanceBasedDownStation(int distance) {
        // given
        SectionRequest sectionRequest = getSectionRequest(잠실역, 문정역, distance);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(lineNumber8.getId(), sectionRequest);

        // then
        지하철_노선에_유효하지_않은_구간_등록할수없음(response);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionAlreadyExistStations() {
        // given
        SectionRequest sectionRequest = getSectionRequest(천호역, 문정역, 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(lineNumber8.getId(), sectionRequest);

        // then
        지하철_노선에_유효하지_않은_구간_등록할수없음(response);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없음")
    @Test
    void addNotIncludedStationsInLine() {
        // given
        StationResponse 강남역 = StationAcceptanceTest.지하철역_생성_요청("강남역").as(StationResponse.class);
        StationResponse 판교역 = StationAcceptanceTest.지하철역_생성_요청("판교역").as(StationResponse.class);
        SectionRequest sectionRequest = getSectionRequest(강남역.getId(), 판교역.getId(), 7);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(lineNumber8.getId(), sectionRequest);

        // then
        지하철_노선에_유효하지_않은_구간_등록할수없음(response);
    }

    @DisplayName("역과 역사이 중간역 삭제")
    @Test
    void deleteMiddleSection() {
        // given
        SectionRequest sectionRequest = new SectionRequest(천호역, 잠실역, 5);
        지하철_노선에_구간_등록_요청(lineNumber8.getId(), sectionRequest);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_제거_요청(lineNumber8.getId(), 잠실역);

        // then
        지하철_노선에_지하철역_제거됨(response);
        지하철_노선에_등록한_구간_포함됨(response, Arrays.asList("천호역", "문정역"));
        지하철_노선에_구간_거리_계산됨(response, Arrays.asList(0, 10));
    }

    private SectionRequest getSectionRequest(Long upStationId, Long downStationId, int distance) {
        return SectionRequest.builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();
    }

    public void 지하철_노선에_등록한_구간_포함됨(ExtractableResponse<Response> response, List<String> expectedStations) {
        List<String> resultStations = response.jsonPath().getList("stations", SectionResponse.class).stream()
                .map(SectionResponse::getName)
                .collect(Collectors.toList());
        assertThat(resultStations).containsAll(expectedStations);
    }

    public ExtractableResponse<Response> 지하철_노선에_구간_등록_요청(Long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    public void 지하철_노선에_구간_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선에_유효하지_않은_구간_등록할수없음(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_제거_요청(Long lineId, Long StationId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + lineId + "/sections?stationId=" + StationId)
                .then().log().all()
                .extract();
    }

    private void 지하철_노선에_지하철역_제거됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선에_구간_거리_계산됨(ExtractableResponse<Response> response, List<Integer> expectedDistances) {
        List<Integer> resultStationDistances = response.jsonPath().getList("stations", SectionResponse.class).stream()
                .map(SectionResponse::getDistance)
                .collect(Collectors.toList());
        assertThat(resultStationDistances).isEqualTo(expectedDistances);
    }

}
