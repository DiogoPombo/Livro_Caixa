package com.Livro.Caixa.service;

import com.Livro.Caixa.model.Caixa;
import com.Livro.Caixa.model.MovimentoCaixa;
import com.Livro.Caixa.model.TipoMovimento;
import com.Livro.Caixa.model.Usuario;
import com.Livro.Caixa.repository.CaixaRepository;
import com.Livro.Caixa.repository.MovimentoCaixaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class CaixaService {

    private final CaixaRepository caixaRepo;
    private final MovimentoCaixaRepository movRepo;
    private final Logger log = LoggerFactory.getLogger(CaixaService.class);

    public CaixaService(CaixaRepository caixaRepo, MovimentoCaixaRepository movRepo) {
        this.caixaRepo = caixaRepo;
        this.movRepo = movRepo;
    }

    // Busca ou cria o caixa único
    public Caixa obterCaixaUnico() {
        return caixaRepo.findAll().stream().findFirst()
                .orElseGet(() -> {
                    Caixa novo = new Caixa();
                    novo.setSaldoAtual(BigDecimal.ZERO);
                    novo.setAtualizadoEm(LocalDateTime.now());
                    return caixaRepo.save(novo);
                });
    }

    /**
     * Lista todos os movimentos ordenados por data mais recente primeiro.
     */
    public List<MovimentoCaixa> listarMovimentos() {
        return movRepo.findAll(Sort.by(Sort.Direction.DESC, "dataHora"));
    }

    /**
     * Calcula o total gasto no mês calendário atual,
     * considerando apenas movimentos do tipo SAQUE.
     * Usa como referência a data de última atualização do caixa.
     */
    public BigDecimal calcularTotalSaquesMes() {
        Caixa caixa = obterCaixaUnico();
        LocalDateTime referencia = caixa.getAtualizadoEm() != null ? caixa.getAtualizadoEm() : LocalDateTime.now();

        // Extrai mês/ano da data de referência
        YearMonth mesAno = YearMonth.of(referencia.getYear(), referencia.getMonth());

        LocalDateTime inicioMes = mesAno.atDay(1).atStartOfDay();
        LocalDateTime fimMes = mesAno.atEndOfMonth().atTime(23, 59, 59);

        return movRepo.findAll().stream()
                .filter(m -> m.getTipo() == TipoMovimento.SAQUE
                        && m.getDataHora().isAfter(inicioMes)
                        && m.getDataHora().isBefore(fimMes))
                .map(MovimentoCaixa::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void registrarMovimento(MovimentoCaixa movimento) {
        Caixa caixa = obterCaixaUnico();
        BigDecimal saldoAtual = caixa.getSaldoAtual() != null ? caixa.getSaldoAtual() : BigDecimal.ZERO;

        if (movimento.getTipo() == TipoMovimento.ENTRADA) {
            caixa.setSaldoAtual(saldoAtual.add(movimento.getValor()));
        } else if (movimento.getTipo() == TipoMovimento.SAQUE) {
            caixa.setSaldoAtual(saldoAtual.subtract(movimento.getValor()));
        }

        caixa.setAtualizadoEm(LocalDateTime.now());

        // Usa a data passada pelo formulário, se não vier usa a atual
        if (movimento.getDataHora() == null) {
            movimento.setDataHora(LocalDateTime.now());
        }

        movRepo.save(movimento);
        caixaRepo.save(caixa);

        log.info("Movimento registrado: tipo={} valor={} obs={}",
                movimento.getTipo(), movimento.getValor(), movimento.getObservacao());
    }

    @Transactional
    public void deletarMovimento(Long id) {
        MovimentoCaixa mov = movRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimento não encontrado"));

        Caixa caixa = obterCaixaUnico();
        BigDecimal saldoAtual = caixa.getSaldoAtual() != null ? caixa.getSaldoAtual() : BigDecimal.ZERO;

        // Ajusta saldo conforme tipo
        if (mov.getTipo() == TipoMovimento.ENTRADA) {
            caixa.setSaldoAtual(saldoAtual.subtract(mov.getValor()));
        } else if (mov.getTipo() == TipoMovimento.SAQUE) {
            caixa.setSaldoAtual(saldoAtual.add(mov.getValor()));
        }

        caixa.setAtualizadoEm(LocalDateTime.now());

        movRepo.delete(mov);
        caixaRepo.save(caixa);

        log.info("Movimento deletado: tipo={} valor={} obs={}",
                mov.getTipo(), mov.getValor(), mov.getObservacao());
    }

    @Transactional
    public void registrarEntrada(BigDecimal valor, String obs, Usuario usuario) {
        Caixa caixa = obterCaixaUnico();
        BigDecimal saldoAtual = caixa.getSaldoAtual() != null ? caixa.getSaldoAtual() : BigDecimal.ZERO;

        caixa.setSaldoAtual(saldoAtual.add(valor));
        caixa.setAtualizadoEm(LocalDateTime.now());
        caixaRepo.save(caixa);

        MovimentoCaixa mov = new MovimentoCaixa();
        mov.setTipo(TipoMovimento.ENTRADA);
        mov.setValor(valor);
        mov.setDataHora(LocalDateTime.now());
        mov.setUsuario(usuario);
        mov.setObservacao(obs);
        movRepo.save(mov);

        log.info("Entrada no caixa valor={} obs={}", valor, obs);
    }

    @Transactional
    public void registrarSaque(BigDecimal valor, String obs, Usuario usuario) {
        Caixa caixa = obterCaixaUnico();
        BigDecimal saldoAtual = caixa.getSaldoAtual() != null ? caixa.getSaldoAtual() : BigDecimal.ZERO;

        caixa.setSaldoAtual(saldoAtual.subtract(valor));
        caixa.setAtualizadoEm(LocalDateTime.now());
        caixaRepo.save(caixa);

        MovimentoCaixa mov = new MovimentoCaixa();
        mov.setTipo(TipoMovimento.SAQUE);
        mov.setValor(valor);
        mov.setDataHora(LocalDateTime.now());
        mov.setUsuario(usuario);
        mov.setObservacao(obs);
        movRepo.save(mov);

        log.info("Saque no caixa valor={} obs={}", valor, obs);
    }
}