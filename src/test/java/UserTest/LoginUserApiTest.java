package UserTest;
import Config.BasePage;
import User.User;
import User.UserClient;
import User.UserGenerate;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

public class LoginUserApiTest {
    private User user;
    String accessToken;
    String refreshToken;
    boolean isCreated = false;
    private UserClient userClient;
    @Before
    public void setUp() {

        user = UserGenerate.getDefaultUser();
        userClient = new UserClient();
        RestAssured.baseURI = Config.BasePage.URL;
    }
//Авторизация существующим пользователем
    @Test
    @DisplayName("Login user")
    public void userCanLoginTest(){
        ValidatableResponse response = userClient.create(user);
        int statusCode = response.extract().statusCode();
        assertEquals("The Response Code is incorrect", SC_OK, statusCode); // проверяем код ответа сервера

        ValidatableResponse loginResponse = userClient.login(user);
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals("User is not login", SC_OK, loginStatusCode);
        accessToken = loginResponse.extract().path("accessToken");
        assertNotNull("Access Token is null",accessToken);
        refreshToken = loginResponse.extract().path("refreshToken");
        assertNotNull("Refresh Token is null",refreshToken);
        boolean isCreated =loginResponse.extract().path("success");
        assertTrue("User not created", isCreated); // проверяем ответ Api

        isCreated = true;
    }

    //Авторизация несуществующим пользователем
    @Test
    @DisplayName("Try to login non exist user")
    public void userCanNotLoginTest(){
        ValidatableResponse loginResponse = userClient.login(user);
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals("User is not login", SC_UNAUTHORIZED, loginStatusCode);
        String textAnswer = loginResponse.extract().path("message");
        assertEquals("The text of answer is incorrect", textAnswer, "email or password are incorrect"); // проверяем формат Ответа
        boolean isCreated =loginResponse.extract().path("success");
        assertFalse("User not created", isCreated); // проверяем ответ Api

        isCreated = false;
    }

    @After
    public void tearDown(){
        if(isCreated==true) {userClient.delete(accessToken);}
    }
}
