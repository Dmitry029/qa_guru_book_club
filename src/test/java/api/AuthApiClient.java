package api;

import models.login.EmptyCredentialsResponseModel;
import models.login.EmptyPasswordResponseModel;
import models.login.EmptyUsernameResponseModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.WrongCredentialsLoginResponseModel;
import models.loguot.EmptyBodyModel;
import models.loguot.LogoutBodyModel;

import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.emptyCredentialsResponseSpec;
import static specs.login.LoginSpec.emptyPasswordLoginResponseSpec;
import static specs.login.LoginSpec.emptyUsernameLoginResponseSpec;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.login.LoginSpec.wrongCredentialsLoginResponseSpec;
import static specs.logout.LogoutSpec.emptyBodyResponseSpec;
import static specs.logout.LogoutSpec.logoutRequestSpec;
import static specs.logout.LogoutSpec.successfulLogoutResponseSpec;

public class AuthApiClient {

    public String loginAndGetAccessToken(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
            .body(loginBody)
            .when()
            .post("/auth/token/")
            .then()
            .spec(successfulLoginResponseSpec)
            .extract()
            .path("access"); // Извлекаем именно access-токен
    }

    public String loginAndGetRefreshToken(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
            .body(loginBody)
            .when()
            .post("/auth/token/")
            .then()
            .spec(successfulLoginResponseSpec)
            .extract()
            .path("refresh");
    }

    public void logout(LogoutBodyModel logoutBody) {
        given(logoutRequestSpec)
            .body(logoutBody)
            .when()
            .post("/auth/logout/")
            .then()
            .spec(successfulLogoutResponseSpec);
    }

    public SuccessfulLoginResponseModel login(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
            .body(loginBody)
            .when()
            .post("/auth/token/")
            .then()
            .spec(successfulLoginResponseSpec)
            .extract()
            .as(SuccessfulLoginResponseModel.class);
    }

    public WrongCredentialsLoginResponseModel loginWithWrongCredentials(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
            .body(loginBody)
            .when()
            .post("/auth/token/")
            .then()
            .spec(wrongCredentialsLoginResponseSpec)
            .extract()
            .as(WrongCredentialsLoginResponseModel.class);
    }

    public EmptyUsernameResponseModel loginWithEmptyUsername(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
            .body(loginBody)
            .when()
            .post("/auth/token/")
            .then()
            .spec(emptyUsernameLoginResponseSpec)
            .extract()
            .as(EmptyUsernameResponseModel.class);
    }

    public EmptyPasswordResponseModel loginWithEmptyPassword(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
            .body(loginBody)
            .when()
            .post("/auth/token/")
            .then()
            .spec(emptyPasswordLoginResponseSpec)
            .extract()
            .as(EmptyPasswordResponseModel.class);
    }

    public EmptyCredentialsResponseModel loginWithEmptyCredentials(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
            .body(loginBody)
            .when()
            .post("/auth/token/")
            .then()
            .spec(emptyCredentialsResponseSpec)
            .extract()
            .as(EmptyCredentialsResponseModel.class);
    }

    public EmptyBodyModel logoutWithoutBody() {
        return given(logoutRequestSpec)
            .body("")
            .basePath("/api/v1")
            .when()
            .post("/auth/logout/")
            .then()
            .spec(emptyBodyResponseSpec)
            .extract()
            .as(EmptyBodyModel.class);
    }

    public void logoutWithIncorrectBasePath(LogoutBodyModel logoutBody) {
        given(logoutRequestSpec)
            .body(logoutBody)
            .basePath("/api/v2")
            .when()
            .post("/auth/logout/")
            .then()
            .statusCode(404);
    }
}
