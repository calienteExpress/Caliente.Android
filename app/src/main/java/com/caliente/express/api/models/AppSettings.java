package com.caliente.express.api.models;

/**
 * Created by Home on 31/1/2559.
 */
public class AppSettings
{
    private String serverStatus;
    private String minDateValue;
    private String maxDateValue;
    private String dateTimeFormat;
    private String dateFormat;
    private String timeFormat;
    private String usernameFormat;
    private String passwordFormat;
    private String sessionExpirationMinutes;
    private String usersRoute;
    private String userRoute;
    private String loginRoute;
    private String logoutRoute;
    private String apiVersion;
    private int usernameMinLength;
    private int usernameMaxLength;
    private int passwordMinLength;
    private int passwordMaxLength;

    public String getServerStatus() {
        return serverStatus;
    }
    public void setServerStatus(String serverStatus) {
        this.serverStatus = serverStatus;
    }

    public String getMinDateValue() {
        return minDateValue;
    }
    public void setMinDateValue(String minDateValue) {
        this.minDateValue = minDateValue;
    }

    public String getMaxDateValue() {
        return maxDateValue;
    }
    public void setMaxDateValue(String maxDateValue) {
        this.maxDateValue = maxDateValue;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }
    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }
    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getUsernameFormat() {
        return usernameFormat;
    }
    public void setUsernameFormat(String usernameFormat) {
        this.usernameFormat = usernameFormat;
    }

    public String getPasswordFormat() {
        return passwordFormat;
    }
    public void setPasswordFormat(String passwordFormat) {
        this.passwordFormat = passwordFormat;
    }

    public String getSessionExpirationMinutes() {
        return sessionExpirationMinutes;
    }
    public void setSessionExpirationMinutes(String sessionExpirationMinutes) {
        this.sessionExpirationMinutes = sessionExpirationMinutes;
    }

    public String getUsersRoute() {
        return usersRoute;
    }
    public void setUsersRoute(String usersRoute) {
        this.usersRoute = usersRoute;
    }

    public String getUserRoute() {
        return userRoute;
    }
    public void setUserRoute(String userRoute) {
        this.userRoute = userRoute;
    }

    public String getLoginRoute() {
        return loginRoute;
    }
    public void setLoginRoute(String loginRoute) {
        this.loginRoute = loginRoute;
    }

    public String getLogoutRoute() {
        return logoutRoute;
    }
    public void setLogoutRoute(String logoutRoute) {
        this.logoutRoute = logoutRoute;
    }

    public String getApiVersion() {
        return apiVersion;
    }
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public int getUsernameMinLength() {
        return usernameMinLength;
    }
    public void setUsernameMinLength(int usernameMinLength) {
        this.usernameMinLength = usernameMinLength;
    }

    public int getUsernameMaxLength() {
        return usernameMaxLength;
    }
    public void setUsernameMaxLength(int usernameMaxLength) {
        this.usernameMaxLength = usernameMaxLength;
    }

    public int getPasswordMinLength() {
        return passwordMinLength;
    }
    public void setPasswordMinLength(int passwordMinLength) {
        this.passwordMinLength = passwordMinLength;
    }

    public int getPasswordMaxLength() {
        return passwordMaxLength;
    }
    public void setPasswordMaxLength(int passwordMaxLength) {
        this.passwordMaxLength = passwordMaxLength;
    }
}
