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
import static specs.logout.LogoutSpec.emptyBodyResponseSpec;
import static specs.logout.LogoutSpec.logoutRequestSpec;
import static tests.TestData.*;

@Feature("Выход из аккаунта")
@DisplayName("Выход из аккаунта (logout)")
public class LogoutTests extends TestBase {

    @Test
    @DisplayName("Успешный выход из аккаунта с валидным refresh-токеном")
    public void successfulLogoutTest() {
        LoginBodyModel loginData = step("Подготовка валидных данных для успешного входа", () ->
            new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD)
        );

        String refreshToken = step("Авторизация и получение токена", () -> {
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

        LoginBodyModel loginData = step("Подготовка валидных данных для успешного входа", () ->
            new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD)
        );

        step("Авторизация и получение токена", () -> {
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

        LoginBodyModel loginData = step("Подготовка валидных данных для успешного входа", () ->
            new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD)
        );

        String refreshToken = step("Авторизация и получение токена", () -> {
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
