package edu.icet.cims.model.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemEntity {

    private String itemCode;
    private String description;
    private String packSize;
    private double unitPrice;
    private int qtyOnHand;
}
