package edu.icet.cims.service;

import edu.icet.cims.model.dto.ItemDto;
import edu.icet.cims.service.exception.ItemServiceException;

import java.util.ArrayList;

public interface ServiceItem {

    ArrayList<ItemDto> getItemDTOList() throws ItemServiceException;

    void deleteItemCall(String itemCode) throws ItemServiceException;
    void validateItemFields(ItemDto item) throws ItemServiceException;
    void addNewItem(ItemDto item) throws ItemServiceException;
    void updateItem(ItemDto item) throws ItemServiceException;

}
