package com.senai.gabrielm.ecommerce_spring_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_CLIENTE")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true) // Regra 1: Email único
    @Email
    private String email;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Endereco> enderecos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();

    public Cliente() {}

    public Cliente(Long id, String nome, String email, List<Endereco> enderecos, List<Pedido> pedidos) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.enderecos = enderecos;
        this.pedidos = pedidos;
    }

    public List<Endereco> getEnderecos() {
        return enderecos;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    // --- Métodos Utilitários para Endereco ---

    public void addEndereco(Endereco endereco) {
        enderecos.add(endereco);
        endereco.setCliente(this);// Sincroniza o outro lado da relação
    }

    public void removeEndereco(Endereco endereco) {
        enderecos.remove(endereco);
        endereco.setCliente(null); // Desfaz a sincronização
    }

    // --- Métodos Utilitários para Pedido ---

    public void addPedido(Pedido pedido) {
        pedidos.add(pedido);
        pedido.setCliente(this);
    }

    public void removePedido(Pedido pedido) {
        pedidos.remove(pedido);
        pedido.setCliente(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}