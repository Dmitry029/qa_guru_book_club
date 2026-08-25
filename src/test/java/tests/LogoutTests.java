package tests;

import models.login.LoginBodyModel;
import models.loguot.EmptyBodyModel;
import models.loguot.LogoutBodyModel;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.logout.LogoutSpec.*;
import static tests.TestData.*;

public class LogoutTests extends TestBase {

    @Test
    public void successfulLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);

        String refreshToken = given(loginRequestSpec)
            .body(loginData)
            .when()
            .post("/auth/token/")
            .then()
            .spec(successfulLoginResponseSpec)
            .extract().path("refresh");
        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);

        given(logoutRequestSpec)
            .body(logoutData)
            .basePath("/api/v1")
            .when()
            .post("/auth/logout/")
            .then()
            .spec(successfulLogoutResponseSpec);
    }

    @Test
    public void logoutWithoutBodyTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);

        given(loginRequestSpec)
            .body(loginData)
            .when()
            .post("/auth/token/")
            .then()
            .spec(successfulLoginResponseSpec);

        EmptyBodyModel response = given(logoutRequestSpec)
            .body("")
            .basePath("/api/v1")
            .when()
            .post("/auth/logout/")
            .then()
            .spec(emptyBodyResponseSpec)
            .extract().as(EmptyBodyModel.class);

        assertEquals(EMPTY_LOGOUT_BODY_ERROR, response.refresh().getFirst());
    }

    @Test
    public void incorrectBasePath404Test() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);

        String refreshToken = given(loginRequestSpec)
            .body(loginData)
            .when()
            .post("/auth/token/")
            .then()
            .spec(successfulLoginResponseSpec)
            .extract().path("refresh");
        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);

        given(logoutRequestSpec)
            .body(logoutData)
            .basePath("/api/v2")
            .when()
            .post("/auth/logout/")
            .then()
            .statusCode(404);
    }
}
