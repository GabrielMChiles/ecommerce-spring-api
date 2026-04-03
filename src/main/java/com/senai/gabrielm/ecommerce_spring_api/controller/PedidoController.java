package com.senai.gabrielm.ecommerce_spring_api.controller;

import com.senai.gabrielm.ecommerce_spring_api.dto.PedidoRequestDTO;
import com.senai.gabrielm.ecommerce_spring_api.dto.PedidoResponseDTO;
import com.senai.gabrielm.ecommerce_spring_api.mapper.PedidoMapper;
import com.senai.gabrielm.ecommerce_spring_api.model.Pedido;
import com.senai.gabrielm.ecommerce_spring_api.model.enums.StatusPedido;
import com.senai.gabrielm.ecommerce_spring_api.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoMapper pedidoMapper;

    public PedidoController(PedidoService pedidoService, PedidoMapper pedidoMapper) {
        this.pedidoService = pedidoService;
        this.pedidoMapper = pedidoMapper;
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criar(@RequestBody PedidoRequestDTO dto) {
        // 1. Traduz o DTO para uma Entidade "falsa" contendo apenas os IDs
        Pedido pedidoStub = pedidoMapper.toEntity(dto);

        // 2. A Service processa a regra de negócio, valida os IDs e cria o pedido real
        Pedido pedidoCriado = pedidoService.criarPedido(pedidoStub);

        // 3. Traduz o pedido salvo para o DTO que está pedindo
        PedidoResponseDTO responseDTO = pedidoMapper.toResponseDTO(pedidoCriado);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        List<PedidoResponseDTO> pedidosDTO = pedidoService.listarTodos().stream()
                .map(pedidoMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(pedidosDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(pedidoMapper.toResponseDTO(pedido));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String statusString = body.get("status");
        StatusPedido novoStatus = StatusPedido.valueOf(statusString.toUpperCase());

        Pedido pedidoAtualizado = pedidoService.atualizarStatus(id, novoStatus);

        return ResponseEntity.ok(pedidoMapper.toResponseDTO(pedidoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.noContent().build();
    }
}