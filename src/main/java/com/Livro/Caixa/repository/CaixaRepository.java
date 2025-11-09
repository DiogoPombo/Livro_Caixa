package com.Livro.Caixa.repository;

import com.Livro.Caixa.model.Caixa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaixaRepository extends JpaRepository<Caixa, Long> {
}