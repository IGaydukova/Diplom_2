package User;//import io.qameta.allure.Step;
import Config.RestClient;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
public class UserClient extends RestClient {
    private static final String CREATE_USER_URL = "/api/auth/register";
    private static final String AUTH_USER_URL = "/api/auth/login";
    private static final String TOKEN_URL = "/api/auth/token";
    private static final String DEL_UPD_URL = "/api/auth/user";
    @Step("Create User")
    public ValidatableResponse create(User user){
        return given()
                .spec(getBaseSpec())
                //.header("Content-type", "application/json")
                .body(user)
                .when()
                .post(CREATE_USER_URL)
                .then();
    }

    @Step("Login User")
    public ValidatableResponse login(User user){
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(AUTH_USER_URL)
                .then();
    }
    @Step("Delete User")
    public ValidatableResponse delete (String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .when()
                .delete(DEL_UPD_URL)
                .then();
    }
    @Step("Upgate User")
    public ValidatableResponse update (String accessToken, User user) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .and()
                .body(user)
                .when()
                .patch(DEL_UPD_URL)
                .then();
    }
    @Step("Update User without Login")
    public ValidatableResponse updateWithoutLogin (User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .patch(DEL_UPD_URL)
                .then();
    }


}
