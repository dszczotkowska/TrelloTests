package base;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {
    protected static final String KEY = "72ed4c1a43b015a85a4b62dbee8e5fd5";
    protected static final String TOKEN = "6e67825f9598b4012b0755e7af1317c410a87303353bb7b5622afe2815645525";
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

