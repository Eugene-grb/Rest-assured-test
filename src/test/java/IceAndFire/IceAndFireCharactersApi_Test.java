package IceAndFire;

import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;

@Execution(ExecutionMode.CONCURRENT)
public class IceAndFireCharactersApi_Test {

    private static final String URL_CHARACTERS = "https://www.anapioficeandfire.com/api/characters/";

    @ParameterizedTest
    @MethodSource({"dataForGetCharacterByNameAndCheckName", "wrongDataForGetCharacterByNameAndCheckName"})
    public void getCharacterByNameAndCheckName(String name, String expectedName, String expectedGender) {
        RestAssured
                .given()
                    .log().uri()
                .when()
                    .get(URL_CHARACTERS + "?name=" + name)
                .then()
                    .log().status()
                    .log().body()
                    .spec(
                            new ResponseSpecBuilder()
                                    .expectStatusCode(200)
                                    .expectBody("name[0]", equalTo(expectedName))
                                    .expectBody("gender[0]", equalTo(expectedGender))
                                    .build()
                    );
    }

    private static Stream<Arguments> dataForGetCharacterByNameAndCheckName() {
        return Stream.of(
                Arguments.of("Jon Snow", "Jon Snow", "Male"),
                Arguments.of("Arya Stark", "Arya Stark", "Female"),
                Arguments.of("Cersei Lannister", "Cersei Lannister", "Female"),
                Arguments.of("Tyrion Lannister", "Tyrion Lannister", "Male"),
                Arguments.of("Daenerys Targaryen", "Daenerys Targaryen", "Female")
        );
    }

    private static Stream<Arguments> wrongDataForGetCharacterByNameAndCheckName() {
        return Stream.of(
                Arguments.of("Jon Snow", "Jon Targaryen", "Male"),
                Arguments.of("Cersei Lannister", "Cersei Lannister", "Male")
        );
    }
}