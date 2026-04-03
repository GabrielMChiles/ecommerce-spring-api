package com.senai.gabrielm.ecommerce_spring_api.mapper;

import com.senai.gabrielm.ecommerce_spring_api.dto.*;
import com.senai.gabrielm.ecommerce_spring_api.model.Cliente;
import com.senai.gabrielm.ecommerce_spring_api.model.Endereco;
import com.senai.gabrielm.ecommerce_spring_api.model.ItemPedido;
import com.senai.gabrielm.ecommerce_spring_api.model.Pedido;
import com.senai.gabrielm.ecommerce_spring_api.model.Produto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public class PedidoMapper {

    // 1. Converte a requisição web em uma Entidade "Dublê" (Stub)
    public Pedido toEntity(PedidoRequestDTO dto) {
        Pedido pedido = new Pedido();

        // Cria um cliente "dublê" só com o ID para a Service buscar depois
        Cliente clienteStub = new Cliente();
        clienteStub.setId(dto.clienteId());
        pedido.setCliente(clienteStub);

        // Cria um endereço "dublê" só com o ID
        Endereco enderecoStub = new Endereco();
        enderecoStub.setId(dto.enderecoEntregaId());
        pedido.setEndereco(enderecoStub);

        // Dubla os itens
        if (dto.itens() != null) {
            for (ItemPedidoRequestDTO itemDto : dto.itens()) {
                ItemPedido item = new ItemPedido();
                item.setQuantidade(itemDto.quantidade());

                Produto produtoStub = new Produto();
                produtoStub.setId(itemDto.produtoId());
                item.setProduto(produtoStub);

                pedido.addItem(item);
            }
        }

        return pedido;
    }

    // 2. Converte a Entidade real do banco de dados para a resposta do usuário
    public PedidoResponseDTO toResponseDTO(Pedido pedido) {
        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getData(),
                pedido.getStatus().name(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNome(),
                new EnderecoDTO(
                        pedido.getEndereco().getRua(),
                        pedido.getEndereco().getCidade(),
                        pedido.getEndereco().getCep()
                ),
                pedido.getItens().stream()
                        .map(this::toItemResponseDTO)
                        .collect(Collectors.toList()),
                pedido.getTotal()
        );
    }

    // Método auxiliar
    private ItemPedidoResponseDTO toItemResponseDTO(ItemPedido item) {
        BigDecimal subtotal = item.getPrecoUnitario().multiply(new BigDecimal(item.getQuantidade()));

        return new ItemPedidoResponseDTO(
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                subtotal
        );
    }
}