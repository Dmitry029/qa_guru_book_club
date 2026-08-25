package tests;

import models.login.*;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.login.LoginSpec.*;
import static tests.TestData.*;

public class LoginTests extends TestBase {

    @Test
    public void successfulLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);

        SuccessfulLoginResponseModel loginResponse = given(loginRequestSpec)
            .body(loginData)
            .when()
            .post("/auth/token/")
            .then()
            .spec(successfulLoginResponseSpec)
            .extract().as(SuccessfulLoginResponseModel.class);

        String expectedTokenPath = LOGIN_TOKEN_PREFIX;
        String actualAccess = loginResponse.access();
        String actualRefresh = loginResponse.refresh();

        assertThat(actualAccess).startsWith(expectedTokenPath);
        assertThat(actualRefresh).startsWith(expectedTokenPath);
        assertThat(actualAccess).isNotEqualTo(actualRefresh);
    }

    @Test
    public void wrongCredentialsLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_WRONG_PASSWORD);

        WrongCredentialsLoginResponseModel loginResponse = given(loginRequestSpec)
            .body(loginData)
            .when()
            .post("/auth/token/")
            .then()
            .spec(wrongCredentialsLoginResponseSpec)
            .extract().as(WrongCredentialsLoginResponseModel.class);

        String actualDetailError = loginResponse.detail();

        assertThat(actualDetailError).isEqualTo(LOGIN_WRONG_CREDENTIALS_ERROR);
    }

    @Test
    public void emptyUsernameLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel("", LOGIN_WRONG_PASSWORD);

        EmptyUsernameResponseModel response = given(loginRequestSpec)
            .body(loginData)
            .when()
            .post("/auth/token/")
            .then()
            .spec(emptyUsernameLoginResponseSpec)
            .extract().as(EmptyUsernameResponseModel.class);

        assertEquals(EMPTY_USERNAME_ERROR, response.username().getFirst());
    }

    @Test
    public void emptyPasswordLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, "");

        EmptyPasswordResponseModel response = given(loginRequestSpec)
            .body(loginData)
            .when()
            .post("/auth/token/")
            .then()
            .spec(emptyPasswordLoginResponseSpec)
            .extract().as(EmptyPasswordResponseModel.class);

        assertEquals(EMPTY_USERNAME_ERROR, response.password().getFirst());
    }

    @Test
    public void emptyCredentialsLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel("", "");

        EmptyCredentialsResponseModel response = given(loginRequestSpec)
            .body(loginData)
            .when()
            .post("/auth/token/")
            .then()
            .spec(emptyCredentialsResponseSpec)
            .extract().as(EmptyCredentialsResponseModel.class);

        assertEquals(EMPTY_USERNAME_ERROR, response.password().getFirst());
        assertEquals(EMPTY_USERNAME_ERROR, response.username().getFirst());
    }

}

// todo add more negative tests


