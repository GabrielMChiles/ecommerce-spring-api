package com.senai.gabrielm.ecommerce_spring_api.controller;

import com.senai.gabrielm.ecommerce_spring_api.dto.ClienteRequestDTO;
import com.senai.gabrielm.ecommerce_spring_api.dto.ClienteResponseDTO;
import com.senai.gabrielm.ecommerce_spring_api.mapper.ClienteMapper;
import com.senai.gabrielm.ecommerce_spring_api.model.Cliente;
import com.senai.gabrielm.ecommerce_spring_api.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final ClienteMapper clienteMapper;

    public ClienteController(ClienteService clienteService, ClienteMapper clienteMapper) {
        this.clienteService = clienteService;
        this.clienteMapper = clienteMapper;
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrar(@RequestBody ClienteRequestDTO dto) {
        // 1. Converte a requisição (DTO) para a entidade de domínio
        Cliente clienteParaSalvar = clienteMapper.toEntity(dto);

        // 2. Passa a entidade para a Service aplicar as regras de negócio e salvar
        Cliente clienteSalvo = clienteService.cadastrarCliente(clienteParaSalvar);

        // 3. Converte a entidade salva de volta para um DTO de resposta limpo
        ClienteResponseDTO responseDTO = clienteMapper.toResponseDTO(clienteSalvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        // Busca as entidades, transforma cada uma em DTO usando Stream API e coleta em uma lista
        List<ClienteResponseDTO> clientesDTO = clienteService.listarTodos().stream()
                .map(clienteMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(clientesDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        // Busca a entidade na Service e converte para DTO antes de devolver
        Cliente cliente = clienteService.buscarPorId(id);
        ClienteResponseDTO responseDTO = clienteMapper.toResponseDTO(cliente);

        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id, @RequestBody ClienteRequestDTO dto) {

        Cliente dadosAtualizados = clienteMapper.toEntity(dto);

        // A Service lida com a busca e a mescla dos dados
        Cliente clienteAtualizado = clienteService.atualizarCliente(id, dadosAtualizados);

        // Converte
        ClienteResponseDTO responseDTO = clienteMapper.toResponseDTO(clienteAtualizado);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }
}