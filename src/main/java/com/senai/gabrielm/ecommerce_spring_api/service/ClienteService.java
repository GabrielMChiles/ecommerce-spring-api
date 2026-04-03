package com.senai.gabrielm.ecommerce_spring_api.service;

import com.senai.gabrielm.ecommerce_spring_api.model.Cliente;
import com.senai.gabrielm.ecommerce_spring_api.model.Endereco;
import com.senai.gabrielm.ecommerce_spring_api.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public Cliente cadastrarCliente(Cliente clienteRecebido) {

        // 1. Regra de Negócio: Validar email único
        if (clienteRepository.existsByEmail(clienteRecebido.getEmail())) {
            throw new RuntimeException("Já existe um cliente cadastrado com este e-mail.");
            // Dica: Mais para frente, trocaremos isso por uma exceção customizada para o @ControllerAdvice pegar.
        }

        // 2. Cria a entidade que será salva
        Cliente novoCliente = new Cliente();
        novoCliente.setNome(clienteRecebido.getNome());
        novoCliente.setEmail(clienteRecebido.getEmail());

        // 3. Precisamos iterar sobre os endereços recebidos e usar o nosso método utilitário
        // para garantir que a chave estrangeira (cliente_id) não fique nula.
        if (clienteRecebido.getEnderecos() != null) {
            for (Endereco enderecoRecebido : clienteRecebido.getEnderecos()) {

                // Cria um endereço novo e limpo para evitar IDs malicioso
                Endereco novoEndereco = new Endereco();
                novoEndereco.setRua(enderecoRecebido.getRua());
                novoEndereco.setCidade(enderecoRecebido.getCidade());
                novoEndereco.setCep(enderecoRecebido.getCep());

                // Amarra os dois lados da relação
                novoCliente.addEndereco(novoEndereco);
            }
        }

        // 4. Salvar no banco (O CascadeType.ALL vai salvar os endereços automaticamente)
        return clienteRepository.save(novoCliente);
    }

    // Lista todos clientes
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    // Buscar cliente por id
    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
    }


    @Transactional
    public Cliente atualizarCliente(Long id, Cliente dadosAtualizados) {

        // 1. Busca minha entidade já salva no banco
        Cliente clienteExistente = buscarPorId(id);

        // 2. MODIFICAR: Validação e Atualização Parcial do E-mail
        if (dadosAtualizados.getEmail() != null && !dadosAtualizados.getEmail().isBlank()) {
            // Regra de Negócio: Se quiser trocar de email, esse novo email já tá cadastrado no banco?
            if (!dadosAtualizados.getEmail().equals(clienteExistente.getEmail()) &&
                    clienteRepository.existsByEmail(dadosAtualizados.getEmail())) {
                throw new RuntimeException("Este e-mail já está em uso por outro cliente.");
            }
            clienteExistente.setEmail(dadosAtualizados.getEmail());
        }

        // 3. MODIFICAR: Atualização Parcial do Nome
        if (dadosAtualizados.getNome() != null && !dadosAtualizados.getNome().isBlank()) {
            clienteExistente.setNome(dadosAtualizados.getNome());
        }

        return clienteRepository.save(clienteExistente);
    }

    @Transactional
    public void deletarCliente(Long id) {

        Cliente clienteExistente = buscarPorId(id);

        // Se o cliente já fez qualquer pedido (independente do status), a exclusão é bloqueada!
        // Isso evita apagar o histórico de vendas ou dar erro de banco.
        // Nota ao professor: o correto seria termos uma coluna no banco de "isAtivo", para não
        // deletar de fato o cliente e seus dados. Mas segui estreitamente os requisitos e padrão que você deixou.
        if (!clienteExistente.getPedidos().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Exclusão negada: O cliente possui histórico de pedidos e não pode ser removido do sistema.");
        }

        // 3. Se passou pelas regras, executa a deleção
        clienteRepository.delete(clienteExistente);
    }









}