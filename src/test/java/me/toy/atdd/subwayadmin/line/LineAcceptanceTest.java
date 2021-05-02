package me.toy.atdd.subwayadmin.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import me.toy.atdd.subwayadmin.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineAcceptanceTestRequest.지하철_노선_생성_요청("8호선", "pink");

        // then
        // 지하철_노선_생성됨
        LineAcceptanceTestResponse.지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineAcceptanceTestRequest.지하철_노선_등록되어_있음("8호선", "pink");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineAcceptanceTestRequest.지하철_노선_생성_요청("8호선", "pink");

        // then
        // 지하철_노선_생성_실패됨
        LineAcceptanceTestResponse.지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        String createResponseLine1 = LineAcceptanceTestRequest.지하철_노선_등록되어_있음("2호선", "green");
        String createResponseLine2 = LineAcceptanceTestRequest.지하철_노선_등록되어_있음("8호선", "pink");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = LineAcceptanceTestRequest.지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        LineAcceptanceTestResponse.지하철_노선_목록_응답됨(response);
        LineAcceptanceTestResponse.지하철_노선_목록_포함됨(response, Arrays.asList(createResponseLine1, createResponseLine2));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_조회_요청

        // then
        // 지하철_노선_응답됨
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_수정_요청

        // then
        // 지하철_노선_수정됨
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_제거_요청

        // then
        // 지하철_노선_삭제됨
    }
}
