package tests;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestsForLesson {

    @Test
    public void testCookies(){

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        Map<String, String> cookies = response.getCookies();

        String cname = "HomeWork";
        String cvalue= "hw_value";

        assertTrue(cookies.containsKey(cname), "Cookie with " + cname + " name is absent");
        assertEquals(cvalue, cookies.get(cname), cname + "cookie  has wrong value: " + cookies.get(cname));
    }

    @Test
    public void testHeaders(){

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        Headers headers = response.getHeaders();

        String hname = "x-secret-homework-header";
        String hvalue = "Some secret value";

        assertTrue(headers.hasHeaderWithName(hname), "Header with " + hname +" name is absent");
        assertEquals(hvalue, headers.getValue(hname), "Header " + hname + " has wrong value: " + headers.getValue(hname) );
    }

}
