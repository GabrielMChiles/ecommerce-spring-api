package com.senai.gabrielm.ecommerce_spring_api.service;

import com.senai.gabrielm.ecommerce_spring_api.model.Produto;
import com.senai.gabrielm.ecommerce_spring_api.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Produto cadastrarProduto(Produto produtoRecebido) {
        // Regra de Negócio Defensiva
        validarValores(produtoRecebido.getPreco(), produtoRecebido.getEstoque());

        Produto novoProduto = new Produto();
        novoProduto.setNome(produtoRecebido.getNome());
        novoProduto.setPreco(produtoRecebido.getPreco());
        novoProduto.setEstoque(produtoRecebido.getEstoque());

        return produtoRepository.save(novoProduto);
    }

    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    @Transactional
    public Produto atualizarProduto(Long id, Produto dadosAtualizados) {
        Produto produtoExistente = buscarPorId(id);

        // Atualização Parcial do nome
        if (dadosAtualizados.getNome() != null && !dadosAtualizados.getNome().isBlank()) {
            produtoExistente.setNome(dadosAtualizados.getNome());
        }

        // Atualização Parcial do preço
        if (dadosAtualizados.getPreco() != null) {
            if (dadosAtualizados.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "O preço deve ser maior que zero.");
            }
            produtoExistente.setPreco(dadosAtualizados.getPreco());
        }

        // Atualização Parcial do estoque
        if (dadosAtualizados.getEstoque() != null) {
            if (dadosAtualizados.getEstoque() < 0) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "O estoque não pode ser negativo.");
            }
            produtoExistente.setEstoque(dadosAtualizados.getEstoque());
        }

        return produtoRepository.save(produtoExistente);
    }

    @Transactional
    public void deletarProduto(Long id) {
        Produto produtoExistente = buscarPorId(id);

        try {
            produtoRepository.delete(produtoExistente);
            // O flush força o Hibernate a mandar o comando DELETE pro banco IMEDIATAMENTE.
            // Se houver um pedido atrelado a este produto, o banco vai gritar e cair no automatic. no catch
            produtoRepository.flush();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Exclusão negada: Este produto já faz parte do histórico de pedidos e não pode ser deletado.");
        }
    }

    // Método auxiliar
    private void validarValores(BigDecimal preco, Integer estoque) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "O preço do produto deve ser maior que zero.");
        }
        if (estoque == null || estoque < 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "O estoque inicial não pode ser negativo.");
        }
    }
}
