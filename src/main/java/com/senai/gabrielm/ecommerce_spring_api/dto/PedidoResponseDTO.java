package com.senai.gabrielm.ecommerce_spring_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
        Long id,
        LocalDateTime data,
        String status,
        Long clienteId,
        String nomeCliente, // Achatamos os dados para facilitar a vida do Front-end
        EnderecoDTO enderecoEntrega,
        List<ItemPedidoResponseDTO> itens,
        BigDecimal total
) {}