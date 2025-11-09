package com.Livro.Caixa.controller;

import com.Livro.Caixa.model.MovimentoCaixa;
import com.Livro.Caixa.service.CaixaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/caixa")
public class CaixaController {

    private final CaixaService caixaService;

    public CaixaController(CaixaService caixaService) {
        this.caixaService = caixaService;
    }

    // Lista todos os movimentos, saldo atual e total gasto no mês
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("caixa", caixaService.obterCaixaUnico());
        model.addAttribute("movimentos", caixaService.listarMovimentos());

        // Novo cálculo: total de saques nos últimos 30 dias
        BigDecimal totalSaquesMes = caixaService.calcularTotalSaquesMes();
        model.addAttribute("totalSaquesMes", totalSaquesMes);

        // Data/hora da atualização
        model.addAttribute("atualizadoEm", LocalDateTime.now());

        return "caixa/lista";
    }

    // Formulário para novo movimento
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("movimento", new MovimentoCaixa());
        return "caixa/form";
    }

    // Salvar novo movimento
    @PostMapping
    public String salvar(@ModelAttribute MovimentoCaixa movimento) {
        caixaService.registrarMovimento(movimento);
        return "redirect:/caixa";
    }

    // Deletar movimento existente
    @PostMapping("/deletar/{id}")
    public String deletarMovimento(@PathVariable Long id) {
        caixaService.deletarMovimento(id);
        return "redirect:/caixa";
    }
}