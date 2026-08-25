package specs.logout;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static specs.BaseSpec.baseRequestSpec;

public class LogoutSpec {

    public static RequestSpecification logoutRequestSpec = baseRequestSpec;

    public static ResponseSpecification successfulLogoutResponseSpec = new ResponseSpecBuilder()
        .log(ALL)
        .expectStatusCode(200)
        .expectBody(equalTo("{}"))
        .build();

    public static ResponseSpecification emptyBodyResponseSpec = new ResponseSpecBuilder()
        .log(ALL)
        .expectStatusCode(400)
        .expectBody(matchesJsonSchemaInClasspath(
            "schemas/logout/empty_body_response_schema.json"))
        .expectBody("refresh", notNullValue())
        .build();
}
