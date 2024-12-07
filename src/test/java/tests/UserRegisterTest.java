package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
@Epic("Registration cases")
@Feature("Registration")
@Story("Add new user")
public class UserRegisterTest extends BaseTestCase {
     String baseUrl = "https://playground.learnqa.ru/api/user";

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test checks registration of user with existing email")
    @DisplayName("Test negative user registration: existing email ")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(baseUrl, userData);

            Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
            Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email+ "' already exists");
    }

    @Test
    @Description("This test checks registration of user with correct parameters")
    @DisplayName("Test positive user registration: correct parameters")
    @Severity(SeverityLevel.BLOCKER)
    public void testCreateUserSuccessfully(){
        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(baseUrl, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    @Description("This test checks registration of user with wrong email")
    @DisplayName("Test negative user registration: wrong email")
    @Owner("N.K.")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserWithWrongEmail(){
        String email = "tmpexample.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(baseUrl, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @Description("This test checks registration of user with short name")
    @DisplayName("Test negative user registration: short name")
    @Owner("N.K.")
    @Severity(SeverityLevel.TRIVIAL)
    @ParameterizedTest
    @ValueSource(strings = {"username", "firstName", "lastName"})
    public void testCreateUserWithShortName(String key){
        String name = "Y";

        Map<String, String> userData = new HashMap<>();
        userData.put(key, name);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(baseUrl, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of '" + key + "' field is too short");
    }

    @Description("This test checks registration of user with long name")
    @DisplayName("Test negative user registration: long name")
    @Owner("N.K.")
    @Severity(SeverityLevel.TRIVIAL)
    @ParameterizedTest
    @ValueSource(strings = {"username", "firstName", "lastName"})
    public void testCreateUserWithLongName(String key){
        String name = "AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj-AbcdeFghj_K";

        Map<String, String> userData = new HashMap<>();
        userData.put(key, name);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(baseUrl, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of '" + key + "' field is too long");
    }

    @Description("This test checks registration of user without 1 mandatory parameters")
    @DisplayName("Test negative user registration: no parameter")
    @Owner("N.K.")
    @Severity(SeverityLevel.CRITICAL)
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithoutParameter(String key){
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);
        userData.remove(key);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(baseUrl, userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " +key);
    }
}
