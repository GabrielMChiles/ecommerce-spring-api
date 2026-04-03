package com.senai.gabrielm.ecommerce_spring_api.controller;

import com.senai.gabrielm.ecommerce_spring_api.dto.ProdutoRequestDTO;
import com.senai.gabrielm.ecommerce_spring_api.dto.ProdutoResponseDTO;
import com.senai.gabrielm.ecommerce_spring_api.mapper.ProdutoMapper;
import com.senai.gabrielm.ecommerce_spring_api.model.Produto;
import com.senai.gabrielm.ecommerce_spring_api.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final ProdutoMapper produtoMapper;

    public ProdutoController(ProdutoService produtoService, ProdutoMapper produtoMapper) {
        this.produtoService = produtoService;
        this.produtoMapper = produtoMapper;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@RequestBody ProdutoRequestDTO dto) {
        Produto produtoParaSalvar = produtoMapper.toEntity(dto);
        Produto produtoSalvo = produtoService.cadastrarProduto(produtoParaSalvar);
        ProdutoResponseDTO responseDTO = produtoMapper.toResponseDTO(produtoSalvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
        List<ProdutoResponseDTO> produtosDTO = produtoService.listarTodos().stream()
                .map(produtoMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(produtosDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        Produto produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(produtoMapper.toResponseDTO(produto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable Long id, @RequestBody ProdutoRequestDTO dto) {
        Produto dadosAtualizados = produtoMapper.toEntity(dto);
        Produto produtoAtualizado = produtoService.atualizarProduto(id, dadosAtualizados);
        return ResponseEntity.ok(produtoMapper.toResponseDTO(produtoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletarProduto(id);
        return ResponseEntity.noContent().build();
    }
}