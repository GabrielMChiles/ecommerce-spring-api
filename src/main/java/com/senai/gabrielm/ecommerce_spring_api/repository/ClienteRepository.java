package com.senai.gabrielm.ecommerce_spring_api.repository;

import com.senai.gabrielm.ecommerce_spring_api.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Busca se já existe um e-mail adicionado no banco
    boolean existsByEmail(String email);
}

