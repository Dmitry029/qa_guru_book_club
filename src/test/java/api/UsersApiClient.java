package api;

import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.user.InvalidEmailResponseModel;
import models.user.UpdateUserBodyModel;
import models.user.UserResponseModel;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.registration.RegistrationSpec.existingUserRegistrationResponseSpec;
import static specs.registration.RegistrationSpec.invalidUserNameRegistrationResponseSpec;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.user.UserSpec.userResponse200Spec;
import static specs.user.UserSpec.userResponse400Spec;

public class UsersApiClient {

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

    public InvalidEmailResponseModel updateUserWithInvalidEmail(String token, UpdateUserBodyModel body) {
        return given(baseRequestSpec)
            .header("Authorization", "Bearer " + token)
            .body(body)
            .when()
            .patch("/users/me/")
            .then()
            .spec(userResponse400Spec)
            .extract()
            .as(InvalidEmailResponseModel.class);
    }

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

    public ExistingUserResponseModel registerExistingUser(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
            .body(body)
            .when()
            .post("/users/register/")
            .then()
            .spec(existingUserRegistrationResponseSpec)
            .extract()
            .as(ExistingUserResponseModel.class);
    }

    public ExistingUserResponseModel registerWithInvalidUsername(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
            .body(body)
            .when()
            .post("/users/register/")
            .then()
            .spec(invalidUserNameRegistrationResponseSpec)
            .extract()
            .as(ExistingUserResponseModel.class);
    }
}
