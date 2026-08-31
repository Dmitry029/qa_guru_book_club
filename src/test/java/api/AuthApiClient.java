package api;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.loguot.LogoutBodyModel;

import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
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
}
