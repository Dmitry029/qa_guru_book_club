package tests;

import io.qameta.allure.Feature;
import models.login.LoginBodyModel;
import models.loguot.EmptyBodyModel;
import models.loguot.LogoutBodyModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tests.TestData.*;

@Feature("Выход из аккаунта")
@DisplayName("Выход из аккаунта (logout)")
public class LogoutTests extends TestBase {

    private LoginBodyModel loginData;

    @BeforeEach
    public void prepareLoginData() {
        loginData = step("Подготовка данных для авторизации", () ->
            new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD)
        );
    }

    @Test
    @DisplayName("Успешный выход из аккаунта с валидным refresh-токеном")
    public void successfulLogoutTest() {
        String refreshToken = api.auth.loginAndGetRefreshToken(loginData);
        LogoutBodyModel logoutData = step("Подготовка данных для logout", () ->
            new LogoutBodyModel(refreshToken)
        );
        api.auth.logout(logoutData);
    }

    @Test
    @DisplayName("Неуспешный выход из аккаунта с пустым телом запроса")
    public void logoutWithoutBodyTest() {
        api.auth.loginAndGetRefreshToken(loginData);
        EmptyBodyModel response = api.auth.logoutWithEmptyBody();

        step("Проверка сообщения об ошибке", () ->
            assertEquals(EMPTY_LOGOUT_BODY_ERROR, response.refresh().getFirst())
        );
    }

    @Test
    @DisplayName("Ошибка 404 при неправильном указании basePath")
    public void incorrectBasePath404Test() {
        String refreshToken = api.auth.loginAndGetRefreshToken(loginData);
        LogoutBodyModel logoutData = step("Подготовка данных для logout", () ->
            new LogoutBodyModel(refreshToken)
        );
        api.auth.logoutWithIncorrectBasePath(logoutData);
    }
}
