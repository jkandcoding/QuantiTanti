package com.example.android.quantitanti.helpers;

public class Helper {


    public static String fromLowerCaseToFirstCapitalizedLetter(String word) {
        return word.substring(0,1).toUpperCase() + word.substring(1);
    }

    public static String fromUperCaseToFirstCapitalizedLetter(String word) {
        return word.substring(0,1) + word.substring(1).toLowerCase();
    }

    public static int fromDoubleToInt(double varDouble) {
        return  (int) (varDouble * 100);
    }

    public static String fromIntToDecimalString(int varInt) {
        if (varInt > 99999999) {
            String millions = String.valueOf(varInt/100000000);
            int thousands = (varInt % 100000000)/100000;
            String thousandsS = thousandsDigits(thousands);
            int hundreds = (varInt % 100000) / 100;
            String hundredsS = hundredsDigits(hundreds);
            String decimal = String.valueOf(varInt % 100);
            if (varInt % 100 < 10) {
                return millions + " " + thousandsS + " " + hundredsS + ".0" + decimal;
            } else {
                return millions + " " + thousandsS + " " + hundredsS + "." + decimal;
            }

        } if (100000000 > varInt && varInt > 99999) {
            String thousands = String.valueOf(varInt / 100000);
            int hundreds = (varInt % 100000) / 100;
            String hundredsS = hundredsDigits(hundreds);
            String decimal = String.valueOf(varInt % 100);
            if (varInt % 100 < 10) {
                return thousands + " " + hundredsS + ".0" + decimal;
            } else {
                return thousands + " " + hundredsS + "." + decimal;
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

    private static String hundredsDigits(int hundreds) {
        if (100 > hundreds && hundreds > 9) {
            return "0" + hundreds;
        } if (10 > hundreds) {
            return "00" + hundreds;
        } if (hundreds > 99) {
            return String.valueOf(hundreds);
        } if (hundreds == 0) {
            return "000";
        }
        return null;
    }

    private static String thousandsDigits(int thousands) {
        if (100 > thousands && thousands > 9) {
            return "0" + thousands;
        } if (10 > thousands) {
            return "00" + thousands;
        } if (thousands > 99) {
            return String.valueOf(thousands);
        } if (thousands == 0) {
            return "000";
        }
        return null;
    }
}



