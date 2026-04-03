package com.senai.gabrielm.ecommerce_spring_api.mapper;

import com.senai.gabrielm.ecommerce_spring_api.dto.ProdutoRequestDTO;
import com.senai.gabrielm.ecommerce_spring_api.dto.ProdutoResponseDTO;
import com.senai.gabrielm.ecommerce_spring_api.model.Produto;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {

    // Converte a requisição web para a Entidade que a Service entende
    public Produto toEntity(ProdutoRequestDTO dto) {
        Produto produto = new Produto();
        produto.setNome(dto.nome());
        produto.setPreco(dto.preco());
        produto.setEstoque(dto.estoque());
        return produto;
    }

    // Converte a Entidade do banco para um formato limpo de resposta
    public ProdutoResponseDTO toResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getPreco(),
                produto.getEstoque()
        );
    }
}