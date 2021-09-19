package ZipCode;

import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;


@Execution(ExecutionMode.CONCURRENT)
public class ZipCodeApi_Test {

    private static final String URL = "http://api.zippopotam.us/ru/";

    @ParameterizedTest
    @MethodSource("zipCodes")
    public void getZipCodeAndCheckStateName(String zipCode, String state, String placeName) {
        RestAssured
                .given()
                    .log().uri()
                .when()
                    .get(URL + zipCode)
                .then()
                    .log().status()
                    .log().body()
                    .spec(
                        new ResponseSpecBuilder()
                                .expectStatusCode(200)
                                // два способа поиска значения
                                .expectBody("places[0].state", equalTo(state))
                                .expectBody("places.find{it}.state", equalTo(state))

                                .expectBody("places[0].'place name'", equalTo(placeName))
                                .expectBody("places.find{it}.'place name'", equalTo(placeName))
                                .build()

                    );
    }


    // Десериализация json и проверка его на соответствие заданным значениям
    @ParameterizedTest
    @MethodSource({"zipCodes", "wrongZipCodes"})
    public void deserializeJsonZipCodeResponse(String zipCode, String state, String placeName) {
        List<ZipCodePojo> zipCodes = given()
                .baseUri(URL)
                .basePath(zipCode)
                .contentType(ContentType.JSON)
                .when()
                    .get()
                .then()
                    .statusCode(200)
                .extract().jsonPath().getList("places", ZipCodePojo.class);

        assertThat(zipCodes)
                .as("Поле 'state' содержит неверное значение")
                .extracting(ZipCodePojo::getState)
                .contains(state);

        assertThat(zipCodes)
                .as("Поле 'placeName' содержит неверное значение")
                .extracting(ZipCodePojo::getPlaceName)
                .contains(placeName);
    }

    private static Stream<Arguments> zipCodes() {
        return Stream.of(
                Arguments.of("400066", "Волгоградская Область", "Волгоград 66"),
                Arguments.of("350000", "Краснодарский Край", "Краснодар"),
                Arguments.of("109012", "Москва", "Москва 12"),
                Arguments.of("630102", "Новосибирская Область", "Новосибирск 102")
        );
    }

    private static Stream<Arguments> wrongZipCodes() {
        return Stream.of(
                Arguments.of("109012", "Москва", "Москва")
        );
    }
}
