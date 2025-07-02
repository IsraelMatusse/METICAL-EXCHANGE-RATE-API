package com.metical_converter.interfaces.enums;

import lombok.Getter;

@Getter
public enum ConversionType {
    SELL_FOREIGN_TO_MZN("Venda de Moeda Estrangeira"),
    BUY_FOREIGN_WITH_MZN("Compra de Moeda Estrangeira"),
    SELL_MZN_FOR_FOREIGN("Venda de Metical"),
    CURRENCY_TO_CURRENCY("Conversão Entre Moedas");

    private final String description;

    ConversionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
