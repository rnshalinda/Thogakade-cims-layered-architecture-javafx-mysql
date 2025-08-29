package edu.icet.cims.util.validator;


import java.time.LocalDate;

public class CommonValidatorUtil {

    // is double format & digit only
    public static boolean isDoubleFormat(String decimalNum){
        return decimalNum.matches("^[0-9]+(\\.[0-9]+)?$");
    }

    // is int format & digit only
    public static boolean isIntFormat(String digit){
        return digit.matches("^[0-9]+$");
    }

    // text length check
    public static boolean charLengthCheck(String txt, int len){
        return txt.length() <= len;            //  (txt.matches("^(?=.{1,"+len+"})$")) ? true : false;  // (?=.{1,30}$)
    }

    // contains english letters only
    public static boolean isLettersOnly(String str){
        return str.matches("^[a-zA-Z]+$");
    }

    // check age less than 120
    public static boolean isValidAge(LocalDate dob, int ageLimit){
        return (LocalDate.now().getYear() - dob.getYear()) <= ageLimit;
    }

    // checks address format allowed characters (Aa9-\#.,/:)
    public static boolean isValidAddress(String address){
        return address.matches("^[A-Za-z0-9/,#.\\-:\\s\\\\]+$");
    }

}
