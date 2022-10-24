package UserTest;

import Order.IDIngredientList;
import Order.IngredientsList;
import Order.OrderClient;
import Order.OrdersList;
import User.User;
import User.UserClient;
import User.UserGenerate;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class OrderListApiTest {
    private IngredientsList ingredientsList;
    private OrderClient orderClient;
    private OrdersList ordersList;
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
        ordersList = new OrdersList();
        RestAssured.baseURI = Config.BasePage.URL;
        ingredientsForOrder = new IDIngredientList();

    }

    @Test
    @DisplayName("Get Order List")
    public void getOrdersListCorrectTest() {
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
        for (int j=0; j<3; j++) {
            //Выбрали ингридиенты в заказ
            Random random = new Random();
            int randNum = 0;
            for (int i = 0; i < 5; i = i + 1) {
                randNum = random.nextInt(size);
                ingredientsForOrder.addId(ingredientsList.getIngredients().get(randNum).get_id());
            }
            //Делаем заказ
            ValidatableResponse orderResponse = orderClient.createOrderWithToken(accessToken, ingredientsForOrder);
            int orderStatusCode = orderResponse.extract().statusCode();
            assertEquals("The Response Code Order Create is incorrect", SC_OK, orderStatusCode); // проверяем код ответа сервера
            boolean isOrderCreated = orderResponse.extract().path("success");
            assertTrue("Order not created", isOrderCreated); // проверяем ответ Api
            assertNotNull("Order is not exists", orderResponse.extract().path("order"));
        }
        ValidatableResponse orderListResponse = orderClient.getOrdersList(accessToken);
        int orderListStatusCode = orderListResponse.extract().statusCode();
        assertEquals("The Response Code Order Create is incorrect", SC_OK, orderListStatusCode);
        ordersList = orderListResponse.extract().as(OrdersList.class);
        int actualNumber = ordersList.getOrders().size();
        int expectedNumber = 3;
        assertEquals("The count of return orders is incorrect",expectedNumber,actualNumber);
    }
    @Test
    @DisplayName("Get Order List without authorization")
    public void getOrdersListWithoutAuthTest() {
        ValidatableResponse orderListResponse = orderClient.getOrdersListWithoutLogin();
        int orderListStatusCode = orderListResponse.extract().statusCode();
        assertEquals("The Response Code Order Create is incorrect", SC_UNAUTHORIZED, orderListStatusCode);
        ordersList = orderListResponse.extract().as(OrdersList.class);
        boolean isSameCreated =orderListResponse.extract().path("success");
        assertFalse("User not created", isSameCreated); // проверяем ответ Api
        String textAnswer = orderListResponse.extract().path("message");
        assertEquals("The text of answer is incorrect", textAnswer, "You should be authorised");
        isCreated = false;

    }

    @After
    public void tearDown() {
        if(isCreated==true) {userClient.delete(accessToken);}
    }
}



