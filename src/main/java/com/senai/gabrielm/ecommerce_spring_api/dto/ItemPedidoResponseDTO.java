package com.senai.gabrielm.ecommerce_spring_api.dto;

import java.math.BigDecimal;

public record ItemPedidoResponseDTO(
        Long produtoId,
        String nomeProduto,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal // Diferencial: Já devolvemos o cálculo do item mastigado!
) {}