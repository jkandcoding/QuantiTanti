package com.example.android.quantitanti.helpers;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {


    public static String fromLowerCaseToFirstCapitalizedLetter(String word) {
        String wordCapitalized = word.substring(0,1).toUpperCase() + word.substring(1);
        return wordCapitalized;
    }

    public static String fromUperCaseToFirstCapitalizedLetter(String word) {
        String wordCapitalized = word.substring(0,1) + word.substring(1).toLowerCase();
        return wordCapitalized;
    }

    public static int fromDoubleToInt(double varDouble) {
        int varInt = (int) (varDouble * 100);
        return varInt;
    }

    public static String fromIntToDecimalString(int varInt) {
        if (varInt > 99999999) {
            String millions = String.valueOf(varInt/100000000);
            String thousands = String.valueOf((varInt % 100000000)/100000);
            String hundreds = String.valueOf((varInt % 100000)/100);
            String decimal = String.valueOf(varInt % 100);
            if (varInt % 100 < 10) {
                return millions + " " + thousands + " " + hundreds + ".0" + decimal;
            } else {
                return millions + " " + thousands + " " + hundreds + "." + decimal;
            }

        } if (100000000 > varInt && varInt > 99999) {
            String thousands = String.valueOf(varInt / 100000);
            String hundreds = String.valueOf((varInt % 100000) / 100);
            String decimal = String.valueOf(varInt % 100);
            if (varInt % 100 < 10) {
                return thousands + " " + hundreds + ".0" + decimal;
            } else {
                return thousands + " " + hundreds + "." + decimal;
            }

        } if (100000 > varInt && varInt > 99) {
            String hundreds = String.valueOf(varInt / 100);
            String decimal = String.valueOf(varInt % 100);
            if (varInt % 100 < 10) {
                return hundreds + ".0" + decimal;
            } else {
                return hundreds + "." + decimal;
            }
        } if (varInt < 100) {
            String decimal = String.valueOf(varInt % 100);
            if (varInt % 100 < 10) {
                return "0.0" + decimal;
            } else {
                return "0." + decimal;
            }
        }
        return "error";
    }

public static void test() {
    int a = 9;
    String b = fromIntToDecimalString(a);
    Log.d(b, "testdecimal");
}




}
