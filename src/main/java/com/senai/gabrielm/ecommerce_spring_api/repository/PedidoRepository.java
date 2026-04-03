package com.senai.gabrielm.ecommerce_spring_api.repository;

import com.senai.gabrielm.ecommerce_spring_api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Busca todos os pedidos atrelados ao ID de um cliente específico
    List<Pedido> findByClienteId(Long clienteId);
}
