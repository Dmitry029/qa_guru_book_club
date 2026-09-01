package tests;

import io.qameta.allure.Step;
import models.club.*;
import models.login.LoginBodyModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

@DisplayName("CRUD клубов")
public class ClubTests extends TestBase {

    private String accessToken;
    private Integer createdClubId;
    private final Faker faker = new Faker();

    @BeforeEach
    public void auth() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        accessToken = api.auth.loginAndGetAccessToken(loginData);
    }

    @AfterEach
    public void cleanupCreatedClub() {
        if (createdClubId == null) {
            return;
        }
        try {
            api.clubs.deleteClub(accessToken, createdClubId);
        } catch (AssertionError ignored) {
            // клуб уже удалён в тесте или недоступен
        }
        createdClubId = null;
    }

    @Test
    @DisplayName("Получение списка клубов")
    public void getClubsListTest() {
        ClubListResponseModel response = api.clubs.getClubs();

        step("Проверка списка клубов", () -> {
            assertThat(response.count()).isGreaterThan(0);
            assertThat(response.results()).isNotEmpty();
            assertThat(response.results().getFirst().id()).isPositive();
            assertThat(response.results().getFirst().bookTitle()).isNotBlank();
        });
    }

    @Test
    @DisplayName("Получение клуба по id")
    public void getClubByIdTest() {
        ClubResponseModel createdClub = createClub();
        ClubResponseModel response = api.clubs.getClub(createdClub.id());

        step("Проверка данных клуба", () -> {
            assertThat(response.id()).isEqualTo(createdClub.id());
            assertThat(response.bookTitle()).isEqualTo(createdClub.bookTitle());
            assertThat(response.bookAuthors()).isEqualTo(createdClub.bookAuthors());
            assertThat(response.publicationYear()).isEqualTo(createdClub.publicationYear());
            assertThat(response.description()).isEqualTo(createdClub.description());
            assertThat(response.telegramChatLink()).isEqualTo(createdClub.telegramChatLink());
        });
    }

    @Test
    @DisplayName("Создание клуба")
    public void createClubTest() {
        ClubBodyModel clubData = newClubBody();
        ClubResponseModel response = api.clubs.createClub(accessToken, clubData);
        createdClubId = response.id();

        step("Проверка созданного клуба", () -> {
            assertThat(response.id()).isPositive();
            assertThat(response.bookTitle()).isEqualTo(clubData.bookTitle());
            assertThat(response.bookAuthors()).isEqualTo(clubData.bookAuthors());
            assertThat(response.publicationYear()).isEqualTo(clubData.publicationYear());
            assertThat(response.description()).isEqualTo(clubData.description());
            assertThat(response.telegramChatLink()).isEqualTo(clubData.telegramChatLink());
            assertThat(response.members()).contains(response.owner());
            assertThat(response.created()).isNotBlank();
        });
    }

    @Test
    @DisplayName("Полное обновление клуба (PUT)")
    public void updateClubTest() {
        ClubResponseModel createdClub = createClub();
        ClubBodyModel updateData = newClubBody();
        ClubResponseModel response = api.clubs.updateClub(accessToken, createdClub.id(), updateData);

        step("Проверка обновлённых данных клуба", () -> {
            assertThat(response.id()).isEqualTo(createdClub.id());
            assertThat(response.bookTitle()).isEqualTo(updateData.bookTitle());
            assertThat(response.bookAuthors()).isEqualTo(updateData.bookAuthors());
            assertThat(response.publicationYear()).isEqualTo(updateData.publicationYear());
            assertThat(response.description()).isEqualTo(updateData.description());
            assertThat(response.telegramChatLink()).isEqualTo(updateData.telegramChatLink());
        });
    }

    @Test
    @DisplayName("Частичное обновление клуба (PATCH)")
    public void patchClubTest() {
        ClubResponseModel createdClub = createClub();
        PatchClubBodyModel patchData = step("Подготовка данных для частичного обновления", () ->
            new PatchClubBodyModel(faker.lorem().sentence())
        );
        ClubResponseModel response = api.clubs.patchClub(accessToken, createdClub.id(), patchData);

        step("Проверка, что изменилось только описание", () -> {
            assertThat(response.id()).isEqualTo(createdClub.id());
            assertThat(response.description()).isEqualTo(patchData.description());
            assertThat(response.bookTitle()).isEqualTo(createdClub.bookTitle());
            assertThat(response.bookAuthors()).isEqualTo(createdClub.bookAuthors());
            assertThat(response.publicationYear()).isEqualTo(createdClub.publicationYear());
            assertThat(response.telegramChatLink()).isEqualTo(createdClub.telegramChatLink());
        });
    }

    @Test
    @DisplayName("Удаление клуба")
    public void deleteClubTest() {
        ClubResponseModel createdClub = createClub();
        api.clubs.deleteClub(accessToken, createdClub.id());
        DetailErrorResponseModel response = api.clubs.getMissingClub(createdClub.id());

        step("Проверка, что клуб не найден", () ->
            assertThat(response.detail()).isEqualTo(NOT_FOUND_ERROR)
        );
        createdClubId = null;
    }

    @Test
    @DisplayName("Поиск клуба по названию книги")
    public void searchClubTest() {
        ClubResponseModel createdClub = createClub();
        ClubListResponseModel response = api.clubs.getClubsBySearch(createdClub.bookTitle());

        step("Проверка, что созданный клуб есть в результатах поиска", () -> {
            assertThat(response.count()).isGreaterThan(0);
            assertThat(response.results())
                .extracting(ClubResponseModel::id)
                .contains(createdClub.id());
        });
    }

    @Test
    @DisplayName("Создание клуба без авторизации")
    public void createClubUnauthorizedTest() {
        ClubBodyModel clubData = newClubBody();
        DetailErrorResponseModel response = api.clubs.createClubUnauthorized(clubData);

        step("Проверка сообщения об ошибке", () ->
            assertThat(response.detail()).isEqualTo(AUTH_CREDENTIALS_ERROR)
        );
    }

    @Test
    @DisplayName("Создание клуба с пустым названием книги")
    public void createClubWithBlankTitleTest() {
        ClubBodyModel clubData = step("Подготовка данных с пустым названием книги", () ->
            new ClubBodyModel(
                "",
                faker.book().author(),
                faker.number().numberBetween(1900, 2026),
                faker.lorem().sentence(),
                CLUB_TELEGRAM_LINK
            )
        );
        BlankBookTitleResponseModel response = api.clubs.createClubWithBlankTitle(accessToken, clubData);

        step("Проверка сообщения об ошибке", () ->
            assertThat(response.bookTitle().getFirst()).isEqualTo(EMPTY_FIELD_ERROR)
        );
    }

    @Test
    @DisplayName("Получение несуществующего клуба")
    public void getNonexistentClubTest() {
        DetailErrorResponseModel response = api.clubs.getMissingClub(NONEXISTENT_CLUB_ID);

        step("Проверка сообщения об ошибке", () ->
            assertThat(response.detail()).isEqualTo(NOT_FOUND_ERROR)
        );
    }

    @Step("Подготовка данных для создания клуба")
    private ClubBodyModel newClubBody() {
        return new ClubBodyModel(
            "QA Club " + faker.book().title() + " " + faker.number().digits(6),
            faker.book().author(),
            faker.number().numberBetween(1900, 2026),
            faker.lorem().paragraph(),
            CLUB_TELEGRAM_LINK
        );
    }

    @Step("Создание клуба")
    private ClubResponseModel createClub() {
        ClubResponseModel club = api.clubs.createClub(accessToken, newClubBody());
        createdClubId = club.id();
        return club;
    }
}
