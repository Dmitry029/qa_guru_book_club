package tests;

import models.login.LoginBodyModel;
import models.user.UpdateUserBodyModel;
import models.user.UserResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.user.UserSpec.userResponse400Spec;
import static tests.TestData.*;

@DisplayName("Обновление данных пользователя")
public class UpdateUserTests extends TestBase {

    private String accessToken;

    @BeforeEach
    public void auth() {
        accessToken = step("Получить access-токена", () -> {
            LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
            return api.auth.loginAndGetAccessToken(loginData);
        });
    }

    @Test
    @DisplayName("Успешное обновление имени, фамилии и email")
    public void successfulUpdateUserTest() {

        UpdateUserBodyModel updateData = step("Подготовка данных для обновления", () ->
            new UpdateUserBodyModel(
                UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME,
                UPDATED_EMAIL
            ));

        UserResponseModel response =
            step("Запрос на обновление профиля с валидным токеном", () ->
                api.users.updateUser(accessToken, updateData)
            );

        step("Проверка того, что данные профиля успешно обновились", () -> {
            assertThat(response.firstName()).isEqualTo(UPDATED_FIRST_NAME);
            assertThat(response.lastName()).isEqualTo(UPDATED_LAST_NAME);
            assertThat(response.email()).isEqualTo(UPDATED_EMAIL);
        });
    }

    @Test
    @DisplayName("Обновление с невалидным email")
    public void wrongPasswordLoginTest() {
        UpdateUserBodyModel updateData = step("Подготовить данные профиля с невалидным email", () ->
            new UpdateUserBodyModel(
                UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME,
                INVALID_EMAIL
            )
        );

        var response = step("Отправить запрос на обновление профиля с некорректным email", () ->
            api.users.updateUserGetResponse(accessToken, updateData, userResponse400Spec)
        );

        step("Проверить наличие ошибки валидации email (400 Bad Request)", () ->
            assertThat(response.path("email[0]").toString()).isEqualTo(INVALID_EMAIL_ERROR)
        );
    }
}
