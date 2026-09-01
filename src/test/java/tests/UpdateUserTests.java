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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tests.TestData.*;

@DisplayName("Обновление данных пользователя")
public class UpdateUserTests extends TestBase {

    private String accessToken;

    @BeforeEach
    public void auth() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        accessToken = api.auth.loginAndGetAccessToken(loginData);
    }

    @Test
    @DisplayName("Успешное обновление имени, фамилии и email")
    public void successfulUpdateUserTest() {
        UpdateUserBodyModel updateData =
            new UpdateUserBodyModel(
                UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME,
                UPDATED_EMAIL
            );
        UserResponseModel response = api.users.updateUser(accessToken, updateData);

        step("Проверка того, что данные профиля успешно обновились", () -> {
            assertThat(response.firstName()).isEqualTo(UPDATED_FIRST_NAME);
            assertThat(response.lastName()).isEqualTo(UPDATED_LAST_NAME);
            assertThat(response.email()).isEqualTo(UPDATED_EMAIL);
        });
    }

    @Test
    @DisplayName("Обновление с невалидным email")
    public void wrongPasswordLoginTest() {
        UpdateUserBodyModel updateData =
            new UpdateUserBodyModel(
                UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME,
                INVALID_EMAIL
            );
        InvalidEmailResponseModel response = api.users.updateUserWithInvalidEmail(accessToken, updateData);

        step("Проверка сообщения об ошибке", () ->
            assertEquals(INVALID_EMAIL_ERROR, response.email().getFirst())
        );
    }
}
