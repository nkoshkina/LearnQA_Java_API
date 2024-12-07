package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Get user data cases")
@Feature("Get user data")
@Story("Get info about user")
public class UserGetTest extends BaseTestCase {

    String baseUrl = "https://playground.learnqa.ru/api/";
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    @Description("This test checks get user data by non-authorized user")
    @DisplayName("Get user data: non-authorized user")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserDataNotAuth(){
        Response responseUserData = apiCoreRequests
                .makeGetRequestWoParams(baseUrl + "user/2");

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Test
    @Description("This test checks get user data of same user by authorized user")
    @DisplayName("Get user data of authorized user")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserDataAuthAsSameUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(baseUrl+"user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = apiCoreRequests
                .makeGetRequestWithHeaderCookie(baseUrl + "user/2", header, cookie);

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }
    @Test
    @Description("This test checks get user data of another user by authorized user")
    @DisplayName("Get user data of another user")
    @Owner("N.K.")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserDataAuthAsOtherUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(baseUrl+"user/login", authData);


        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = apiCoreRequests
                .makeGetRequestWithHeaderCookie(baseUrl + "user/1", header, cookie);

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

}
