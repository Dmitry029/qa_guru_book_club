package tests;

import models.lombok.RegistrationBodyLombokModel;
import models.lombok.RegistrationResponseLombokModel;
import models.records.RegistrationBodyRecordsModel;
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

        given()
            .log().all()
            .contentType(JSON)
            .body(data)
            .when()
            .post("/register/")
            .then()
            .log().all()
            .statusCode(400)
            .body("username[0]", is("A user with that username already exists."));
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
