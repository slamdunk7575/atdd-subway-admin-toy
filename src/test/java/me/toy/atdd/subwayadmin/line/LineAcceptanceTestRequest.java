package me.toy.atdd.subwayadmin.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import me.toy.atdd.subwayadmin.line.dto.LineRequest;
import org.springframework.http.MediaType;

public class LineAcceptanceTestRequest {

    private LineAcceptanceTestRequest() {
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        LineRequest request = new LineRequest(name, color);

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();

        return response;
    }

    public static String 지하철_노선_등록되어_있음(String name, String color) {
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(name, color);
        return createResponse.header("Location");
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }


}
