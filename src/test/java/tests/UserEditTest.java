package tests;

import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Edit user cases")
@Feature("Editing")
@Story("User editing")
public class UserEditTest extends BaseTestCase {

    String baseUrl = "https://playground.learnqa.ru/api/";
    String userId;
    Map<String, String> userData;
    JsonPath responseCreateAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void generateUser(){
        //GENERATE USER
        this.userData = DataGenerator.getRegistrationData();

        this.responseCreateAuth = apiCoreRequests
                .makePostRequestJsonResponse(baseUrl + "user", userData);

        this.userId =responseCreateAuth.getString("id");
    }

    @Test
    @Description("This test successfully edit created user")
    @DisplayName("Test positive edit user: EditJustCreated")
    @Severity(SeverityLevel.BLOCKER)
    public void testEditJustCreatedTest(){
        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(baseUrl + "user/login",authData);

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequestWithHeaderCookie(
                        baseUrl + "user/" +userId,
                        editData,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequestWithHeaderCookie(
                        baseUrl + "user/" +userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @Description("This test edit user by non-authorized")
    @DisplayName("Test negative edit user: w/o authorization")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("N.K.")
    public void testEditByNonAuthorized(){
        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequestWoHeaderCookie(
                        baseUrl + "user/" +userId,
                        editData
                );

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"Auth token not supplied\"}");
    }

    @Test
    @Description("This test edit user by other user")
    @DisplayName("Test negative: edit user by other user")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("N.K.")
    public void testEditOtherUser(){
        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(baseUrl + "user/login",authData);

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequestWithHeaderCookie(
                        baseUrl + "user/" + userId,
                        editData,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );
        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"Please, do not edit test users with ID 1, 2, 3, 4 or 5.\"}");
    }

    @Test
    @Description("This test try to edit created user with wrong email")
    @DisplayName("Test negative edit user: wrong email")
    @Severity(SeverityLevel.NORMAL)
    @Owner("N.K.")
    public void testEditJustCreatedWithWrongEmail(){
        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(baseUrl + "user/login",authData);

        //EDIT
        String email = "testtest.com";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", email);

        Response responseEditUser = apiCoreRequests
                .makePutRequestWithHeaderCookie(
                        baseUrl + "user/" +userId,
                        editData,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );
        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"Invalid email format\"}");

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequestWithHeaderCookie(
                        baseUrl + "user/" +userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );

        Assertions.assertJsonByName(responseUserData, "email", userData.get("email"));
    }
    @Test
    @Description("This test try to edit created user with wrong firstName")
    @DisplayName("Test negative edit user: wrong firstName")
    @Owner("N.K.")
    @Severity(SeverityLevel.TRIVIAL)
    public void testEditJustCreatedWithWrongFirstName(){
        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(baseUrl + "user/login",authData);

        //EDIT
        String firstName = "X";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", firstName);

        Response responseEditUser = apiCoreRequests
                .makePutRequestWithHeaderCookie(
                        baseUrl + "user/" +userId,
                        editData,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );
        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"The value for field `firstName` is too short\"}");

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequestWithHeaderCookie(
                        baseUrl + "user/" +userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );

        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }
}
