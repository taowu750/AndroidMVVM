package com.wutaodsg.androidmvvm.model;

/**
 * Created by wutao on 2018/3/25.
 */

public class UserInfoConfirmUtil {

    private static final int MIN_USER_NAME_LENGTH = 6;
    private static final int MIN_PASSWORD_LENGTH = 6;

    private static final String VALID_USER_NAME = "wutaodsg";
    private static final String VALID_PASSWORD = "wutaodsg";


    public static boolean isValidUserName(String userName) {
        return userName.trim().length() >= MIN_USER_NAME_LENGTH;
    }

    public static boolean isValidPassword(String password) {
        return password.trim().length() >= MIN_PASSWORD_LENGTH;
    }

    public static boolean isValidUserNameAndPassword(String userName, String password) {
        return userName.trim().length() >= MIN_USER_NAME_LENGTH &&
                password.trim().length() >= MIN_PASSWORD_LENGTH;
    }

    public static boolean isRegisteredUserNameAndPassword(String userName, String password) {
        return userName.trim().equals(VALID_USER_NAME) && password.trim().equals(VALID_PASSWORD);
    }
}
