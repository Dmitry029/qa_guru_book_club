package tests;

import io.qameta.allure.Feature;
import models.login.LoginBodyModel;
import models.loguot.EmptyBodyModel;
import models.loguot.LogoutBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tests.TestData.*;

@Feature("Выход из аккаунта")
@DisplayName("Выход из аккаунта (logout)")
public class LogoutTests extends TestBase {

    @Test
    @DisplayName("Успешный выход из аккаунта с валидным refresh-токеном")
    public void successfulLogoutTest() {
        LoginBodyModel loginData = step("Подготовка данных для авторизации", () ->
            new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD)
        );

        String refreshToken = step("Авторизация и получение refresh-токена", () ->
            api.auth.loginAndGetRefreshToken(loginData)
        );

        LogoutBodyModel logoutData = step("Подготовка данных для logout", () ->
            new LogoutBodyModel(refreshToken)
        );

        step("Отправка запроса logout с refresh-токеном", () ->
            api.auth.logout(logoutData)
        );
    }

    @Test
    @DisplayName("Неуспешный выход из аккаунта с пустым телом запроса")
    public void logoutWithoutBodyTest() {
        LoginBodyModel loginData = step("Подготовка данных для авторизации", () ->
            new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD)
        );

        step("Авторизация", () ->
            api.auth.loginAndGetRefreshToken(loginData)
        );

        EmptyBodyModel response = step("Отправка запроса logout с пустым body", () ->
            api.auth.logoutWithoutBody()
        );

        step("Проверка сообщения об ошибке", () ->
            assertEquals(EMPTY_LOGOUT_BODY_ERROR, response.refresh().getFirst())
        );
    }

    @Test
    @DisplayName("Ошибка 404 при неправильном указании basePath")
    public void incorrectBasePath404Test() {
        LoginBodyModel loginData = step("Подготовка данных для авторизации", () ->
            new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD)
        );

        String refreshToken = step("Авторизация и получение refresh-токена", () ->
            api.auth.loginAndGetRefreshToken(loginData)
        );

        LogoutBodyModel logoutData = step("Подготовка данных для logout", () ->
            new LogoutBodyModel(refreshToken)
        );

        step("Отправка запроса logout с неправильным basePath", () ->
            api.auth.logoutWithIncorrectBasePath(logoutData)
        );
    }
}
