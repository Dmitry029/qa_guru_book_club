package tests;

import models.pojo.RegistrationBodyPojoModel;
import models.pojo.RegistrationResponsePojoModel;
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
    public void successfulRegistrationTest_with_pojo() {

        RegistrationBodyPojoModel data = new RegistrationBodyPojoModel();
        data.setUsername(username);
        data.setPassword(password);

        RegistrationResponsePojoModel registrationResponse = given()
            .log().all()
            .contentType(JSON)
            .body(data)
            .when()
            .post("/register/")
            .then()
            .log().all()
            .statusCode(201)
            .extract()
            .as(RegistrationResponsePojoModel.class);

        assertEquals(username, registrationResponse.getUsername());
    }

    @Test
    public void existingUser400Test() {
        RegistrationBodyPojoModel data = new RegistrationBodyPojoModel();
        data.setUsername(username);
        data.setPassword(password);

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
        RegistrationBodyPojoModel data = new RegistrationBodyPojoModel();
        data.setUsername(username);
        data.setPassword(password);

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
        RegistrationBodyPojoModel data = new RegistrationBodyPojoModel();
        data.setUsername(username);
        data.setPassword(password);

        given()
            .body(data)
            .when()
            .post("/register")
            .then()
            .statusCode(500);
    }
}
