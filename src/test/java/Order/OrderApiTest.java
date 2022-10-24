package Order;

import User.User;
import User.UserClient;
import User.UserGenerate;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class OrderApiTest {
    private IngredientsList ingredientsList;
    private OrderClient orderClient;
    private IDIngredientList ingredientsForOrder;
    private User user;
    String accessToken;
    boolean isCreated = false;
    private UserClient userClient;
    @Before
    public void setUp() {
        orderClient = new OrderClient();
        user = UserGenerate.getDefaultUser();
        userClient = new UserClient();
        RestAssured.baseURI = Config.BasePage.URL;
        ingredientsForOrder = new IDIngredientList();

    }

    @Test
    @DisplayName("Create Order")
    public void orderCreatedCorrectTest(){
        //Получили список всех доступных ингредиентов
        ValidatableResponse response = orderClient.getIngredientsList();
        ingredientsList = response.extract().as(IngredientsList.class);
        int size = this.ingredientsList.getIngredients().size();
        //Выбрали ингридиенты в заказ
        Random random = new Random();
        int randNum = 0;
        for (int i=0;i<5; i=i+1) {
            randNum = random.nextInt(size);
            ingredientsForOrder.addId(ingredientsList.getIngredients().get(randNum).get_id());
        }
        //Создали пользователя
        ValidatableResponse userResponse = userClient.create(user);
        int statusCode = userResponse.extract().statusCode();
        assertEquals("The Response Code is incorrect", SC_OK, statusCode); // проверяем код ответа сервера
        //Авторизовались
        ValidatableResponse loginResponse = userClient.login(user);
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals("User is not login", SC_OK, loginStatusCode);
        accessToken = loginResponse.extract().path("accessToken");
        accessToken = accessToken.split(" ")[1];
        assertNotNull("Access Token is null",accessToken);
        isCreated = true;
        //Делаем заказ
        ValidatableResponse orderResponse = orderClient.createOrderWithToken(accessToken,ingredientsForOrder);
        int orderStatusCode = orderResponse.extract().statusCode();
        assertEquals("The Response Code Order Create is incorrect", SC_OK, orderStatusCode); // проверяем код ответа сервера
        boolean isOrderCreated = orderResponse.extract().path("success");
        assertTrue("Order not created", isOrderCreated); // проверяем ответ Api
        assertNotNull("Order is not exists", orderResponse.extract().path("order"));

    }
    @Test
    @DisplayName("Create Order without authorization")
    public void orderCreatedWithoutAuthTest(){
        //Получили список всех доступных ингредиентов
        ValidatableResponse response = orderClient.getIngredientsList();
        ingredientsList = response.extract().as(IngredientsList.class);
        int size = this.ingredientsList.getIngredients().size();
        //Выбрали ингридиенты в заказ
        Random random = new Random();
        int randNum = 0;
        for (int i=0;i<5; i=i+1) {
            randNum = random.nextInt(size);
            ingredientsForOrder.addId(ingredientsList.getIngredients().get(randNum).get_id());
        }
        //Делаем заказ
        ValidatableResponse orderResponse = orderClient.createOrderWithoutToken(ingredientsForOrder);
        int orderStatusCode = orderResponse.extract().statusCode();
        assertEquals("The Response Code Order Create is incorrect", SC_OK, orderStatusCode); // проверяем код ответа сервера
        boolean isOrderCreated = orderResponse.extract().path("success");
        assertTrue("Order not created", isOrderCreated); // проверяем ответ Api
        assertNotNull("Order is not exists", orderResponse.extract().path("order"));

    }
    @Test
    @DisplayName("Try to create Order without ingredients")
    public void orderNotCreatedWithoutIngredientsTest(){
        //Получили список всех доступных ингредиентов
        ValidatableResponse response = orderClient.getIngredientsList();
        ingredientsList = response.extract().as(IngredientsList.class);
        int size = this.ingredientsList.getIngredients().size();
        //Создали пользователя
        ValidatableResponse userResponse = userClient.create(user);
        int statusCode = userResponse.extract().statusCode();
        assertEquals("The Response Code is incorrect", SC_OK, statusCode); // проверяем код ответа сервера
        //Авторизовались
        ValidatableResponse loginResponse = userClient.login(user);
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals("User is not login", SC_OK, loginStatusCode);
        accessToken = loginResponse.extract().path("accessToken");
        accessToken = accessToken.split(" ")[1];
        assertNotNull("Access Token is null",accessToken);
        isCreated = true;
        //Делаем заказ
        ValidatableResponse orderResponse = orderClient.createOrderWithToken(accessToken,ingredientsForOrder);
        int orderStatusCode = orderResponse.extract().statusCode();
        assertEquals("The Response Code Order Create is incorrect", SC_BAD_REQUEST, orderStatusCode); // проверяем код ответа сервера
        boolean isOrderCreated = orderResponse.extract().path("success");
        assertFalse("Order not created", isOrderCreated); // проверяем ответ Api
        String textAnswer = orderResponse.extract().path("message");
        assertEquals("The text of answer is incorrect", textAnswer, "Ingredient ids must be provided");

    }
    @Test
    @DisplayName("Try Create Order with incorrect ingredients")
    public void orderCreatedIncorrectIngredientsTest(){
        //Выбрали ингридиенты в заказ
        Random random = new Random();
        int randNum = 0;
        for (int i=0;i<5; i=i+1) {
            ingredientsForOrder.addId(RandomStringUtils.randomAlphanumeric(10));
        }

        //Создали пользователя
        ValidatableResponse userResponse = userClient.create(user);
        int statusCode = userResponse.extract().statusCode();
        assertEquals("The Response Code is incorrect", SC_OK, statusCode); // проверяем код ответа сервера
        //Авторизовались
        ValidatableResponse loginResponse = userClient.login(user);
        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals("User is not login", SC_OK, loginStatusCode);
        accessToken = loginResponse.extract().path("accessToken");
        accessToken = accessToken.split(" ")[1];
        assertNotNull("Access Token is null",accessToken);
        isCreated = true;
        //Делаем заказ
        ValidatableResponse orderResponse = orderClient.createOrderWithToken(accessToken,ingredientsForOrder);
        int orderStatusCode = orderResponse.extract().statusCode();
        assertEquals("The Response Code Order Create is incorrect", SC_INTERNAL_SERVER_ERROR, orderStatusCode); // проверяем код ответа сервера

    }
    @After
    public void tearDown() {
        if(isCreated==true) {userClient.delete(accessToken);}
    }
}