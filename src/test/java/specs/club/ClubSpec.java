package specs.club;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;
import static specs.BaseSpec.baseRequestSpec;

public class ClubSpec {

    public static RequestSpecification clubRequestSpec = baseRequestSpec;

    public static ResponseSpecification clubListResponseSpec = new ResponseSpecBuilder()
        .log(ALL)
        .expectStatusCode(200)
        .expectBody(matchesJsonSchemaInClasspath("schemas/club/club_list_response_schema.json"))
        .expectBody("count", notNullValue())
        .expectBody("results", notNullValue())
        .build();

    public static ResponseSpecification clubCreatedResponseSpec = new ResponseSpecBuilder()
        .log(ALL)
        .expectStatusCode(201)
        .expectBody(matchesJsonSchemaInClasspath("schemas/club/club_response_schema.json"))
        .expectBody("id", notNullValue())
        .build();

    public static ResponseSpecification clubResponseSpec = new ResponseSpecBuilder()
        .log(ALL)
        .expectStatusCode(200)
        .expectBody(matchesJsonSchemaInClasspath("schemas/club/club_response_schema.json"))
        .expectBody("id", notNullValue())
        .build();

    public static ResponseSpecification clubDeletedResponseSpec = new ResponseSpecBuilder()
        .log(ALL)
        .expectStatusCode(204)
        .build();

    public static ResponseSpecification clubUnauthorizedResponseSpec = new ResponseSpecBuilder()
        .log(ALL)
        .expectStatusCode(401)
        .expectBody("detail", notNullValue())
        .build();

    public static ResponseSpecification clubNotFoundResponseSpec = new ResponseSpecBuilder()
        .log(ALL)
        .expectStatusCode(404)
        .expectBody("detail", notNullValue())
        .build();

    public static ResponseSpecification clubBlankTitleResponseSpec = new ResponseSpecBuilder()
        .log(ALL)
        .expectStatusCode(400)
        .expectBody("bookTitle", notNullValue())
        .build();
}
