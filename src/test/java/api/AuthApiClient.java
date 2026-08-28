package api;

import io.qameta.allure.Step;
import models.login.LoginBodyModel;

import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;

public class AuthApiClient {

    @Step("Авторизация и получение access-токена")
    public String loginAndGetAccessToken(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
            .body(loginBody)
            .when()
            .post("/auth/token/")
            .then()
            .spec(successfulLoginResponseSpec)
            .extract()
            .path("access"); // Извлекаем именно access-токен
    }
}
