package tests;

import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.registration.RegistrationSpec.*;
import static tests.TestData.*;

@DisplayName("Регистрация пользователя")
public class RegistrationTests extends TestBase {

    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().lastName();
    }

    @Test
    @DisplayName("Успешная регистрация")
    public void successfulRegistrationTest_with_records() {
        RegistrationBodyModel data = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = step("Отправить POST запрос на регистрацию", () ->
            api.users.register(data)
        );

        step("Проверить значений ответа успешной регистрации", () -> {
            assertEquals(username, registrationResponse.username());
            assertThat(registrationResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);
        });
    }

    @Test
    @DisplayName("Регистрация уже существующего пользователя")
    public void existingUserWrongRegistrationTest() {
        RegistrationBodyModel data = new RegistrationBodyModel(username, password);
        api.users.register(data);

        ExistingUserResponseModel response = step("Зарегистрировать того же пользователя снова", () ->
            given(registrationRequestSpec)
                .body(data)
                .when()
                .post("/users/register/")
                .then()
                .spec(existingUserRegistrationResponseSpec)
                .extract()
                .as(ExistingUserResponseModel.class)
        );

        step("Проверить сообщение об ошибке", () ->
            assertEquals(REGISTRATION_EXISTING_USER_ERROR, response.username().getFirst())
        );
    }

    @Test
    @DisplayName("Регистрация пользователя с невалидным username")
    public void invalidUsername400Test() {
        String username = new Faker().name().fullName();
        RegistrationBodyModel data = new RegistrationBodyModel(username, password);

        ExistingUserResponseModel response = step("Отправка запроса на регистрацию с невалидным username", () ->
            given(registrationRequestSpec)
                .body(data)
                .when()
                .post("/users/register/")
                .then()
                .spec(invalidUserNameRegistrationResponseSpec)
                .extract()
                .as(ExistingUserResponseModel.class)
        );

        step("Проверка сообщения об ошибке)", () ->
            assertEquals(REGISTRATION_EXISTING_INVALID_USER_NAME_ERROR, response.username().getFirst())
        );
    }

    @Test
    @DisplayName("Регистрация пользователя с отсутствующим username")
    public void registrationWithoutUsernameTest() {
        RegistrationBodyModel data = new RegistrationBodyModel("", password);

        var response = step("Отправить POST запрос на регистрацию с пустым логином", () ->
            api.users.registerWithSpec(data, invalidUserNameRegistrationResponseSpec)
        );

        step("Проверить кода ошибки и сообщения об ошибке", () ->
            assertThat(response.path("username[0]").toString()).isEqualTo(EMPTY_USERNAME_ERROR)
        );
    }
}
