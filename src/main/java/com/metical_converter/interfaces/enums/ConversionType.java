package com.metical_converter.interfaces.enums;

import lombok.Getter;

@Getter
public enum ConversionType {
    SELL_FOREIGN_TO_MZN("Venda de Moeda Estrangeira"),
    CROSS_CURRENCY("Conversão Cruzada"),
    SELL_MZN_FOR_FOREIGN("Venda de Metical");
    private final String description;
    ConversionType(String description) {
        this.description = description;
    }

}
