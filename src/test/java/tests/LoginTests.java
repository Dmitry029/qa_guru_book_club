package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static tests.TestData.LOGIN_PASSWORD;
import static tests.TestData.LOGIN_USERNAME;

public class LoginTests extends TestBase {

    @Test
    public void successfulLoginTest() {

        Faker faker = new Faker();
        String username = faker.name().firstName();
        String password = faker.name().firstName();
        LoginBodyModel loginData = new LoginBodyModel(username, password);


        SuccessfulLoginResponseModel loginResponse = given()
            .log().all()
            .contentType(JSON)
            .body(loginData)
            //.basePath("/api/v1")
            .when()
            .post("http://127.0.0.1:8000/api/v1/users/register/")
            .then()
            .log().all()
            .statusCode(201)

            /*.body(matchesJsonSchemaInClasspath("schemas/login/successful_login_response_schema.json"))
            .body("access", notNullValue())
            .body("refresh", notNullValue())*/
            .extract().as(SuccessfulLoginResponseModel.class);

        /*String expectedTokenPath = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        String actualAccess = loginResponse.access();
        String actualRefresh = loginResponse.refresh();

        assertThat(actualAccess).startsWith(expectedTokenPath);
        assertThat(actualRefresh).startsWith(expectedTokenPath);
        assertThat(actualAccess).isNotEqualTo(actualRefresh);*/
    }

    /*@Test
    public void wrongCredentialsLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_WRONG_PASSWORD);

        WrongCredentialsLoginResponseModel loginResponse = api.auth.loginWrongCredentials(loginData);

        String expectedDetailError = LOGIN_WRONG_CREDENTIALS_ERROR;
        String actualDetailError = loginResponse.detail();
        assertThat(actualDetailError).isEqualTo(expectedDetailError);
    }*/

    // todo add more negative tests

}
