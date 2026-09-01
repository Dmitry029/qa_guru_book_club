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
        LoginBodyModel loginData = step("Подготовка данных для авторизации", () ->
            new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD)
        );
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        step("Проверка полученных данных", () -> {
            String actualAccess = loginResponse.access();
            String actualRefresh = loginResponse.refresh();

            assertThat(actualAccess).startsWith(LOGIN_TOKEN_PREFIX);
            assertThat(actualRefresh).startsWith(LOGIN_TOKEN_PREFIX);
            assertThat(actualAccess).isNotEqualTo(actualRefresh);
        });
    }

    @Test
    @DisplayName("Вход с невалидным паролем")
    public void wrongCredentialsLoginTest() {
        LoginBodyModel loginData = step("Подготовка данных с невалидным паролем", () ->
            new LoginBodyModel(LOGIN_USERNAME, LOGIN_WRONG_PASSWORD)
        );
        WrongCredentialsLoginResponseModel loginResponse = api.auth.loginWithInvalidPassword(loginData);

        step("Проверка сообщения об ошибке", () ->
            assertThat(loginResponse.detail()).isEqualTo(LOGIN_WRONG_CREDENTIALS_ERROR)
        );
    }

    @Test
    @DisplayName("Вход с пустым username")
    public void emptyUsernameLoginTest() {
        LoginBodyModel loginData = step("Подготовка данных с пустым username", () ->
            new LoginBodyModel("", LOGIN_PASSWORD)
        );
        EmptyUsernameResponseModel response = api.auth.loginWithEmptyUsername(loginData);

        step("Проверка сообщения об ошибке", () ->
            assertEquals(EMPTY_USERNAME_ERROR, response.username().getFirst())
        );
    }

    @Test
    @DisplayName("Вход с пустым password")
    public void emptyPasswordLoginTest() {
        LoginBodyModel loginData = step("Подготовка данных с пустым password", () ->
            new LoginBodyModel(LOGIN_USERNAME, "")
        );
        EmptyPasswordResponseModel response = api.auth.loginWithEmptyPassword(loginData);

        step("Проверка сообщения об ошибке", () ->
            assertEquals(EMPTY_PASSWORD_ERROR, response.password().getFirst())
        );
    }

    @Test
    @DisplayName("Вход с пустыми password и username")
    public void emptyCredentialsLoginTest() {
        LoginBodyModel loginData = step("Подготовка данных с пустыми username и password", () ->
            new LoginBodyModel("", "")
        );
        EmptyCredentialsResponseModel response = api.auth.loginWithEmptyCredentials(loginData);

        step("Проверка сообщения об ошибке", () -> {
            assertEquals(EMPTY_USERNAME_ERROR, response.username().getFirst());
            assertEquals(EMPTY_PASSWORD_ERROR, response.password().getFirst());
        });
    }
}
