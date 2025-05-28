package com.sammy.vehiclemanagementsystem.enums;

import lombok.Getter;

@Getter
public enum IEmailTemplate {
    RESET_PASSWORD("forgot-password-email"),
    ACTIVATE_ACCOUNT("verify-account-email"),
    ACCOUNT_VERIFIED("account-verification-successful"),
    PASSWORD_RESET_SUCCESS("password-reset-successful"),
    ACCOUNT_VERIFICATION("account_verification");

    private final String name;

    IEmailTemplate(String name) {
        this.name = name;
    }


}
