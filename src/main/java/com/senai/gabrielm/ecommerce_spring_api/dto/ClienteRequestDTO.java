package com.senai.gabrielm.ecommerce_spring_api.dto;

import java.util.List;

public record ClienteRequestDTO(
        String nome,
        String email,
        List<EnderecoDTO> enderecos
) {}