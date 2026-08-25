package models.login;

import java.util.List;

public record EmptyCredentialsResponseModel(List<String> username, List<String> password) {
}
