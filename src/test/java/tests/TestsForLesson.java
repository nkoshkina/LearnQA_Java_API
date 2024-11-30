package tests;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
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

    @ParameterizedTest
    @CsvSource({"'Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30', Mobile,No,Android",
            "'Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1', Mobile, Chrome, iOS",
            "'Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', Googlebot, Unknown, Unknown",
            "'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0', Web, Chrome,No",
            "'Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1', Mobile, No, iPhone"
    })
    public void testUserAgent(String user_agent, String platform, String browser, String device){
        Map<String, String>  headers = new HashMap<>();
        headers.put("User-Agent", user_agent);

        JsonPath response = RestAssured
                .given()
                .headers(headers)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .jsonPath();

        //response.prettyPrint();

        String responsePlatform = response.get("platform");
        String responseBrowser = response.get("browser");
        String responseDevice = response.get("device");

    //    System.out.println(user_agent);
    //    System.out.println("Platform: " + responsePlatform);
    //    System.out.println("Browser: " + responseBrowser);
    //    System.out.println("Device: " + responseDevice);
        assertEquals(platform,responsePlatform, "Wrong platform for "+ user_agent);
        assertEquals(browser,responseBrowser, "Wrong browser for "+ user_agent);
        assertEquals(device,responseDevice, "Wrong device for "+ user_agent);

    }

}
