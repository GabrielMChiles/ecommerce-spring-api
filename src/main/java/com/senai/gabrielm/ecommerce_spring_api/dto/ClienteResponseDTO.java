package com.senai.gabrielm.ecommerce_spring_api.dto;

import java.util.List;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String email,
        List<EnderecoDTO> enderecos
) {}