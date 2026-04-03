package com.senai.gabrielm.ecommerce_spring_api.mapper;

import com.senai.gabrielm.ecommerce_spring_api.dto.ClienteRequestDTO;
import com.senai.gabrielm.ecommerce_spring_api.dto.ClienteResponseDTO;
import com.senai.gabrielm.ecommerce_spring_api.dto.EnderecoDTO;
import com.senai.gabrielm.ecommerce_spring_api.model.Cliente;
import com.senai.gabrielm.ecommerce_spring_api.model.Endereco;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ClienteMapper {

    // Transforma a requisição web em uma Entidade de banco
    public Cliente toEntity(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());

        if (dto.enderecos() != null) {
            for (EnderecoDTO endDto : dto.enderecos()) {
                Endereco endereco = new Endereco();
                endereco.setRua(endDto.rua());
                endereco.setCidade(endDto.cidade());
                endereco.setCep(endDto.cep());

                cliente.addEndereco(endereco); // Mantém o relacionamento intacto!
            }
        }
        return cliente;
    }

    // Transforma a Entidade de banco em uma resposta limpa para a web
    public ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getEnderecos().stream()
                        .map(end -> new EnderecoDTO(end.getRua(), end.getCidade(), end.getCep()))
                        .collect(Collectors.toList())
        );
    }
}