package tests;

import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.registration.RegistrationSpec.*;

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
        String ipAddrRegexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
            + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
        assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);
    }

    @Test
    public void existingUser400Test() {
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

        String expectedError = "A user with that username already exists.";
        assertEquals(expectedError, response.username().getFirst());
    }

    @Test
    public void invalidUsername400Test() {
        Faker faker = new Faker();
        String username = faker.name().fullName();
        RegistrationBodyModel data = new RegistrationBodyModel(username, password);

        given()
            .log().all()
            .contentType(JSON)
            .body(data)
            .when()
            .post("/users/register/")
            .then()
            .log().all()
            .statusCode(400)
            .body("username[0]",
                is("Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters."));

    }

    @Test
    public void negativeRegistration500Test() {
        RegistrationBodyModel data = new RegistrationBodyModel(username, password);

        given()
            .body(data)
            .when()
            .post("/users/register")
            .then()
            .statusCode(500);
    }
}
//todo move to specs