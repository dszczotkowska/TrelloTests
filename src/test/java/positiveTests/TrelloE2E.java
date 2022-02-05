package positiveTests;

import base.BaseTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class TrelloE2E extends BaseTest {
    private static String fakeDisplayName;
    private static String fakeDesc;
    private static String fakeWeb;
    private static String fakeBoardName;
    private static String fakeListname;
    private static String fakeCardName;
    private static String orgId;
    private static String boardId;
    private static String firstListId;
    private static String secondListId;
    private static String cardId;


    @BeforeEach
    public void beforeEach() {
        fakeDisplayName = faker.company().name();
        fakeDesc = faker.lorem().sentence(10);
        fakeWeb = faker.internet().url();
        fakeBoardName = faker.harryPotter().character();
        fakeListname = faker.harryPotter().location();

    }

    private static Stream<Arguments> createChecklistsOnCard() {
        return Stream.of(
                Arguments.of("Checklist1", "4"),
                Arguments.of("Checklist2", "2"),
                Arguments.of("Checklist3", "3"),
                Arguments.of("Checklist4", "1")
        );
    }

    @Test
    @Order(1)
    public void createOrg() {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("displayName", fakeDisplayName);
        queryParams.put("desc", fakeDesc);
        queryParams.put("website", fakeWeb);

        Response response = given()
                .spec(reqspec)
                .queryParams(queryParams)
                .when()
                .post(BASE_URL + ORGANIZATION)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        assertThat(json.getString("displayName")).isEqualTo(fakeDisplayName);
        assertThat(json.getString("desc"))
                .isEqualTo(fakeDesc)
                .isNotNull();
        assertThat(json.getString("website")).isEqualTo("http://" + fakeWeb);
        assertThat(json.getString("name"))
                .isLowerCase()
                .hasSizeGreaterThan(2);

        orgId = json.getString("id");
    }

    @Test
    @Order(2)
    public void createBoard() {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", fakeBoardName);
        queryParams.put("desc", fakeDesc);
        queryParams.put("idOrganization", orgId);

        Response response = given()
                .spec(reqspec)
                .queryParams(queryParams)
                .queryParam("defaultLists", false)
                .when()
                .post(BASE_URL + BOARDS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("desc")).isEqualTo(fakeDesc);
        assertThat(json.getString("name")).isEqualTo(fakeBoardName);
        assertThat(json.getString("idOrganization")).isEqualTo(orgId);

        boardId = json.getString("id");
    }

    @Test
    @Order(3)
    public void createFirstList() {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", fakeListname);
        queryParams.put("idBoard", boardId);

        Response response = given()
                .spec(reqspec)
                .queryParams(queryParams)
                .when()
                .post(BASE_URL + LISTS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("name")).isEqualTo(fakeListname);

        firstListId = json.getString("id");
    }

    @Test
    @Order(4)
    public void createSecondList() {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", fakeListname);
        queryParams.put("idBoard", boardId);

        Response response = given()
                .spec(reqspec)
                .queryParams(queryParams)
                .when()
                .post(BASE_URL + LISTS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("name")).isEqualTo(fakeListname);

        secondListId = json.getString("id");

    }

    @Test
    @Order(5)
    public void createCard() {

        fakeCardName = faker.harryPotter().spell();

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", fakeCardName);
        queryParams.put("idList", firstListId);

        Response response = given()
                .spec(reqspec)
                .queryParams(queryParams)
                .when()
                .post(BASE_URL + CARDS)
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("name")).isEqualTo(fakeCardName);
        assertThat(json.getString("idList")).isEqualTo(firstListId);

        cardId = json.getString("id");
    }

    @Test
    @Order(6)
    public void updateCard() {
        Response response = given()
                .spec(reqspec)
                .pathParam("id", cardId)
                .queryParam("idList", secondListId)
                .when()
                .put(BASE_URL + CARDS + "/{id}")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("name")).isEqualTo(fakeCardName);
        assertThat(json.getString("idList")).isEqualTo(secondListId);

        cardId = json.getString("id");
    }

    @ParameterizedTest
    @MethodSource("createChecklistsOnCard")
    @Order(7)
    public void addChecklistsOnCard(String name, String pos) {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", name);
        queryParams.put("pos", pos);

        Response response = given()
                .spec(reqspec)
                .pathParam("id", cardId)
                .queryParams(queryParams)
                .when()
                .post(BASE_URL + CARDS + "/{id}/checklists")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        assertThat(json.getString("name")).isEqualTo(name);
        assertThat(json.getString("pos")).isEqualTo(pos);
    }

    @Test
    @Order(8)
    public void deleteOrg() {
        given()
                .spec(reqspec)
                .pathParam("id", orgId)
                .when()
                .delete(BASE_URL + ORGANIZATION + "/{id}")
                .then()
                .statusCode(200);
    }
}
