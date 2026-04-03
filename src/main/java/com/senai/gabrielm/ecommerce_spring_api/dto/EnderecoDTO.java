package com.senai.gabrielm.ecommerce_spring_api.dto;

public record EnderecoDTO(
        String rua,
        String cidade,
        String cep
) {}