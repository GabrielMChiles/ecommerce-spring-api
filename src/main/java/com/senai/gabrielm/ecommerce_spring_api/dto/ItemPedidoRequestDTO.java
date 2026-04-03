package com.senai.gabrielm.ecommerce_spring_api.dto;

public record ItemPedidoRequestDTO(
        Long produtoId,
        Integer quantidade
) {}