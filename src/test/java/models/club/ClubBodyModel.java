package models.club;

public record ClubBodyModel(
    String bookTitle,
    String bookAuthors,
    Integer publicationYear,
    String description,
    String telegramChatLink
) {
}
