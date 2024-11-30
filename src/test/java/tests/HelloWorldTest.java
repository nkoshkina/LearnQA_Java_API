package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelloWorldTest {
    @ParameterizedTest
    @ValueSource(strings = {"", "Hello, world", "Hello from Nadezhda"})
    public void testStringLength(String str) {

        assertTrue(str.length() >15, "Length <= 15");
    }

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
    @Test
    public void testAllRedirects() {
        int statusCode;
        String url = "https://playground.learnqa.ru/api/long_redirect";
        do {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();

            url = response.getHeader("Location");
            System.out.println(url);
            statusCode = response.getStatusCode();
            System.out.println(statusCode);
        } while (statusCode != 200);
    }

    @Test
    public void testWithToken() {

        JsonPath response = RestAssured
                //.given()
                //.when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String token = response.get("token");
        //System.out.println(token);
        int seconds = response.get("seconds");
        //System.out.println(seconds);

        Map<String, String> params = new HashMap<>();
        params.put("token", token);

        response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String status = response.get("status");
        if (Objects.equals(status, "Job is NOT ready")) {
            System.out.println("Status has correct value: " + status);
        } else {
            System.out.println("Status has wrong value: " + status);
        }
        ;

        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            response = RestAssured
                    .given()
                    .queryParams(params)
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .jsonPath();

            status = response.get("status");
            String result = response.get("result");

            if (Objects.equals(status, "Job is ready")) {
                System.out.println("Status has correct value: " + status);
            } else {
                System.out.println("Status has wrong value: " + status);
            }
            ;
            if (result == null) {
                System.out.println("Result is not defined");
            } else {
                System.out.println("Result has correct value: " + result);
            }
            ;

        }
    }
    @Test
    public void testWithPassword() {
        String[] password =
                {"123456", "123456789", "qwerty", "password", "1234567",
                        "12345678", "12345", "iloveyou", "111111", "123123",
                        "abc123", "qwerty123", "1q2w3e4r", "admin", "qwertyuiop",
                        "654321", "555555", "lovely", "7777777", "welcome",
                        "888888", "princess", "dragon", "password1", "123qwe"
                };
        int i = 0;
        String outText;
        boolean res;

        do {
            Map<String, String> params = new HashMap<>();
            params.put("login", "super_admin");
            params.put("password", password[i]);

            Response response = RestAssured
                    .given()
                    .body(params)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String responseCookie =response.getCookie("auth_cookie");

            Map<String,String> cookies =new HashMap<>();
            cookies.put("auth_cookie", responseCookie);


            Response responseForCheck =RestAssured
                    .given()
                    //.body(params)
                    .cookies(cookies)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();

            outText = responseForCheck.print();
            res = (Objects.equals(outText, "You are NOT authorized"));

            i = i+1;
        } while ((i < (password.length -1)) &res);

        System.out.println("\n_____Results:_____");
        System.out.println("Correct password:"+password[i-1]);
        System.out.println(outText);
    }


}
