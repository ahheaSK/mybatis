package com.example.mybatis.constants;

public final class ApiMessages {

    private ApiMessages() {
    }

    // Pagination & list
    public static final String PAGINATION_SUCCESS = "Get All With Pagination Data Successfully";

    // Auth
    public static final String LOGIN_SUCCESS = "Login successful";
    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password";

    // Security
    public static final String UNAUTHORIZED = "Unauthorized";
    public static final String TOO_MANY_REQUESTS = "Too many requests. Try again later.";

    // CRUD (shared for User, Role, Permission, Menu)
    public static final String GET_ONE_SUCCESS = "Get successfully";
    public static final String CREATED_SUCCESS = "Created successfully";
    public static final String UPDATED_SUCCESS = "Updated successfully";
    public static final String DELETED_SUCCESS = "Deleted successfully";

    // Role–menu
    public static final String GET_MENUS_BY_ROLE_SUCCESS = "Get menus by role successfully";
    public static final String MENUS_ASSIGNED = "Menus assigned successfully";

    // Errors (exception handler & generic)
    public static final String INVALID_REQUEST_BODY = "Invalid request body or JSON format";
    public static final String DATA_INTEGRITY_VIOLATION = "Data integrity violation";
    public static final String DUPLICATE_KEY = "Duplicate value violates unique constraint";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
}
