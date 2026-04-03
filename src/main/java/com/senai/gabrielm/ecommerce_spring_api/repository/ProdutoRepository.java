package com.senai.gabrielm.ecommerce_spring_api.repository;

import com.senai.gabrielm.ecommerce_spring_api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
