package tests;

import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Delete user cases")
@Feature("Deleting")
@Story("User deleting")
public class UserDeleteTest extends BaseTestCase {

    String baseUrl = "https://playground.learnqa.ru/api/";

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    @Description("This test try to delete special user")
    @DisplayName("Test negative: delete special user")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("N.K.")
    public void testDeleteSpecialUser(){
        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(baseUrl + "user/login",authData);

        //TRY TO DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequestWithHeaderCookie(
                        baseUrl + "user/2",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );
        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertResponseTextEquals(responseDeleteUser, "{\"error\":\"Please, do not delete test users with ID 1, 2, 3, 4 or 5.\"}");

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequestWithHeaderCookie(
                        baseUrl + "user/2",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );

        Assertions.assertJsonByName(responseUserData, "id", "2");
    }

    @Test
    @Description("This test deletes created user")
    @DisplayName("Test positive: delete created user")
    @Severity(SeverityLevel.NORMAL)
    @Owner("N.K.")
    public void testDeleteCreatedUser(){
        //CREATE
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests
                .makePostRequestJsonResponse(baseUrl + "user", userData);

        String userId =responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(baseUrl + "user/login",authData);

        //TRY TO DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequestWithHeaderCookie(
                        baseUrl + "user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );
        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);
        Assertions.assertResponseTextEquals(responseDeleteUser, "{\"success\":\"!\"}");

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequestWithHeaderCookie(
                        baseUrl + "user/" +userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );

        Assertions.assertResponseCodeEquals(responseUserData, 404);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    @Test
    @Description("This test tries to delete other user")
    @DisplayName("Test negative: delete other user")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("N.K.")
    public void testDeleteOtherUser(){
        //CREATE
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests
                .makePostRequestJsonResponse(baseUrl + "user", userData);

        String userId =responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(baseUrl + "user/login",authData);

        //TRY TO DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequestWithHeaderCookie(
                        baseUrl + "user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );
        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertResponseTextEquals(responseDeleteUser, "{\"error\":\"Please, do not delete test users with ID 1, 2, 3, 4 or 5.\"}");

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequestWithHeaderCookie(
                        baseUrl + "user/" +userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );

        Assertions.assertResponseCodeEquals(responseUserData, 200);
        Assertions.assertResponseTextEquals(responseUserData, "{\"username\":\""+ userData.get("username")+"\"}");
    }

}
