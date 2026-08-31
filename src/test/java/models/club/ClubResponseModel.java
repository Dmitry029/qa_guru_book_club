package models.club;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClubResponseModel(
    int id,
    String bookTitle,
    String bookAuthors,
    int publicationYear,
    String description,
    String telegramChatLink,
    int owner,
    List<Integer> members,
    String created,
    String modified
) {
}
