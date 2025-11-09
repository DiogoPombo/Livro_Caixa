package com.Livro.Caixa.service;

import com.Livro.Caixa.model.Produto;
import com.Livro.Caixa.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repo;
    private final Logger log = LoggerFactory.getLogger(ProdutoService.class);

    public ProdutoService(ProdutoRepository repo) {
        this.repo = repo;
    }

    // Salvar ou atualizar produto
    public Produto salvar(Produto produto) {
        Produto saved = repo.save(produto);
        log.info("Produto salvo: id={}, nome={}", saved.getId(), saved.getNome());
        return saved;
    }

    // Deletar produto por ID
    public void deletar(Long id) {
        repo.deleteById(id);
        log.info("Produto deletado: id={}", id);
    }

    // Listar todos os produtos ativos
    public List<Produto> listarAtivos() {
        return repo.findByAtivoTrue();
    }

    // Buscar produto por ID
    public Produto buscarPorId(Long id) {
        return repo.findById(id).orElse(null);
    }
}