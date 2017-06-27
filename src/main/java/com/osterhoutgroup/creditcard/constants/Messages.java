package com.osterhoutgroup.creditcard.constants;

public class Messages {
    // Argument not provided exception message
    public final static String ARGUMENT_NOT_PROVIDED_EXCEPTION_MESSAGE =
        "must be provided.";

    // Configuration exception message
    public final static String CONFIGURATION_KEY_MISSING =
        "Unable to resolve requested configuration, %s is not defined.";

    // General error messages
    public final static String CHANGES_CANNOT_BE_PERSISTED =
        "Requested change cannot be made.";

    // User controller messages
    public final static String INACTIVE_USER = "Inactive user";
    public final static String NO_PERMISSION_TO_EDIT_USER =
        "No permission to edit another user";
    public final static String UNABLE_TO_SOLVE_USER_BY_ID =
        "Unable to solve user by user id";
    public final static String INVALID_ACCESS_TOKEN =
        "Access token invalid or expired!";
    public final static String USER_NOT_RECOGNIZED = "Email not recognized.";
    public final static String USER_NOT_VERIFIED =
        "Please click the link in your email to confirm your account.";
    public final static String USER_DISABLED =
        "This account has been disabled by ODG admin. Please contact ODG support!";
    public final static String TOKEN_EXPIRED =
        "Access token invalid or expired!";
    public final static String UNABLE_TO_RESOLVE_USER_FOR_TOKEN =
        "Unable to resolve user for provided tocken.";
    public final static String EMAIL_ALREADY_USED =
        "Email address already registered.";
    public final static String MISSING_NEW_PASSWORD =
        "New password must be provided.";
    public final static String MISSING_OLD_PASSWORD =
        "Old password must be provided.";
    public final static String OLD_PASSWORD_MISMATCH =
        "Old password does not match!";
    public final static String INVALID_PASSWORD_RESET_CODE =
        "Invalid password reset request code.";
    public final static String USER_DOES_NOT_EXIST_RECREATE =
        "User account with provided email address does not exist, please create user account.";

            //  Credit card controller messages
    public final static String UNABLE_TO_RESOLVE_USER_FOR_EMAIL =
        "Unable to resolve user for provided email address.";
    public final static String ERROR_WHILE_CREATING_CUSTOMER =
        "Unable to create customer account.";
    public final static String UNABLE_TO_CREATE_CREDIT_CARD =
        "Unable to store credit card data.";
    public final static String CREDIT_CARD_ALREADY_REGISTERED =
        "This card is already added to your account!";
    public final static String CREDIT_CARD_SERVICE_DATA_ERROR =
        "The stripe service of store credit card didn't return correct data!";
    public final static String UNABLE_TO_RETRIEVE_CUSTOMER_DATA =
        "Unable to retrieve customer data for provided customer identifier.";

    // Licence key controller messages
    public final static String LICENCE_KEY_DOES_NOT_EXIST =
        "Licence key does not exist!";
    public final static String LICENCE_KEY_DEACTIVATED =
        "Licence key deactivated!";
    public final static String PACKAGE_NAME_OR_FINGERPRINT_INVALID =
        "Package name or fingerprint not valid.";
    public final static String PACKAGE_REQUEST_ALREADY_SUBMITTED =
        "Request for this package name is already submitted!";

}
