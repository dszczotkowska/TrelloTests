package base;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {
    protected static final String KEY = "";
    protected static final String TOKEN = "";
    protected final String BASE_URL = "https://api.trello.com/1/";
    protected final String ORGANIZATION = "organizations";
    protected final String BOARDS = "boards";
    protected final String LISTS = "lists";
    protected final String CARDS = "cards";
    protected final String CHECKLISTS = "checklists";
    protected static RequestSpecBuilder reqBuilder;
    protected static RequestSpecification reqspec;
    protected static Faker faker;

    @BeforeAll
    public static void beforeAll() {
        reqBuilder = new RequestSpecBuilder();
        reqBuilder.addQueryParam("key", KEY);
        reqBuilder.addQueryParam("token", TOKEN);
        reqBuilder.setContentType(ContentType.JSON);

        reqspec = reqBuilder.build();
        faker = new Faker();
    }
}

