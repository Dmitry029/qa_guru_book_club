package tests;

public class TestData {

    public static final String LOGIN_USERNAME = "qaguru";
    public static final String LOGIN_PASSWORD = "qaguru123";
    public static final String LOGIN_WRONG_PASSWORD = "qaguru1234";

    public static final String LOGIN_TOKEN_PREFIX = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    public static final String LOGIN_WRONG_CREDENTIALS_ERROR = "Invalid username or password.";
    public static final String EMPTY_USERNAME_ERROR = "This field may not be blank.";
    public static final String EMPTY_LOGOUT_BODY_ERROR = "This field is required.";

    public static final String REGISTRATION_EXISTING_USER_ERROR =
        "A user with that username already exists.";

    public static final String REGISTRATION_EXISTING_INVALID_USER_NAME_ERROR =
        "Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.";

    public static final String REGISTRATION_IP_REGEXP =
        "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
            + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";

    public static final String UPDATED_FIRST_NAME = "TestFirstName";
    public static final String UPDATED_LAST_NAME = "TestFirstLastName";
    public static final String UPDATED_EMAIL = "test@test.com";
    public static final String INVALID_EMAIL = "invalid_email_com";

}
