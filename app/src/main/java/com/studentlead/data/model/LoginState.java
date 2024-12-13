package com.studentlead.data.model;

public class LoginState {
    private final boolean isLoading;
    private final String error;

    private final int errorResource;
    private final boolean isSuccess;

    public LoginState(boolean isLoading, String error, boolean isSuccess, int errorResource) {
        this.isLoading = isLoading;
        this.error = error;
        this.isSuccess = isSuccess;
        this.errorResource = errorResource;
    }

    public static LoginState loading() {
        return new LoginState(true, null, false, -1);
    }

    public static LoginState error(String message) {
        return new LoginState(false, message, false, -1);
    }

    public static LoginState errorResource(int error) {
        return new LoginState(false, null, false, error);
    }

    public static LoginState success() {
        return new LoginState(false, null, true, -1);
    }

    public static LoginState initial() {
        return new LoginState(false, null, false, -1);
    }

    public boolean isLoading() {
        return isLoading;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public int getErrorResource() {
        return errorResource;
    }
}