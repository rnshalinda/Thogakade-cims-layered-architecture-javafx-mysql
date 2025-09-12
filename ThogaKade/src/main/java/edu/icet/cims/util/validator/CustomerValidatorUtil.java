package edu.icet.cims.util.validator;

import java.time.LocalDate;

public class CustomerValidatorUtil {

    // checks if customer-id format - alphanumeric only
    public static boolean isValidCustId(String custID){
        return (custID.matches("^[A-Z][0-9]{3,}$")) ? true : false;                   // valid format : C001
    }

    // cheks customer name
    public static boolean isValidName(String name){
        return (name.matches("^([A-Z][a-z]+)( [A-Z][a-z]+)*?$")) ? true : false;      // names fist char uppercase, spaced between - (...[A
    }

    // checks if the dob is before the present date
    public static boolean isDobFuture(LocalDate dob){
        return (dob.isAfter(LocalDate.now())) ? false : true;
    }

    // validate postal code - only alphanumeric & #.,-/ \
    public static boolean isValidPostal(String posatal){
        return posatal.matches("^[A-Za-z0-9.,#\\-\\s]+$");
    }

}
