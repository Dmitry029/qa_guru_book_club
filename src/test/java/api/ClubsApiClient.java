package api;

import models.club.BlankBookTitleResponseModel;
import models.club.ClubBodyModel;
import models.club.ClubListResponseModel;
import models.club.ClubResponseModel;
import models.club.DetailErrorResponseModel;
import models.club.PatchClubBodyModel;

import static io.restassured.RestAssured.given;
import static specs.club.ClubSpec.clubBlankTitleResponseSpec;
import static specs.club.ClubSpec.clubCreatedResponseSpec;
import static specs.club.ClubSpec.clubDeletedResponseSpec;
import static specs.club.ClubSpec.clubListResponseSpec;
import static specs.club.ClubSpec.clubNotFoundResponseSpec;
import static specs.club.ClubSpec.clubRequestSpec;
import static specs.club.ClubSpec.clubResponseSpec;
import static specs.club.ClubSpec.clubUnauthorizedResponseSpec;

public class ClubsApiClient {

    public ClubListResponseModel getClubs() {
        return given(clubRequestSpec)
            .when()
            .get("/clubs/")
            .then()
            .spec(clubListResponseSpec)
            .extract()
            .as(ClubListResponseModel.class);
    }

    public ClubListResponseModel getClubsBySearch(String search) {
        return given(clubRequestSpec)
            .queryParam("search", search)
            .when()
            .get("/clubs/")
            .then()
            .spec(clubListResponseSpec)
            .extract()
            .as(ClubListResponseModel.class);
    }

    public ClubResponseModel getClub(int id) {
        return given(clubRequestSpec)
            .when()
            .get("/clubs/{id}/", id)
            .then()
            .spec(clubResponseSpec)
            .extract()
            .as(ClubResponseModel.class);
    }

    public ClubResponseModel createClub(String token, ClubBodyModel body) {
        return given(clubRequestSpec)
            .header("Authorization", "Bearer " + token)
            .body(body)
            .when()
            .post("/clubs/")
            .then()
            .spec(clubCreatedResponseSpec)
            .extract()
            .as(ClubResponseModel.class);
    }

    public DetailErrorResponseModel createClubUnauthorized(ClubBodyModel body) {
        return given(clubRequestSpec)
            .body(body)
            .when()
            .post("/clubs/")
            .then()
            .spec(clubUnauthorizedResponseSpec)
            .extract()
            .as(DetailErrorResponseModel.class);
    }

    public BlankBookTitleResponseModel createClubWithBlankTitle(String token, ClubBodyModel body) {
        return given(clubRequestSpec)
            .header("Authorization", "Bearer " + token)
            .body(body)
            .when()
            .post("/clubs/")
            .then()
            .spec(clubBlankTitleResponseSpec)
            .extract()
            .as(BlankBookTitleResponseModel.class);
    }

    public ClubResponseModel updateClub(String token, int id, ClubBodyModel body) {
        return given(clubRequestSpec)
            .header("Authorization", "Bearer " + token)
            .body(body)
            .when()
            .put("/clubs/{id}/", id)
            .then()
            .spec(clubResponseSpec)
            .extract()
            .as(ClubResponseModel.class);
    }

    public ClubResponseModel patchClub(String token, int id, PatchClubBodyModel body) {
        return given(clubRequestSpec)
            .header("Authorization", "Bearer " + token)
            .body(body)
            .when()
            .patch("/clubs/{id}/", id)
            .then()
            .spec(clubResponseSpec)
            .extract()
            .as(ClubResponseModel.class);
    }

    public void deleteClub(String token, int id) {
        given(clubRequestSpec)
            .header("Authorization", "Bearer " + token)
            .when()
            .delete("/clubs/{id}/", id)
            .then()
            .spec(clubDeletedResponseSpec);
    }

    public DetailErrorResponseModel getMissingClub(int id) {
        return given(clubRequestSpec)
            .when()
            .get("/clubs/{id}/", id)
            .then()
            .spec(clubNotFoundResponseSpec)
            .extract()
            .as(DetailErrorResponseModel.class);
    }
}
