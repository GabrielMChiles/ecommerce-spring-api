package com.senai.gabrielm.ecommerce_spring_api.service;

import com.senai.gabrielm.ecommerce_spring_api.model.*;
import com.senai.gabrielm.ecommerce_spring_api.model.enums.StatusPedido;
import com.senai.gabrielm.ecommerce_spring_api.repository.ClienteRepository;
import com.senai.gabrielm.ecommerce_spring_api.repository.PedidoRepository;
import com.senai.gabrielm.ecommerce_spring_api.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Pedido criarPedido(Pedido pedidoRecebido) {
        // 1. Valida Cliente
        Cliente cliente = clienteRepository.findById(pedidoRecebido.getCliente().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));

        // 2. Valida Endereço (O endereço pertence a ESTE cliente?)
        Long enderecoIdRequisitado = pedidoRecebido.getEndereco().getId();
        Endereco enderecoReal = cliente.getEnderecos().stream()
                .filter(end -> end.getId().equals(enderecoIdRequisitado))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Endereço de entrega inválido ou não pertence a este cliente."));

        // 3. Montar o Pedido Base
        Pedido novoPedido = new Pedido();
        novoPedido.setCliente(cliente);
        novoPedido.setEndereco(enderecoReal); // Usa a entidade real e validada
        novoPedido.setData(LocalDateTime.now());
        novoPedido.setStatus(StatusPedido.CRIADO);

        BigDecimal totalPedido = BigDecimal.ZERO;

        // 4. Processar Itens e Estoque
        for (ItemPedido itemRecebido : pedidoRecebido.getItens()) {
            Produto produto = produtoRepository.findById(itemRecebido.getProduto().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));

            if (produto.getEstoque() < itemRecebido.getQuantidade()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Estoque insuficiente para o produto: " + produto.getNome());
            }

            produto.setEstoque(produto.getEstoque() - itemRecebido.getQuantidade());

            ItemPedido novoItem = new ItemPedido();
            novoItem.setProduto(produto);
            novoItem.setQuantidade(itemRecebido.getQuantidade());
            novoItem.setPrecoUnitario(produto.getPreco());

            BigDecimal subtotal = novoItem.getPrecoUnitario().multiply(new BigDecimal(novoItem.getQuantidade()));
            totalPedido = totalPedido.add(subtotal);

            novoPedido.addItem(novoItem);
        }

        novoPedido.setTotal(totalPedido);

        return pedidoRepository.save(novoPedido);
    }

    // Busca por Id
    @Transactional(readOnly = true)
    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
    }

    // Lista todos
    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    // Atualiza status do pedido
    @Transactional
    public Pedido atualizarStatus(Long id, StatusPedido novoStatus) {
        Pedido pedido = buscarPorId(id);
        StatusPedido statusAtual = pedido.getStatus();

        // Regra de Negócio: Não pula etapas e não volta
        boolean transicaoValida = false;

        if (statusAtual == StatusPedido.CRIADO && novoStatus == StatusPedido.PAGO) {
            transicaoValida = true;
        } else if (statusAtual == StatusPedido.PAGO && novoStatus == StatusPedido.ENVIADO) {
            transicaoValida = true;
        }

        if (!transicaoValida) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Transição de status inválida de " + statusAtual + " para " + novoStatus);
        }

        pedido.setStatus(novoStatus);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void cancelarPedido(Long id) {
        Pedido pedido = buscarPorId(id);

        // Regra de Negócio: Só cancela se estiver CRIADO
        if (pedido.getStatus() != StatusPedido.CRIADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Apenas pedidos com status CRIADO podem ser cancelados.");
        }

        // Devolve o estoque dos produtos
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoque(produto.getEstoque() + item.getQuantidade());
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }
}