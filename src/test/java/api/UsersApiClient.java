package api;

import io.qameta.allure.Step;
import models.user.UpdateUserBodyModel;
import models.user.UserResponseModel;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
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
}
