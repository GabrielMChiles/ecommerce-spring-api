package com.senai.gabrielm.ecommerce_spring_api.dto;

import java.math.BigDecimal;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        BigDecimal preco,
        Integer estoque
) {}