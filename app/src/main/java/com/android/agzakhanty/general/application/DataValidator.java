package com.android.agzakhanty.general.application;

import android.text.TextUtils;

import java.util.Date;

/**
 * Created by a.moanes on 25/09/2017.
 */

public class DataValidator {

    public static boolean isStringEmpty(String toCheck) {
        return toCheck.isEmpty();
    }

    public static boolean isNotNull(Object o) {
        if (o == null)
            return false;
        return true;
    }

    public static boolean isPasswordNotLessThan(String str, int minLength) {
        return !TextUtils.isEmpty(str) && !(str.length() < minLength);
    }

    public static boolean isPasswordConfirmed(String pass, String confPass) {
        return pass.equals(confPass);
    }

    public static boolean isDateBeforeNow(Date d1) {
        return new Date().before(d1);
    }

    public static boolean isDateBeforeDate(Date d1, Date d2) {
        return d1.before(d2);
    }

    public static Boolean validateEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static Boolean validateMobileNumber(String phone) {
        return !TextUtils.isEmpty(phone) && (phone.length() == 11);
    }


}
