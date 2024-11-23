import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {
    @Test
    public void testHelloWorld() {

        System.out.println("Hello from Nadezhda");
    }
    @Test
    public void testGet() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "Tom");

        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();

        String answer = response.get("answer");
        System.out.println(answer);
    }
    @Test
    public void testGetFullJson() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .andReturn();
        response.prettyPrint();
    }
    @Test
    public void testGetJsonValue() {

        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        String message = response.get("messages.message[1]");
        if (message == null) {
            System.out.println("Value of 2nd message or 2nd message key  is absent");
        } else {
            System.out.println(message);
        }
    }

    @Test
    public void testRedirect() {

        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }

}

