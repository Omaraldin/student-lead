package com.studentlead.data.model;

public class LoginState {
    private final boolean isLoading;
    private final String error;
    private final boolean isSuccess;

    public LoginState(boolean isLoading, String error, boolean isSuccess) {
        this.isLoading = isLoading;
        this.error = error;
        this.isSuccess = isSuccess;
    }

    public static LoginState loading() {
        return new LoginState(true, null, false);
    }

    public static LoginState error(String message) {
        return new LoginState(false, message, false);
    }

    public static LoginState success() {
        return new LoginState(false, null, true);
    }

    public static LoginState initial() {
        return new LoginState(false, null, false);
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
}