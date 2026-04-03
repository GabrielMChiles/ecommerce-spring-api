package com.senai.gabrielm.ecommerce_spring_api.dto;

import java.math.BigDecimal;

public record ProdutoRequestDTO(
        String nome,
        BigDecimal preco,
        Integer estoque
) {}