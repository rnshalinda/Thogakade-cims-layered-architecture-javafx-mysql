package edu.icet.cims.service;

import edu.icet.cims.model.dto.ItemDTO;
import edu.icet.cims.model.entity.ItemEntity;
import edu.icet.cims.repository.RepositoryItem;
import edu.icet.cims.util.validator.CommonValidatorUtil;
import edu.icet.cims.util.validator.ItemValidatorUtil;

import java.sql.SQLException;
import java.util.ArrayList;

public class ServiceItem {

    RepositoryItem repository = new RepositoryItem();

    // populate item details table
    public ArrayList<ItemDTO> getItemDTOList() throws SQLException {

        ArrayList<ItemEntity>  entities = repository.getItemEntityList();

        ArrayList<ItemDTO> listDTO = new ArrayList<>();

        for (ItemEntity entity : entities){
            listDTO.add(
                    new ItemDTO(
                          entity.getItemCode(),
                          entity.getDescription(),
                          entity.getPackSize(),
                          entity.getUnitPrice(),
                          entity.getQtyOnHand()
                    )
            );
        }

        return listDTO;
    }

    // delete items from DB
    public String deleteItemCall(String itemCode) throws SQLException {
        String status = validateItemCode(itemCode);
        if(status.equalsIgnoreCase("Valid")){

            return (repository.deleteItem(itemCode)) ? "Successful" : "Error! Item may not exist";
        }
        return status;
    }

    // validate item code
    private String validateItemCode(String itemCode){
        return (itemCode.isEmpty() || itemCode == null) ? "Null Input" : (!itemCode.matches("^[a-zA-Z0-9]+$")) ? "Invalid Item-code format" : "Valid";
    }

    // validate fields
    public String fieldFormatValidation(ItemDTO item){

        if(!ItemValidatorUtil.isValidItemCode(item.getCode())){                   // check item code format
            return "Invalid Item-code, Allowed format :- P001, B0123";
        }

        if (!CommonValidatorUtil.charLengthCheck(item.getDescription(), 100)) {       // description allowed length 100 char
            return "Description exceeded max allowed character length";
        }

        if(!ItemValidatorUtil.isValidPackSize(item.getPackSize())){               // check item size unit format
            return "Invalid Pack size, Must be :- 1kg, 1.5L";
        }

        if(!ItemValidatorUtil.isValidPrice(item.getUnitPrice())){                 // check item price format
            return "Invalid, Price cannot be Rs. 0.00/=";
        }

        if(!ItemValidatorUtil.isValideQty(item.getQty())){                        // check qty format
            return "Invalid, Quantity cannot be 0";
        }

        return "valid";         // if all fields has correct format
    }

    // add new item
    public String addNewItem(ItemDTO itemDTO) throws SQLException {
        ItemEntity itemEntity = new ItemEntity(itemDTO.getCode(), itemDTO.getDescription(), itemDTO.getPackSize(), itemDTO.getUnitPrice(), itemDTO.getQty());

        if(repository.itemExistCheck(itemEntity.getItemCode())){
            return "Error! Item already exist in Database";
        }
        return (repository.addNewItem(itemEntity)) ? "Successful" : "Error! Could not add item to Database";
    }

    // update existing item
    public  String updateItem(ItemDTO itemDTO) throws SQLException {
        ItemEntity itemEntity = new ItemEntity(itemDTO.getCode(), itemDTO.getDescription(), itemDTO.getPackSize(), itemDTO.getUnitPrice(), itemDTO.getQty());

        if(repository.itemExistCheck(itemEntity.getItemCode())){
            return (repository.updateItem(itemEntity)) ? "Successful" : "Error! Could not update, Check item code";
        }
        return "Error! Item not found in Database";
    }
}
