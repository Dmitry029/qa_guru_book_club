package tests;

import io.qameta.allure.Feature;
import models.login.LoginBodyModel;
import models.loguot.EmptyBodyModel;
import models.loguot.LogoutBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.logout.LogoutSpec.*;
import static tests.TestData.*;

@Feature("Выход из аккаунта")
@DisplayName("Выход из аккаунта (logout)")
public class LogoutTests extends TestBase {

    @Test
    @DisplayName("Успешный выход из аккаунта с валидным refresh-токеном")
    public void successfulLogoutTest() {

        String refreshToken = step("Авторизация и получение токена", () ->{
            LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
            return api.auth.loginAndGetRefreshToken(loginData);
        });

        step("Отправка запроса logout с refresh-токеном и проверка ответа (200)", () -> {
            LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
            api.auth.logout(logoutData);
        });
    }

    @Test
    @DisplayName("Неуспешный выход из аккаунта с пустым телом запроса")
    public void logoutWithoutBodyTest() {

        step("Авторизация и получение токена", () ->{
            LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
            api.auth.loginAndGetRefreshToken(loginData);
        });

        EmptyBodyModel response = step("Отправка запроса logout с пустым body", () ->
            given(logoutRequestSpec)
                .body("")
                .basePath("/api/v1")
                .when()
                .post("/auth/logout/")
                .then()
                .spec(emptyBodyResponseSpec)
                .extract().as(EmptyBodyModel.class)
        );
        assertEquals(EMPTY_LOGOUT_BODY_ERROR, response.refresh().getFirst());
    }

    @Test
    @DisplayName("Ошибка 404 при неправильном указании basePath")
    public void incorrectBasePath404Test() {

        String refreshToken = step("Авторизация и получение токена", () ->{
            LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
            return api.auth.loginAndGetRefreshToken(loginData);
        });

        step("Отправка запроса logout с неправильным basePath и проверка ответа (404)", () -> {
            LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
            given(logoutRequestSpec)
                .body(logoutData)
                .basePath("/api/v2")
                .when()
                .post("/auth/logout/")
                .then()
                .statusCode(404);
        });
    }
}
