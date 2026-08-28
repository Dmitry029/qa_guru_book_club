package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.WrongCredentialsLoginResponseModel;
import models.user.UpdateUserBodyModel;
import models.user.UserResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static tests.TestData.*;
import static tests.TestData.UPDATED_EMAIL;

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
        UpdateUserBodyModel updateData = new UpdateUserBodyModel(
            UPDATED_FIRST_NAME,
            UPDATED_LAST_NAME,
            UPDATED_EMAIL
        );

        UserResponseModel response = api.users.updateUser(accessToken, updateData);

        assertThat(response.firstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(response.lastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(response.email()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Disabled
    public void wrongCredentialsLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_WRONG_PASSWORD);

        WrongCredentialsLoginResponseModel loginResponse = given()
            .log().all()
            .contentType(JSON)
            .body(loginData)
            .basePath("/api/v1")
            .when()
            .post("/auth/token/")
            .then()
            .log().all()
            .statusCode(401)
            .body(matchesJsonSchemaInClasspath(
                "schemas/login/wrong_credentials_login_response_schema.json"))
            .body("detail", notNullValue())
            .extract().as(WrongCredentialsLoginResponseModel.class);

        String expectedDetailError = "Invalid username or password.";
        String actualDetailError = loginResponse.detail();
        assertThat(actualDetailError).isEqualTo(expectedDetailError);
    }
}
