package Order;
import Config.RestClient;
import User.User;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import Config.RestClient;

import java.util.List;

public class OrderClient extends RestClient {
    public static final String INGREDIENTS_LIST = "/api/ingredients";
    public static final String CREATE_ORDER= "/api/orders";
    public static final String LIST_ORDERS= "/api/orders";


    @Step("Get ingredients LIST")
    public ValidatableResponse getIngredientsList(){
        return given()
                .spec(getBaseSpec())
                .when()
                .get(INGREDIENTS_LIST)
                .then();

    }

    @Step("Create order")
    public ValidatableResponse createOrderWithToken(String accessToken, IDIngredientList ingredients){
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .and()
                .body(ingredients)
                .when()
                .post(CREATE_ORDER)
                .then();
    }
    @Step("Create order without auth")
    public ValidatableResponse createOrderWithoutToken(IDIngredientList ingredients){
        return given()
                .spec(getBaseSpec())
                .body(ingredients)
                .when()
                .post(CREATE_ORDER)
                .then();
    }

    @Step("Get orders List")
    public ValidatableResponse getOrdersList(String accessToken){
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .when()
                .get(LIST_ORDERS)
                .then();
    }
    @Step("Get orders List Whithout Auth")
    public ValidatableResponse getOrdersListWithoutLogin(){
        return given()
                .spec(getBaseSpec())
                .when()
                .get(LIST_ORDERS)
                .then();
    }


}

