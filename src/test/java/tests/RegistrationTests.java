package tests;

import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.registration.RegistrationSpec.*;
import static tests.TestData.*;

public class RegistrationTests extends TestBase {

    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName();
    }

    @Test
    public void successfulRegistrationTest_with_records() {

        RegistrationBodyModel data = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = given(registrationRequestSpec)
            .body(data)
            .when()
            .post("/users/register/")
            .then()
            .spec(successfulRegistrationResponseSpec)
            .extract()
            .as(SuccessfulRegistrationResponseModel.class);

        assertEquals(username, registrationResponse.username());
        assertThat(registrationResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);
    }

    @Test
    public void existingUserWrongRegistrationTest() {
        RegistrationBodyModel data = new RegistrationBodyModel(username, password);

        given(registrationRequestSpec)
            .body(data)
            .when()
            .post("/users/register/")
            .then()
            .spec(successfulRegistrationResponseSpec);

        ExistingUserResponseModel response = given(registrationRequestSpec)
            .body(data)
            .when()
            .post("/users/register/")
            .then()
            .spec(existingUserRegistrationResponseSpec)
            .extract()
            .as(ExistingUserResponseModel.class);

        assertEquals(REGISTRATION_EXISTING_USER_ERROR, response.username().getFirst());
    }

    @Test
    public void invalidUsername400Test() {
        Faker faker = new Faker();
        String username = faker.name().fullName();
        RegistrationBodyModel data = new RegistrationBodyModel(username, password);

        ExistingUserResponseModel response = given(registrationRequestSpec)
            .body(data)
            .when()
            .post("/users/register/")
            .then()
            .spec(invalidUserNameRegistrationResponseSpec)
            .extract()
            .as(ExistingUserResponseModel.class);

        assertEquals(REGISTRATION_EXISTING_INVALID_USER_NAME_ERROR, response.username().getFirst());
    }

    @Test
    public void negativeRegistration500Test() {
        RegistrationBodyModel data = new RegistrationBodyModel(username, password);

        given(registrationRequestSpec)
            .body(data)
            .when()
            .post("/users/register")
            .then()
            .statusCode(500);
    }
}