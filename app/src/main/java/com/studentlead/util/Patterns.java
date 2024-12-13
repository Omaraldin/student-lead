package com.studentlead.util;

import java.util.regex.Pattern;

public class Patterns {

    public static final Pattern STUDENT_CODE = Pattern.compile("^\\d{8}$\n");

    public static final Pattern NATIONAL_ID = Pattern.compile("^\\d{14}$\n");

    public static final Pattern EMAIL_ADDRESS
            = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
}
