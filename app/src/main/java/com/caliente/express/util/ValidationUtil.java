package com.caliente.express.util;

import com.caliente.express.storage.LocalStorage;

/**
 * Created by John R. Kosinski on 29/1/2559.
 * Utilities for validating input.
 */
public class ValidationUtil
{
    public static String validatePassword(String password)
    {
        if (password.length() == 0)
        return "password required";
        if (password.length() < LocalStorage.getAppSettings().getPasswordMinLength())
            return "minimum length is " + Integer.toString(LocalStorage.getAppSettings().getPasswordMinLength());
        if (password.length() > LocalStorage.getAppSettings().getPasswordMaxLength())
            return "maximum length is " + Integer.toString(LocalStorage.getAppSettings().getPasswordMaxLength());
        if (password.contains(" "))
            return "may not contain spaces";
        return "";
    }

    public static String validateUsername(String username)
    {
        if (username.length() == 0)
            return "username required";
        if (username.length() < LocalStorage.getAppSettings().getUsernameMinLength())
            return "minimum length is " + Integer.toString(LocalStorage.getAppSettings().getUsernameMinLength());
        if (username.length() > LocalStorage.getAppSettings().getUsernameMaxLength())
            return "maximum length is " + Integer.toString(LocalStorage.getAppSettings().getUsernameMaxLength());
        if (!StringUtil.matchesPattern(username, LocalStorage.getAppSettings().getUsernameFormat())) // "^[a-zA-Z0-9_]*$"))
            return "contains invalid characters";
        return "";
    }
}
