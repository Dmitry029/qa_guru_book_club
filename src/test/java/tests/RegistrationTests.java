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
import static specs.registration.RegistrationSpec.invalidUserNameRegistrationResponseSpec;
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
        RegistrationBodyModel data = step("Подготовка данных для регистрации", () ->
            new RegistrationBodyModel(username, password)
        );
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(data);

        step("Проверить значений ответа успешной регистрации", () -> {
            assertEquals(username, registrationResponse.username());
            assertThat(registrationResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);
        });
    }

    @Test
    @DisplayName("Регистрация уже существующего пользователя")
    public void existingUserWrongRegistrationTest() {
        RegistrationBodyModel data = step("Подготовка данных для регистрации", () ->
            new RegistrationBodyModel(username, password)
        );
        api.users.register(data);
        ExistingUserResponseModel response = api.users.registerExistingUser(data);

        step("Проверка сообщения об ошибке", () ->
            assertEquals(REGISTRATION_EXISTING_USER_ERROR, response.username().getFirst())
        );
    }

    @Test
    @DisplayName("Регистрация пользователя с невалидным username")
    public void invalidUsername400Test() {
        RegistrationBodyModel data = step("Подготовка данных с невалидным username", () ->
            new RegistrationBodyModel(new Faker().name().fullName(), password)
        );
        ExistingUserResponseModel response = api.users.registerWithInvalidUsername(data);

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
        var response = api.users.registerWithSpec(data, invalidUserNameRegistrationResponseSpec);

        step("Проверка сообщения об ошибке", () ->
            assertThat(response.path("username[0]").toString()).isEqualTo(EMPTY_USERNAME_ERROR)
        );
    }
}
