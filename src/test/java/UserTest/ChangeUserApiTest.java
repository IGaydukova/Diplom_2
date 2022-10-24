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
public class ChangeUserApiTest {
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
    //Изменение данных пользователя (авторизация)
    @Test
    @DisplayName("Change user name")
    public void userCanChangedNameAfterLoginTest(){
        //Создали пользователя
        ValidatableResponse response = userClient.create(user);
        int statusCode = response.extract().statusCode();
        assertEquals("The Response Code is incorrect", SC_OK, statusCode); // проверяем код ответа сервера
       //Авторизовались
        ValidatableResponse loginResponse = userClient.login(user);
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals("User is not login", SC_OK, loginStatusCode);
        accessToken = loginResponse.extract().path("accessToken");
        accessToken = accessToken.split(" ")[1];
        assertNotNull("Access Token is null",accessToken);
        refreshToken = loginResponse.extract().path("refreshToken");
        assertNotNull("Refresh Token is null",refreshToken);
        boolean isCreated =loginResponse.extract().path("success");
        assertTrue("User not created", isCreated); // проверяем ответ Api
        isCreated = true;
        user.setName("Update"+user.getName());
        ValidatableResponse updateResponse = userClient.update(accessToken, user);
        int updateStatusCode = updateResponse.extract().statusCode();
        assertEquals("The Response Code is incorrect", SC_OK, updateStatusCode); // проверяем код ответа сервера
    }

    @Test
    @DisplayName("Change user password")
    public void userCanChangedPasswordAfterLoginTest(){
        //Создали пользователя
        ValidatableResponse response = userClient.create(user);
        int statusCode = response.extract().statusCode();
        assertEquals("The Response Code is incorrect", SC_OK, statusCode); // проверяем код ответа сервера
        //Авторизовались
        ValidatableResponse loginResponse = userClient.login(user);
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals("User is not login", SC_OK, loginStatusCode);
        accessToken = loginResponse.extract().path("accessToken");
        accessToken = accessToken.split(" ")[1];
        assertNotNull("Access Token is null",accessToken);
        refreshToken = loginResponse.extract().path("refreshToken");
        assertNotNull("Refresh Token is null",refreshToken);
        boolean isCreated =loginResponse.extract().path("success");
        assertTrue("User not created", isCreated); // проверяем ответ Api
        isCreated = true;
        user.setPassword("upd"+user.getPassword());
        ValidatableResponse updateResponse = userClient.update(accessToken, user);
        int updateStatusCode = updateResponse.extract().statusCode();
        assertEquals("The Response Code is incorrect", SC_OK, updateStatusCode); // проверяем код ответа сервера
    }

    @Test
    @DisplayName("Try to change user without authorization")
    public void userCanNotChangedWithoutLoginTest(){
        //Создали пользователя
        ValidatableResponse response = userClient.create(user);
        int statusCode = response.extract().statusCode();
        assertEquals("The Response Code is incorrect", SC_OK, statusCode); // проверяем код ответа сервера
        boolean isCreated =response.extract().path("success");
        assertTrue("User not created", isCreated); // проверяем ответ Api
        isCreated = true;
        user.setName("Update"+user.getName());
        user.setEmail("Update"+user.getEmail());
        user.setPassword("upd"+user.getPassword());
        ValidatableResponse updateResponse = userClient.updateWithoutLogin(user);
        int updateStatusCode = updateResponse.extract().statusCode();
        assertEquals("The Response Code is incorrect", SC_UNAUTHORIZED, updateStatusCode); // проверяем код ответа сервера
        String textAnswer = updateResponse.extract().path("message");
        assertEquals("The text of answer is incorrect", "You should be authorised", textAnswer); // проверяем формат с

    }
    @Test
    @DisplayName("Change user email")
    public void userCanChangedEmailAfterLoginTest(){
        //Создали пользователя
        ValidatableResponse response = userClient.create(user);
        int statusCode = response.extract().statusCode();
        assertEquals("The Response Code is incorrect", SC_OK, statusCode); // проверяем код ответа сервера
        //Авторизовались
        ValidatableResponse loginResponse = userClient.login(user);
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals("User is not login", SC_OK, loginStatusCode);
        accessToken = loginResponse.extract().path("accessToken");
        accessToken = accessToken.split(" ")[1];
        assertNotNull("Access Token is null",accessToken);
        refreshToken = loginResponse.extract().path("refreshToken");
        assertNotNull("Refresh Token is null",refreshToken);
        boolean isCreated =loginResponse.extract().path("success");
        assertTrue("User not created", isCreated); // проверяем ответ Api
        isCreated = true;
        user.setEmail("Update"+user.getEmail());
        ValidatableResponse updateResponse = userClient.update(accessToken, user);
        int updateStatusCode = updateResponse.extract().statusCode();
        assertEquals("The Response Code is incorrect", SC_OK, updateStatusCode); // проверяем код ответа сервера


    }
    @After
    public void tearDown(){
        if(isCreated==true) {userClient.delete(accessToken);}
    }
}
