package api;

import io.qameta.allure.Step;
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

    @Step("Авторизация и получение access-токена")
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

    @Step("Авторизация и получение refresh-токена")
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

    @Step("Выход из аккаунта")
    public void logout(LogoutBodyModel logoutBody) {
        given(logoutRequestSpec)
            .body(logoutBody)
            .when()
            .post("/auth/logout/")
            .then()
            .spec(successfulLogoutResponseSpec);
    }

    @Step("Авторизация")
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

    @Step("Авторизация с невалидным паролем")
    public WrongCredentialsLoginResponseModel loginWithInvalidPassword(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
            .body(loginBody)
            .when()
            .post("/auth/token/")
            .then()
            .spec(wrongCredentialsLoginResponseSpec)
            .extract()
            .as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Авторизация с пустым username")
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

    @Step("Авторизация с пустым password")
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

    @Step("Авторизация с пустыми username и password")
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

    @Step("Выход из аккаунта с пустым телом запроса")
    public EmptyBodyModel logoutWithEmptyBody() {
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

    @Step("Выход из аккаунта с неправильным basePath")
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
