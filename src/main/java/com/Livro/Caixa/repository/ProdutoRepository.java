package com.Livro.Caixa.repository;

import com.Livro.Caixa.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Retorna apenas os produtos ativos
    List<Produto> findByAtivoTrue();
}