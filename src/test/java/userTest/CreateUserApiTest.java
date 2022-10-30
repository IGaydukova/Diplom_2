package userTest;
import user.User;
import user.UserClient;
import user.UserGenerate;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class CreateUserApiTest  {
private User user;
String accessToken;
String refreshToken;
boolean isCreated = false;
private UserClient userClient;
    @Before
    public void setUp() {
        isCreated = false;
        user = UserGenerate.getDefaultUser();
        userClient = new UserClient();
        RestAssured.baseURI = config.BasePage.URL;
    }

    //Создание пользователя +
    @Test
    @DisplayName("Create user")
    public void userCanCreatedTest(){
        ValidatableResponse response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        assertEquals("The Response Code is incorrect", SC_OK, statusCode); // проверяем код ответа сервера
        isCreated =response.extract().path("success");
        assertTrue("User not created", isCreated); // проверяем ответ Api
        accessToken = response.extract().path("accessToken");
        assertNotNull("Access Token is null",accessToken);
        refreshToken = response.extract().path("refreshToken");
        assertNotNull("Refresh Token is null",refreshToken);
        ValidatableResponse loginResponse = userClient.login(user);
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals("User is not login", SC_OK, loginStatusCode);
    }

    //Нельзя создать двух одинаковых пользователей +
    @Test
    @DisplayName("Try to create same user")
    public void sameUserCanNotCreatedTest(){
        ValidatableResponse response = userClient.createUser(user); // создали пользователя
        int statusCode = response.extract().statusCode();
        assertEquals("The Response Code is incorrect", SC_OK, statusCode); // проверяем код ответа сервера
        isCreated =response.extract().path("success");
        assertTrue("User not created", isCreated); // проверяем ответ Api
        accessToken = response.extract().path("accessToken");
        assertNotNull("Access Token is null",accessToken);
        ValidatableResponse responseSame = userClient.createUser(user); // создали еще одного такого же пользователя
        int statusSameCode = responseSame.extract().statusCode();
        assertEquals("The Response Same Code is incorrect", SC_FORBIDDEN, statusSameCode); // проверяем код ответа сервера
        boolean isSameCreated =responseSame.extract().path("success");
        assertFalse("User not created", isSameCreated); // проверяем ответ Api
        String textAnswer = responseSame.extract().path("message");
        assertEquals("The text of answer is incorrect", textAnswer, "User already exists"); // проверяем формат сообщения
    }

    //если одного из полей нет, запрос возвращает ошибку +
    //без имени
    @Test
    @DisplayName("Try to create user without name")
    public void createUserWithoutNameTest()    {
        user = UserGenerate.getUserWithoutName();
        ValidatableResponse response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        assertEquals("The Response Code is incorrect", SC_FORBIDDEN, statusCode); // проверяем код ответа сервера
        boolean isSameCreated =response.extract().path("success");
        assertFalse("User not created", isSameCreated); // проверяем ответ Api
        String textAnswer = response.extract().path("message");
        assertEquals("The text of answer is incorrect", textAnswer, "Email, password and name are required fields");
        isCreated = false;
    }
    //без пароля
    @Test
    @DisplayName("Try to create user without password")
    public void userCreateWithoutPassTest()    {
        user = UserGenerate.getUserWithoutPassword();
        ValidatableResponse response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        assertEquals("The Response Code is incorrect", SC_FORBIDDEN, statusCode); // проверяем код ответа сервера
        boolean isSameCreated =response.extract().path("success");
        assertFalse("User not created", isSameCreated); // проверяем ответ Api
        String textAnswer = response.extract().path("message");
        assertEquals("The text of answer is incorrect", textAnswer, "Email, password and name are required fields");
        isCreated = false;
    }

    @After
    public void tearDown(){
        if(isCreated) {userClient.deleteUser(accessToken);}
    }
}