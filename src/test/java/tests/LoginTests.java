package tests;

import models.login.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tests.TestData.*;

@DisplayName("Вход в аккаунт")
public class LoginTests extends TestBase {

    @Test
    @DisplayName("Успешный вход")
    public void successfulLoginTest() {
        LoginBodyModel loginData = step("Подготовка данных для запроса", () ->
            new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD)
        );

        SuccessfulLoginResponseModel loginResponse = step("Авторизация", () ->
            api.auth.login(loginData)
        );

        step("Проверка полученных данных", () -> {
            String expectedTokenPath = LOGIN_TOKEN_PREFIX;
            String actualAccess = loginResponse.access();
            String actualRefresh = loginResponse.refresh();

            assertThat(actualAccess).startsWith(expectedTokenPath);
            assertThat(actualRefresh).startsWith(expectedTokenPath);
            assertThat(actualAccess).isNotEqualTo(actualRefresh);
        });
    }

    @Test
    @DisplayName("Вход с невалидным паролем")
    public void wrongCredentialsLoginTest() {
        LoginBodyModel loginData = step("Подготовка данных для запроса", () ->
            new LoginBodyModel(LOGIN_USERNAME, LOGIN_WRONG_PASSWORD)
        );

        WrongCredentialsLoginResponseModel loginResponse = step("Отправка запроса с невалидным паролем", () ->
            api.auth.loginWithWrongCredentials(loginData)
        );

        step("Проверка сообщения об ошибке", () ->
            assertThat(loginResponse.detail()).isEqualTo(LOGIN_WRONG_CREDENTIALS_ERROR)
        );
    }

    @Test
    @DisplayName("Вход с пустым username")
    public void emptyUsernameLoginTest() {
        LoginBodyModel loginData = step("Подготовка данных для запроса", () ->
            new LoginBodyModel("", LOGIN_PASSWORD)
        );

        EmptyUsernameResponseModel response = step("Отправка запроса с пустым username", () ->
            api.auth.loginWithEmptyUsername(loginData)
        );

        step("Проверка сообщения об ошибке", () ->
            assertEquals(EMPTY_USERNAME_ERROR, response.username().getFirst())
        );
    }

    @Test
    @DisplayName("Вход с пустым password")
    public void emptyPasswordLoginTest() {
        LoginBodyModel loginData = step("Подготовка данных для запроса", () ->
            new LoginBodyModel(LOGIN_USERNAME, "")
        );

        EmptyPasswordResponseModel response = step("Отправка запроса с пустым password", () ->
            api.auth.loginWithEmptyPassword(loginData)
        );

        step("Проверка сообщения об ошибке", () ->
            assertEquals(EMPTY_PASSWORD_ERROR, response.password().getFirst())
        );
    }

    @Test
    @DisplayName("Вход с пустыми password и username")
    public void emptyCredentialsLoginTest() {
        LoginBodyModel loginData = step("Подготовка данных для запроса", () ->
            new LoginBodyModel("", "")
        );

        EmptyCredentialsResponseModel response = step("Отправка запроса с пустыми данными", () ->
            api.auth.loginWithEmptyCredentials(loginData)
        );

        step("Проверка сообщения об ошибке", () -> {
            assertEquals(EMPTY_USERNAME_ERROR, response.username().getFirst());
            assertEquals(EMPTY_PASSWORD_ERROR, response.password().getFirst());
        });
    }
}
