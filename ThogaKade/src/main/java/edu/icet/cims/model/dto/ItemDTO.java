package edu.icet.cims.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDTO {

    private String code;
    private String description;
    private String packSize;
    private double unitPrice;
    private int qty;
}
