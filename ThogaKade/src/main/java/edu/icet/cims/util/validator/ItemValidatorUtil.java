package edu.icet.cims.util.validator;

public class ItemValidatorUtil {

    public static boolean isValidItemCode(String itemCode){
        return (itemCode.matches("^[A-Z][0-9]{3,}$")) ? true : false;                       // valid format : C001
    }

    public static boolean isValidPackSize(String packSize){
        return (packSize.matches("^[0-9]+(\\.[0-9]+)?(g|kg|ml|L|cm|m)$")) ? true : false;   // valid format : 1Kg, 1.5L
    }

    public static boolean isValidPrice(double unitPrice){
        return (unitPrice > 0) ? true : false;
    }

    public  static boolean isValideQty(int qty){
        return (qty > 0) ? true : false;
    }
}
