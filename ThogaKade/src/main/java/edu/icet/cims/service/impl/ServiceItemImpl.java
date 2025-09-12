package edu.icet.cims.service.impl;

import edu.icet.cims.model.dto.ItemDto;
import edu.icet.cims.model.entity.ItemEntity;
import edu.icet.cims.repository.RepositoryItem;
import edu.icet.cims.service.exception.ItemServiceException;
import edu.icet.cims.util.validator.CommonValidatorUtil;
import edu.icet.cims.util.validator.ItemValidatorUtil;

import java.sql.SQLException;
import java.util.ArrayList;

public class ServiceItemImpl implements edu.icet.cims.service.ServiceItem {

    private RepositoryItem repository = new RepositoryItem();

    // populate item details table
    @Override
    public ArrayList<ItemDto> getItemDTOList() {

        try{
            ArrayList<ItemEntity>  entities = repository.getItemEntityList();       // throw SQLException

            ArrayList<ItemDto> listDTO = new ArrayList<>();

            for (ItemEntity entity : entities){
                listDTO.add(
                        new ItemDto(
                              entity.getItemCode(),
                              entity.getDescription(),
                              entity.getPackSize(),
                              entity.getUnitPrice(),
                              entity.getQtyOnHand()
                        )
                );
            }
            return listDTO;

        } catch (SQLException e) {
            throw new ItemServiceException("Database error while fetching item list, check SQL syntax.", e);
        }
    }


    // delete items from DB
    @Override
    public void deleteItemCall(String itemCode) {
        // validate item code
        String status = validateItemCode(itemCode);
        if(!status.equalsIgnoreCase("Valid")) {
            throw new ItemServiceException(status);                 // invalid code error
        }
        // try delete
        try {
            boolean deleted = repository.deleteItem(itemCode);      // true / false
            if (!deleted) {
                throw new ItemServiceException("Error! Item may not exist");
            }
        } catch (SQLException e) {
            throw new ItemServiceException("Database error while deleting item, check SQL syntax.", e);
        }
    }

    // validate item code -- inner class methode
    private String validateItemCode(String itemCode){
        return (itemCode.isEmpty() || itemCode == null) ? "Null Input" : (!itemCode.matches("^[a-zA-Z0-9]+$")) ? "Invalid Item-code format" : "Valid";
    }


    // validate fields
    @Override
    public void validateItemFields(ItemDto item){

        if(!ItemValidatorUtil.isValidItemCode(item.getCode())){                         // check item code format
            throw new ItemServiceException( "Invalid Item-code, Allowed format :- P001, B0123");
        }

        if (!CommonValidatorUtil.charLengthCheck(item.getDescription(), 100)) {     // description allowed length 100 char
            throw new ItemServiceException( "Description exceeded max allowed character length");
        }

        if(!ItemValidatorUtil.isValidPackSize(item.getPackSize())){                     // check item size unit format
            throw new ItemServiceException( "Invalid Pack size, Must be :- 1kg, 1.5L");
        }

        if(!ItemValidatorUtil.isValidPrice(item.getUnitPrice())){                       // check item price format
            throw new ItemServiceException( "Invalid, Price cannot be Rs. 0.00/=");
        }

        if(!ItemValidatorUtil.isValideQty(item.getQty())){                              // check qty format
            throw new ItemServiceException( "Invalid, Quantity cannot be 0");
        }
    }


    // add new item
    @Override
    public void addNewItem(ItemDto itemDTO) {
        try {
            ItemEntity itemEntity = new ItemEntity(itemDTO.getCode(), itemDTO.getDescription(), itemDTO.getPackSize(), itemDTO.getUnitPrice(), itemDTO.getQty());

            if (repository.itemExistCheck(itemEntity.getItemCode())) {
                throw new ItemServiceException("Error! Item already exist in Database");
            }

            boolean added = repository.addNewItem(itemEntity);
            if ( !added ) throw new ItemServiceException("Could not add new item");

        } catch (SQLException  e) {
            throw new ItemServiceException("Database error while adding item, check SQL syntax.", e);
        }
    }


    // update existing item
    @Override
    public void updateItem(ItemDto itemDTO) {
        try{
            ItemEntity itemEntity = new ItemEntity(itemDTO.getCode(), itemDTO.getDescription(), itemDTO.getPackSize(), itemDTO.getUnitPrice(), itemDTO.getQty());

            if(!repository.itemExistCheck(itemEntity.getItemCode())){
                throw new ItemServiceException("Error! Item not found in Database, Check item code");
            }

            boolean updated = repository.updateItem(itemEntity);

            if ( !updated )  throw new ItemServiceException("Failed to update item");
        }
        catch (SQLException e){
            throw new ItemServiceException("Database error while updating item, check SQL syntax.", e);
        }
    }
}
