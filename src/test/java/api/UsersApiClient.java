package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.user.UpdateUserBodyModel;
import models.user.UserResponseModel;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.user.UserSpec.userResponse200Spec;

public class UsersApiClient {

    @Step("Обновление данных пользователя")
    public UserResponseModel updateUser(String token, UpdateUserBodyModel body) {
        return given(baseRequestSpec)
            .header("Authorization", "Bearer " + token)
            .body(body)
            .when()
            .patch("/users/me/") // или /users/profile/ в зависимости от API
            .then()
            .spec(userResponse200Spec)
            .extract()
            .as(UserResponseModel.class);
    }

    @Step("Обновление данных пользователя")
    public Response updateUserGetResponse(String token, UpdateUserBodyModel body, ResponseSpecification spec) {
        return given(baseRequestSpec)
            .header("Authorization", "Bearer " + token)
            .body(body)
            .when()
            .patch("/users/me/") // или /users/profile/ в зависимости от API
            .then()
            .spec(spec)
            .extract()
            .response();
    }

    @Step("Регистрация пользователя")
    public SuccessfulRegistrationResponseModel register(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
            .body(body)
            .when()
            .post("/users/register/")
            .then()
            .spec(successfulRegistrationResponseSpec)
            .extract()
            .as(SuccessfulRegistrationResponseModel.class);
    }

    @Step("Регистрация пользователя и получение объекта Response")
    public Response registerWithSpec(RegistrationBodyModel body, ResponseSpecification spec) {
        return given(registrationRequestSpec)
            .body(body)
            .when()
            .post("/users/register/")
            .then()
            .spec(spec)
            .extract()
            .response();
    }
}
