package tests;

import models.registration.lombok.RegistrationBodyLombokModel;
import models.registration.lombok.RegistrationResponseLombokModel;
import models.registration.records.ExistingUserResponseRecordsModel;
import models.registration.records.RegistrationBodyRecordsModel;
import models.registration.records.RegistrationResponseRecordsModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void successfulRegistrationTest_with_lombok() {

        RegistrationBodyLombokModel data = new RegistrationBodyLombokModel();
        data.setUsername(username);
        data.setPassword(password);

        RegistrationResponseLombokModel registrationResponse = given()
            .log().all()
            .contentType(JSON)
            .body(data)
            .when()
            .post("/register/")
            .then()
            .log().all()
            .statusCode(201)
            .extract()
            .as(RegistrationResponseLombokModel.class);

        assertEquals(username, registrationResponse.getUsername());
    }

    @Test
    public void successfulRegistrationTest_with_records() {

        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel (username, password);

        RegistrationResponseRecordsModel  registrationResponse = given()
            .log().all()
            .contentType(JSON)
            .body(data)
            .when()
            .post("/register/")
            .then()
            .log().all()
            .statusCode(201)
            .extract()
            .as(RegistrationResponseRecordsModel.class);

        assertEquals(username, registrationResponse.username());
    }

    @Test
    public void existingUser400Test() {
        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel (username, password);

        given()
            .log().all()
            .contentType(JSON)
            .body(data)
            .when()
            .post("/register/")
            .then()
            .log().all()
            .statusCode(201)
            .body("username", is(username))
            .body("id", notNullValue());

        ExistingUserResponseRecordsModel response = given()
            .log().all()
            .contentType(JSON)
            .body(data)
            .when()
            .post("/register/")
            .then()
            .log().all()
            .statusCode(400)
            .extract()
            .as(ExistingUserResponseRecordsModel.class);

        String expectedError = "A user with that username already exists.";
        assertEquals(expectedError, response.username().getFirst());
    }

    @Test
    public void invalidUsername400Test() {
        Faker faker = new Faker();
        String username = faker.name().fullName();
        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel (username, password);

        given()
            .log().all()
            .contentType(JSON)
            .body(data)
            .when()
            .post("/register/")
            .then()
            .log().all()
            .statusCode(400)
            .body("username[0]",
                is("Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters."));

    }

    @Test
    public void negativeRegistration500Test() {
        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel (username, password);

        given()
            .body(data)
            .when()
            .post("/register")
            .then()
            .statusCode(500);
    }
}
