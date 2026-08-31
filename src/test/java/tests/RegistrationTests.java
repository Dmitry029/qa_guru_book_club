package tests;

import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tests.TestData.*;

@DisplayName("Регистрация пользователя")
public class RegistrationTests extends TestBase {

    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        step("Подготовка тестовых данных", () -> {
            Faker faker = new Faker();
            username = faker.name().firstName();
            password = faker.name().lastName();
        });
    }

    @Test
    @DisplayName("Успешная регистрация")
    public void successfulRegistrationTest_with_records() {
        RegistrationBodyModel data = step("Подготовка данных для запроса", () ->
            new RegistrationBodyModel(username, password)
        );

        SuccessfulRegistrationResponseModel registrationResponse = step("Отправка запроса на регистрацию", () ->
            api.users.register(data)
        );

        step("Проверка ответа успешной регистрации", () -> {
            assertEquals(username, registrationResponse.username());
            assertThat(registrationResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);
        });
    }

    @Test
    @DisplayName("Регистрация уже существующего пользователя")
    public void existingUserWrongRegistrationTest() {
        RegistrationBodyModel data = step("Подготовка данных для запроса", () ->
            new RegistrationBodyModel(username, password)
        );

        step("Регистрация пользователя", () ->
            api.users.register(data)
        );

        ExistingUserResponseModel response = step("Повторная регистрация того же пользователя", () ->
            api.users.registerExistingUser(data)
        );

        step("Проверка сообщения об ошибке", () ->
            assertEquals(REGISTRATION_EXISTING_USER_ERROR, response.username().getFirst())
        );
    }

    @Test
    @DisplayName("Регистрация пользователя с невалидным username")
    public void invalidUsername400Test() {
        RegistrationBodyModel data = step("Подготовка данных с невалидным username", () -> {
            String invalidUsername = new Faker().name().fullName();
            return new RegistrationBodyModel(invalidUsername, password);
        });

        ExistingUserResponseModel response = step("Отправка запроса на регистрацию с невалидным username", () ->
            api.users.registerWithInvalidUsername(data)
        );

        step("Проверка сообщения об ошибке", () ->
            assertEquals(REGISTRATION_EXISTING_INVALID_USER_NAME_ERROR, response.username().getFirst())
        );
    }

    @Test
    @DisplayName("Регистрация пользователя с отсутствующим username")
    public void registrationWithoutUsernameTest() {
        RegistrationBodyModel data = step("Подготовка данных с пустым username", () ->
            new RegistrationBodyModel("", password)
        );

        ExistingUserResponseModel response = step("Отправка запроса на регистрацию с пустым username", () ->
            api.users.registerWithInvalidUsername(data)
        );

        step("Проверка сообщения об ошибке", () ->
            assertEquals(EMPTY_USERNAME_ERROR, response.username().getFirst())
        );
    }
}
