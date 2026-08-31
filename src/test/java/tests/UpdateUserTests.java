package tests;

import models.login.LoginBodyModel;
import models.user.InvalidEmailResponseModel;
import models.user.UpdateUserBodyModel;
import models.user.UserResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

@DisplayName("Обновление данных пользователя")
public class UpdateUserTests extends TestBase {

    private String accessToken;

    @BeforeEach
    public void auth() {
        LoginBodyModel loginData = step("Подготовка данных для авторизации", () ->
            new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD)
        );

        accessToken = step("Авторизация и получение access-токена", () ->
            api.auth.loginAndGetAccessToken(loginData)
        );
    }

    @Test
    @DisplayName("Успешное обновление имени, фамилии и email")
    public void successfulUpdateUserTest() {
        UpdateUserBodyModel updateData = step("Подготовка данных для обновления профиля", () ->
            new UpdateUserBodyModel(
                UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME,
                UPDATED_EMAIL
            )
        );

        UserResponseModel response = step("Отправка запроса на обновление профиля", () ->
            api.users.updateUser(accessToken, updateData)
        );

        step("Проверка данных профиля", () -> {
            assertThat(response.firstName()).isEqualTo(UPDATED_FIRST_NAME);
            assertThat(response.lastName()).isEqualTo(UPDATED_LAST_NAME);
            assertThat(response.email()).isEqualTo(UPDATED_EMAIL);
        });
    }

    @Test
    @DisplayName("Обновление с невалидным email")
    public void wrongPasswordLoginTest() {
        UpdateUserBodyModel updateData = step("Подготовка данных с невалидным email", () ->
            new UpdateUserBodyModel(
                UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME,
                INVALID_EMAIL
            )
        );

        InvalidEmailResponseModel response = step("Отправка запроса на обновление профиля с некорректным email", () ->
            api.users.updateUserWithInvalidEmail(accessToken, updateData)
        );

        step("Проверка сообщения об ошибке", () ->
            assertThat(response.email().getFirst()).isEqualTo(INVALID_EMAIL_ERROR)
        );
    }
}
